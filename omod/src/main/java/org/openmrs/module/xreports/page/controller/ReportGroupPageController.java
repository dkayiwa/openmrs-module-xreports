package org.openmrs.module.xreports.page.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportGroupPageController {

	protected final Log log = LogFactory.getLog(getClass());
	
	public void get(PageModel model, @RequestParam(value = "groupId", required = false) Integer groupId) {
		
		XReportGroup group = new XReportGroup();
		if (groupId != null) {
			group = Context.getService(XReportsService.class).getReportGroup(groupId);
		}
		
		List<XReportGroup> groups = Context.getService(XReportsService.class).getReportGroups();
		if (groupId != null) {
			groups.remove(group);
		}
		
		model.addAttribute("group", group);
		model.put("groups", groups);
	}
	
	public String post(UiSessionContext sessionContext, PageModel model,
			@RequestParam(value = "groupId", required = false) Integer groupId,
            @RequestParam(value = "groupName", required = false) String name,
            @RequestParam(value = "identifier", required = false) String identifier,
            @RequestParam(value = "parentGroup", required = false) Integer parentGroupId,
            HttpSession session, UiUtils ui) {

		sessionContext.requireAuthentication();
		
		if (StringUtils.isBlank(name)) {
			session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
				    ui.message("xreports.name.required", "xreports"));
			return null;
		}
		
		XReportsService service = Context.getService(XReportsService.class);
		
		XReportGroup group = new XReportGroup();
		if (groupId != null) {
			group = service.getReportGroup(groupId);
		}
		
		group.setName(name);
		group.setIdentifier(identifier);
		if (parentGroupId != null) {
			group.setParentGroup(service.getReportGroup(parentGroupId));
		}
		else {
			group.setParentGroup(null);
		}
		
		service.saveReportGroup(group);
	
		return "redirect:/xreports/reportGroups.page";
	}
}
