package pctelelog;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;

@JsonTypeInfo(use=Id.CLASS, include=As.WRAPPER_OBJECT)
public class PhoneNumber {
	@JsonProperty("number") private String m_number = null;
	
	@JsonCreator
	public PhoneNumber(@JsonProperty("number") String number) {
		this.m_number = number;
	}
	
	public String getNumber() {
		return m_number;
	}
	
	@Override
	public String toString() {
		/*
		 * Returns the phone number as a hyphenated phone number
		 */

		return this.m_number;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		try {
			PhoneNumber number = PhoneNumber.class.cast(obj);
			
			// Compare numbers if both have one
			if(number.getNumber() != null && number.getNumber() != null) {
				if(! number.getNumber().equals(this.getNumber())) {
					return false;
				}
			}
			else if(number.getNumber() != this.getNumber()) { // Check if matching nulls
				return false;
			}
		} catch(ClassCastException e) {
			return false;
		}
		
		return true;
	}
}
