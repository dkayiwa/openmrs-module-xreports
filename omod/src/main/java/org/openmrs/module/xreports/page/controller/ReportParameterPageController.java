package org.openmrs.module.xreports.page.controller;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.NameValue;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportParameterPageController {

	public void controller(PageModel model,
			@RequestParam(required = false, value = "groupId") Integer groupId,
			@RequestParam(required = false, value = "reportId") Integer reportId,
			UiSessionContext emrContext, UiUtils ui) {
		
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
		
		XReport report = Context.getService(XReportsService.class).getReport(reportId);
		model.put("formName", report.getName());
	}
}
