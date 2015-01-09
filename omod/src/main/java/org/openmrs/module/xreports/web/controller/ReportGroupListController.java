package org.openmrs.module.xreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.xreports.api.XReportsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReportGroupListController {
		
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/xreports/group.list")
	public String displayGroups(ModelMap model) throws Exception {
		if (!Context.isAuthenticated()) {
			return null;
		}

		model.put("groups", Context.getService(XReportsService.class).getReportGroups());
		
		return "/module/xreports/groupList";
	}
}
