package org.openmrs.module.xreports;

import java.util.ArrayList;
import java.util.List;


public class ReportParameter {
	
	public static final String DT_TEXT = "Text";
	public static final String DT_NUMBER = "Number";
	public static final String DT_DATE = "Date";
	
	private String name;
	private String description;
	private String dataType;
	private String binding;
	private List<NameValue> values = new ArrayList<NameValue>();
	
    /**
     * @return the name
     */
    public String getName() {
    	return name;
    }
	
    /**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }
	
    /**
     * @return the description
     */
    public String getDescription() {
    	return description;
    }
	
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
    	this.description = description;
    }
	
    /**
     * @return the dataType
     */
    public String getDataType() {
    	return dataType;
    }
	
    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
    	this.dataType = dataType;
    }
	
    /**
     * @return the binding
     */
    public String getBinding() {
    	return binding;
    }
	
    /**
     * @param binding the binding to set
     */
    public void setBinding(String binding) {
    	this.binding = binding;
    }
	
    /**
     * @return the values
     */
    public List<NameValue> getValues() {
    	return values;
    }
	
    /**
     * @param values the values to set
     */
    public void setValues(List<NameValue> values) {
    	this.values = values;
    }
    
    public void addValue(NameValue value) {
    	values.add(value);
    }
}
