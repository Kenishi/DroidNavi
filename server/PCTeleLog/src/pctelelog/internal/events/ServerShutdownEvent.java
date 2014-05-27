package pctelelog.internal.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

import pctelelog.events.AbstractEvent;
import pctelelog.events.EventType;

/**
 * The class event for when the server is shutting down.
 * 
 * @author Jeremy May
 *
 */
public class ServerShutdownEvent extends AbstractEvent {
	private static EventType event_type = EventType.SERVERSHUTDOWN;

	protected ServerShutdownEvent(@JsonProperty("time") Date time) {
		super(event_type, time);
	}
	
	
}
