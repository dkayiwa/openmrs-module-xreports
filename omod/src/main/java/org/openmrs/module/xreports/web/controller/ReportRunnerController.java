package org.openmrs.module.xreports.web.controller;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.api.XReportsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportRunnerController {
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/xreports/reportRunner.form")
	public String displayReport(@RequestParam(value = "reportId", required = false) Integer reportId,
	                          @RequestParam(value = "groupId", required = false) Integer groupId,
	                        HttpServletRequest request, HttpSession session, ModelMap map) throws Exception {
		
        Object data =  session.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);;
		
		XReport report = null;
		if (data == null) {
			report = Context.getService(XReportsService.class).getReport(reportId);
		}
		else {
			report = getXReport((ReportData)data);
		}
		
		map.put("formId", report.getReportId());
		map.put("formName", report.getName()); //get name for each template
		
		AdministrationService service = Context.getAdministrationService();
		map.put("defaultFontFamily",
			service.getGlobalProperty("xreports.defaultFontFamily", "Arial"));
		map.put("defaultFontSize", service.getGlobalProperty("xreports.defaultFontSize", "16"));
		
		String color = "#C70074";
		map.put("defaultGroupBoxHeaderBgColor", color);
		
		map.put("formatXml", "false");
		
		String url = "/module/reporting/reports/reportHistory.form";
		if (data == null) {
			data = "/module/xreports/runReports.list" + (groupId != null ? "?groupId=" + groupId : "");
		}
		map.put("closeUrl", request.getContextPath() + url);
		
		map.put("title", report.getName());
		
		url = "/moduleServlet/xreports/reportDownloadServlet?contentType=xml&runner=true&";
		url += request.getQueryString() + "&";
		map.put("reportDownloadServlet", URLDecoder.decode(url, "UTF-8"));
		
		map.put("exportPdfServlet", URLDecoder.decode(("/moduleServlet/xreports/exportPdfServlet?" + request.getQueryString() + "&"), "UTF-8"));
		
		return null;
	}
	
	private XReport getXReport(ReportData reportData) {
		XReport report = new XReport();
		report.setReportId(1);
		report.setName("Some Rugayo");
		return report;
	}
}
