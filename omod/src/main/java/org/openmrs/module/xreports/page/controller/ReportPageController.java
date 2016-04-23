package org.openmrs.module.xreports.page.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.xreports.NameValue;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportPageController {

	protected final Log log = LogFactory.getLog(getClass());
	
	public void get(PageModel model, 
	                @RequestParam(required = false, value = "groupId") Integer groupId,
	                @RequestParam(value = "reportId", required = false) Integer reportId) {
		
		String name = "New Report";
		XReport report = new XReport();
		if (reportId != null) {
			report = Context.getService(XReportsService.class).getReport(reportId);
			name = report.getName();
		}

		model.addAttribute("report", report);
		model.put("groups", Context.getService(XReportsService.class).getReportGroups());
		model.put("reportDefinitions", Context.getService(ReportDefinitionService.class).getAllDefinitions(false));
		model.put("reportName", name);
		
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
			@RequestParam(value = "reportId", required = false) Integer reportId,
            @RequestParam(value = "reportName", required = false) String name,
            @RequestParam(value = "identifier", required = false) String identifier,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
            @RequestParam(value = "group", required = false) Integer groupId,
            @RequestParam(value = "externalReportUuid", required = false) String externalReportUuid,
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
		report.setDisplayOrder(displayOrder);
		report.setExternalReportUuid(externalReportUuid);
		
		if (groupId != null) {
			report.setGroup(service.getReportGroup(groupId));
		}
		else {
			report.setGroup(null);
		}
		
		service.saveReport(report);
	
		return "redirect:/xreports/reports.page" + (groupId != null ? "?groupId=" + groupId : "");
	}
}
