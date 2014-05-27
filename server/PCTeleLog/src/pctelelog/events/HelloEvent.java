package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class HelloEvent extends AbstractEvent {
	
	private static final EventType event_type = EventType.HELLO;
	
	public HelloEvent(@JsonProperty("time") Date time) {
		super(event_type, time);
	}
}
