package org.openmrs.module.xreports.page.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.NameValue;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportsPageController {

	public void controller(PageModel model, 
	                       @RequestParam(required = false, value = "groupId") Integer groupId,
	                       UiSessionContext emrContext) {

		emrContext.requireAuthentication();
		
		List<XReport> reports = Context.getService(XReportsService.class).getReports(groupId);
		List<XReportGroup> groups = Context.getService(XReportsService.class).getReportGroups(groupId);
		
		model.addAttribute("reports", reports);
		model.addAttribute("groups", groups);
		
		List<NameValue> crumbs = new ArrayList<NameValue>();
		while (groupId != null) {
			XReportGroup group = Context.getService(XReportsService.class).getReportGroup(groupId);
			crumbs.add(0, new NameValue(group.getName(), group.getId().toString()));
			XReportGroup parent = group.getParentGroup();
			if (parent != null)
				groupId = parent.getGroupId();
			else
				groupId = null;
		}
		
		model.addAttribute("crumbs", crumbs);
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
