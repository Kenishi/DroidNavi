package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class HeartBeat extends AbstractEvent {
	private static final EventType event_type = EventType.HEARTBEAT;
	
	public HeartBeat() {
		super(event_type, new Date());
	}
	
	@JsonCreator
	public HeartBeat(@JsonProperty("time") Date time) {
		super(event_type, time);
	}
}
