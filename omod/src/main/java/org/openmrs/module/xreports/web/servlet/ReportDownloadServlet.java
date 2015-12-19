package org.openmrs.module.xreports.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition.CohortDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition.Column;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition.SimpleIndicatorColumn;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.xreports.DOMUtil;
import org.openmrs.module.xreports.DesignItem;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.PdfDocument;
import org.openmrs.module.xreports.web.ReportBuilder;
import org.openmrs.module.xreports.web.ReportCommandObject;
import org.openmrs.module.xreports.web.util.WebUtil;
import org.openmrs.reporting.export.ExportColumn;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReportDownloadServlet extends HttpServlet {
	
	public static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/** The formId request parameter. */
	public static final String REQUEST_PARAM_REPORT_ID = "formId";
	
	private List<String> idlist;
	private List<Element> customItems;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter writer = null;
		
		if (!"true".equals(request.getParameter("renderer"))) {
			writer = response.getWriter();
		}
		
		try {
			//try to authenticate users who log on inline (with the request).
			try {
				WebUtil.authenticateInlineUser(request);
			}
			catch (ContextAuthenticationException e) {
				log.error(e.getMessage(), e);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			if (!WebUtil.isAuthenticated(request, response, null)) {
				return;
			}
			
			response.setHeader(XReportsConstants.HTTP_HEADER_CONTENT_TYPE, XReportsConstants.HTTP_HEADER_CONTENT_TYPE_XML);
			response.setCharacterEncoding(XReportsConstants.DEFAULT_CHARACTER_ENCODING);
			
			Integer reportId = Integer.parseInt(request.getParameter(REQUEST_PARAM_REPORT_ID));
			XReport report = Context.getService(XReportsService.class).getReport(reportId);
			String xml = report.getXml();

			if ("true".equals(request.getParameter("runner"))) {
				if (StringUtils.isNotBlank(xml)) {
					ReportCommandObject reportParamData = (ReportCommandObject)request.getSession().getAttribute(XReportsConstants.REPORT_PARAMETER_DATA);
					xml = new ReportBuilder().build(xml, request.getQueryString(), report, reportParamData);
				}
			}
			else if ("true".equals(request.getParameter("renderer"))) {
				ReportCommandObject reportParamData = (ReportCommandObject)request.getSession().getAttribute(XReportsConstants.REPORT_PARAMETER_DATA);

				String filename = DateUtil.formatDate(new Date(), "yyyy-MM-dd-HHmmss");
				filename = reportParamData.getReportDefinition().getName() + "_" + filename + ".pdf";;
	            
				response.setHeader(XReportsConstants.HTTP_HEADER_CONTENT_DISPOSITION, 
						XReportsConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + WebUtil.getXmlToken(filename));
				response.setContentType(XReportsConstants.CONTENT_TYPE_PDF);
				
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", -1);
				response.setHeader("Cache-Control", "no-store");
				response.setCharacterEncoding(XReportsConstants.DEFAULT_CHARACTER_ENCODING);
				
				new PdfDocument().writeFromXml(response.getOutputStream(), new ReportBuilder().build(xml, request.getQueryString(), report, reportParamData), request.getRealPath(""));
				
				return;
			}
			else {
				String uuid = report.getExternalReportUuid();
				if (uuid != null) {
					ReportDefinition reportDef = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
					if (xml == null) {
						xml = "";
					}
					
					Document doc = DOMUtil.fromString2Doc(xml);
					xml = mergeDesignItems(doc, getDesignItems(reportDef, doc));
				}
			}
			
			if (StringUtils.isBlank(xml)) {
				xml = " ";
			}
			
			writer.print(xml);
		}
		catch (Exception ex) {
			WebUtil.reportError(ex, request, response, writer);
		}
	}
	
	private String getDesignItems(ReportDefinition reportDef, Document doc) {
		if (reportDef == null) {
			return "";
		}
		
		Map<String, Element> map = getItemBindingMap(doc);
		
		int id = 0;
		String xml = "<DesignItems>";
		
		for (Map.Entry<String, Mapped<? extends DataSetDefinition>> e : reportDef.getDataSetDefinitions().entrySet()) {
			DataSetDefinition def = e.getValue().getParameterizable();
			
			id = getNextId(id);
			xml += "<DesignItem type='0' binding='" + e.getKey() + "' id='" + id + "' name='" + def.getName() + "' description='" + def.getDescription() + "' sourceType='Grouping' >";
			
			if (def instanceof SimplePatientDataSetDefinition) {
				
				for (String property : ((SimplePatientDataSetDefinition) def).getPatientProperties()) {
					Element node = map.get(property);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(property, property, node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id +"' name='" + property + "' binding='" + property + "' text='" + property + "' sourceType='Custom' />";
					}
				}
				for (PersonAttributeType attribute : ((SimplePatientDataSetDefinition) def).getPersonAttributeTypes()) {
					String property = StringEscapeUtils.escapeXml(attribute.getName());
					Element node = map.get(property);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(property, attribute.getId().toString(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id +"' name='" + property + "' binding='" + attribute.getId() + "' text='" + property + "' sourceType='Custom' />";
					}
				}
				for (PatientIdentifierType identifier : ((SimplePatientDataSetDefinition) def).getIdentifierTypes()) {
					String property = StringEscapeUtils.escapeXml(identifier.getName());
					Element node = map.get(property);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(property, identifier.getId().toString(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id +"' name='" + property + "' binding='" + identifier.getId() + "' text='" + property + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof CohortIndicatorDataSetDefinition) {
				for (CohortIndicatorAndDimensionColumn col : ((CohortIndicatorDataSetDefinition) def).getColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getName() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof CohortCrossTabDataSetDefinition) {
				for (CohortDataSetColumn col : ((CohortCrossTabDataSetDefinition) def).getDataSetColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getName() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof CohortIndicatorAndDimensionDataSetDefinition) {
				for (CohortIndicatorAndDimensionSpecification col : ((CohortIndicatorAndDimensionDataSetDefinition) def).getSpecifications()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getIndicatorNumber(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getIndicatorNumber() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof CohortsWithVaryingParametersDataSetDefinition) {
				for (Column col : ((CohortsWithVaryingParametersDataSetDefinition) def).getColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof DataExportDataSetDefinition) {
				for (ExportColumn col : ((DataExportDataSetDefinition) def).getDataExport().getColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getColumnName(), col.getColumnName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id +"' name='" + col.getColumnName() + "' binding='" + col.getColumnName() + "' text='" + col.getColumnName() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof LogicDataSetDefinition) {
				for (LogicDataSetDefinition.Column col : ((LogicDataSetDefinition) def).getColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof RowPerObjectDataSetDefinition) {
				for (DataSetColumn col : ((RowPerObjectDataSetDefinition) def).getDataSetColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof SimpleIndicatorDataSetDefinition) {
				for (SimpleIndicatorColumn col : ((SimpleIndicatorDataSetDefinition) def).getColumns()) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col.getLabel(), col.getName(), node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
					}
				}
			}
			else if (def instanceof SqlDataSetDefinition) {
				List<String> columns = Context.getService(XReportsService.class).getColumns(((SqlDataSetDefinition) def).getSqlQuery());
				for (String col : columns) {
					Element node = map.get(col);
					if (node != null && ((Element)node.getParentNode()).getAttribute("binding").equals(e.getKey())) {
						xml += copyAttributes(col, col, node);
					}
					else {
						id = getNextId(id);
						xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id +"' name='" + col + "' binding='" + col + "' text='" + col + "' sourceType='Custom' />";
					}
				}
			}
			
			xml += "</DesignItem>";
		}
		
		xml += "</DesignItems>";
		
		return xml;
		//return " PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR " + xml;
	}
	
	private String copyAttributes(String name, String binding, Element node) {
		return "<DesignItem type='" + node.getAttribute("type") + "' id='" + node.getAttribute("id") +"' name='" + name +
				"' binding='" + binding + "' text='" + node.getAttribute("text") + 
				"' description='" + node.getAttribute("description") + "' prefix='" + node.getAttribute("prefix") +
				"' suffix='" + node.getAttribute("suffix") + "' dataType='" + node.getAttribute("dataType") +
				"' sourceValue='" + node.getAttribute("sourceValue") + "' otherData='" + node.getAttribute("otherData") +
				"' sourceType='Custom' />";
	}
	
	private String mergeDesignItems(Document doc, String designItemsXml) throws Exception {
		
		if (StringUtils.isNotBlank(designItemsXml)) {
			NodeList nodes = doc.getDocumentElement().getElementsByTagName("DesignItems");
			if (nodes != null && nodes.getLength() > 0) {
				Element parent = (Element)nodes.item(0);
				parent.getParentNode().removeChild(parent);
			}
			
			Node designItemsNode = DOMUtil.fromString2Doc(designItemsXml).getDocumentElement();
			designItemsNode =  doc.importNode(designItemsNode, true);
			doc.getDocumentElement().appendChild(designItemsNode);
			
			//now add user custom design items
			for (Element item : customItems) {
				List<String> bindings = new ArrayList<String>();
				Element parent = (Element)item.getParentNode();
				while (parent != null) {
					String binding = parent.getAttribute("binding");
					if (StringUtils.isNotBlank(binding)) {
						bindings.add(binding);
					}
					else {
						break;
					}
					
					parent = (Element)parent.getParentNode();
				}
				
				while (bindings.size() > 0) {
					int index = bindings.size() -1;
					String binding = bindings.get(index);
					Element node = getNode(binding, designItemsNode.getChildNodes());
					if (node == null) {
						break;
					}
					
					bindings.remove(index);
					
					if (bindings.size() == 0) {
						Node importedNode =  doc.importNode(item, true);
						node.appendChild(importedNode);
					}
				}
			}
		}
		
		return DOMUtil.doc2String(doc);
	}
	
	private Element getNode(String binding, NodeList nodes) {
		for (int index = 0; index < nodes.getLength(); index++) {
			Element node = (Element)nodes.item(index);
			if (binding.equals(node.getAttribute("binding"))) {
				return node;
			}
		}
		return null;
	}
	
	private Map<String, Element> getItemBindingMap(Document doc) {
		
		HashMap<String, Element> map = new HashMap<String, Element>();
		idlist = new ArrayList<String>();
		customItems = new ArrayList<Element>();
		
		NodeList nodes = doc.getDocumentElement().getElementsByTagName("DesignItem");
		for (int index = 0; index < nodes.getLength(); index++) {
			Element node = (Element)nodes.item(index);
			String binding = node.getAttribute("binding");
			map.put(binding, node);
			idlist.add(node.getAttribute("id"));
			
			String sourceValue = node.getAttribute("sourceValue");
			if ("Numbering".equalsIgnoreCase(binding) || StringUtils.isNotBlank(sourceValue)) {
				customItems.add(node);
			}
		}
		
		return map;
	}
	
	private int getNextId(int id) {
		id++;
		while (idlist.contains(String.valueOf(id))) {
			id++;
		}
		return id;
	}
}