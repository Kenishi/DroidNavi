package pctelelog.events;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

import pctelelog.ContactInfo;

public class CallEndedEvent extends AbstractCallEvent {
	static final private EventType event_type = EventType.CALL_ENDED;
	
	public CallEndedEvent(@JsonProperty("time")Date time, 
			@JsonProperty("info") ContactInfo info) {
		super(event_type, time, info);
	}
}
