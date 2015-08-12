package org.openmrs.module.xreports.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.xreports.ReportParameter;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.ReportBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportParameterController {
	
	@RequestMapping(value = "/module/xreports/reportParameter.form", method = RequestMethod.GET)
	public void showReport(ModelMap model, @RequestParam(required = true, value = "reportId") Integer reportId) throws Exception {
		XReport report = Context.getService(XReportsService.class).getReport(reportId);
		ReportBuilder builder = new ReportBuilder();
		List<ReportParameter> parameters = builder.buildParameters(report.getXml());
		model.put("parameters", parameters);
		model.put("reportName", report.getName());
		model.put("reportId", reportId);
		model.put("parameterNames", getParameterNames(parameters));
	}
	
	@RequestMapping(value = "/module/xreports/reportParameter.form", method = RequestMethod.POST)
	public String onPost(ModelMap model, @RequestParam(required = true, value = "reportId") Integer reportId,
	                     @RequestParam(required = true, value = "parameterNames") String parameterNames,
	                     HttpServletRequest request) throws Exception {
		String url = "redirect:/module/xreports/reportRunner.form?reportId=" + reportId;
		String[] names = parameterNames.split(",");
		for (String name : names) {
			String value = request.getParameter(name);
			if (StringUtils.isNotBlank(value)) {
				url += "&" + name + "=" + value;
			}
		}
		return url;
	}
	
	private String getParameterNames(List<ReportParameter> parameters) {
		String names = null;
		for (ReportParameter parameter : parameters) {
			if (names == null) {
				names = parameter.getBinding();
			}
			else {
				names += "," + parameter.getBinding();
			}
		}
		return names;
	}
}
