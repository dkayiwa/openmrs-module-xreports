package org.openmrs.module.xreports.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


public class ImageUploadServlet extends HttpServlet {
	
	public static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			CommonsMultipartResolver multipartResover = new CommonsMultipartResolver();
			if (multipartResover.isMultipart(request)) {
				MultipartHttpServletRequest multipartRequest = multipartResover.resolveMultipart(request);
				MultipartFile uploadedFile = multipartRequest.getFile("filecontents");
				if (uploadedFile != null && !uploadedFile.isEmpty()) {
					String filename = WebUtil.stripFilename(uploadedFile.getOriginalFilename());
					String pathName = request.getSession().getServletContext().getRealPath("");
					if (!pathName.endsWith(File.separator)) {
						pathName += File.separator;
					}
					pathName += "images" + File.separator + filename;
					FileOutputStream outputStream = new FileOutputStream(pathName);
					OpenmrsUtil.copyFile(uploadedFile.getInputStream(), outputStream);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}