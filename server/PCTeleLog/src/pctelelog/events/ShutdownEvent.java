package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A ShutdownEvent may occur in 2 situations.
 * 1) When the event service on the remote device is shutting down.
 * 2) When the server is shutting down and wants to inform all listeners that
 * 		the event dispatch thread is shutting off.
 * 		In this instance, a call to getDevice() will return Device.NO_DEVICE
 * 
 * @author Kei
 *
 */
public class ShutdownEvent extends AbstractEvent {
	static final private EventType event_type = EventType.SHUTDOWN;
	
	public ShutdownEvent(@JsonProperty("time") Date time) {
		super(event_type, time);
	}
}
