package org.openmrs.module.xreports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;

@Handler
@Localized("xreports.XReportRenderer")
public class XReportRenderer extends ReportDesignRenderer implements WebReportRenderer {

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
		Writer w = new OutputStreamWriter(out, "UTF-8");

		w.write("<?xml version=\"1.0\"?>\n");
		for (String dsKey : reportData.getDataSets().keySet()) {
			DataSet dataset = reportData.getDataSets().get(dsKey);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();
			w.write("<dataset name=\"" + dsKey + "\">\n");
			w.write("\t<rows>\n");
			for (DataSetRow row : dataset) {		
				w.write("\t\t<row>");
				for (DataSetColumn column : columns) {			
					Object colValue = row.getColumnValue(column);
					w.write("<" + column.getLabel() + ">");
					if (colValue != null) { 
						if (colValue instanceof Cohort) {
							w.write(Integer.toString(((Cohort) colValue).size()));
						} 
						else {
							w.write(colValue.toString());
						}
					}
					w.write("</" + column.getLabel() + ">");
				}
				w.write("</row>\n");
			}
		}
		w.write("\t</rows>\n");
		w.write("</dataset>\n");
		w.flush();
    }

	@Override
    public String getLinkUrl(ReportDefinition arg0) {
		return "module/xreports/reportRunner.form";
    }
}