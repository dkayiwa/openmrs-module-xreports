package org.openmrs.module.xreports.web.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;

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
	
	/**
	 * Converts a string into a valid XML token (tag name)
	 * 
	 * @param s string to convert into XML token
	 * @return valid XML token based on s
	 */
	public static String getXmlToken(String s) {
		// Converts a string into a valid XML token (tag name)
		// No spaces, start with a letter or underscore, not 'xml*'
		
		// if len(s) < 1, return '_blank'
		if (s == null || s.length() < 1)
			return "_blank";
		
		// xml tokens must start with a letter
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
		
		// after the leading letter, xml tokens may have
		// digits, period, or hyphen
		String nameChars = letters + "0123456789.-";
		
		// special characters that should be replaced with valid text
		// all other invalid characters will be removed
		Hashtable<String, String> swapChars = new Hashtable<String, String>();
		swapChars.put("!", "bang");
		swapChars.put("#", "pound");
		swapChars.put("\\*", "star");
		swapChars.put("'", "apos");
		swapChars.put("\"", "quote");
		swapChars.put("%", "percent");
		swapChars.put("<", "lt");
		swapChars.put(">", "gt");
		swapChars.put("=", "eq");
		swapChars.put("/", "slash");
		swapChars.put("\\\\", "backslash");
		
		// start by cleaning whitespace and converting to lowercase
		s = s.replaceAll("^\\s+", "").replaceAll("\\s+$", "").replaceAll("\\s+", "_").toLowerCase();
		
		// swap characters
		Set<Entry<String, String>> swaps = swapChars.entrySet();
		for (Entry<String, String> entry : swaps) {
			if (entry.getValue() != null)
				s = s.replaceAll(entry.getKey(), "_" + entry.getValue() + "_");
			else
				s = s.replaceAll(String.valueOf(entry.getKey()), "");
		}
		
		// ensure that invalid characters and consecutive underscores are
		// removed
		String token = "";
		boolean underscoreFlag = false;
		for (int i = 0; i < s.length(); i++) {
			if (nameChars.indexOf(s.charAt(i)) != -1) {
				if (s.charAt(i) != '_' || !underscoreFlag) {
					token += s.charAt(i);
					underscoreFlag = (s.charAt(i) == '_');
				}
			}
		}
		
		// remove extraneous underscores before returning token
		token = token.replaceAll("_+", "_");
		token = token.replaceAll("_+$", "");
		
		// make sure token starts with valid letter
		if (letters.indexOf(token.charAt(0)) == -1 || token.startsWith("xml"))
			token = "_" + token;
		
		// return token
		return token;
	}
}
