package org.openmrs.module.xreports.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.module.xreports.XReportsConstants;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Handles saving of files (xforms) as needed by the form designer
 */
public class FileSaveServlet extends HttpServlet {
	
	public static final long serialVersionUID = 1L;
	
	private final String KEY_FILE_CONTENTS = "FileContents";
	
	private final String KEY_FILE_NAME = "FileNname";
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String filecontents = null;
			CommonsMultipartResolver multipartResover = new CommonsMultipartResolver(/*this.getServletContext()*/);
			if (multipartResover.isMultipart(request)) {
				MultipartHttpServletRequest multipartRequest = multipartResover.resolveMultipart(request);
				filecontents = multipartRequest.getParameter("filecontents");
				if (filecontents == null || filecontents.trim().length() == 0)
					return;
			}
			
			String filename = "filename.xml";
			if (request.getParameter("filename") != null) {
				filename = request.getParameter("filename") + ".xml";
				filename = filename.replace(" ", "-");
			}
			
			HttpSession session = request.getSession();
			session.setAttribute(KEY_FILE_NAME, filename);
			session.setAttribute(KEY_FILE_CONTENTS, filecontents);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		response.setHeader(XReportsConstants.HTTP_HEADER_CONTENT_DISPOSITION,
		    XReportsConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + session.getAttribute(KEY_FILE_NAME));
		response.setContentType(XReportsConstants.HTTP_HEADER_CONTENT_TYPE_XML);
		
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-store");
		
		response.setCharacterEncoding(XReportsConstants.DEFAULT_CHARACTER_ENCODING);
		response.getWriter().print((String) session.getAttribute(KEY_FILE_CONTENTS));
	}
}
