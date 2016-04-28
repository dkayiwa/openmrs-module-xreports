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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.module.xreports.XReport;
import org.openmrs.module.xreports.XReportGroup;
import org.openmrs.module.xreports.api.db.XReportsDAO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

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
	public Object getSqlValue(String sql) throws Exception {
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		return query.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReports()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<XReport> getReports() {
		return getCurrentSession().createQuery("from XReport order by displayOrder, name").list();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReportGroups()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<XReportGroup> getReportGroups() {
		return getCurrentSession().createQuery("from XReportGroup order by displayOrder, name").list();
	}
	
	/**
     * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReports(java.lang.Integer)
     */
    @Override
    public List<XReport> getReports(Integer groupId) {
    	Query query = getCurrentSession().createQuery("from XReport where group = :group order by displayOrder, name");
		query.setParameter("group", new XReportGroup(groupId));
		
		if (groupId == null) {
			query = getCurrentSession().createQuery("from XReport where group is null order by displayOrder, name");
		}
		
		return query.list();
    }
    
    /**
     * @see org.openmrs.module.xreports.api.XReportsService#getReportsByExternalUuid(java.lang.String)
     */
    @Override
    public List<XReport> getReportsByExternalUuid(String externalReportUuid) {
    	Query query = getCurrentSession().createQuery("from XReport where externalReportUuid = :externalReportUuid");
		query.setParameter("externalReportUuid", externalReportUuid);
		return query.list();
    }

	/**
     * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReportGroups(java.lang.Integer)
     */
    @Override
    public List<XReportGroup> getReportGroups(Integer groupId) {
    	Query query = getCurrentSession().createQuery("from XReportGroup where parentGroup = :parentGroup order by displayOrder, name");
		query.setParameter("parentGroup", new XReportGroup(groupId));
		
		if (groupId == null) {
			query = getCurrentSession().createQuery("from XReportGroup where parentGroup is null order by displayOrder, name");
		}
		
		return query.list();
    }

	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReport(java.lang.Integer)
	 */
	@Override
	public XReport getReport(Integer reportId) {
		Query query = getCurrentSession().createQuery("from XReport where reportId = :reportId");
		query.setParameter("reportId", reportId);
		return (XReport) query.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#getReportGroup(java.lang.Integer)
	 */
	@Override
	public XReportGroup getReportGroup(Integer groupId) {
		Query query = getCurrentSession().createQuery("from XReportGroup where groupId = :groupId");
		query.setParameter("groupId", groupId);
		return (XReportGroup) query.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#saveReport(org.openmrs.module.xreports.XReport)
	 */
	@Override
	public XReport saveReport(XReport report) {
		getCurrentSession().save(report);
		return report;
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#saveReportGroup(org.openmrs.module.xreports.XReportGroup)
	 */
	@Override
	public XReportGroup saveReportGroup(XReportGroup group) {
		getCurrentSession().save(group);
		return group;
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#deleteReport(org.openmrs.module.xreports.XReport)
	 */
	@Override
	public void deleteReport(XReport report) {
		getCurrentSession().delete(report);
	}
	
	/**
	 * @see org.openmrs.module.xreports.api.db.XReportsDAO#deleteReportGroup(org.openmrs.module.xreports.XReportGroup)
	 */
	@Override
	public void deleteReportGroup(XReportGroup group) {
		getCurrentSession().delete(group);
	}
	
	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 * 
	 * @return the current hibernate session.
	 */
	private org.hibernate.Session getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.hibernate.Session)method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}

	@Override
	public List<String> getColumns(String sql) {
		
		List<String> columns = new ArrayList<String>();
		
		Connection con = getConnection();
		
		try {
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			ResultSetMetaData metadata = resultSet.getMetaData();

			for (int column = 1; column <= metadata.getColumnCount(); column++) {
				columns.add(metadata.getColumnLabel(column));
			}
		}
		catch (SQLException ex) {
			log.error("Failed to get column labels: " + ex.getMessage(), ex);
		}
		return columns;
	}
	
	private Connection getConnection() {
        try {
            // reflective lookup to bridge between Hibernate 3.x and 4.x
            Method connectionMethod = getCurrentSession().getClass().getMethod("connection");
            return (Connection) ReflectionUtils.invokeMethod(connectionMethod, getCurrentSession());
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Cannot find connection() method on Hibernate session", ex);
        }
    }
}
