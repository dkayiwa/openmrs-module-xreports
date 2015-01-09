package org.openmrs.module.xreports.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.ReportGroupEditor;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ReportFormController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(XReportGroup.class, new ReportGroupEditor());
	}
	
	@ModelAttribute("report")
	public XReport formBackingObject(WebRequest request, @RequestParam(required = false, value = "reportId") Integer reportId) {
		if (!Context.isAuthenticated()) {
			return null;
		}
		
		XReport report = null;
		if (reportId != null) {
			report = Context.getService(XReportsService.class).getReport(reportId);
		} else {
			report = new XReport();
		}
		
		return report;
	}
	
	@RequestMapping(value = "/module/xreports/report.form", method = RequestMethod.GET)
	public String showForm(@RequestParam(required = false, value = "reportId") Integer reportId,
	                       @ModelAttribute("report") XReport report, ModelMap model) {
		if (!Context.isAuthenticated()) {
			return null;
		}
		
		model.put("groups", Context.getService(XReportsService.class).getReportGroups());
    	model.put("reportDefinitions", Context.getService(ReportDefinitionService.class).getAllDefinitions(false));

		// not using the default view name because I'm converting from an existing form
		return "module/xreports/reportForm";
	}
	
	@RequestMapping(value = "/module/xreports/report.form", method = RequestMethod.POST)
	public String handleSubmission(WebRequest request, HttpSession httpSession, ModelMap model,
	                               @RequestParam(required = false, value = "action") String action,
	                               @ModelAttribute("report") XReport report, BindingResult errors) {
		if (!Context.isAuthenticated()) {
			return null;
		}
		
		MessageSourceService mss = Context.getMessageSourceService();
		
		if (!Context.isAuthenticated()) {
			errors.reject("auth.invalid");
		} else if (mss.getMessage("general.delete").equals(action)) {
			try {
				if (report.getXml() != null) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "xreports.report.delete.xml.failure");
					return "redirect:/module/xreports/report.form?reportId=" + request.getParameter("reportId");
				}
				Context.getService(XReportsService.class).deleteReport(report);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "xreports.report.delete.success");
				return "redirect:report.list";
			}
			catch (Exception ex) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "xreports.report.delete.failure");
				log.error("Failed to delete report", ex);
				return "redirect:/module/xreports/report.form?reportId=" + request.getParameter("reportId");
			}
		} else {
			Context.getService(XReportsService.class).saveReport(report);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "xreports.report.saved");
		}
		
		return "redirect:report.list";
	}
}
