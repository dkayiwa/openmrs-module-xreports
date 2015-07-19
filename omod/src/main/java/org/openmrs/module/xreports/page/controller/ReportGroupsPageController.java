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

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportGroupsPageController {

	public void controller(PageModel model, UiSessionContext emrContext) {

		emrContext.requireAuthentication();
		
		List<XReportGroup> groups = Context.getService(XReportsService.class).getReportGroups();
		model.addAttribute("groups", groups);
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
}
