package com.aberdyne.androidtelelog.client;

import pctelelog.events.AbstractEvent;

/**
 * A ServerConnection that will not connect to the supplied IP.
 * 
 * This class is useful in cases where the state may need to be updated,
 * 	such as when the server needs to be removed from pairing, and the
 * 	app may already be connected to the server.
 *  
 * @author Jeremy May
 * 
 */
public class NonActiveServerConnection extends ServerConnection {

	public NonActiveServerConnection(String ip) {
		super(ip, false);
	}
	
	@Override
	public boolean sendEvent(AbstractEvent event) { return false; }
	
	@Override
	public boolean sendEvent(String json) { return false; }

	@Override
	public void shutdown(boolean sendShutDownEvent) {}
	
	
}
