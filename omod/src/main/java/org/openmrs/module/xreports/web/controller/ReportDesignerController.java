package org.openmrs.module.xreports.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
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
		map.put("closeUrl", request.getContextPath() + (refapp ? "/xreports/reports.page" : "/module/xreports/report.form") + "?reportId=" + reportId);
	}
}
