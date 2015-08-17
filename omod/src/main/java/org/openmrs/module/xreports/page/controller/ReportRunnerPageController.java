package org.openmrs.module.xreports.page.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.xreports.NameValue;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportRunnerPageController {

	public void controller(PageModel model,
			@RequestParam(required = false, value = "groupId") Integer groupId,
			@RequestParam(value = "reportId", required = false) Integer reportId,
			@RequestParam(required = false, value = "reportTitle") String reportTitle,
			HttpServletRequest request, HttpSession session,
			UiSessionContext emrContext, UiUtils ui) throws Exception {

		emrContext.requireAuthentication();
		

        Object data =  session.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);;
		
		XReport report = null;
		if (data == null) {
			report = Context.getService(XReportsService.class).getReport(reportId);
		}
		else {
			report = getXReport((ReportData)data);
		}
		
		model.put("formId", report.getReportId());
		model.put("formName", report.getName()); //get name for each template
		
		AdministrationService service = Context.getAdministrationService();
		model.put("defaultFontFamily",
			service.getGlobalProperty("xreports.defaultFontFamily", "Arial"));
		model.put("defaultFontSize", service.getGlobalProperty("xreports.defaultFontSize", "16"));
		
		String color = "#C70074";
		model.put("defaultGroupBoxHeaderBgColor", color);
		
		model.put("formatXml", "false");
		
		String url = "/module/reporting/reports/reportHistory.form";
		if (data == null) {
			url = "/xreports/runReports.page" + (groupId != null ? "?groupId=" + groupId : "");
		}
		model.put("closeUrl", request.getContextPath() + url);
		
		model.put("title", report.getName());
		
		url = "/moduleServlet/xreports/reportDownloadServlet?contentType=xml&runner=true&";
		url += request.getQueryString() + "&";
		model.put("reportDownloadServlet", URLDecoder.decode(url, "UTF-8"));
		
		model.put("exportPdfServlet", URLDecoder.decode(("/moduleServlet/xreports/exportPdfServlet?" + request.getQueryString() + "&"), "UTF-8"));

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
	
	private XReport getXReport(ReportData reportData) {
		XReport report = new XReport();
		report.setReportId(1);
		report.setName("Some Rugayo");
		return report;
	}
}
