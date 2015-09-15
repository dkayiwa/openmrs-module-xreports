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
package org.openmrs.module.xreports.api.db;

import java.util.List;

import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;

/**
 * Database methods for {@link XReportsService}.
 */
public interface XReportsDAO {
	
	/**
	 * Executes a select sql statement which returns an integer value
	 * 
	 * @param sql the select sql statement
	 * @return the integer value if any results, else null
	 * @throws Exception
	 */
	public Float getSqlIntValue(String sql) throws Exception;
	
	/**
	 * Gets all reports
	 * 
	 * @return List of report groups
	 */
	public List<XReport> getReports();
	
	/**
	 * Gets all reports within a given group of without a group if groupId is null
	 * 
	 * @param groupId the group id
	 * @return List of reports
	 */
	public List<XReport> getReports(Integer groupId);
	
	/**
	 * Gets a list of report that point to an external report uuid.
	 * 
	 * @param externalReportUuid the external report uuid
	 * @return the list of reports
	 */
	public List<XReport> getReportsByExternalUuid(String externalReportUuid);
	
	/**
	 * Gets all report groups
	 * 
	 * @return List of report groups
	 */
	public List<XReportGroup> getReportGroups();
	
	/**
	 * Gets all report groups within a given group of without a group if groupId is null
	 * 
	 * @param groupId the group id
	 * @return List of report groups
	 */
	public List<XReportGroup> getReportGroups(Integer groupId);
	
	/**
	 * Gets a report with a given id
	 * 
	 * @param reportId the report id
	 * @return the report
	 */
	public XReport getReport(Integer reportId);
	
	/**
	 * Gets a report group with a given id
	 * 
	 * @param groupId the report group id
	 * @return the report group
	 */
	public XReportGroup getReportGroup(Integer groupId);
	
	/**
	 * Saves a report
	 * 
	 * @param report the report to save
	 * @return the saved report
	 */
	public XReport saveReport(XReport report);
	
	/**
	 * Saves a report group
	 * 
	 * @param group the report group to save
	 * @return the saved report group
	 */
	public XReportGroup saveReportGroup(XReportGroup group);
	
	/**
	 * Deletes a report from the database
	 * 
	 * @param report the report to delete
	 */
	public void deleteReport(XReport report);
	
	/**
	 * Deletes a report group from
	 * 
	 * @param group the report group to delete
	 */
	public void deleteReportGroup(XReportGroup group);
}
