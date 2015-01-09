package org.openmrs.module.xreports.web.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.web.WebConstants;

public class WebUtil {
	
	private static Log log = LogFactory.getLog(WebUtil.class);
	
	/**
	 * Authenticates users who logon inline (with the request by appending user name and password to
	 * the url).
	 * 
	 * @param request
	 * @throws ContextAuthenticationException
	 */
	public static void authenticateInlineUser(HttpServletRequest request) throws ContextAuthenticationException {
		if (!Context.isAuthenticated()) {
			String name = request.getParameter("uname");
			String pw = request.getParameter("pw");
			if (name != null & pw != null)
				Context.authenticate(name, pw);
		}
	}
	
	/**
	 * Checks if a user is authenticated. If not, takes them to the login page.
	 * 
	 * @param request the http request.
	 * @param response the http response.
	 * @param loginRedirect the part of the url appended to the Context Path, that the user is
	 *            redireted to on successfully logging in.
	 * @return true if user is authenticated, else false.
	 * @throws Exception
	 */
	public static boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response, String loginRedirect) {
		try {
			if (!Context.isAuthenticated()) {
				if (loginRedirect != null)
					request.getSession().setAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR,
					    request.getContextPath() + loginRedirect);
				
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.session.expired");
				response.sendRedirect(request.getContextPath() + "/logout");
				return false;
			}
		}
		catch (Exception e) {
			log.equals(e);
			return false;
		}
		return true;
	}
	
	/**
	 * Convenience method that recursively attempts to pull the root case from a Throwable
	 * 
	 * @param t the Throwable object
	 * @param isOriginalError specifies if the passed in Throwable is the original Exception that
	 *            was thrown
	 * @return the root cause if any was found
	 */
	public static Throwable getActualRootCause(Throwable t, boolean isOriginalError) {
		if (t.getCause() != null)
			return getActualRootCause(t.getCause(), false);
		
		if (!isOriginalError)
			return t;
		
		return t;
	}
	
	public static void reportError(Throwable ex, HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
	    throws IOException {
		
		ex = getActualRootCause(ex, true);
		
		String message = ex.getMessage(); //"Could not process request. Click the more button for details.";
		Object msg = request.getAttribute(XReportsConstants.REQUEST_ATTRIBUTE_ID_ERROR_MESSAGE);
		if (msg != null)
			message = msg.toString();
		
		response.setContentType("text/plain" /*XformConstants.HTTP_HEADER_CONTENT_TYPE_XML*/);
		response.setHeader(XReportsConstants.HEADER_PURCFORMS_ERROR_MESSAGE, message);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ex.printStackTrace(writer);
	}
}
