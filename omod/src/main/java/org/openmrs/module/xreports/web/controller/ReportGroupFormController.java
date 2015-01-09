package org.openmrs.module.xreports.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
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
public class ReportGroupFormController {
	
	private final Log log = LogFactory.getLog(getClass());
		
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(XReportGroup.class, new ReportGroupEditor());
	}
	
	@ModelAttribute("group")
	public XReportGroup formBackingObject(WebRequest request,
	                                     @RequestParam(required = false, value = "groupId") Integer groupId) {
		if (!Context.isAuthenticated()) {
			return null;
		}
		
		XReportGroup group = null;
		if (groupId != null) {
			group = Context.getService(XReportsService.class).getReportGroup(groupId);
		} else {
			group = new XReportGroup();
		}
		
		return group;
	}
	
	@RequestMapping(value = "/module/xreports/group.form", method = RequestMethod.GET)
	public String showForm(@RequestParam(required = false, value = "groupId") Integer groupId,
	                       @ModelAttribute("group") XReportGroup group, ModelMap model) {
		if (!Context.isAuthenticated()) {
			return null;
		}
		
		List<XReportGroup> groups = null;
		if (groupId != null) {
			//groups = service.getByFieldsNotEqual(new String[] { "groupId" }, new Integer[] { groupId }, null,
			//    new String[] { "name" }, null, null);
		} else {
			groups = Context.getService(XReportsService.class).getReportGroups();
		}
		model.put("groups", groups);
		
		// not using the default view name because I'm converting from an existing form
		return "module/xreports/groupForm";
	}
	
	@RequestMapping(value = "/module/xreports/group.form", method = RequestMethod.POST)
	public String handleSubmission(WebRequest request, HttpSession httpSession, ModelMap model,
	                               @RequestParam(required = false, value = "action") String action,
	                               @ModelAttribute("group") XReportGroup group, BindingResult errors) {
		if (!Context.isAuthenticated()) {
			return null;
		}
		
		MessageSourceService mss = Context.getMessageSourceService();
		
		if (!Context.isAuthenticated()) {
			errors.reject("auth.invalid");
		} else if (mss.getMessage("general.delete").equals(action)) {
			try {
				Context.getService(XReportsService.class).deleteReportGroup(group);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "xreports.reportGroup.delete.success");
				return "redirect:group.list";
			}
			catch (Exception ex) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "xreports.reportGroup.delete.failure");
				log.error("Failed to delete report group", ex);
				return "redirect:/module/xreports/group.form?groupId=" + request.getParameter("groupId");
			}
		} else {
			Context.getService(XReportsService.class).saveReportGroup(group);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "xreports.reportGroup.saved");
		}
		
		return "redirect:group.list";
	}
}
