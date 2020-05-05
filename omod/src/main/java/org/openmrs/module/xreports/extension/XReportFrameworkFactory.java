package org.openmrs.module.xreports.extension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.api.XReportsService;

@OpenmrsProfile(modules = {"appframework:2.*"})
public class XReportFrameworkFactory implements AppFrameworkFactory {
	
	@Override
    public List<AppDescriptor> getAppDescriptors() throws IOException {
	    return Collections.emptyList();
    }

	@Override
    public List<Extension> getExtensions() throws IOException {
		List<Extension> extensions = new ArrayList<Extension>();
		
		List<XReport> reports = Context.getService(XReportsService.class).getReports();
		for (XReport report : reports) {
			String extension = report.getExtension();
			if (StringUtils.isBlank(extension)) {
				continue;
			}
			
			try {
				extensions.add(new XReportExtension().fromReferenceString(extension));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
        }
		
	    return extensions;
    }

	@Override
    public List<AppTemplate> getAppTemplates() throws IOException {
	    return Collections.emptyList();
    }
}