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
package org.openmrs.module.xreports.api.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.XReportsService;
import org.openmrs.module.xreports.api.db.XReportsDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link XReportsService}.
 */
public class XReportsServiceImpl extends BaseOpenmrsService implements XReportsService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private XReportsDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(XReportsDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public XReportsDAO getDao() {
	    return dao;
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getSqlIntValue(java.lang.String)
     */
    @Override
    public Integer getSqlIntValue(String sql) throws Exception {
    	return dao.getSqlIntValue(sql);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getReports()
     */
    @Override
    @Transactional(readOnly = true)
    public List<XReport> getReports() {
	    return dao.getReports();
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getReportGroups()
     */
    @Override
    @Transactional(readOnly = true)
    public List<XReportGroup> getReportGroups() {
	    return dao.getReportGroups();
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getReport(java.lang.Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public XReport getReport(Integer reportId) {
	    return dao.getReport(reportId);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getReportGroup(java.lang.Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public XReportGroup getReportGroup(Integer groupId) {
	    return dao.getReportGroup(groupId);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getReports(java.lang.Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public List<XReport> getReports(Integer groupId) {
	    return dao.getReports(groupId);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#getReportGroups(java.lang.Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public List<XReportGroup> getReportGroups(Integer groupId) {
	    return dao.getReportGroups(groupId);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#saveReport(org.openmrs.module.xreports.XReport)
     */
    @Override
    public XReport saveReport(XReport report) {
	    return dao.saveReport(report);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#saveReportGroup(org.openmrs.module.xreports.XReportGroup)
     */
    @Override
    public XReportGroup saveReportGroup(XReportGroup group) {
	    return dao.saveReportGroup(group);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#deleteReport(org.openmrs.module.xreports.XReport)
     */
    @Override
    public void deleteReport(XReport report) {
	    dao.deleteReport(report);
    }

	/**
     * @see org.openmrs.module.xreports.api.XReportsService#deleteReportGroup(org.openmrs.module.xreports.XReportGroup)
     */
    @Override
    public void deleteReportGroup(XReportGroup group) {
	    dao.deleteReportGroup(group);
    }
}