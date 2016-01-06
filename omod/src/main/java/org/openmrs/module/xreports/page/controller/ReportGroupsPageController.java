/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.xreports.page.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.XReportGroupAndDepth;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportGroupsPageController {

	public void controller(PageModel model, UiSessionContext emrContext) {

		emrContext.requireAuthentication();
		
		List<XReportGroup> groups = Context.getService(XReportsService.class).getReportGroups();

		List<XReportGroupAndDepth> groupsAndDepths = new ArrayList<XReportGroupAndDepth>();
		List<XReportGroup> rootGroups = getRootGroups(groups);
		populateGroupsAndDepthList(groupsAndDepths, rootGroups, groups, 0);
    	model.addAttribute("groupsAndDepth", groupsAndDepths);
	}
	
	public String post(PageModel model,
			@RequestParam(value = "groupId") Integer groupId,
            @RequestParam(value = "action") String action,
            HttpSession session, UiUtils ui) {

		if (action.equals("removeGroup")) {
			XReportsService service = Context.getService(XReportsService.class);
			XReportGroup group = service.getReportGroup(groupId);
			Context.getService(XReportsService.class).deleteReportGroup(group);
		}
	
		return "redirect:/xreports/reportGroups.page";
	}
	
	private List<XReportGroup> getRootGroups(List<XReportGroup> groups) {
		List<XReportGroup> list = new ArrayList<XReportGroup>();
		for (XReportGroup group : groups) {
			if (group.getParentGroup() == null) {
				list.add(group);
			}
		}
		return list;
	}
	
	private List<XReportGroup> getChildGroups(XReportGroup parent, List<XReportGroup> groups) {
		List<XReportGroup> list = new ArrayList<XReportGroup>();
		for (XReportGroup group : groups) {
			if (group.getParentGroup() != null && group.getParentGroup().getId().intValue() == parent.getId().intValue()) {
				list.add(group);
			}
		}
		return list;
	}
	
	private void populateGroupsAndDepthList(List<XReportGroupAndDepth> groupAndDepths, 
	                                  		List<XReportGroup> groups, List<XReportGroup> allgroups, int depth) {
	                                  	
		for (XReportGroup group : groups) {
			groupAndDepths.add(new XReportGroupAndDepth(depth, group));
			List<XReportGroup> children = getChildGroups(group, allgroups);
			if (children.size() > 0) {
				populateGroupsAndDepthList(groupAndDepths, children, allgroups, depth + 1);
			}
		}
	}
}