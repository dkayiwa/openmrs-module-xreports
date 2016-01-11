package org.openmrs.module.xreports.web;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.xreports.DOMUtil;
import org.openmrs.module.xreports.DesignItem;
import org.w3c.dom.NodeList;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Represents a pdf document
 */
public class PdfDocument {
	
	private float imageDepth = 0;
	private float DEPTH = -13;
	
	public PdfDocument() {
		String depth = org.openmrs.api.context.Context.getAdministrationService().getGlobalProperty("xreports.image.depth", "0");
		if (StringUtils.isNotBlank(depth)) {
			imageDepth = Float.parseFloat(depth);
		}
	}
	
	private Font getFont(String fontFamily, String fontStyle, String fontWeight, String textDecoration, String color) throws Exception {

		Font ft = FontFactory.getFont("Arial");
		
		if (StringUtils.isNotBlank(fontStyle)) {
			ft.setStyle(fontStyle);
		}
		
		if (StringUtils.isNotBlank(fontWeight)) {
			ft.setStyle(fontWeight);
		}
		
		if (StringUtils.isNotBlank(textDecoration)) {
			ft.setStyle(textDecoration);
		}
		
		if (StringUtils.isNotBlank(color)) {
			ft.setColor(Markup.decodeColor(color));
		}
		
		return ft;
	}
	
	private BaseFont getBaseFont(String fontFamily, String fontStyle, String fontWeight, String textDecoration, String color) throws Exception {
		try {
			Font ft = getFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
			BaseFont bf = ft.getCalculatedBaseFont(false);
			if (bf != null) {
				return bf;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
	}
	
	private Document createNewDocument(org.w3c.dom.Document doc) {
		String width = doc.getDocumentElement().getAttribute(LayoutConstants.PROPERTY_WIDTH);
		String height = doc.getDocumentElement().getAttribute(LayoutConstants.PROPERTY_HEIGHT);
		
		float w = Float.parseFloat(width.substring(0, width.length() - 2));
		float h = Float.parseFloat(height.substring(0, height.length() - 2));
		
		String s1 = doc.getDocumentElement().getAttribute("PageHeight");
		if (s1 != null && s1.trim().length() > 0) {
			h = Float.parseFloat(s1);;
		}
		
		float margin = 50;
		s1 = doc.getDocumentElement().getAttribute("PageMargin");
		if (s1 != null && s1.trim().length() > 0) {
			margin = Float.parseFloat(s1);
		}
		
		float denominator = 98.5f;
		float pageWidth = (w * 72) / denominator;
		float pageHeight = (h * 72) / denominator;
		
		DEPTH = -13;
		if (pageWidth > pageHeight) {
			DEPTH = -13;
		}
		
		return new Document(new Rectangle(pageWidth, pageHeight), margin, margin, margin, margin);
	}
	
	/**
	 * Converts a purcreports/purcforms layout document to pdf and writes it to a stream.
	 * 
	 * @param os the output stream
	 * @param layoutXml the layout xml
	 */
	public void writeFromXml(OutputStream os, String layoutXml, String realPath) {
		try {	
			org.w3c.dom.Document doc = DOMUtil.fromString2Doc(layoutXml);
			
			Document document = createNewDocument(doc);
			PdfWriter writer = PdfWriter.getInstance(document, os);
			document.open();
			generate(doc, document, writer, realPath);

			document.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void drawRectangle(PdfContentByte content, float x, float y, float width, float height, String bgolor, String bdcolor) {
	    content.saveState();
	    PdfGState state = new PdfGState();
	    //state.setFillOpacity(0.3f);
	    content.setGState(state);
	    Color clr = Markup.decodeColor(bgolor);
	    if (clr != null) {
	    	content.setColorFill(clr);
	    	content.setColorStroke(clr);
	    }
	    clr = Markup.decodeColor(bdcolor);
	    if (clr != null) {
	    	content.setColorStroke(clr);
	    }
	    content.rectangle(x, y, width, height);
	    content.fillStroke();
	    content.restoreState();
	}

	private void generate(org.w3c.dom.Document doc, Document document, PdfWriter writer, String realPath) throws Exception, BadElementException,
        MalformedURLException, IOException, DocumentException {
	    float bottom = document.getPageSize().getTop();
	    
	    NodeList nodes = doc.getDocumentElement().getElementsByTagName(DesignItem.NAME_ITEM);
	    if (nodes == null)
	    	return;
	    
	    PdfContentByte cb = writer.getDirectContent();

	    float parentLeft = 0;
	    float parentTop = 0;
	    
	    float diff = 15;
	    float denominator = 98.5f;
	    
	    //begin new
	    float pageHeight = 1114;
		float pageMargin = 50;
		int noPages = 1;
		
	    String s1 = doc.getDocumentElement().getAttribute("PageHeight");
		if (s1 != null && s1.trim().length() > 0) {
			pageHeight = Float.parseFloat(s1);
		}
		s1 = doc.getDocumentElement().getAttribute("PageMargin");
		if (s1 != null && s1.trim().length() > 0) {
			pageMargin = Float.parseFloat(s1);
		}

		float pageBottom = pageHeight - pageMargin;
		float prevPageBottom = 0;
		//end new
		
		String parentWidth = null;
	    
		List<ReportItem> items = getSortedReportItems(nodes);
	    //for (int index = 0; index < nodes.getLength(); index++) {
		for (ReportItem reportItem : items) {
	    	//org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(index);
			org.w3c.dom.Element element = reportItem.getNode();
	    	org.w3c.dom.Element parentElement = (org.w3c.dom.Element)element.getParentNode();

	    	if (LayoutConstants.TYPE_LABEL.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    		String text = element.getAttribute(LayoutConstants.PROPERTY_TEXT);
	    		String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
	    		if (StringUtils.isBlank(text)) {
	    			String bgColor = element.getAttribute(LayoutConstants.PROPERTY_BACKGROUND_COLOR);
	    			if (!"100%".equals(width) || StringUtils.isBlank(bgColor) || "white".equalsIgnoreCase(bgColor)) {
	    				continue;
	    			}
	    		}

	    		String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    		String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
	    		String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    		String fontSize = element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE);
	    		String fontFamily = element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY);
	    		String fontStyle = element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE);
	    		String fontWeight = element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT);
	    		String textDecoration = element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION);
	    		String color = element.getAttribute(LayoutConstants.PROPERTY_COLOR);
	    		String rotated = element.getAttribute(LayoutConstants.PROPERTY_ROTATED);
	    		String textAlign = element.getAttribute("textAlign");
	    		
	    		BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
	    		
	    		float x = Float.parseFloat(left.substring(0, left.length() - 2));
	    		float y = Float.parseFloat(top.substring(0, top.length() - 2));
	    		
	    		//begin new
	    		if (y > pageBottom) {
					noPages++;
					prevPageBottom = pageBottom + pageMargin;
					pageBottom = (noPages * pageHeight) - pageMargin;
					
					document.newPage();
					bottom = document.getPageSize().getTop();
				}
	    		
	    		y = y - prevPageBottom;
	    		//end new
	    		
	    		float size = Float.parseFloat(fontSize.substring(0, fontSize.length() - 2));
	    		size = (size * 72) / denominator;
	    		
	    		if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
	    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    			String s = top;
	    			left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    			top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
	    			
	    			parentLeft = Float.parseFloat(left.substring(0, left.length() - 2));
	    			parentTop = Float.parseFloat(top.substring(0, top.length() - 2));
	    			
	    			parentWidth = parentElement.getAttribute(LayoutConstants.PROPERTY_WIDTH);
	    			
	    			//debug block for text beyond table - hence invisible
	    			String parentHeight = parentElement.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    			int t1 = (int)Float.parseFloat(parentHeight.substring(0, parentHeight.length() - 2));
	    			int t2 = (int)Float.parseFloat(s.substring(0, s.length() - 2));
	    			if (t2 >= t1) {
	    				continue;
	    			}
	    		}
	    		else {
	    			 parentLeft = 0;
	    		     parentTop = 0;
	    		}
	            
	    		cb.saveState();
	    	    cb.beginText();
	    	    
	    	    float xpos = ((x + parentLeft) * 72) / denominator;
	    	    float ypos = bottom - (((y + parentTop) * 72) / denominator);
	    	    ypos += DEPTH;
	    	    
	    	    if(StringUtils.isNotBlank(color)) {
	    	    	Color clr = Markup.decodeColor(color);
	    	    	cb.setColorFill(clr);
	    	    	cb.setColorStroke(clr);
	    	    }
	    	    
	    	    cb.setFontAndSize(font, size);
	    	    
	    	    //this first if i temporarily disabled for now because its buggy
	    	    if (false && StringUtils.isNotBlank(width) && StringUtils.isNotBlank(height) && !width.equals("100%")) {
	    	    	float w = Float.parseFloat(width.substring(0, width.length() - 2));
	    	    	w = (w * 72) / denominator;
	    	    	ColumnText ct = new ColumnText(cb);
	    	    	ypos += 15;
	                ct.setSimpleColumn(xpos, ypos, xpos + w, ypos - 500, 15, Element.ALIGN_LEFT);
	                ct.addText(new Phrase(text, getFont(fontFamily, fontStyle, fontWeight, textDecoration, color)));
	                ct.go();
	    	    }
	    	    else if ("true".equals(rotated)) {
	    	    	cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text, xpos, ypos, 90);
	    	    	xpos += 3;
	    	    }
	    	    else {
	    	    	String bgcolor = element.getAttribute(LayoutConstants.PROPERTY_BACKGROUND_COLOR);
	    	    	String bdcolor = element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	    	    	if ("center".equals(textAlign) || "right".equals(textAlign)) {
	    	    		int align = PdfContentByte.ALIGN_CENTER;
	    	    		float w = size/2;
	    	    		if ("right".equals(textAlign)) {
	    	    			align = PdfContentByte.ALIGN_RIGHT;
	    	    			w = size;
	    	    		}
	    	    		
	    	    		if (width.equals("100%")) {
	    	    			w = (Float.parseFloat(parentWidth.substring(0, parentWidth.length() - 2)) * 72) / denominator;
	    	    			float h = Float.parseFloat(height.substring(0, height.length() - 2));
	    	    			if (StringUtils.isBlank(bdcolor)) {
	    	    				bdcolor = parentElement.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	    	    			}
	    	    			//drawRectangle(cb, xpos, ypos - 6, w, ((h * 72) / denominator), bgcolor, bdcolor);

	    		    	    ypos = (bottom * noPages) - (((h + parentTop - diff) * 72) / denominator);
	    		    	    ypos += DEPTH;
	    		    	    
	    	    			drawRectangle(cb, xpos, ypos, w, ((h * 72) / denominator), bgcolor, bdcolor);
	    	    			cb.showTextAligned(align, text, (xpos + w/2), ypos - 1, 0);
	    	    		}
	    	    		else {
    	    				w = (Float.parseFloat(width.substring(0, width.length() - 2)) * 72) / denominator;

	    	    			if (StringUtils.isNotBlank(bgcolor) && StringUtils.isNotBlank(width)) {
	    	    				if (StringUtils.isBlank(height)) {
	    	    					height = "25px";
	    	    				}
		    	    			float h = Float.parseFloat(height.substring(0, height.length() - 2));
		    	    			//drawRectangle(cb, xpos, ypos - 6, w, ((h * 72) / denominator), bgcolor, bdcolor);
		    	    			
		    	    			//ypos = (bottom * noPages) - (((h + parentTop - diff) * 72) / denominator);
		    		    	    //ypos += DEPTH;
		    		    	    
		    		    	    drawRectangle(cb, xpos, ypos, w, ((h * 72) / denominator), bgcolor, bdcolor);
		    		    	    
		    	    			if (align == PdfContentByte.ALIGN_CENTER) {
		    	    				cb.showTextAligned(align, text, (xpos + w/2), ypos, 0);
		    	    			}
		    	    			else {
		    	    				cb.showTextAligned(align, text, xpos + w, ypos, 0);
		    	    			}
	    	    			}
	    	    			else {
	    	    				cb.showTextAligned(align, text, (xpos + w/2), ypos, 0);
	    	    			}
	    	    		}
	    	    	}
	    	    	else {
	    	    		if (StringUtils.isNotBlank(bgcolor) && StringUtils.isNotBlank(width)) {
    	    				float w = (Float.parseFloat(width.substring(0, width.length() - 2)) * 72) / denominator;
    	    				if (StringUtils.isBlank(height)) {
    	    					height = "25px";
    	    				}
	    	    			float h = Float.parseFloat(height.substring(0, height.length() - 2));
	    	    			//drawRectangle(cb, xpos, ypos - 6, w, ((h * 72) / denominator), bgcolor, bdcolor);
	    	    			
	    	    			ypos = (bottom * noPages) - (((h + parentTop - diff) * 72) / denominator);
	    		    	    ypos += DEPTH;
	    		    	    
	    		    	    drawRectangle(cb, xpos, ypos, w, ((h * 72) / denominator), bgcolor, bdcolor);
    	    			}
	    	    		
			    	    cb.setTextMatrix(xpos, ypos);
			    	    cb.showText(text);
	    	    	}
	    	    	ypos -= 3;
	    	    }
	    	    
	    	    cb.endText();
	    	    
	    	    if (StringUtils.isNotBlank(textDecoration) && textDecoration.contains("underline")) {
		            cb.setLineWidth(1f);
		    	    float length = font.getWidthPoint(text, size);
		    	    
		    	    if ("right".equals(textAlign)) {
		    	    	float w = (Float.parseFloat(width.substring(0, width.length() - 2)) * 72) / denominator;
		    	    	cb.moveTo(xpos + (w - length), ypos);
			    	    if ("true".equals(rotated)) {
			    	    	cb.lineTo(xpos, ypos + length);
			    	    } else {
			    	    	cb.lineTo(xpos + w, ypos);
			    	    }
    	    		}
		    	    else {
		    	    	cb.moveTo(xpos, ypos);
			    	    if ("true".equals(rotated)) {
			    	    	cb.lineTo(xpos, ypos + length);
			    	    } else {
			    	    	cb.lineTo(xpos + length, ypos);
			    	    }
		    	    }
		    	    
		            cb.stroke();
	    	    }
	    	    
	    	    cb.restoreState();
	    	} 
	    	else if (LayoutConstants.TYPE_HORIZONTAL_LINE.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    		String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    		String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
	    		String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
	    		String fontSize = element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE);
	    		String fontFamily = element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY);
	    		String fontStyle = element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE);
	    		String fontWeight = element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT);
	    		String textDecoration = element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION);
	    		String color = element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	    		String borderWidth = element.getAttribute(LayoutConstants.PROPERTY_BORDER_WIDTH);
	    		String borderStyle = element.getAttribute(LayoutConstants.PROPERTY_BORDER_STYLE);
	    		
	    		BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
	    		
	    		float x = Float.parseFloat(left.substring(0, left.length() - 2));
	    		float y = Float.parseFloat(top.substring(0, top.length() - 2));
	    		float w = Float.parseFloat(width.substring(0, width.length() - 2));
	    		float bw = 0f;
	    		if (StringUtils.isNotBlank(borderWidth)) {
	    			bw = Float.parseFloat(borderWidth.substring(0, borderWidth.length() - 2)) / 2;
	    		}
	    		
	    		//begin new
	    		if (y > pageBottom) {
					noPages++;
					prevPageBottom = pageBottom + pageMargin;
					pageBottom = (noPages * pageHeight) - pageMargin;
					
					document.newPage();
					bottom = document.getPageSize().getTop();
				}
	    		
	    		y = y - prevPageBottom;
	    		//end new
	    		
	    		float size = Float.parseFloat(fontSize.substring(0, fontSize.length() - 2));
	    		size = (size * 72) / denominator;
	    		
	    		if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
	    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    			String s = top;
	    			left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    			top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
	    			
	    			parentLeft = Float.parseFloat(left.substring(0, left.length() - 2));
	    			parentTop = Float.parseFloat(top.substring(0, top.length() - 2));
	    			
	    			//debug block for lines beyond table - hence invisible
	    			String parentHeight = parentElement.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    			int t1 = (int)Float.parseFloat(parentHeight.substring(0, parentHeight.length() - 2));
	    			int t2 = (int)Float.parseFloat(s.substring(0, s.length() - 2));
	    			if (t2 >= t1) {
	    				continue;
	    			}
	    		}
	    		else {
	    			 parentLeft = 0;
	    		     parentTop = 0;
	    		}
	    		
	    		float xpos = ((x + parentLeft) * 72) / denominator;
	    	    float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
	    	    ypos += DEPTH;
	    	    float length = (w * 72) / denominator;
	    	    
	    	    cb.saveState();
	    		cb.setLineWidth(bw);
	    	    
	    	    if(StringUtils.isNotBlank(color)) {
	    	    	cb.setColorStroke(Markup.decodeColor(color));
	    	    }
	    	    
	    	    cb.setFontAndSize(font, size);
	    	    
	    	    if (borderStyle != null && !borderStyle.contains("solid")) {
	    	    	cb.setLineDash(bw, bw);
	    	    }
	    	    
	            cb.moveTo(xpos, ypos);
	            cb.lineTo(xpos + length, ypos);
	            cb.stroke();
	            cb.restoreState();
	    	} 
	    	else if (LayoutConstants.TYPE_VERTICAL_LINE.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    		String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    		String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
	    		String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    		String fontSize = element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE);
	    		String fontFamily = element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY);
	    		String fontStyle = element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE);
	    		String fontWeight = element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT);
	    		String textDecoration = element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION);
	    		String color = element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	    		String borderWidth = element.getAttribute(LayoutConstants.PROPERTY_BORDER_WIDTH);
	    		String borderStyle = element.getAttribute(LayoutConstants.PROPERTY_BORDER_STYLE);
	    		
	    		BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
	    		
	    		float x = Float.parseFloat(left.substring(0, left.length() - 2));
	    		float y = Float.parseFloat(top.substring(0, top.length() - 2));
	    		float h = Float.parseFloat(height.substring(0, height.length() - 2));
	    		float bw = 0f;
	    		if (StringUtils.isNotBlank(borderWidth)) {
	    			bw = Float.parseFloat(borderWidth.substring(0, borderWidth.length() - 2)) / 2;
	    		}
	    		
	    		//begin new
	    		if (y > pageBottom) {
					noPages++;
					prevPageBottom = pageBottom + pageMargin;
					pageBottom = (noPages * pageHeight) - pageMargin;
					
					document.newPage();
					bottom = document.getPageSize().getTop();
				}
	    		
	    		y = y - prevPageBottom;
	    		//end new
	    		
	    		float size = Float.parseFloat(fontSize.substring(0, fontSize.length() - 2));
	    		size = (size * 72) / denominator;
	    		
	    		if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
	    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    			left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    			top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
	    			
	    			parentLeft = Float.parseFloat(left.substring(0, left.length() - 2));
	    			parentTop = Float.parseFloat(top.substring(0, top.length() - 2));
	    		}
	    		else {
	    			 parentLeft = 0;
	    		     parentTop = 0;
	    		}
	    		
	    		float xpos = ((x + parentLeft) * 72) / denominator;
	    	    float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
	    	    ypos += DEPTH;
	    	    float length = (h * 72) / denominator;
	    	    
	    	    cb.saveState();
	    		cb.setLineWidth(bw);
	    		
	    	    if(StringUtils.isNotBlank(color)) {
	    	    	cb.setColorStroke(Markup.decodeColor(color));
	    	    }
	    	    
	    	    cb.setFontAndSize(font, size);
	    	    
	    	    if (borderStyle != null && !borderStyle.contains("solid")) {
	    	    	cb.setLineDash(bw, bw);
	    	    }
	    	    
	            cb.moveTo(xpos, ypos);
	            cb.lineTo(xpos, ypos - length);
	            cb.stroke();
	            cb.restoreState();
	    	}
	    	else if (LayoutConstants.TYPE_TABLE.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    		String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    		String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
	    		String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
	    		String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    		String fontSize = element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE);
	    		String fontFamily = element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY);
	    		String fontStyle = element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE);
	    		String fontWeight = element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT);
	    		String textDecoration = element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION);
	    		String color = element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	    		String borderWidth = element.getAttribute(LayoutConstants.PROPERTY_BORDER_WIDTH);
	    		String borderStyle = element.getAttribute(LayoutConstants.PROPERTY_BORDER_STYLE);
	    		
	    		BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
	    		
	    		float x = Float.parseFloat(left.substring(0, left.length() - 2));
	    		float y = Float.parseFloat(top.substring(0, top.length() - 2));
	    		float w = Float.parseFloat(width.substring(0, width.length() - 2));
	    		float h = Float.parseFloat(height.substring(0, height.length() - 2));
	    		float bw = 0f;
	    		if (StringUtils.isNotBlank(borderWidth)) {
	    			bw = Float.parseFloat(borderWidth.substring(0, borderWidth.length() - 2)) / 2;
	    		}
	    		
	    		//begin new
	    		if (y > pageBottom) {
					noPages++;
					prevPageBottom = pageBottom + pageMargin;
					pageBottom = (noPages * pageHeight) - pageMargin;
					
					document.newPage();
					bottom = document.getPageSize().getTop();
				}
	    		
	    		y = y - prevPageBottom;
	    		//end new
	    		
	    		float size = Float.parseFloat(fontSize.substring(0, fontSize.length() - 2));
	    		size = (size * 72) / denominator;
	    		
	    		if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
	    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    			left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    			top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
	    			
	    			parentLeft = Float.parseFloat(left.substring(0, left.length() - 2));
	    			parentTop = Float.parseFloat(top.substring(0, top.length() - 2));
	    		}
	    		else {
	    			 parentLeft = 0;
	    		     parentTop = 0;
	    		}
	    		
	    		float xpos = ((x + parentLeft) * 72) / denominator;
	    	    float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
	    	    ypos += DEPTH;
	    	    float lengthw = (w * 72) / denominator;
	    	    float lengthh = (h * 72) / denominator;
	    	    
	    	    cb.saveState();
	    	    
	    	    if(StringUtils.isNotBlank(color)) {
	    	    	cb.setColorStroke(Markup.decodeColor(color));
	    	    }
	    	    
	    	    cb.setFontAndSize(font, size);
	    	    
	    	    if (borderStyle != null && !borderStyle.contains("solid")) {
	    	    	cb.setLineDash(bw, bw);
	    	    }

	    	    cb.setLineWidth(bw);
	    	    
	    	    bw = (bw * 72) / 150;
	    	    
	    	    //top horz line
	            cb.moveTo(xpos - bw, ypos);
	            cb.lineTo(xpos + lengthw + bw, ypos);
	            
	            //botton horz line
	            cb.moveTo(xpos - bw, ypos - lengthh);
	            cb.lineTo(xpos + lengthw + bw, ypos - lengthh);
	            
	            //left vert line
	            cb.moveTo(xpos, ypos);
	            cb.lineTo(xpos, ypos - lengthh);
	            
	            //right vert line
	            cb.moveTo(xpos + lengthw, ypos);
	            cb.lineTo(xpos + lengthw, ypos - lengthh);
	            
	            cb.stroke();
	            cb.restoreState();
	    	}
	    	else if (LayoutConstants.TYPE_GROUPBOX.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    		String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    		String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
	    		String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
	    		String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    		String fontSize = element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE);
	    		String fontFamily = element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY);
	    		String fontStyle = element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE);
	    		String fontWeight = element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT);
	    		String textDecoration = element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION);
	    		String color = element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	    		String borderWidth = element.getAttribute(LayoutConstants.PROPERTY_BORDER_WIDTH);
	    		
	    		BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
	    		
	    		float x = Float.parseFloat(left.substring(0, left.length() - 2));
	    		float y = Float.parseFloat(top.substring(0, top.length() - 2));
	    		float w = Float.parseFloat(width.substring(0, width.length() - 2));
	    		float h = Float.parseFloat(height.substring(0, height.length() - 2));
	    		float bw = 0f;
	    		if (StringUtils.isNotBlank(borderWidth)) {
	    			bw = Float.parseFloat(borderWidth.substring(0, borderWidth.length() - 2)) / 2;
	    		}
	    		
	    		//begin new
	    		if (y > pageBottom) {
					noPages++;
					prevPageBottom = pageBottom + pageMargin;
					pageBottom = (noPages * pageHeight) - pageMargin;
					
					document.newPage();
					bottom = document.getPageSize().getTop();
				}
	    		
	    		y = y - prevPageBottom;
	    		//end new
	    		
	    		float size = Float.parseFloat(fontSize.substring(0, fontSize.length() - 2));
	    		size = (size * 72) / denominator;
	    		
	    		if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
	    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    			left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    			top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
	    			
	    			parentLeft = Float.parseFloat(left.substring(0, left.length() - 2));
	    			parentTop = Float.parseFloat(top.substring(0, top.length() - 2));
	    		}
	    		else {
	    			 parentLeft = 0;
	    		     parentTop = 0;
	    		}
	    		
	    		float xpos = ((x + parentLeft) * 72) / denominator;
	    	    float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
	    	    ypos += DEPTH;
	    	    float lengthw = (w * 72) / denominator;
	    	    float lengthh = (h * 72) / denominator;
	    	    
	    	    cb.saveState();
	    	    
	    	    if(StringUtils.isNotBlank(color)) {
	    	    	cb.setColorStroke(Markup.decodeColor(color));
	    	    }
	    	    
	    	    cb.setFontAndSize(font, size);

	    	    cb.setLineWidth(bw);
	    	    
	    	    bw = (bw * 72) / 150;
	    	    
	    	    //top horz line
	            cb.moveTo(xpos - bw, ypos);
	            cb.lineTo(xpos + lengthw + bw, ypos);
	            
	            //botton horz line
	            cb.moveTo(xpos - bw, ypos - lengthh);
	            cb.lineTo(xpos + lengthw + bw, ypos - lengthh);
	            
	            //left vert line
	            cb.moveTo(xpos, ypos);
	            cb.lineTo(xpos, ypos - lengthh);
	            
	            //right vert line
	            cb.moveTo(xpos + lengthw, ypos);
	            cb.lineTo(xpos + lengthw, ypos - lengthh);
	            
	            cb.stroke();
	            cb.restoreState();
	            
	            String bdColor = element.getAttribute(LayoutConstants.PROPERTY_BORDER_COLOR);
	            String bgColor = element.getAttribute(LayoutConstants.PROPERTY_BACKGROUND_COLOR);
    			if (StringUtils.isNotBlank(bgColor)) {
    				w = (Float.parseFloat(width.substring(0, width.length() - 2)) * 72) / denominator;

	    			if (StringUtils.isNotBlank(bgColor) && StringUtils.isNotBlank(width)) {
	    				if (StringUtils.isBlank(height)) {
	    					height = "25px";
	    				}
    	    			h = Float.parseFloat(height.substring(0, height.length() - 2));
    	    			//drawRectangle(cb, xpos, ypos - 6, w, ((h * 72) / denominator), bgcolor, bdcolor);
    	    			
    	    			//ypos = (bottom * noPages) - (((h + parentTop - diff) * 72) / denominator);
    		    	    //ypos += DEPTH;
    		    	    
    		    	    drawRectangle(cb, xpos, ypos - lengthh, w, ((h * 72) / denominator), bgColor, bdColor);
	    			}
    			}
	    	}
	    	else if (LayoutConstants.TYPE_LOGO.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
	    		String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
	    		String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
	    		String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
	    		String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
	    		String externalSource = element.getAttribute(LayoutConstants.PROPERTY_EXTERNALSOURCE);
		    		
	    		if (StringUtils.isNotBlank(externalSource)) {
	    			externalSource = "images/" + externalSource;
		    		float x = Float.parseFloat(left.substring(0, left.length() - 2));
		    		float y = Float.parseFloat(top.substring(0, top.length() - 2));
		    		float w = Float.parseFloat(width.substring(0, width.length() - 2));
		    		float h = Float.parseFloat(height.substring(0, height.length() - 2));
		    				
		    		//begin new
		    		if (y > pageBottom) {
						noPages++;
						prevPageBottom = pageBottom + pageMargin;
						pageBottom = (noPages * pageHeight) - pageMargin;
						
						document.newPage();
						bottom = document.getPageSize().getTop();
					}
		    		
		    		y = y - prevPageBottom;
		    		//end new
		    		
		    		if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
		    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
		    			left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
		    			top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
		    			
		    			parentLeft = Float.parseFloat(left.substring(0, left.length() - 2));
		    			parentTop = Float.parseFloat(top.substring(0, top.length() - 2));
		    		}
		    		else {
		    			 parentLeft = 0;
		    		     parentTop = 0;
		    		}
		    		
		    		float xpos = ((x + parentLeft) * 72) / denominator;
		    	    //float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
		    		float ypos = bottom - (((y + h + parentTop) * 72) / denominator);
		    	    ypos += imageDepth; //DEPTH;
		    	    float lengthw = (w * 72) / denominator;
		    	    float lengthh = (h * 72) / denominator;
		    	    
		    	    try {
		    	    	if (!realPath.endsWith(File.separator) && !externalSource.startsWith(File.separator)) {
		    	    		realPath += File.separator;
		    	    	}
			    	    Image image = Image.getInstance(realPath + externalSource);
			            image.setAbsolutePosition(xpos, ypos);
			            image.scaleAbsolute(lengthw, lengthh);
		
			            document.add(image);
		    	    }
		    	    catch (Exception ex) {
		    	    	ex.printStackTrace();
		    	    }
	    		}
	    	}
	    }
    }
	
	public static int registerFontDirectories() { 
		int count = 0;
		
		String windir = System.getenv("windir"); 
		String fileseparator = System.getProperty("file.separator"); 
		if (windir != null && fileseparator != null) {
			count += FontFactory.registerDirectory(windir + fileseparator + "fonts");
		}
		count += FontFactory.registerDirectory("/usr/share/X11/fonts", true); 
		count += FontFactory.registerDirectory("/usr/X/lib/X11/fonts", true); 
		count += FontFactory.registerDirectory("/usr/openwin/lib/X11/fonts", true); 
		count += FontFactory.registerDirectory("/usr/share/fonts", true); 
		count += FontFactory.registerDirectory("/usr/X11R6/lib/X11/fonts", true); 
		count += FontFactory.registerDirectory("/Library/Fonts"); 
		count += FontFactory.registerDirectory("/System/Library/Fonts"); 
		
		return count;
	}
	
	private List<ReportItem> getSortedReportItems(NodeList nodes) {
		List<ReportItem> items = new ArrayList<ReportItem>();
		
		for (int index = 0; index < nodes.getLength(); index++) {
	    	org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(index);
	    	items.add(new ReportItem(element));
		}
		
		Collections.sort(items);
		
		return items;
	}
	
	private class ReportItem implements Comparable<ReportItem> {

		private org.w3c.dom.Element node;
		private Float xpos;
		
		public ReportItem (org.w3c.dom.Element node) {
			this.node = node;
			
			String parentTop = null;
			org.w3c.dom.Element parentElement = (org.w3c.dom.Element)node.getParentNode();
			if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE)) ||
    				LayoutConstants.TYPE_GROUPBOX.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
				parentTop = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
			}
			
			String top = node.getAttribute(LayoutConstants.PROPERTY_TOP);
			if (StringUtils.isNotBlank(top)) {
				this.xpos = Float.parseFloat(top.substring(0, top.length() - 2));
			}
			else {
				this.xpos = 0f;
			}
			
			if (parentTop != null) {
				this.xpos += Float.parseFloat(parentTop.substring(0, parentTop.length() - 2));
			}
			
			//make background color transparent
			if (LayoutConstants.TYPE_GROUPBOX.equals(node.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
				this.xpos = 0f;
			}
		}
		
		@Override
		public int compareTo(ReportItem o) {
			return xpos.compareTo(o.getXpos());
		}
		
		public org.w3c.dom.Element getNode() {
			return node;
		}
		
		public Float getXpos() {
			return xpos;
		}
	}
}