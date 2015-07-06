package org.openmrs.module.xreports.page.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportGroupPageController {

	protected final Log log = LogFactory.getLog(getClass());
	
	public void get(PageModel model, @RequestParam(value = "groupId", required = false) XReportGroup group) {
		
		if (group == null) {
			group = new XReportGroup();
		}
		model.addAttribute("group", group);
	}
	
	public String post(PageModel model, @ModelAttribute(value = "groupId") @BindParams XReportGroup group,
            @RequestParam("action") String action,
            @SpringBean("xreportsService") XReportsService service, HttpSession session, UiUtils ui) {

	/*try {
		AppDescriptor descriptor = mapper.readValue(userApp.getJson(), AppDescriptor.class);
		if (!userApp.getAppId().equals(descriptor.getId())) {
			session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
			    ui.message("referenceapplication.app.errors.IdsShouldMatch"));
		} else if ("add".equals(action) && service.getUserApp(userApp.getAppId()) != null) {
			session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
			    ui.message("referenceapplication.app.errors.duplicateAppId"));
		} else {
			service.saveUserApp(userApp);
			
			InfoErrorMessageUtil.flashInfoMessage(session,
			    ui.message("referenceapplication.app.userApp.save.success", userApp.getAppId()));
			
			return "redirect:/referenceapplication/manageApps.page";
		}
	}
	catch (Exception e) {
		session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
		    ui.message("referenceapplication.app.userApp.save.fail", userApp.getAppId()));
	}*/
	
	model.addAttribute("group", group);
	
	return null;
}
}
