package org.openmrs.module.xreports.page.controller;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.page.PageModel;

public class ReportsPageController {

	public void controller(PageModel model, UiSessionContext emrContext) {

		emrContext.requireAuthentication();
		
		List<XReport> reports = Context.getService(XReportsService.class).getReports();
		model.addAttribute("reports", reports);
	}
}
