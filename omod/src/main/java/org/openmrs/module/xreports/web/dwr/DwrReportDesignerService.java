package org.openmrs.module.xreports.web.dwr;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;

public class DwrReportDesignerService {
	
	public boolean isAuthenticated() {
		return Context.isAuthenticated();
	}
	
	public boolean authenticate(String user, String pass) {
		try {
			Context.authenticate(user, pass);
			return true;
		}
		catch (ContextAuthenticationException ex) {
			return false;
		}
	}
}
