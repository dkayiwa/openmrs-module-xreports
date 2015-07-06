package org.openmrs.module.xreports.page.controller;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class RunReportsPageController {

	public void controller(PageModel model, @RequestParam(required = false, value = "groupId") Integer groupId,
			UiSessionContext emrContext) {

		emrContext.requireAuthentication();
		
		List<XReport> reports = Context.getService(XReportsService.class).getReports(groupId);
		List<XReportGroup> groups = Context.getService(XReportsService.class).getReportGroups(groupId);
		
		model.addAttribute("reports", reports);
		model.addAttribute("groups", groups);
	}
}
