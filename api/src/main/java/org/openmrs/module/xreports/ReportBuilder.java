package org.openmrs.module.xreports;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.xreports.api.XReportsService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ReportBuilder {
	
	public static final String SOURCE_TYPE = "SourceType";
	public static final String PREFIX = "Prefix";
	public static final String SUFFIX = "Suffix";
	public static final String SOURCE_VALUE = "SourceValue";
	public static final String SQL = "SQL";
	public static final String CUSTOM = "Custom";
	public static final String GROUPING = "Grouping";
	public static final String BINDING = "Binding";
	public static final String PARAMETERS = "parameters";
	
	private Map<String, String> fieldValues = new HashMap<String, String>();
	private Map<Element, Element> customItems = new HashMap<Element, Element>();
	
	private List<ReportParameter> parameters = new ArrayList<ReportParameter>();
	
	private String parameterSql = null;
	
	private XReportsService service;
	
	DecimalFormat numberFormat;
	DecimalFormat currencyFormat;
	
	public String build(String xml, String queryStr, XReport report) throws Exception {
		
		service = Context.getService(XReportsService.class);
		
		numberFormat = new DecimalFormat(Context.getAdministrationService().getGlobalProperty("xreports.format.number", "###,###.###"));
		currencyFormat = new DecimalFormat(Context.getAdministrationService().getGlobalProperty("xreports.format.currency", "###,###.### Shs"));
		
		queryStr = URLDecoder.decode(queryStr, "UTF-8");
		
		queryStr = queryStr.replace("contentType=xml&runner=true&", "");
		int index = queryStr.indexOf("&formId=");
		if (index == -1) {
			index = queryStr.indexOf("formId=");
		}
		queryStr = queryStr.substring(0, index);
		if (StringUtils.isNotBlank(queryStr)) {
			String[] params = queryStr.split("&");
			for (String param : params) {
				String[] values = param.split("=");
				if (values.length == 2) {
					fieldValues.put(values[0], values[1]);
				}
			}
		}
		
		Document doc = DOMUtil.fromString2Doc(xml);
		
		String uuid = report.getExternalReportUuid();
		if (StringUtils.isNotBlank(uuid)) {
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportDefinition reportDef = rds.getDefinitionByUuid(uuid);
			ReportData reportData = rds.evaluate(reportDef, new EvaluationContext());
			displayReportData(reportData, doc);
		}
		else {
			parameters = buildParameters(doc);
			buildParameterSql();
			
			processDesignItemValues(doc);
			
			NodeList nodes = doc.getDocumentElement().getElementsByTagName(DesignItem.NAME_PT_POS);
			for (index = 0; index < nodes.getLength(); index++) {
				Node node = nodes.item(index);
				buildPtPosItems(doc, (Element)node);
			}
			
			loadCustomItems();
			
			fieldValues.clear();
			customItems.clear();
		}
		
		return DOMUtil.doc2String(doc);
	}
	
	private void displayReportData(ReportData reportData, Document doc) {
		
	}
	
	private void processDesignItemValues(Document doc) throws Exception {
		List<Element> parameterNodes = new ArrayList<Element>();
		
		NodeList nodes = doc.getDocumentElement().getElementsByTagName("DesignItem");
		for (int index = 0; index < nodes.getLength(); index++) {
			Element item = (Element)nodes.item(index);
			String binding = item.getAttribute("binding");
			if (StringUtils.isBlank(binding)) {
				continue;
			}
			
			if (parameterNodes.contains(item)) {
				continue;
			}
			
			if (PARAMETERS.equals(binding) && GROUPING.equals(item.getAttribute("sourceType"))) {
				//loadParameters(item, parameterNodes);
				continue;
			}
			
			if (fieldValues.containsKey(binding)) {
				continue;
			}
			
			if (SQL.equals(item.getAttribute("sourceType"))) {
				String sql = item.getAttribute("sourceValue");
				if (StringUtils.isNotBlank(sql)) {
					Integer value = service.getSqlIntValue(sql);
					if (value == null) {
						value = 0;
					}
					
					fieldValues.put(binding, value.toString());
				}
			}
		}
		
		//we no longer need design items. so remove them
		nodes = doc.getDocumentElement().getElementsByTagName("DesignItems");
		for (int index = 0; index < nodes.getLength(); index++) {
			Node node = nodes.item(index);
			node.getParentNode().removeChild(node);
		}
	}
	
	private void loadParameters(Element element, List<Element> parameterNodes) {		
		NodeList nodes = element.getChildNodes();
		for (int index = 0; index < nodes.getLength(); index++) {
			Element item = (Element)nodes.item(index);
			
			if (parameterNodes != null) {
				parameterNodes.add(item);
			}
			
			String binding = item.getAttribute("binding");
			if (StringUtils.isBlank(binding)) {
				continue;
			}
			
			String sourceType = item.getAttribute("sourceType");
			if (StringUtils.isBlank(sourceType)) {
				continue;
			}
			
			String sourceValue = item.getAttribute("sourceValue");
			if (StringUtils.isBlank(sourceValue)) {
				continue;
			}
			
			ReportParameter parameter = new ReportParameter();
			parameter.setBinding(binding);
			parameter.setName(item.getAttribute("name"));
			parameter.setDescription(item.getAttribute("description"));
			parameter.setDataType(item.getAttribute("dataType"));
			
			if (SQL.equals(sourceType)) {
				List<List<Object>> values = Context.getAdministrationService().executeSQL(sourceValue, true);
				for (List<Object> value : values) {
					parameter.addValue(new NameValue(value.get(0).toString(), value.get(1).toString()));
				}
			}
			else if (CUSTOM.equals(sourceType)) {
				String[] values = sourceValue.split(",");
				for (String value : values) {
					String[] vals = value.split(":");
					if (vals.length != 2) {
						continue;
					}
					parameter.addValue(new NameValue(vals[0], vals[1]));
				}
			}
			else {
				continue;
			}
			
			if (parameter.getValues().size() > 0) {
				parameter.getValues().add(0, new NameValue("All", "-1"));
			}
			
			parameters.add(parameter);
		}
	}
	
	public List<ReportParameter> buildParameters(String xml) throws Exception {
		return buildParameters(DOMUtil.fromString2Doc(xml));
	}
	
	private List<ReportParameter> buildParameters(Document doc) throws Exception {
		NodeList nodes = doc.getDocumentElement().getElementsByTagName("DesignItem");
		for (int index = 0; index < nodes.getLength(); index++) {
			Element item = (Element)nodes.item(index);
			String binding = item.getAttribute("binding");
			if (StringUtils.isBlank(binding)) {
				continue;
			}

			if (PARAMETERS.equals(binding) && GROUPING.equals(item.getAttribute("sourceType"))) {
				loadParameters(item, null);
				break;
			}
		}
		
		return parameters;
	}
	
	private void buildPtPosItems(Document doc, Element ptPosElement) throws Exception {
		NodeList nodes = ptPosElement.getElementsByTagName(DesignItem.NAME_ITEM);
		if (nodes == null || nodes.getLength() == 0) {
			ptPosElement.getParentNode().removeChild(ptPosElement);
			return;
		}
				
		for (int index = 0; index < nodes.getLength(); index++) {
			Node node = nodes.item(index);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element item = (Element) node;
			
			Element widgetNode = doc.createElement(DesignItem.NAME_ITEM);
			ptPosElement.getParentNode().appendChild(widgetNode);
			
			widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_WIDGETTYPE, DesignItem.WIDGET_TYPE_LABEL);
			
			widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_LEFT, item.getAttribute(DesignItem.PROPERTY_XPOS));
			widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_TOP, item.getAttribute(DesignItem.PROPERTY_YPOS));
			widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_COLOR, item.getAttribute(DesignItem.WIDGET_PROPERTY_COLOR));
			widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_FONT_SIZE,
			    item.getAttribute(DesignItem.WIDGET_PROPERTY_FONT_SIZE));
			widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_FONT_FAMILY,
			    item.getAttribute(DesignItem.WIDGET_PROPERTY_FONT_FAMILY));
			
			String value = item.getAttribute(DesignItem.WIDGET_PROPERTY_FONT_WEIGHT);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_FONT_WEIGHT, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_FONT_STYLE);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_FONT_STYLE, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_TEXT_DECORATION);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_TEXT_DECORATION, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_TEXT_ALIGN);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_TEXT_ALIGN, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_BACKGROUND_COLOR);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_BACKGROUND_COLOR, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_BORDER_STYLE);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_BORDER_STYLE, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_BORDER_WIDTH);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_BORDER_WIDTH, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_BORDER_COLOR);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_BORDER_COLOR, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_HEIGHT);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_HEIGHT, value);
			
			value = item.getAttribute(DesignItem.WIDGET_PROPERTY_WIDTH);
			if (StringUtils.isNotBlank(value))
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_WIDTH, value);

			setValue(item, widgetNode);
		}
		
		ptPosElement.getParentNode().removeChild(ptPosElement);
	}
	
	private void setValue(Element item, Element widgetNode) throws Exception {
		String sourceType = item.getAttribute(SOURCE_TYPE);
		if (SQL.equals(sourceType)) {
			String sql = item.getAttribute(SOURCE_VALUE);
			if (StringUtils.isNotBlank(sql)) {
				Integer value = null;
				try {
					value = service.getSqlIntValue(sql);
				}
				catch(Exception ex) {
					System.out.println(sql);
					ex.printStackTrace();
					throw ex;
				}
				
				String binding = item.getAttribute(BINDING);
				fieldValues.put(binding, value != null ? value.toString() : "0");
				
				String prefix = item.getAttribute(PREFIX);
				if (prefix == null || value == null) {
					prefix = "";
				}
				String suffix = item.getAttribute(SUFFIX);
				if (suffix == null || value == null) {
					suffix = "";
				}
				
				String sValue = (value != null ? value.toString() : "");
				String type = item.getAttribute("DataType");
				if (value != null) {
					if ("Number".equals(type)) {
						sValue = numberFormat.format(value);
					}
					else if ("Currency".equals(type)) {
						sValue = currencyFormat.format(value);
					}
				}
				widgetNode.setAttribute(DesignItem.WIDGET_PROPERTY_TEXT, prefix + sValue + suffix);
			}
		}
		else if (CUSTOM.equals(sourceType)) {
			customItems.put(item, widgetNode);
		}
	}
	
	private void loadCustomItems() throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

		for (Element item : customItems.keySet()) {
			String prefix = item.getAttribute(PREFIX);
			if (prefix == null) {
				prefix = "";
			}
			String suffix = item.getAttribute(SUFFIX);
			if (suffix == null) {
				suffix = "";
			}
			
			String value = item.getAttribute(SOURCE_VALUE);
			value = substituteVariables(value, fieldValues);	
			String result = engine.eval(value).toString();
			if (result.endsWith(".0")) {
				result = "0";
			}
			else if (result.equals("NaN")) {
				result = "";
				prefix = "";
				suffix = "";
			}
			else if (result.contains("Infinity")) {
				result = "0";
			}
			
			//round off JavaScript decimal places
			int index = result.indexOf('.');
			if (index > 0) {
				result = result.substring(0, index);
			}
			
			customItems.get(item).setAttribute(DesignItem.WIDGET_PROPERTY_TEXT, prefix + result + suffix);
		}	
	}
	
	private String substituteVariables(String template, Map<String, String> variables) {
	    Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
	    Matcher matcher = pattern.matcher(template);
	    // StringBuilder cannot be used here because Matcher expects StringBuffer
	    StringBuffer buffer = new StringBuffer();
	    while (matcher.find()) {
	        if (variables.containsKey(matcher.group(1))) {
	            String replacement = variables.get(matcher.group(1));
	            // quote to work properly with $ and {,} signs
	            matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
	        }
	    }
	    matcher.appendTail(buffer);
	    return buffer.toString();
	}
		
	private void buildParameterSql() {
		if (parameters.size() == 0) {
			return;
		}
		
		for (ReportParameter parameter : parameters) {
			String value = fieldValues.get(parameter.getBinding());
			if (StringUtils.isBlank(value) || "-1".equals(value)) {
				continue;
			}
			
			if (parameterSql == null) {
				parameterSql = "";
			}
			else {
				parameterSql += " and ";
			}
			
			if (ReportParameter.DT_TEXT.equals(parameter.getDataType())) {
				parameterSql += parameter.getBinding() + "='" + value + "'";
			}
			else {
				parameterSql += parameter.getBinding() + "=" + value;
			}
		}
		
		if (parameterSql != null) {
			parameterSql = " where " + parameterSql + " and ";
		}
	}
	
	public List<ReportParameter> getParameters() {
		return parameters;
	}
}
