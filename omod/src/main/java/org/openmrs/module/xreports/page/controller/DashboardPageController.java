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

import java.util.Collections;
import java.util.List;

import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class DashboardPageController {

	public static final String XREPORTS_EXTENSION_POINT = "xreports.apps";

    public void controller(PageModel model, UiSessionContext emrContext,
                           @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService) {

        emrContext.requireAuthentication();

        List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser(XREPORTS_EXTENSION_POINT);

        Collections.sort(extensions);
        model.addAttribute("extensions", extensions);
    }
}
