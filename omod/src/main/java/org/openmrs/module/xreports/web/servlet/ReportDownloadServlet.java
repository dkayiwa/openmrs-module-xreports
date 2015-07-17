package org.openmrs.module.xreports.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.xreports.DesignItem;
import org.openmrs.module.xreports.ReportBuilder;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.util.WebUtil;
import org.openmrs.reporting.export.ExportColumn;

public class ReportDownloadServlet extends HttpServlet {
	
	public static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/** The formId request parameter. */
	public static final String REQUEST_PARAM_REPORT_ID = "formId";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter writer = response.getWriter();
		
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
			
			System.out.println("aaaaaaaaaaaaaaa........................bbbbbbbbbbbb");
			
			if ("true".equals(request.getParameter("runner"))) {
				if (StringUtils.isNotBlank(xml)) {
					xml = new ReportBuilder().build(xml, request.getQueryString(), report);
				}
			}
			else {
				String uuid = report.getExternalReportUuid();
				if (uuid != null) {
					ReportDefinition reportDef = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
					if (xml == null) {
						xml = "";
					}
					xml += getDesignItems(reportDef);
				}
			}
			
			if (xml == null) {
				xml = " ";
			}
			
			writer.print(xml);
		}
		catch (Exception ex) {
			WebUtil.reportError(ex, request, response, writer);
		}
	}
	
	private String getDesignItems(ReportDefinition reportDef) {
		if (reportDef == null) {
			return "";
		}
		
		int id = 1;
		
		String xml = "<DesignItems>";
		
		for (Map.Entry<String, Mapped<? extends DataSetDefinition>> e : reportDef.getDataSetDefinitions().entrySet()) {
			DataSetDefinition def = e.getValue().getParameterizable();
			
			xml += "<DesignItem type='0' id='0" + DesignItem.NONE + "' name='" + def.getName() + "' description='" + def.getDescription() + "'>";
			
			if (def instanceof SimplePatientDataSetDefinition) {
				
				for (String property : ((SimplePatientDataSetDefinition) def).getPatientProperties()) {
					xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id++ +"' name='" + property + "' binding='" + property + "' text='" + property + "' sourceType='Custom' />";
				}
				for (PersonAttributeType attribute : ((SimplePatientDataSetDefinition) def).getPersonAttributeTypes()) {
					String property = StringEscapeUtils.escapeXml(attribute.getName());
					xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id++ +"' name='" + property + "' binding='" + attribute.getId() + "' text='" + property + "' sourceType='Custom' />";
				}
				for (PatientIdentifierType identifier : ((SimplePatientDataSetDefinition) def).getIdentifierTypes()) {
					String property = StringEscapeUtils.escapeXml(identifier.getName());
					xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id++ +"' name='" + property + "' binding='" + identifier.getId() + "' text='" + property + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof CohortIndicatorDataSetDefinition) {
				for (CohortIndicatorAndDimensionColumn col : ((CohortIndicatorDataSetDefinition) def).getColumns()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getName() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof CohortCrossTabDataSetDefinition) {
				for (CohortDataSetColumn col : ((CohortCrossTabDataSetDefinition) def).getDataSetColumns()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getName() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof CohortIndicatorAndDimensionDataSetDefinition) {
				for (CohortIndicatorAndDimensionSpecification col : ((CohortIndicatorAndDimensionDataSetDefinition) def).getSpecifications()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getIndicatorNumber() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof CohortsWithVaryingParametersDataSetDefinition) {
				for (Column col : ((CohortsWithVaryingParametersDataSetDefinition) def).getColumns()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof DataExportDataSetDefinition) {
				for (ExportColumn col : ((DataExportDataSetDefinition) def).getDataExport().getColumns()) {
					xml += "<DesignItem type='" + DesignItem.X_POS + "' id='" + id++ +"' name='" + col.getColumnName() + "' binding='" + col.getColumnName() + "' text='" + col.getColumnName() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof LogicDataSetDefinition) {
				for (LogicDataSetDefinition.Column col : ((LogicDataSetDefinition) def).getColumns()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof RowPerObjectDataSetDefinition) {
				for (DataSetColumn col : ((RowPerObjectDataSetDefinition) def).getDataSetColumns()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
				}
			}
			else if (def instanceof SimpleIndicatorDataSetDefinition) {
				for (SimpleIndicatorColumn col : ((SimpleIndicatorDataSetDefinition) def).getColumns()) {
					xml += "<DesignItem type='" + DesignItem.PT_POS + "' id='" + id++ +"' name='" + col.getLabel() + "' binding='" + col.getName() + "' text='" + col.getLabel() + "' sourceType='Custom' />";
				}
			}
			
			xml += "</DesignItem>";
		}
		
		xml += "</DesignItems>";
		
		return " PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR " + xml;
	}
}
