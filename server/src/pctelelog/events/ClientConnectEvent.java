package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class ClientConnectEvent extends AbstractEvent {
	static final private EventType event_type = EventType.CLIENT_CONNECT;
	
	public ClientConnectEvent(@JsonProperty("time") Date time) {
		super(event_type, time);
		// TODO Auto-generated constructor stub
	}
	
}
