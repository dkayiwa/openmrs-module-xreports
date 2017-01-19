package org.openmrs.module.xreports.web;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.xreports.DOMUtil;
import org.openmrs.module.xreports.DesignItem;
import org.openmrs.module.xreports.NameValue;
import org.openmrs.module.xreports.ReportParameter;
import org.openmrs.module.xreports.XReport;
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
	
	public static final int SECOND_POSITION = 110;
	public static final int SECOND_PAGE = 111;
	
	private Map<String, String> fieldValues = new HashMap<String, String>();
	private Map<Element, Element> customItems = new HashMap<Element, Element>();
	
	private List<ReportParameter> parameters = new ArrayList<ReportParameter>();
	
	private String parameterSql = null;
	
	private XReportsService service;
	
	private DecimalFormat numberFormat;
	private DecimalFormat currencyFormat;
	
	private int currentIndex = 0;
	
	private Map<String, String> fieldMapping = new HashMap<String, String>();
	private Map<String, Element> designItemMap = new HashMap<String, Element>();
	private Map<String, Integer> ptPosXDisp = new HashMap<String, Integer>();
	private Map<String, Integer> ptPosYDisp = new HashMap<String, Integer>();
	
	private boolean columnFoundInDataset = false;
	
	public String build(String xml, String queryStr, XReport report, ReportCommandObject reportParamData) throws Exception {

		service = Context.getService(XReportsService.class);
		
		numberFormat = new DecimalFormat(Context.getAdministrationService().getGlobalProperty("xreports.format.number", "###,###.###"));
		currencyFormat = new DecimalFormat(Context.getAdministrationService().getGlobalProperty("xreports.format.currency", "###,###.### Shs"));
		
		queryStr = URLDecoder.decode(queryStr, "UTF-8");
		
		queryStr = queryStr.replace("contentType=xml&runner=true&", "");
		int index = queryStr.indexOf("&formId=");
		if (index == -1) {
			index = queryStr.indexOf("formId=");
		}
		
		if (index != -1) {
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
		}
		
		Document doc = DOMUtil.fromString2Doc(xml);
		
		String uuid = report.getExternalReportUuid();
		if (StringUtils.isNotBlank(uuid)) {
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportDefinition reportDef = rds.getDefinitionByUuid(uuid);
			EvaluationContext context = new EvaluationContext();
			if (reportParamData != null) {
				if (reportParamData.getBaseCohort() != null) {
					Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(reportParamData.getBaseCohort(), context);
					context.setBaseCohort(baseCohort);
				}
				
				Map<String, Object> params = getParameters(reportParamData);
				if (params.size() > 0) {
					context.setParameterValues(params);
				}
			}
			ReportData reportData = rds.evaluate(reportDef, context);
			displayReportData(reportData, doc);
		}
		else {
			parameters = buildParameters(doc);
			buildParameterSql();
			
			processDesignItemValues(doc);
			
			NodeList nodes = doc.getDocumentElement().getElementsByTagName(DesignItem.NAME_PT_POS);
			for (index = 0; index < nodes.getLength(); index++) {
				Node node = nodes.item(index);
				buildPtPosItems(doc, (Element)node, null, null);
			}
			
			loadCustomItems();
			
			fieldValues.clear();
			customItems.clear();
		}

		return DOMUtil.doc2String(doc);
	}
	
	public Map<String, Object> getParameters(ReportCommandObject userParams) {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		ReportDefinition reportDefinition = userParams.getReportDefinition();
		if (reportDefinition.getParameters() != null && (userParams.getUserEnteredParams() != null || userParams.getExpressions() != null)) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				Object value = null;
				String expression = null;
				if(userParams.getExpressions() != null && ObjectUtil.notNull(userParams.getExpressions().get(parameter.getName())))
					expression = userParams.getExpressions().get(parameter.getName());
				else
					value = userParams.getUserEnteredParams().get(parameter.getName());
				
				if (ObjectUtil.notNull(value) || ObjectUtil.notNull(expression)) {
					try {
						if (StringUtils.isNotEmpty(expression))
							value = expression;
						else
							value = WidgetUtil.parseInput(value, parameter.getType(), parameter.getCollectionType());
						
						params.put(parameter.getName(), value);
					}
					catch (Exception ex) {
						System.out.println("userEnteredParams[" + parameter.getName() + "]" + "  " +  ex.getMessage());
					}
				}
			}
		}
		
		return params;
	}
	
	private void displayReportData(ReportData reportData, Document doc) throws Exception {
		String TYPE_XPOS = "2";
		NodeList nodes = doc.getDocumentElement().getElementsByTagName("DesignItem");
		for (int index = 0; index < nodes.getLength(); index++) {
			Element node = (Element)nodes.item(index);
			if (TYPE_XPOS.equals(node.getAttribute("type"))) {
				fieldMapping.put(node.getAttribute("id"), node.getAttribute("binding"));
				designItemMap.put(node.getAttribute("id"), node);
			}
		}
		
		buildXPosItems(reportData, doc);
		
		//now point pos items'
		String TYPE_PT_POS = "1";
		fieldMapping.clear();
		for (int index = 0; index < nodes.getLength(); index++) {
			Element node = (Element)nodes.item(index);
			if (TYPE_PT_POS.equals(node.getAttribute("type"))) {
				fieldMapping.put(node.getAttribute("id"), node.getAttribute("binding"));
			}
		}
		
		nodes = doc.getDocumentElement().getElementsByTagName(DesignItem.NAME_PT_POS);
		for (int index = 0; index < nodes.getLength(); index++) {
			Node node = nodes.item(index);
			
			columnFoundInDataset = false;
			
			Iterator<String> iterator = reportData.getDataSets().keySet().iterator();
			while (iterator.hasNext()) {
				String dataSetName = iterator.next();
				DataSet ds = reportData.getDataSets().get(dataSetName);
				DataSetRow row = getPtPosRow(ds.iterator());
				
				buildPtPosItems(doc, (Element)node, row, dataSetName);
				
				if (columnFoundInDataset) {
					break;
				}
			}
		}
	}
		
	private DataSetRow getPtPosRow(Iterator<DataSetRow> iterator) {
		if (!iterator.hasNext()) {
			return null;
		}
		DataSetRow row = iterator.next();
		if (iterator.hasNext()) {
			//return null;
		}
		return row;
	}
	
	public String getReportData(ReportData reportData, Document doc) throws Exception {
		service = Context.getService(XReportsService.class);
		
		numberFormat = new DecimalFormat(Context.getAdministrationService().getGlobalProperty("xreports.format.number", "###,###.###"));
		currencyFormat = new DecimalFormat(Context.getAdministrationService().getGlobalProperty("xreports.format.currency", "###,###.### Shs"));

		displayReportData(reportData, doc);
		
		return DOMUtil.doc2String(doc);
	}
	
	private List<Element> getTables(Document doc) {
		List<Element> tables = new ArrayList<Element>();
		NodeList nodes = doc.getDocumentElement().getElementsByTagName(DesignItem.NAME_ITEM);
		for (int index = 0; index < nodes.getLength(); index++) {
			Element element = (Element) nodes.item(index);
			String widgetType = element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE);
			if (LayoutConstants.TYPE_TABLE.equals(widgetType)) {
				tables.add(element);
			}
		}
		return tables;
	}
	
	private void buildXPosItems(ReportData reportData, Document doc) throws Exception {
		
		Element root = doc.getDocumentElement();
		
		int pageHeight = 800;
		int pageMargin = 50;
		int noPages = 1;

		String s = root.getAttribute("PageHeight");
		if (s != null && s.trim().length() > 0) {
			pageHeight = Integer.parseInt(s);
		}
		s = root.getAttribute("PageMargin");
		if (s != null && s.trim().length() > 0) {
			pageMargin = Integer.parseInt(s);
		}
		
		int pageBottom = pageHeight - pageMargin;
		
		Integer secondPage = null;
		NodeList nodes = doc.getElementsByTagName(DesignItem.NAME_PT_POS);
		if (nodes != null && nodes.getLength() > 0) {
			for (int index = 0; index < nodes.getLength(); index++) {
				Element ptPosItem = (Element)nodes.item(0);
				NodeList nodelist = ptPosItem.getElementsByTagName(DesignItem.NAME_ITEM);
				for (int i = 0; i < nodelist.getLength(); i++) {
					Element element = (Element) nodelist.item(i);
					s = element.getAttribute(LayoutConstants.PROPERTY_ID);
					if ((SECOND_PAGE + "").equals(s)) {
						s = element.getAttribute(DesignItem.PROPERTY_YPOS);
						secondPage = Integer.parseInt(s.substring(0, s.length() - 2));
						break;
					}
				}
				
				/*if (ptPosItem != null) {
					ptPosItem.getParentNode().removeChild(ptPosItem);
					index--;
				}*/
			}
		}
		
		if (secondPage == null) {
			secondPage = pageHeight + pageMargin;
		}

		List<String> finishedDatasets = new ArrayList<String>();
		List<Element> tables = getTables(doc);
		if (tables.size() > 0) {
			for (Element tableElement : tables) {
				
				Element lineElement = null;
				List<Element> verticalLines = new ArrayList<Element>();
				int orgLineY = 0;
				
				nodes = tableElement.getElementsByTagName(DesignItem.NAME_ITEM);
				for (int index = 0; index < nodes.getLength(); index++) {
					Element element = (Element) nodes.item(index);
					String widgetType = element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE);
					if (LayoutConstants.TYPE_HORIZONTAL_LINE.equals(widgetType)) {
						String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
						orgLineY = Integer.parseInt(top.substring(0, top.length() - 2));
						lineElement = element;
					}
					else if (LayoutConstants.TYPE_VERTICAL_LINE.equals(widgetType)) {
						verticalLines.add(element);
					}
				}
				
				Element tableParent = (Element)tableElement.getParentNode();
				Element cloneTableElement = (Element)tableElement.cloneNode(true);
	
				s = tableElement.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
				int orgTableHeight = Integer.parseInt(s.substring(0, s.length() - 2));
				
				s = tableElement.getAttribute(LayoutConstants.PROPERTY_TOP);
				int tableTop = Integer.parseInt(s.substring(0, s.length() - 2));
	
				int increment = orgTableHeight - orgLineY;
					
				Element xposItem = null;
				nodes = tableElement.getElementsByTagName(DesignItem.NAME_XPOS);
				if (nodes != null && nodes.getLength() > 0) {
					xposItem = (Element)nodes.item(0);
					nodes = xposItem.getElementsByTagName(DesignItem.NAME_ITEM);
				}
				
				//compute the displacement size of the pt pos in x pos items
				ptPosXDisp.clear();
				ptPosYDisp.clear();
				NodeList ptPosItemNodes = null;
				NodeList ptPosNodes = tableElement.getElementsByTagName(DesignItem.NAME_PT_POS);
				if (ptPosNodes != null && ptPosNodes.getLength() > 0) {
					
					ptPosItemNodes = ((Element)ptPosNodes.item(0)).getElementsByTagName(DesignItem.NAME_ITEM);
					
					for (int index = 0; index < ptPosItemNodes.getLength(); index++) {
						Element ptItem = (Element)ptPosItemNodes.item(index);
						
						s = ptItem.getAttribute(DesignItem.PROPERTY_XPOS);
						int ptXpos = (Integer.parseInt(s.substring(0, s.length() - 2)));
						
						int minXdiff = Integer.MAX_VALUE;
						int yDiff = 0;
						
						for (int i = 0; i < nodes.getLength(); i++) {
							Element element = (Element) nodes.item(i);
							
							if ("Numbering".equalsIgnoreCase(element.getAttribute("Binding"))) {
								continue;
							}
							
							s = element.getAttribute(DesignItem.PROPERTY_XPOS);
							int orgXpos = (Integer.parseInt(s.substring(0, s.length() - 2)));
							
							int diff = orgXpos - ptXpos;
							if (diff < minXdiff) {
								minXdiff = diff;
							}
							
							if (yDiff == 0) {
								s = ptItem.getAttribute(DesignItem.PROPERTY_YPOS);
								int ptYpos = (Integer.parseInt(s.substring(0, s.length() - 2)));
								
								s = element.getAttribute(DesignItem.PROPERTY_YPOS);
								int orgYpos = (Integer.parseInt(s.substring(0, s.length() - 2)));
								
								yDiff = orgYpos - ptYpos;
							}
						}
						
						String id = ptItem.getAttribute(DesignItem.PROPERTY_ID);
						
						ptPosXDisp.put(id, minXdiff);
						ptPosYDisp.put(id, yDiff);
					}
				}
	
				int tableHeight = orgTableHeight;
				int lineY = orgLineY;
				int currentY = tableTop + lineY;
				int currentTableIndex = 0;
				
				Iterator<String> iterator = reportData.getDataSets().keySet().iterator();
				while (iterator.hasNext()) {
					columnFoundInDataset = false;
					currentIndex = 0;
					currentTableIndex = 0;
					String dataSetName = iterator.next();
					DataSet ds = reportData.getDataSets().get(dataSetName);
					for (DataSetRow row : ds) {
						currentTableIndex++;
						
						if (++currentIndex > 1) {
							if (currentIndex == 2 && !columnFoundInDataset) {
								break;
							}
							
							currentY += increment;
							lineY += increment;
							tableHeight += increment;
							
							if (currentY > pageBottom) {
								tableHeight -= increment;
								
								s = tableHeight + "px";
								tableElement.setAttribute(LayoutConstants.PROPERTY_HEIGHT, s);
								for (Element line : verticalLines) {
									line.setAttribute(LayoutConstants.PROPERTY_HEIGHT, s);
								}
								
								currentTableIndex = 1;
								
								lineY = orgLineY;
								tableHeight = orgTableHeight;
								tableTop = pageBottom + (secondPage - pageHeight) + pageMargin;
								currentY = tableTop + lineY;
								noPages++;
								pageBottom = (noPages * pageHeight) - pageMargin;;
								
								tableElement = (Element)cloneTableElement.cloneNode(true);
								tableElement.setAttribute(LayoutConstants.PROPERTY_TOP, tableTop + "px");
								tableParent.appendChild(tableElement);
								
								verticalLines.clear();
								NodeList nds = tableElement.getElementsByTagName(DesignItem.NAME_ITEM);
								for (int index = 0; index < nds.getLength(); index++) {
									Element element = (Element) nds.item(index);
									String widgetType = element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE);
									if (LayoutConstants.TYPE_VERTICAL_LINE.equals(widgetType)) {
										verticalLines.add(element);
									}
								}
							}
							
							if (currentTableIndex > 1) {
								//Add separator horizontal line
								Element item = doc.createElement(DesignItem.NAME_ITEM);
								tableElement.appendChild(item);
								
								copyAttributes(item, lineElement);
								item.setAttribute(LayoutConstants.PROPERTY_TOP, lineY + "px");
								item.setAttribute(LayoutConstants.PROPERTY_WIDGETTYPE, LayoutConstants.TYPE_HORIZONTAL_LINE);
							}
						}
						
						//Add xpos items for the current row
						for (int i = 0; i < nodes.getLength(); i++) {
							Element element = (Element) nodes.item(i);
							
							String value = getValue(element, row, dataSetName);
							if (value == null) {
								continue;
							}
							
							s = element.getAttribute(DesignItem.PROPERTY_YPOS);
							int ypos = (Integer.parseInt(s.substring(0, s.length() - 2)));
							ypos += (increment * (currentTableIndex - 1));
							
							Element item = doc.createElement(DesignItem.NAME_ITEM);
							tableElement.appendChild(item);
							
							copyAttributes(item, element);
							item.setAttribute(LayoutConstants.PROPERTY_LEFT, element.getAttribute(DesignItem.PROPERTY_XPOS));
							item.setAttribute(LayoutConstants.PROPERTY_TOP, ypos + "px");
							item.setAttribute(LayoutConstants.PROPERTY_TEXT, value);
							
							//add displacement items if any
							if (ptPosItemNodes != null && !"Numbering".equalsIgnoreCase(element.getAttribute("Binding"))) {
								
								Element ptPosItemNode = (Element)ptPosItemNodes.item(0);
								
								String id = ptPosItemNode.getAttribute(DesignItem.PROPERTY_ID);
								
								value = getValue(ptPosItemNode.getAttribute("Binding"), ptPosItemNode, row, dataSetName);
								if (value == null) {
									continue;
								}
								
								int xdiff = ptPosXDisp.get(id);
								int ydiff = ptPosYDisp.get(id);
								
								s = element.getAttribute(DesignItem.PROPERTY_XPOS);
								int xpos = (Integer.parseInt(s.substring(0, s.length() - 2)));
								
								item = doc.createElement(DesignItem.NAME_ITEM);
								tableElement.appendChild(item);
								
								copyAttributes(item, ptPosItemNode);
								item.setAttribute(LayoutConstants.PROPERTY_LEFT, (xpos - xdiff) + "px");
								item.setAttribute(LayoutConstants.PROPERTY_TOP, (ypos - ydiff) + "px");
								item.setAttribute(LayoutConstants.PROPERTY_TEXT, value);
							}
						}
						
						if (currentIndex == 1 && columnFoundInDataset) {
							finishedDatasets.add(dataSetName);
						}
					}
				}
				
				//Set vertical lines height
				s = tableHeight + "px";
				tableElement.setAttribute(LayoutConstants.PROPERTY_HEIGHT, s);
				for (Element line : verticalLines) {
					s = line.getAttribute(LayoutConstants.PROPERTY_TOP);
					int top = (Integer.parseInt(s.substring(0, s.length() - 2)));
					s = (tableHeight - top) + "px";
					line.setAttribute(LayoutConstants.PROPERTY_HEIGHT, s);
				}
				
				if (xposItem != null) {
					xposItem.getParentNode().removeChild(xposItem);
				}
				
				for (String name : finishedDatasets) {
					reportData.getDataSets().remove(name);
				}
				finishedDatasets.clear();
			}
		}
		
		
		//now those which are not in tables
		Integer secondPos = null;
		nodes = doc.getElementsByTagName(DesignItem.NAME_PT_POS);
		if (nodes != null && nodes.getLength() > 0) {
			Element ptPosItem = (Element)nodes.item(0);
			nodes = ptPosItem.getElementsByTagName(DesignItem.NAME_ITEM);
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				s = element.getAttribute(LayoutConstants.PROPERTY_ID);
				if ((SECOND_POSITION + "").equals(s)) {
					s = element.getAttribute(DesignItem.PROPERTY_YPOS);
					secondPos = Integer.parseInt(s.substring(0, s.length() - 2));
					break;
				}
			}
			
			/*if (ptPosItem != null) {
				ptPosItem.getParentNode().removeChild(ptPosItem);
			}*/
		}

		nodes = doc.getElementsByTagName(DesignItem.NAME_XPOS);
		if (nodes != null && nodes.getLength() > 0) {
			Element xposItem = (Element)nodes.item(0);
			nodes = xposItem.getElementsByTagName(DesignItem.NAME_ITEM);
			
			int ypos = -1;
			int increment = 30;
			
			Iterator<String> iterator = reportData.getDataSets().keySet().iterator();
			while (iterator.hasNext()) {
				columnFoundInDataset = false;
				currentIndex = 0;
				String dataSetName = iterator.next();
				DataSet ds = reportData.getDataSets().get(dataSetName);
				for (DataSetRow row : ds) {
					currentIndex++;
					
					if (currentIndex == 2 && !columnFoundInDataset) {
						break;
					}
					
					if (currentIndex > 1) {
						ypos += increment;
					}
					
					if (ypos > pageBottom) {
						ypos = (noPages * pageHeight) + pageMargin;
						noPages++;
						pageBottom = (noPages * pageHeight) - pageMargin;
					}
					
					for (int i = 0; i < nodes.getLength(); i++) {
						Element element = (Element) nodes.item(i);
						
						if (ypos == -1) {
							s = element.getAttribute(DesignItem.PROPERTY_YPOS);
							ypos = (Integer.parseInt(s.substring(0, s.length() - 2)));
							if (secondPos != null) {
								increment = secondPos - ypos;
							}
						}
						
						Element item = doc.createElement(DesignItem.NAME_ITEM);
						doc.getDocumentElement().appendChild(item);
						
						copyAttributes(item, element);
						item.setAttribute(LayoutConstants.PROPERTY_LEFT, element.getAttribute(DesignItem.PROPERTY_XPOS));
						item.setAttribute(LayoutConstants.PROPERTY_TOP, ypos + "px");
						item.setAttribute(LayoutConstants.PROPERTY_TEXT, getValue(element, row, dataSetName));
					}
				}
			}
			
			if (xposItem != null) {
				xposItem.getParentNode().removeChild(xposItem);
			}
		}
		
		s = root.getAttribute("Height");
		int height = (Integer.parseInt(s.substring(0, s.length() - 2)));
		if (height < (pageHeight * noPages)) {
			root.setAttribute("Height", (pageHeight * noPages) + "px");
		}
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
					Object value = service.getSqlValue(sql);
					if (value == null) {
						value = 0f;
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
	
	private void buildPtPosItems(Document doc, Element ptPosElement, DataSetRow row, String dataSetName) throws Exception {
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

			setValue(item, widgetNode, row, dataSetName);
		}
		
		//ptPosElement.getParentNode().removeChild(ptPosElement);
	}
	
	private void setValue(Element item, Element widgetNode, DataSetRow row, String dataSetName) throws Exception {
		String sourceType = item.getAttribute(SOURCE_TYPE);
		if (SQL.equals(sourceType)) {
			String sql = item.getAttribute(SOURCE_VALUE);
			if (StringUtils.isNotBlank(sql)) {
				Object value = null;
				try {
					value = service.getSqlValue(sql);
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
		else if (row != null) {
			widgetNode.setAttribute(LayoutConstants.PROPERTY_TEXT, getValue(item, row, dataSetName));
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
	    String value = buffer.toString();
	    
	    //save the user from requiring the ${name} syntax
	    if (value.equals(template)) {
	    	for (Entry<String, String> entry : variables.entrySet()) {
	    		value = value.replace(entry.getKey(), entry.getValue());
	    	}
	    }
	    
	    return value;
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
	
	private Element copyAttributes(Element item, Element element) {
		item.setAttribute(LayoutConstants.PROPERTY_WIDGETTYPE, LayoutConstants.TYPE_LABEL);
		item.setAttribute(LayoutConstants.PROPERTY_LEFT, element.getAttribute(LayoutConstants.PROPERTY_LEFT));
		item.setAttribute(LayoutConstants.PROPERTY_FONT_FAMILY, element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY));
		item.setAttribute(LayoutConstants.PROPERTY_FONT_SIZE, element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE));
		item.setAttribute(LayoutConstants.PROPERTY_COLOR, element.getAttribute(LayoutConstants.PROPERTY_COLOR));
		item.setAttribute(LayoutConstants.PROPERTY_WIDTH, element.getAttribute(LayoutConstants.PROPERTY_WIDTH));
		item.setAttribute(LayoutConstants.PROPERTY_HEIGHT, element.getAttribute(LayoutConstants.PROPERTY_HEIGHT));
		item.setAttribute(LayoutConstants.PROPERTY_BORDER_STYLE, element.getAttribute(LayoutConstants.PROPERTY_BORDER_STYLE));
		item.setAttribute(LayoutConstants.PROPERTY_BORDER_WIDTH, element.getAttribute(LayoutConstants.PROPERTY_BORDER_WIDTH));
		item.setAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT, element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT));
		item.setAttribute(LayoutConstants.PROPERTY_BORDER_COLOR, element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR));
		item.setAttribute(LayoutConstants.PROPERTY_FONT_STYLE, element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE));
		item.setAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION, element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION));
		item.setAttribute(LayoutConstants.PROPERTY_TEXT_ALIGN, element.getAttribute(LayoutConstants.PROPERTY_TEXT_ALIGN));
		item.setAttribute(LayoutConstants.PROPERTY_BACKGROUND_COLOR, element.getAttribute(LayoutConstants.PROPERTY_BACKGROUND_COLOR));
		return item;
	}
	
	public String getValue(Element item, DataSetRow row, String dataSetName) {
		return this.getValue(null, item, row, dataSetName);
	}
	
	public String getValue(String binding, Element item, DataSetRow row, String dataSetName) {
		String designItemId = item.getAttribute(DesignItem.PROPERTY_ID);
		if (binding == null) {
			binding = fieldMapping.get(designItemId);
		}
		if (binding != null) {

			String prefix = item.getAttribute(PREFIX);
			if (prefix == null) {
				prefix = "";
			}
			String suffix = item.getAttribute(SUFFIX);
			if (suffix == null) {
				suffix = "";
			}
			
			if ("Numbering".equalsIgnoreCase(binding)) {
				Element node = designItemMap.get(designItemId);
				if (node != null && dataSetName.equals(((Element)node.getParentNode()).getAttribute("binding"))) {
					return prefix + currentIndex + suffix;
				}
			}
			else {
				Object value = row.getColumnValue(binding);
				if (value instanceof Date) {
					columnFoundInDataset = true;
					return prefix + Context.getDateFormat().format(value) + suffix;
				}
				if (value != null) {
					columnFoundInDataset = true;
					return prefix + value.toString() + suffix;
				}
			}
		}
		
		return null;
	}
}
