package pctelelog.internal.events;

import java.util.Date;

import pctelelog.events.AbstractEvent;
import pctelelog.events.EventType;

/**
 * An event fired when a Client socket has closed.
 * 
 * This event may be fired off accompanying a ShutdownEvent or it may
 * be fired off unexpectedly such as when a client loses internet connection.
 * 
 * Upon receiving this event, the socket will have been closed.
 * It is up to UI to handle this Event and remove the client from the
 * 	EventOperator, which will remove it from the client pool.
 * @author Jeremy May
 *
 */
public class ClientSocketClosedEvent extends AbstractEvent {
	private static final EventType event_type = EventType.CLIENT_SOCKET_CLOSE;
	
	public ClientSocketClosedEvent() {
		super(event_type, new Date());
	}
}
