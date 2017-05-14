/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.xreports.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportsConstants;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.web.ReportCommandObject;
import org.openmrs.module.xreports.web.XReportPdfRenderer;
import org.openmrs.module.xreports.web.XReportRenderer;
import org.quartz.CronExpression;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This controller runs a report (which must be passed in with the reportId parameter) after
 * allowing the user to enter parameters (if any) and to choose a ReportRenderer. If the chosen
 * ReportRenderer is a WebReportRenderer, then the report data is placed in the session and this
 * page redirects to the WebReportRenderer's specified URL. Otherwise the renderer writes to this
 * form's response.
 */
public class FillParameterFormController extends SimpleFormController implements Validator {
	
	/**
	 * @see BaseCommandController#initBinder(HttpServletRequest, ServletRequestDataBinder)
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		//super.initBinder(request, binder);
		binder.registerCustomEditor(Mapped.class, new MappedEditor());
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean supports(Class c) {
		return c == ReportCommandObject.class;
	}
	
	@Override
	public void validate(Object commandObject, Errors errors) {
		ReportCommandObject command = (ReportCommandObject) commandObject;
		ValidationUtils.rejectIfEmpty(errors, "reportDefinition", "reporting.Report.run.error.missingReportID");
		if (command.getReportDefinition() != null) {
			ReportDefinition reportDefinition = command.getReportDefinition();
			Set<String> requiredParams = new HashSet<String>();
			if (reportDefinition.getParameters() != null) {
				for (Parameter parameter : reportDefinition.getParameters()) {
					if (parameter.isRequired()) {
						requiredParams.add(parameter.getName());
					}
				}
			}
			
			for (Map.Entry<String, Object> e : command.getUserEnteredParams().entrySet()) {
				if (e.getValue() instanceof Iterable || e.getValue() instanceof Object[]) {
					Object iterable = e.getValue();
					if (e.getValue() instanceof Object[]) {
						iterable = Arrays.asList((Object[]) e.getValue());
					}
					
					boolean hasNull = true;
					
					for (Object value : (Iterable<Object>) iterable) {
						hasNull = !ObjectUtil.notNull(value);
                    }
					
					if (!hasNull) {
						requiredParams.remove(e.getKey());
					}
				} else if (ObjectUtil.notNull(e.getValue())) {
					requiredParams.remove(e.getKey());
				}
			}
			if (requiredParams.size() > 0) {
				for (Iterator<String> iterator = requiredParams.iterator(); iterator.hasNext();) {
					String parameterName = iterator.next();
					if (StringUtils.hasText(command.getExpressions().get(parameterName))) {
						String expression = command.getExpressions().get(parameterName);
						if (!EvaluationUtil.isExpression(expression)){
							errors.rejectValue("expressions[" + parameterName + "]",
							    "reporting.Report.run.error.invalidParamExpression");
						}
					} else {
						errors.rejectValue("userEnteredParams[" + parameterName + "]", parameterName + " is required",
						    new Object[] { "This parameter" }, "{0} is required");
					}
				}
			}
			
			if (reportDefinition.getDataSetDefinitions() == null || reportDefinition.getDataSetDefinitions().size() == 0) {
				errors.reject("reporting.Report.run.error.definitionNotDeclared");
			}
			
			if (ObjectUtil.notNull(command.getSchedule())) {
				if (!CronExpression.isValidExpression(command.getSchedule())) {
					errors.rejectValue("schedule", "reporting.Report.run.error.invalidCronExpression");
				}
			}
		}
		ValidationUtils.rejectIfEmpty(errors, "selectedRenderer", "reporting.Report.run.error.noRendererSelected");
	}
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		
		request.getSession().removeAttribute(XReportsConstants.REPORT_PARAMETER_DATA);
		
		ReportCommandObject command = new ReportCommandObject();
		if (Context.isAuthenticated()) {
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportService reportService = Context.getService(ReportService.class);
			if (StringUtils.hasText(request.getParameter("copyRequest"))) {
				ReportRequest req = reportService.getReportRequestByUuid(request.getParameter("copyRequest"));
				// avoid lazy init exceptions
				command.setReportDefinition(rds.getDefinitionByUuid(req.getReportDefinition().getParameterizable().getUuid()));
				for (Map.Entry<String, Object> param : req.getReportDefinition().getParameterMappings().entrySet()) {
					Object value = param.getValue();
					if ( value != null && EvaluationUtil.isExpression( value.toString() ) ) {
						command.getExpressions().put( param.getKey(),  ( String ) value );
						value = "";
					} 
					command.getUserEnteredParams().put(param.getKey(), value );
				}
				command.setSelectedRenderer(req.getRenderingMode().getDescriptor());
			}
			else if (StringUtils.hasText(request.getParameter("requestUuid"))) {
				String reqUuid = request.getParameter("requestUuid");
				ReportRequest rr = reportService.getReportRequestByUuid(reqUuid);
				command.setExistingRequestUuid(reqUuid);
				command.setReportDefinition(rr.getReportDefinition().getParameterizable());
				command.setUserEnteredParams(rr.getReportDefinition().getParameterMappings());
				command.setBaseCohort(rr.getBaseCohort());
				command.setSelectedRenderer(rr.getRenderingMode().getDescriptor());
				command.setSchedule(rr.getSchedule());
			}
			else {
				String uuid = request.getParameter("reportId");
				ReportDefinition reportDefinition = rds.getDefinitionByUuid(uuid);
				command.setReportDefinition(reportDefinition);
				for (Parameter p : reportDefinition.getParameters()) {
					if (p.getDefaultValue() != null) {
						command.getUserEnteredParams().put(p.getName(), p.getDefaultValue());
					}
				}
			}
			
			//make the xreports renderer the default selection
			RenderingMode xreportsMode = null;
			List<RenderingMode> renderingModes = reportService.getRenderingModes(command.getReportDefinition());
			for (RenderingMode mode : renderingModes) {
				if (mode.getRenderer() instanceof XReportPdfRenderer) {
					xreportsMode = mode;
					break;
				}
			}
			if (xreportsMode != null) {
				renderingModes.remove(xreportsMode);
				renderingModes.add(0, xreportsMode);
			}
			command.setRenderingModes(renderingModes);
		}
		return command;
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object commandObject, BindException errors) throws Exception {
		ReportCommandObject command = (ReportCommandObject) commandObject;
		ReportDefinition reportDefinition = command.getReportDefinition();

		// Parse the input parameters into appropriate objects and fail validation if any are invalid
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		if (reportDefinition.getParameters() != null && (command.getUserEnteredParams() != null || command.getExpressions() != null)) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				Object value = null;
				String expression = null;
				if (command.getExpressions() != null && ObjectUtil.notNull(command.getExpressions().get(parameter.getName()))) {
					expression = command.getExpressions().get(parameter.getName());
				}
				else {
					value = command.getUserEnteredParams().get(parameter.getName());
				}
				if (ObjectUtil.notNull(value) || ObjectUtil.notNull(expression)) {
					try {
						if (StringUtils.hasText(expression))
							value = expression;
						else
							value = WidgetUtil.parseInput(value, parameter.getType(), parameter.getCollectionType());

						params.put(parameter.getName(), value);
					}
					catch (Exception ex) {
						errors.rejectValue("userEnteredParams[" + parameter.getName() + "]", ex.getMessage());
					}
				}
			}
		}
		
		// Ensure that the chosen renderer is valid for this report
		RenderingMode renderingMode = command.getSelectedMode();
		if (!renderingMode.getRenderer().canRender(reportDefinition)) {
			errors.rejectValue("selectedRenderer", "reporting.Report.run.error.invalidRenderer");
		}

		if (errors.hasErrors()) {
			return showForm(request, response, errors);
		}
		
		request.getSession().setAttribute(XReportsConstants.REPORT_PARAMETER_DATA, command);

		String id = request.getParameter("formId");
		if (renderingMode.getRenderer() instanceof XReportRenderer) {
			XReport report = Context.getService(XReportsService.class).getReport(Integer.parseInt(id));
			String group = report.getGroup() != null ? "&groupId=" + report.getGroup().getGroupId() : "";
			return new ModelAndView("redirect:/xreports/reportRunner.page?reportId=" + id + group);
		}
		else if (renderingMode.getRenderer() instanceof XReportPdfRenderer) {
			return new ModelAndView("redirect:/moduleServlet/xreports/reportDownloadServlet?renderer=true&formId=" + id);
		}
		else {
			ReportService rs = Context.getService(ReportService.class);
			
			ReportRequest rr = null;
			if (command.getExistingRequestUuid() != null) {
				rr = rs.getReportRequestByUuid(command.getExistingRequestUuid());
			} else {
				rr = new ReportRequest();
			}
			rr.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
			rr.setBaseCohort(command.getBaseCohort());
			rr.setRenderingMode(command.getSelectedMode());
			rr.setPriority(Priority.NORMAL);
			rr.setSchedule(command.getSchedule());
			
			// TODO: We might want to check here if this exact same report request is already queued and just re-direct if so
			
			rr = rs.queueReport(rr);
			rs.processNextQueuedReports();
			
			return new ModelAndView(new RedirectView("../reporting/reports/reportHistoryOpen.form?uuid=" + rr.getUuid()));
		}
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object commandObject, Errors errors) throws Exception {
		ReportCommandObject command = (ReportCommandObject) commandObject;
		Map<String, Object> map = new HashMap<String, Object>();
		EvaluationContext ec = new EvaluationContext();
		Set<String> expSupportedTypes = new HashSet<String>();
		Set<String> inputsToToggle = new HashSet<String>();
		for (Object value : ec.getContextValues().values()) {
			expSupportedTypes.add(value.getClass().getName());
		}
		map.put("expSupportedTypes", expSupportedTypes);

		for (Map.Entry<String, Object> e : command.getUserEnteredParams().entrySet()) {
			if (StringUtils.hasText(command.getExpressions().get(e.getKey()))) {
				inputsToToggle.add( e.getKey() );
			}
		}
		map.put( "inputsToToggle", inputsToToggle );
		return map;
	}
}
