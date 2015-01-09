package org.openmrs.module.xreports.web;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.xreports.DOMUtil;
import org.openmrs.module.xreports.DesignItem;
import org.w3c.dom.NodeList;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Represents a pdf document
 */
public class PdfDocument {
	
	private float DEPTH = -13;
	
	public PdfDocument() {
		
	}
	
	private Font getFont(String fontFamily, String fontStyle, String fontWeight, String textDecoration, String color)
	    throws Exception {
		
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
	
	private BaseFont getBaseFont(String fontFamily, String fontStyle, String fontWeight, String textDecoration, String color)
	    throws Exception {
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
		
		float w = Integer.parseInt(width.substring(0, width.length() - 2));
		float h = Integer.parseInt(height.substring(0, height.length() - 2));
		
		float denominator = 98.5f;
		float pageWidth = (w * 72) / denominator;
		float pageHeight = (h * 72) / denominator;
		
		DEPTH = 0;//-13;
		if (pageWidth > pageHeight) {
			DEPTH = 0;//-13;
		}
		
		return new Document(new Rectangle(pageWidth, pageHeight), 50, 50, 50, 50);
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
	
	private void generate(org.w3c.dom.Document doc, Document document, PdfWriter writer, String realPath) throws Exception,
	    BadElementException, MalformedURLException, IOException, DocumentException {
		float bottom = document.getPageSize().getTop();
		
		NodeList nodes = doc.getDocumentElement().getElementsByTagName(DesignItem.NAME_ITEM);
		if (nodes == null)
			return;
		
		PdfContentByte cb = writer.getDirectContent();
		
		float parentLeft = 0;
		float parentTop = 0;
		
		float diff = 15;
		float denominator = 98.5f;
		
		for (int index = 0; index < nodes.getLength(); index++) {
			org.w3c.dom.Element element = (org.w3c.dom.Element) nodes.item(index);
			org.w3c.dom.Element parentElement = (org.w3c.dom.Element) element.getParentNode();
			
			if (LayoutConstants.TYPE_LABEL.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
				
				String text = element.getAttribute(LayoutConstants.PROPERTY_TEXT);
				String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
				String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
				String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
				String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
				String fontSize = element.getAttribute(LayoutConstants.PROPERTY_FONT_SIZE);
				String fontFamily = element.getAttribute(LayoutConstants.PROPERTY_FONT_FAMILY);
				String fontStyle = element.getAttribute(LayoutConstants.PROPERTY_FONT_STYLE);
				String fontWeight = element.getAttribute(LayoutConstants.PROPERTY_FONT_WEIGHT);
				String textDecoration = element.getAttribute(LayoutConstants.PROPERTY_TEXT_DECORATION);
				String color = element.getAttribute(LayoutConstants.PROPERTY_COLOR);
				String rotated = element.getAttribute(LayoutConstants.PROPERTY_ROTATED);
				String textAlign = element.getAttribute("textAlign");
				
				BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
				
				float x = Integer.parseInt(left.substring(0, left.length() - 2));
				float y = Integer.parseInt(top.substring(0, top.length() - 2));
				
				float size = Integer.parseInt(fontSize.substring(0, fontSize.length() - 2));
				size = (size * 72) / denominator;
				
				if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))
				        || LayoutConstants.TYPE_GROUPBOX.equals(parentElement
				                .getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
					String s = top;
					left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
					top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
					
					parentLeft = Integer.parseInt(left.substring(0, left.length() - 2));
					parentTop = Integer.parseInt(top.substring(0, top.length() - 2));
					
					//debug block for text beyond table - hence invisible
					String parentHeight = parentElement.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
					int t1 = Integer.parseInt(parentHeight.substring(0, parentHeight.length() - 2));
					int t2 = Integer.parseInt(s.substring(0, s.length() - 2));
					if (t2 >= t1) {
						continue;
					}
				} else {
					parentLeft = 0;
					parentTop = 0;
				}
				
				cb.saveState();
				cb.beginText();
				
				float xpos = ((x + parentLeft) * 72) / denominator;
				float ypos = bottom - (((y + parentTop) * 72) / denominator);
				ypos += 2; //DEPTH;
				
				if (StringUtils.isNotBlank(color)) {
					Color clr = Markup.decodeColor(color);
					cb.setColorFill(clr);
					cb.setColorStroke(clr);
				}
				
				cb.setFontAndSize(font, size);
				
				if (StringUtils.isNotBlank(width) && StringUtils.isNotBlank(height)) {
					float w = Integer.parseInt(width.substring(0, width.length() - 2));
					w = (w * 72) / denominator;
					ColumnText ct = new ColumnText(cb);
					ypos += 15;
					ct.setSimpleColumn(xpos, ypos, xpos + w, ypos - 500, 15, Element.ALIGN_LEFT);
					ct.addText(new Phrase(text, getFont(fontFamily, fontStyle, fontWeight, textDecoration, color)));
					ct.go();
				} else if ("true".equals(rotated)) {
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text, xpos, ypos, 90);
					xpos += 3;
				} else {
					if ("center".equals(textAlign) || "right".equals(textAlign)) {
						int align = PdfContentByte.ALIGN_CENTER;
						float w = size / 2;
						if ("right".equals(textAlign)) {
							align = PdfContentByte.ALIGN_RIGHT;
							w = size;
						}
						cb.showTextAligned(align, text, xpos + w, ypos, 0);
					} else {
						cb.setTextMatrix(xpos, ypos);
						cb.showText(text);
					}
					ypos -= 3;
				}
				
				cb.endText();
				
				if (StringUtils.isNotBlank(textDecoration) && textDecoration.contains("underline")) {
					cb.setLineWidth(1f);
					float length = font.getWidthPoint(text, size);
					cb.moveTo(xpos, ypos);
					if ("true".equals(rotated)) {
						cb.lineTo(xpos, ypos + length);
					} else {
						cb.lineTo(xpos + length, ypos);
					}
					cb.stroke();
				}
				
				cb.restoreState();
			} else if (LayoutConstants.TYPE_HORIZONTAL_LINE
			        .equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
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
				
				BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
				
				float x = Integer.parseInt(left.substring(0, left.length() - 2));
				float y = Integer.parseInt(top.substring(0, top.length() - 2));
				float w = Integer.parseInt(width.substring(0, width.length() - 2));
				float bw = 0f;
				if (StringUtils.isNotBlank(borderWidth)) {
					bw = Integer.parseInt(borderWidth.substring(0, borderWidth.length() - 2));
				}
				
				float size = Integer.parseInt(fontSize.substring(0, fontSize.length() - 2));
				size = (size * 72) / denominator;
				
				if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))
				        || LayoutConstants.TYPE_GROUPBOX.equals(parentElement
				                .getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
					String s = top;
					left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
					top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
					
					parentLeft = Integer.parseInt(left.substring(0, left.length() - 2));
					parentTop = Integer.parseInt(top.substring(0, top.length() - 2));
					
					//debug block for lines beyond table - hence invisible
					String parentHeight = parentElement.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
					int t1 = Integer.parseInt(parentHeight.substring(0, parentHeight.length() - 2));
					int t2 = Integer.parseInt(s.substring(0, s.length() - 2));
					if (t2 >= t1) {
						continue;
					}
				} else {
					parentLeft = 0;
					parentTop = 0;
				}
				
				float xpos = ((x + parentLeft) * 72) / denominator;
				float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
				ypos += DEPTH;
				float length = (w * 72) / denominator;
				
				cb.setLineWidth(bw);
				
				if (StringUtils.isNotBlank(color)) {
					cb.setColorStroke(Markup.decodeColor(color));
				}
				
				cb.setFontAndSize(font, size);
				
				cb.moveTo(xpos, ypos);
				cb.lineTo(xpos + length, ypos);
				cb.stroke();
			} else if (LayoutConstants.TYPE_VERTICAL_LINE.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
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
				
				BaseFont font = getBaseFont(fontFamily, fontStyle, fontWeight, textDecoration, color);
				
				float x = Integer.parseInt(left.substring(0, left.length() - 2));
				float y = Integer.parseInt(top.substring(0, top.length() - 2));
				float h = Integer.parseInt(height.substring(0, height.length() - 2));
				float bw = 0f;
				if (StringUtils.isNotBlank(borderWidth)) {
					bw = Integer.parseInt(borderWidth.substring(0, borderWidth.length() - 2));
				}
				
				float size = Integer.parseInt(fontSize.substring(0, fontSize.length() - 2));
				size = (size * 72) / denominator;
				
				if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))
				        || LayoutConstants.TYPE_GROUPBOX.equals(parentElement
				                .getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
					left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
					top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
					
					parentLeft = Integer.parseInt(left.substring(0, left.length() - 2));
					parentTop = Integer.parseInt(top.substring(0, top.length() - 2));
				} else {
					parentLeft = 0;
					parentTop = 0;
				}
				
				float xpos = ((x + parentLeft) * 72) / denominator;
				float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
				ypos += DEPTH;
				float length = (h * 72) / denominator;
				
				cb.setLineWidth(bw);
				
				if (StringUtils.isNotBlank(color)) {
					cb.setColorStroke(Markup.decodeColor(color));
				}
				
				cb.setFontAndSize(font, size);
				
				cb.moveTo(xpos, ypos);
				cb.lineTo(xpos, ypos - length);
				cb.stroke();
			} else if (LayoutConstants.TYPE_TABLE.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
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
				
				float x = Integer.parseInt(left.substring(0, left.length() - 2));
				float y = Integer.parseInt(top.substring(0, top.length() - 2));
				float w = Integer.parseInt(width.substring(0, width.length() - 2));
				float h = Integer.parseInt(height.substring(0, height.length() - 2));
				float bw = 0f;
				if (StringUtils.isNotBlank(borderWidth)) {
					bw = Integer.parseInt(borderWidth.substring(0, borderWidth.length() - 2));
				}
				
				float size = Integer.parseInt(fontSize.substring(0, fontSize.length() - 2));
				size = (size * 72) / denominator;
				
				if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))
				        || LayoutConstants.TYPE_GROUPBOX.equals(parentElement
				                .getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
					left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
					top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
					
					parentLeft = Integer.parseInt(left.substring(0, left.length() - 2));
					parentTop = Integer.parseInt(top.substring(0, top.length() - 2));
				} else {
					parentLeft = 0;
					parentTop = 0;
				}
				
				float xpos = ((x + parentLeft) * 72) / denominator;
				float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
				ypos += DEPTH;
				float lengthw = (w * 72) / denominator;
				float lengthh = (h * 72) / denominator;
				
				if (StringUtils.isNotBlank(color)) {
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
			} else if (LayoutConstants.TYPE_GROUPBOX.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
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
				
				float x = Integer.parseInt(left.substring(0, left.length() - 2));
				float y = Integer.parseInt(top.substring(0, top.length() - 2));
				float w = Integer.parseInt(width.substring(0, width.length() - 2));
				float h = Integer.parseInt(height.substring(0, height.length() - 2));
				float bw = 0f;
				if (StringUtils.isNotBlank(borderWidth)) {
					bw = Integer.parseInt(borderWidth.substring(0, borderWidth.length() - 2));
				}
				
				float size = Integer.parseInt(fontSize.substring(0, fontSize.length() - 2));
				size = (size * 72) / denominator;
				
				if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))
				        || LayoutConstants.TYPE_GROUPBOX.equals(parentElement
				                .getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
					left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
					top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
					
					parentLeft = Integer.parseInt(left.substring(0, left.length() - 2));
					parentTop = Integer.parseInt(top.substring(0, top.length() - 2));
				} else {
					parentLeft = 0;
					parentTop = 0;
				}
				
				float xpos = ((x + parentLeft) * 72) / denominator;
				float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
				ypos += DEPTH;
				float lengthw = (w * 72) / denominator;
				float lengthh = (h * 72) / denominator;
				
				if (StringUtils.isNotBlank(color)) {
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
			} else if (LayoutConstants.TYPE_LOGO.equals(element.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
				String left = element.getAttribute(LayoutConstants.PROPERTY_LEFT);
				String top = element.getAttribute(LayoutConstants.PROPERTY_TOP);
				String width = element.getAttribute(LayoutConstants.PROPERTY_WIDTH);
				String height = element.getAttribute(LayoutConstants.PROPERTY_HEIGHT);
				String externalSource = element.getAttribute(LayoutConstants.PROPERTY_EXTERNALSOURCE);
				
				if (StringUtils.isNotBlank(externalSource)) {
					float x = Integer.parseInt(left.substring(0, left.length() - 2));
					float y = Integer.parseInt(top.substring(0, top.length() - 2));
					float w = Integer.parseInt(width.substring(0, width.length() - 2));
					float h = Integer.parseInt(height.substring(0, height.length() - 2));
					
					if (LayoutConstants.TYPE_TABLE.equals(parentElement.getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))
					        || LayoutConstants.TYPE_GROUPBOX.equals(parentElement
					                .getAttribute(LayoutConstants.PROPERTY_WIDGETTYPE))) {
						left = parentElement.getAttribute(LayoutConstants.PROPERTY_LEFT);
						top = parentElement.getAttribute(LayoutConstants.PROPERTY_TOP);
						
						parentLeft = Integer.parseInt(left.substring(0, left.length() - 2));
						parentTop = Integer.parseInt(top.substring(0, top.length() - 2));
					} else {
						parentLeft = 0;
						parentTop = 0;
					}
					
					float xpos = ((x + parentLeft) * 72) / denominator;
					float ypos = bottom - (((y + parentTop - diff) * 72) / denominator);
					ypos += -30; //DEPTH;
					float lengthw = (w * 72) / denominator;
					float lengthh = (h * 72) / denominator;
					
					try {
						if (!realPath.endsWith(File.separator) && !externalSource.startsWith(File.separator)) {
							realPath += File.separator;
						}
						Image image = Image.getInstance(realPath + externalSource);
						image.setAbsolutePosition(xpos, ypos - 80);
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
	
	public void main(OutputStream os) {
		//http://www.coderanch.com/how-to/java/ItextExample
		
		// creation of the document with a certain size and certain margins
		// (you can use PageSize.Letter instead of PageSize.A4)
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		try {
			// creation of the different writers
			PdfWriter writer = PdfWriter.getInstance(document, os);
			
			// various fonts
			BaseFont bf_helv = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
			BaseFont bf_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", false);
			BaseFont bf_courier = BaseFont.createFont(BaseFont.COURIER, "Cp1252", false);
			BaseFont bf_symbol = BaseFont.createFont(BaseFont.SYMBOL, "Cp1252", false);
			
			// headers and footers must be added before the document is opened
			HeaderFooter footer = new HeaderFooter(new Phrase("This is page: ", new Font(bf_courier)), true);
			footer.setBorder(Rectangle.NO_BORDER);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.setFooter(footer);
			
			HeaderFooter header = new HeaderFooter(new Phrase("This is a header without a page number", new Font(bf_times)),
			        false);
			header.setAlignment(Element.ALIGN_CENTER);
			document.setHeader(header);
			
			document.open();
			
			int y_line1 = 650;
			int y_line2 = y_line1 - 50;
			int y_line3 = y_line2 - 50;
			
			// draw a few lines ...
			PdfContentByte cb = writer.getDirectContent();
			cb.setLineWidth(0f);
			cb.moveTo(250, y_line3 - 100);
			cb.lineTo(250, y_line1 + 100);
			cb.moveTo(50, y_line1);
			cb.lineTo(400, y_line1);
			cb.moveTo(50, y_line2);
			cb.lineTo(400, y_line2);
			cb.moveTo(50, y_line3);
			cb.lineTo(400, y_line3);
			cb.stroke();
			// ... and some text that is aligned in various ways
			cb.beginText();
			cb.setFontAndSize(bf_helv, 12);
			String text = "Sample text for alignment";
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text + " Center", 250, y_line1, 0);
			cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, text + " Right", 250, y_line2, 0);
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text + " Left", 250, y_line3, 0);
			cb.endText();
			
			// start second page
			document.newPage();
			
			// add text in three paragraphs from top to bottom with various font styles
			Paragraph par = new Paragraph("bold paragraph");
			par.getFont().setStyle(Font.BOLD);
			document.add(par);
			par = new Paragraph("italic paragraph");
			par.getFont().setStyle(Font.ITALIC);
			document.add(par);
			par = new Paragraph("underlined and strike-through paragraph");
			par.getFont().setStyle(Font.UNDERLINE | Font.STRIKETHRU);
			document.add(par);
			
			// demonstrate some table features
			Table table = new Table(3);
			// 2 pixel wide blue border
			table.setBorderWidth(2);
			table.setBorderColor(new Color(0, 0, 255));
			table.setPadding(5);
			table.setSpacing(5);
			Cell c = new Cell("header");
			c.setHeader(true);
			c.setColspan(3);
			table.addCell(c);
			table.endHeaders();
			c = new Cell("example cell with rowspan 2 and red border");
			c.setRowspan(2);
			c.setBorderColor(new Color(255, 0, 0));
			table.addCell(c);
			table.addCell("1.1");
			table.addCell("2.1");
			table.addCell("1.2");
			table.addCell("2.2");
			c = new Cell("align center");
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c);
			Cell cell = new Cell("big cell");
			cell.setRowspan(2);
			cell.setColspan(2);
			table.addCell(cell);
			c = new Cell("align right");
			c.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c);
			document.add(table);
			
			// add text at an absolute position
			cb.beginText();
			cb.setFontAndSize(bf_times, 14);
			cb.setTextMatrix(100, 300);
			cb.showText("Text at position 100, 300.");
			cb.endText();
			
			// rotated text at an absolute position
			PdfTemplate template = cb.createTemplate(300, 300);
			template.beginText();
			template.setFontAndSize(bf_times, 14);
			template.showText("Rotated text at position 400, 200.");
			template.endText();
			
			float rotate = 90;
			float x = 400;
			float y = 200;
			float angle = (float) (-rotate * (Math.PI / 180));
			float xScale = (float) Math.cos(angle);
			float yScale = (float) Math.cos(angle);
			float xRot = (float) -Math.sin(angle);
			float yRot = (float) Math.sin(angle);
			
			cb.addTemplate(template, xScale, xRot, yRot, yScale, x, y);
			
			// we're done!
			document.close();
			
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
}
