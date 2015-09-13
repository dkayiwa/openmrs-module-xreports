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
package org.openmrs.module.xreports.api.db.hibernate;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.db.XReportsDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link XReportsDAO}.
 */
public class HibernateXReportsDAO implements XReportsDAO {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getSqlIntValue(java.lang.String)
	 */
	@Override
	public Float getSqlIntValue(String sql) throws Exception {
		Float value = null;
		Statement statement = sessionFactory.getCurrentSession().connection().createStatement();
		ResultSet res = statement.executeQuery(sql);
		if (res.next()) {
			value = res.getFloat(1);
			if (res.wasNull()) {
				value = null;
			}
		}
		res.close();
		statement.close();
		return value;
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReports()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<XReport> getReports() {
		return sessionFactory.getCurrentSession().createQuery("from XReport order by name").list();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReportGroups()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<XReportGroup> getReportGroups() {
		return sessionFactory.getCurrentSession().createQuery("from XReportGroup order by name").list();
	}
	
	/**
     * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReports(java.lang.Integer)
     */
    @Override
    public List<XReport> getReports(Integer groupId) {
    	Query query = sessionFactory.getCurrentSession().createQuery("from XReport where group = :group order by name");
		query.setParameter("group", new XReportGroup(groupId));
		
		if (groupId == null) {
			query = sessionFactory.getCurrentSession().createQuery("from XReport where group is null order by name");
		}
		
		return query.list();
    }

	/**
     * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReportGroups(java.lang.Integer)
     */
    @Override
    public List<XReportGroup> getReportGroups(Integer groupId) {
    	Query query = sessionFactory.getCurrentSession().createQuery("from XReportGroup where parentGroup = :parentGroup order by name");
		query.setParameter("parentGroup", new XReportGroup(groupId));
		
		if (groupId == null) {
			query = sessionFactory.getCurrentSession().createQuery("from XReportGroup where parentGroup is null order by name");
		}
		
		return query.list();
    }

	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReport(java.lang.Integer)
	 */
	@Override
	public XReport getReport(Integer reportId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from XReport where reportId = :reportId");
		query.setParameter("reportId", reportId);
		return (XReport) query.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReportGroup(java.lang.Integer)
	 */
	@Override
	public XReportGroup getReportGroup(Integer groupId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from XReportGroup where groupId = :groupId");
		query.setParameter("groupId", groupId);
		return (XReportGroup) query.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#saveReport(org.openmrs.module.xreports.XReport)
	 */
	@Override
	public XReport saveReport(XReport report) {
		sessionFactory.getCurrentSession().save(report);
		return report;
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#saveReportGroup(org.openmrs.module.xreports.XReportGroup)
	 */
	@Override
	public XReportGroup saveReportGroup(XReportGroup group) {
		sessionFactory.getCurrentSession().save(group);
		return group;
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#deleteReport(org.openmrs.module.xreports.XReport)
	 */
	@Override
	public void deleteReport(XReport report) {
		sessionFactory.getCurrentSession().delete(report);
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#deleteReportGroup(org.openmrs.module.xreports.XReportGroup)
	 */
	@Override
	public void deleteReportGroup(XReportGroup group) {
		sessionFactory.getCurrentSession().delete(group);
	}
}
