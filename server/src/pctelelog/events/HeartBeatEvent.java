package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class HeartBeatEvent extends AbstractEvent {
	private static final EventType event_type = EventType.HEARTBEAT;
	
	public HeartBeatEvent() {
		super(event_type, new Date());
	}
	
	@JsonCreator
	public HeartBeatEvent(@JsonProperty("time") Date time) {
		super(event_type, time);
	}
}
