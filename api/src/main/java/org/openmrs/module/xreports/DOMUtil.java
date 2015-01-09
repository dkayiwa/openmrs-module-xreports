package org.openmrs.module.xreports;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil {
	
	/**
	 * Gets the value of an element with a given name in a document.
	 * 
	 * @param doc - the document.
	 * @param name - the name of the element.
	 * @return - the value.
	 */
	public static String getElementValue(Element root, String name) {
		NodeList elemList = root.getElementsByTagName(name);
		if (!(elemList != null && elemList.getLength() > 0))
			return null;
		return elemList.item(0).getTextContent();
	}
	
	public static Element getElement(Document doc, String name) {
		return getElement(doc.getDocumentElement(), name);
	}
	
	public static Element getElement(Element root, String name) {
		NodeList elemList = root.getElementsByTagName(name);
		if (!(elemList != null && elemList.getLength() > 0))
			return null;
		return (Element) elemList.item(0);
	}
	
	public static String getElementValue(Document doc, String name) {
		return getElementValue(doc.getDocumentElement(), name);
	}
	
	public static boolean setElementValue(Element root, String name, String value) {
		NodeList elemList = root.getElementsByTagName(name);
		if (!(elemList != null && elemList.getLength() > 0))
			return false;
		elemList.item(0).setTextContent(value);
		return true;
	}
	
	public static Document fromString2Doc(String xml) throws Exception {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder()
		        .parse(IOUtils.toInputStream(xml, XReportsConstants.DEFAULT_CHARACTER_ENCODING));
	}
	
	/**
	 * Converts a document to its text representation.
	 * 
	 * @param doc - the document.
	 * @return - the text representation of the document.
	 */
	public static String doc2String(Node doc) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			
			//This adds unnecessary indenting which makes the xform too big for mobile devices.
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter outStream = new StringWriter();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outStream);
			transformer.transform(source, result);
			return outStream.getBuffer().toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
