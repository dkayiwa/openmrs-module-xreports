package org.openmrs.module.xreports.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.xreports.ReportBuilder;
import org.openmrs.module.xreports.ReportParameter;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportController {
	
	@RequestMapping(value = "/module/xreports/runReports.list", method = RequestMethod.GET)
	public String showReport(ModelMap model,
			@RequestParam(required = false, value = "groupId") Integer groupId, 
			@RequestParam(required = false, value = "refApp") String refApp,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			WebUtil.authenticateInlineUser(request);
		}
		catch (ContextAuthenticationException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		
		if (!WebUtil.isAuthenticated(request, response, null)) {
			return null;
		}
		
		XReportGroup group = null;
		if (groupId != null) {
			group = Context.getService(XReportsService.class).getReportGroup(groupId);
			model.put("reportTitle", group.getName());
		}
		
		XReportsService service = Context.getService(XReportsService.class);
		
		List<XReport> reports = service.getReports(groupId);
		List<XReportGroup> groups = service.getReportGroups(groupId);
		
		model.put("reports", reports);
		model.put("groups", groups);
		
		if ("true".equals(refApp)) {
			return "redirect:/xreports/runReports.page" + (groupId != null ? "?groupId=" + groupId : "");
		}
		else {
			return "module/xreports/runReportsList";
		}
	}
	
	@RequestMapping(value = {"/module/xreports/runReport.form", "/xreports/runReport.form"}, method = RequestMethod.GET)
	public String showForm(@RequestParam(required = false, value = "reportId") Integer reportId,
	                       @RequestParam(required = false, value = "groupId") Integer groupId,
	                       @RequestParam(required = false, value = "refApp") String refApp,
	                       ModelMap model,
	                       HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			WebUtil.authenticateInlineUser(request);
		}
		catch (ContextAuthenticationException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		
		if (!WebUtil.isAuthenticated(request, response, null)) {
			return null;
		}
		
		if (reportId != null) {
			XReport report = Context.getService(XReportsService.class).getReport(reportId);
			model.put("reportName", report.getName());
			ReportBuilder builder = new ReportBuilder();
			List<ReportParameter> parameters = new ArrayList<ReportParameter>();
			String xml = report.getXml();
			if (StringUtils.isNotBlank(xml)) {
				parameters = builder.buildParameters(xml);
			}
			
			if (parameters.size() == 0) {
				return "redirect:/module/xreports/reportRunner.form?reportId=" + report.getReportId() + (groupId != null ? "&groupId=" + groupId : "");
			}
			else {
				return "redirect:/module/xreports/reportParameter.form?reportId=" + report.getReportId();
			}
		} else {
			return "redirect:/module/xreports/runReports.list?groupId=" + groupId;
		}
	}
}
