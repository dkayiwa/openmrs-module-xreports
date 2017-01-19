/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.xreports.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;

public class ReportCommandObject {
	
	private transient Log log = LogFactory.getLog(this.getClass());

	private String existingRequestUuid;
	private ReportDefinition reportDefinition;
	private Mapped<CohortDefinition> baseCohort;
	private Map<String, Object> userEnteredParams;			
	private String selectedRenderer; // as RendererClass!Arg
	private String schedule;
	private Map<String, String> expressions;
	
	private List<RenderingMode> renderingModes;	
	
	private Cohort cohort;
	
	public ReportCommandObject() {
		userEnteredParams = new LinkedHashMap<String, Object>();
		expressions = new HashMap<String ,String>();
	}
	
	@SuppressWarnings("unchecked")
	public RenderingMode getSelectedMode() {
		if (selectedRenderer != null) {
			try {
				String[] temp = selectedRenderer.split("!");
				Class<? extends ReportRenderer> rc = (Class<? extends ReportRenderer>) Context.loadClass(temp[0]);
				String arg = (temp.length > 1 && StringUtils.hasText(temp[1])) ? temp[1] : null;
				for (RenderingMode mode : renderingModes) {
					if (mode.getRenderer().getClass().equals(rc) && OpenmrsUtil.nullSafeEquals(mode.getArgument(), arg)) {
						return mode;
					}
				}
				log.warn("Could not find requested rendering mode: " + selectedRenderer);
			}
			catch (Exception e) {
				log.warn("Could not load requested renderer", e);
			}
		}
		return null;
	}

	public String getExistingRequestUuid() {
		return existingRequestUuid;
	}

	public void setExistingRequestUuid(String existingRequestUuid) {
		this.existingRequestUuid = existingRequestUuid;
	}

	public List<RenderingMode> getRenderingModes() {
		return renderingModes;
	}
	
	public void setRenderingModes(List<RenderingMode> rendereringModes) {
		this.renderingModes = rendereringModes;
	}
	
	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}
	
	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}

	public Cohort getCohort() {
		return cohort;
	}
	
	public void setCohort(Cohort cohort) {
		this.cohort = cohort;
	}
	
	public Mapped<CohortDefinition> getBaseCohort() {
		return baseCohort;
	}

	public void setBaseCohort(Mapped<CohortDefinition> baseCohort) {
		this.baseCohort = baseCohort;
	}

	public String getSelectedRenderer() {
		return selectedRenderer;
	}
	
	public void setSelectedRenderer(String selectedRenderer) {
		this.selectedRenderer = selectedRenderer;
	}
	
	public Map<String, Object> getUserEnteredParams() {
		return userEnteredParams;
	}
	
	public void setUserEnteredParams(Map<String, Object> userEnteredParams) {
		this.userEnteredParams = userEnteredParams;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * @return the expressions
	 */
	public Map<String, String> getExpressions() {
		return expressions;
	}
	
	/**
	 * @param expressions the expressions to set
	 */
	public void setExpressions(Map<String, String> expressions) {
		this.expressions = expressions;
	}
}
