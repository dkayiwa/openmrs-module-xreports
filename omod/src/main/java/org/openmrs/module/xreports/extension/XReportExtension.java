package org.openmrs.module.xreports.extension;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.InvalidCustomValueException;
import org.openmrs.module.appframework.domain.Extension;

public class XReportExtension implements CustomDatatype<Extension> {

	@Override
    public void setConfiguration(String config) {	    
    }

	@Override
    public String save(Extension typedValue, String existingValueReference) throws InvalidCustomValueException {
	  	return getReferenceStringForValue(typedValue);
    }

	@Override
    public String getReferenceStringForValue(Extension typedValue) throws UnsupportedOperationException {
		ObjectMapper mapper = new ObjectMapper();
	  	try {
	        return mapper.writeValueAsString(typedValue);
        }
        catch (IOException e) {
        	throw new InvalidCustomValueException("Cannot convert extensions to JSON", e);
        }
    }

	@Override
    public Extension fromReferenceString(String referenceString) throws InvalidCustomValueException {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
	        return mapper.readValue(referenceString, Extension.class);
        }
        catch (IOException e) {
        	throw new InvalidCustomValueException("Cannot convert JSON to extensions: " + referenceString, e);
        }
    }

	@Override
    public org.openmrs.customdatatype.CustomDatatype.Summary getTextSummary(String referenceString) {
	    return new Summary(referenceString, true);
    }

	@Override
    public void validate(Extension typedValue) throws InvalidCustomValueException {
		//no validation needed
    }
}
