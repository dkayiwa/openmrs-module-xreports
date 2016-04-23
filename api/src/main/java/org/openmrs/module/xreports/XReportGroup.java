package org.openmrs.module.xreports;

import java.io.Serializable;

import org.openmrs.BaseOpenmrsMetadata;

public class XReportGroup extends BaseOpenmrsMetadata implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer groupId;
	
	private String identifier;
	
	private XReportGroup parentGroup;
	
	private Integer displayOrder;
	
	public XReportGroup() {
		
	}
	
	public XReportGroup(Integer groupId) {
		setGroupId(groupId);
	}
	
	/**
	 * @return the groupId
	 */
	public Integer getGroupId() {
		return groupId;
	}
	
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
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
	 * @return the parentGroup
	 */
	public XReportGroup getParentGroup() {
		return parentGroup;
	}
	
	/**
	 * @param parentGroup the parentGroup to set
	 */
	public void setParentGroup(XReportGroup parentGroup) {
		this.parentGroup = parentGroup;
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
	    return getGroupId();
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer arg0) {
	    setGroupId(arg0);
    }
    
    @Override
	public String toString() {
		return getName();
	}
}
