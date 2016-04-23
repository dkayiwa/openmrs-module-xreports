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
package org.openmrs.module.xreports;

import java.io.Serializable;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or
 * {@link BaseOpenmrsMetadata}.
 */
public class XReport extends BaseOpenmrsMetadata implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer reportId;
	
	private String identifier;
	
	private String xml;
	
	private XReportGroup group;
	
	private String externalReportUuid;
	
	private Integer displayOrder;
	
	/**
	 * @return the reportId
	 */
	public Integer getReportId() {
		return reportId;
	}
	
	/**
	 * @param reportId the reportId to set
	 */
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}
	
	/**
	 * @param xml the xml to set
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	/**
	 * @return the group
	 */
	public XReportGroup getGroup() {
		return group;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(XReportGroup group) {
		this.group = group;
	}

    /**
     * @return the externalReportUuid
     */
    public String getExternalReportUuid() {
    	return externalReportUuid;
    }
	
    /**
     * @param externalReportUuid the externalReportUuid to set
     */
    public void setExternalReportUuid(String externalReportUuid) {
    	this.externalReportUuid = externalReportUuid;
    }
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getReportId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer arg0) {
		setReportId(arg0);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
