package org.openmrs.module.xreports.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.xreports.ReportBuilder;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.PdfDocument;

public class ExportPdfServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			String formId = request.getParameter("formId");
	
			String xml = Context.getService(XReportsService.class).getReport(Integer.parseInt(formId)).getXml();
			if (xml == null) {
				return;
			}
			
			String filename = "document.pdf";
			if (request.getParameter("docName") != null) {
				filename = request.getParameter("docName") + ".pdf";
				filename = filename.replace(" ", "-");
			}
						
			response.setHeader(XReportsConstants.HTTP_HEADER_CONTENT_DISPOSITION, "inline");
			response.setContentType(XReportsConstants.CONTENT_TYPE_PDF);
			
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", -1);
			response.setHeader("Cache-Control", "no-store");
			response.setCharacterEncoding(XReportsConstants.DEFAULT_CHARACTER_ENCODING);
			
			new PdfDocument().writeFromXml(response.getOutputStream(), new ReportBuilder().build(xml, request.getQueryString()), request.getRealPath(""));
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			reportError(ex, request, response, response.getWriter());
			ex.printStackTrace();
		}
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
}
