package org.openmrs.module.xreports.page.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportPageController {

	protected final Log log = LogFactory.getLog(getClass());
	
	public void get(PageModel model, @RequestParam(value = "reportId", required = false) Integer reportId) {
		
		XReport report = new XReport();
		if (reportId != null) {
			report = Context.getService(XReportsService.class).getReport(reportId);
		}
		
		List<XReportGroup> groups = Context.getService(XReportsService.class).getReportGroups();

		model.addAttribute("report", report);
		model.put("groups", groups);
	}
	
	public String post(PageModel model,
			@RequestParam(value = "reportId", required = false) Integer reportId,
            @RequestParam(value = "reportName", required = false) String name,
            @RequestParam(value = "identifier", required = false) String identifier,
            @RequestParam(value = "group", required = false) Integer groupId,
            HttpSession session, UiUtils ui) {

		if (StringUtils.isBlank(name)) {
			session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
				    ui.message("xreports.name.required", "xreports"));
			return null;
		}
		
		XReportsService service = Context.getService(XReportsService.class);
		
		XReport report = new XReport();
		if (reportId != null) {
			report = service.getReport(reportId);
		}
		
		report.setName(name);
		report.setIdentifier(identifier);
		if (groupId != null) {
			report.setGroup(service.getReportGroup(groupId));
		}
		else {
			report.setGroup(null);
		}
		
		service.saveReport(report);
	
		return "redirect:/xreports/reports.page";
	}
}
