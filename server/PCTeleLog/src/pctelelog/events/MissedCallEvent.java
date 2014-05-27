package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

import pctelelog.ContactInfo;

public class MissedCallEvent extends AbstractCallEvent {
	
	static final private EventType event_type = EventType.MISSED_CALL;
	
	public MissedCallEvent(@JsonProperty("time")Date time, 
			@JsonProperty("info") ContactInfo info) {
		super(event_type, time, info);
	}
}
