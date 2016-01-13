package org.openmrs.module.xreports.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.xreports.DOMUtil;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.module.xreports.api.XReportsService;
import org.w3c.dom.Document;

@Handler
@Localized("xreports.XReportRenderer")
public class XReportRenderer extends ReportDesignRenderer /*implements WebReportRenderer*/ {

	@Override
    public String getFilename(ReportRequest request) {
		return getFilenameBase(request) + ".pdf";
    }

	@Override
    public String getRenderedContentType(ReportRequest arg0) {
		return XReportsConstants.CONTENT_TYPE_PDF;
    }

	@Override
    public void render(ReportData reportData, String arg0, OutputStream out) throws IOException, RenderingException {
		
		String uuid = reportData.getDefinition().getUuid();
		List<XReport> reports = Context.getService(XReportsService.class).getReportsByExternalUuid(uuid);
		if (reports == null || reports.size() == 0) {
			return;
		}
		
		try {
			String xml = reports.get(0).getXml();
			Document doc = DOMUtil.fromString2Doc(xml);
			xml = new ReportBuilder().getReportData(reportData, doc);
			String path = Context.getAdministrationService().getGlobalProperty("xreports.imagesBaseFolder", "");
			new PdfDocument().writeFromXml(out, xml, path);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
    }

	/*@Override
    public String getLinkUrl(ReportDefinition arg0) {
		return "module/xreports/reportRunner.form";
    }*/
}