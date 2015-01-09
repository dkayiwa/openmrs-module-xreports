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
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
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
			
			if ("true".equals(request.getParameter("runner"))) {
				if (StringUtils.isNotBlank(xml)) {
					xml = new ReportBuilder().build(xml, request.getQueryString());
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
			if (def instanceof SimplePatientDataSetDefinition) {
				xml += "<DesignItem type='0' id='0" + DesignItem.NONE + "' name='" + def.getName() + "' description='" + def.getDescription() + "'>";
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
				xml += "</DesignItem>";
			}
		}
		xml += "</DesignItems>";
		
		return " PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR " + xml;
	}
}
