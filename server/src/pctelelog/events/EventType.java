package pctelelog.events;

import org.codehaus.jackson.annotate.JsonIgnoreType;

@JsonIgnoreType
public enum EventType {
	/**
	 * Sent by the Client to notify connection
	 */
	CLIENT_CONNECT,
	
	/**
	 * A missed call
	 */
	MISSED_CALL,
	
	/**
	 * An incoming call, ie: The phone is ringing.
	 */
	INCOMING_CALL,
	
	/**
	 * A (incoming) call has ended
	 */
	CALL_ENDED,
	
	/**
	 * Client is disconnecting. 
	 */
	SHUTDOWN,
	
	/* Internal App or Protocol Events */
	/**
	 * Handshake event. Shouldn't be handled
	 */
	HELLO,
	
	/**
	 * Fired by the Java server in instances that it has to shutdown.
	 * This event would likely require the GUI to have to restart.
	 */
	SERVERSHUTDOWN, // Fired to EventListeners when server is shutting down
	
	/**
	 * Fired when a socket has closed (possibly unexpectedly)
	 */
	CLIENT_SOCKET_CLOSE // Fired when a client socket closes
}