package org.openmrs.module.xreports.page.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.xreports.NameValue;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class RunReportsPageController {

	public String controller(PageModel model,
			@RequestParam(required = false, value = "groupId") Integer groupId,
			@RequestParam(required = false, value = "reportId") Integer reportId,
			@RequestParam(required = false, value = "reportTitle") String reportTitle,
			UiSessionContext emrContext, UiUtils ui) {

		emrContext.requireAuthentication();
		
		if (reportId != null) {
			XReport report = Context.getService(XReportsService.class).getReport(reportId);
			String uuid = report.getExternalReportUuid();
			if (StringUtils.isNotBlank(uuid)) {
				ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
				ReportDefinition reportDef = rds.getDefinitionByUuid(uuid);
				if (reportDef != null && reportDef.getParameters().size() > 0) {
					return "redirect:/xreports/reportParameter.page?reportId=" + reportId + (groupId != null ? "&groupId=" + groupId : "");
				}
			}
			return "redirect:/xreports/reportRunner.page?reportId=" + reportId + (groupId != null ? "&groupId=" + groupId : "");
		}
		
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
		
		return null;
	}
}
