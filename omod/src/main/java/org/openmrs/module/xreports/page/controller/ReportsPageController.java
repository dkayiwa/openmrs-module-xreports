package org.openmrs.module.xreports.page.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportsPageController {

	public void controller(PageModel model, UiSessionContext emrContext) {

		emrContext.requireAuthentication();
		
		List<XReport> reports = Context.getService(XReportsService.class).getReports();
		model.addAttribute("reports", reports);
	}
	
	public String post(PageModel model,
			@RequestParam(value = "reportId") Integer reportId,
            @RequestParam(value = "action") String action,
            HttpSession session, UiUtils ui) {

		if (action.equals("removeReport")) {
			XReportsService service = Context.getService(XReportsService.class);
			XReport report = service.getReport(reportId);
			Context.getService(XReportsService.class).deleteReport(report);
		}
	
		return "redirect:/xreports/reports.page";
	}
}
