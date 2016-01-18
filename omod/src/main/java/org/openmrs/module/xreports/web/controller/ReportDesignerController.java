package org.openmrs.module.xreports.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.api.XReportsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ReportDesignerController {
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/xreports/reportDesigner.form")
	public void displayReportDesigner(HttpServletRequest request, ModelMap map) {
		
		AdministrationService service = Context.getAdministrationService();
		
		String reportId = request.getParameter("reportId");
		
		int id = Integer.parseInt(reportId);
		map.put("formId", id);
		map.put("templateName", Context.getService(XReportsService.class).getReport(id).getName());
		map.put("defaultFontFamily", service.getGlobalProperty("xreports.defaultFontFamily", "Arial"));
		map.put("defaultFontSize", service.getGlobalProperty("xreports.defaultFontSize", "16"));
		
		String color = "#C70074";
		map.put("defaultGroupBoxHeaderBgColor", color);
		map.put("showSubmitSuccessMsg", service.getGlobalProperty("xreports.showSubmitSuccessMsg", "false"));
		map.put("undoRedoBufferSize", service.getGlobalProperty("xreports.undoRedoBufferSize", "100"));
		
		boolean refapp = "true".equals(request.getParameter("refApp"));
		String url = (refapp ? "/xreports/reports.page" : "/module/xreports/report.form") + "?reportId=" + reportId;
		String returnUrl = request.getParameter("returnUrl");
		if (StringUtils.isNotBlank(returnUrl)) {
			url = returnUrl;
		}
		map.put("closeUrl", request.getContextPath() + url);
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/reporting/reports/renderers/defaultReportDesignEditor.htm")
	public String displayReportDesigner(HttpServletRequest request) {
		
		String parameters = request.getParameter("parameters");
		String reportDesignUuid = parameters.replace("reportDesignUuid=", "");
		reportDesignUuid = reportDesignUuid.replace("|", "");
		
		ReportService rs = Context.getService(ReportService.class);
		ReportDesign design = rs.getReportDesignByUuid(reportDesignUuid);
		if (design != null) {
			ReportDefinition reportDef = design.getReportDefinition();
			XReportsService service = Context.getService(XReportsService.class);
			List<XReport> reports = service.getReportsByExternalUuid(reportDef.getUuid());
			if (reports != null && reports.size() > 0) {
				return "redirect:/module/xreports/reportDesigner.form?reportId=" + reports.get(0).getReportId() + 
						"&parameters=" + parameters + 
						"&returnUrl=/module/reporting/reports/manageReportDesigns.form";
			}
		}
		
		return null;
	}
}