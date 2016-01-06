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

import org.openmrs.module.xreports.XReportGroup;

public class XReportGroupAndDepth {

	private int depth;
	
	private XReportGroup group;
	
	public XReportGroupAndDepth() {
		
	}
	
	/**
	 * @param depth
	 * @param group
	 */
	public XReportGroupAndDepth(int depth, XReportGroup group) {
		this.depth = depth;
		this.group = group;
	}
	
	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}
	
	/**
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
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
}
