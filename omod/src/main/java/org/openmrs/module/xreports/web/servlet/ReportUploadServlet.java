package org.openmrs.module.xreports.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.util.WebUtil;

public class ReportUploadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//try to authenticate users who logon inline (with the request).
		WebUtil.authenticateInlineUser(request);
		
		//check if user is authenticated
		if (!WebUtil.isAuthenticated(request, response, "/moduleServlet/xreports/reportUploadServlet"))
			return;
		
		String xml = IOUtils.toString(request.getInputStream(), XReportsConstants.DEFAULT_CHARACTER_ENCODING);
		
		Integer reportId = Integer.parseInt(request.getParameter("formId"));
		
		XReportsService service = Context.getService(XReportsService.class);
		XReport report = service.getReport(reportId);
		report.setXml(xml);
		service.saveReport(report);
	}
}
