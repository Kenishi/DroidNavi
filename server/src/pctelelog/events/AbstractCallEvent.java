package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

import pctelelog.ContactInfo;

public class AbstractCallEvent extends AbstractEvent {
	
	@JsonProperty("info") private ContactInfo m_info = null;
		
	public AbstractCallEvent(EventType type, Date time, ContactInfo info) { 
		super(type, time);
		m_info = info; 
	}
	
	@JsonProperty("info")
	public ContactInfo getContactInfo() {
		return m_info;
	}
	
	@Override
	public String toString() {
		String str = "";
		str = super.toString() + ": " + m_info.toString();

		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		try {
			AbstractCallEvent event = AbstractCallEvent.class.cast(obj);
			
			// Compare contact info if there is one
			if(event.getContactInfo() != null && this.getContactInfo() != null) {
				if(! event.getContactInfo().equals(this.getContactInfo())) {
					return false;
				}
			}
			// Check if matching nulls
			else if(event.getContactInfo() != this.getContactInfo()) { 
				return false;
			}
		} catch(ClassCastException e) {
			return false;
		}
		
		return super.equals(obj);
	}
}
