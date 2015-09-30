package org.openmrs.module.xreports;

public class XReportsConstants {
	
	/** The default character encoding used when writting and reading bytes to and from streams. */
	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	
	/** The content disposition http header. */
	public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	
	public static final String CONTENT_TYPE_PDF = "application/pdf ";
	
	public static final String HEADER_PURCFORMS_ERROR_MESSAGE = "PURCFORMS-ERROR-MESSAGE";
	
	public static final String REQUEST_ATTRIBUTE_ID_ERROR_MESSAGE = "ERROR_MESSAGE";
	
	/** The content type http header. */
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	
	/** The text/xml http content type. */
	public static final String HTTP_HEADER_CONTENT_TYPE_XML = "text/xml; charset=utf-8";
	
	public static final String HTTP_HEADER_CONTENT_DISPOSITION_VALUE = "attachment; filename=";
	
	// Constants used within sessions to key report parameter data that can be retrieved
	public static final String REPORT_PARAMETER_DATA = "__xreports_report_parameter_data";
}
