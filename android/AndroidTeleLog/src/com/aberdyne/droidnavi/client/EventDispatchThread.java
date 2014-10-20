package com.aberdyne.droidnavi.client;

import java.util.Stack;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.aberdyne.droidnavi.client.ServerListManager.ServerListListener;

import pctelelog.events.AbstractEvent;
import pctelelog.events.MissedCallEvent;

public class EventDispatchThread extends Thread implements ServerListListener {
	
	/* Logger */
	private static final Logger logger = LoggerFactory.getLogger(EventDispatchThread.class);
	
	/* 
	 * Server connection sets
	 * m_connectedServers: servers currently connected
	 * m_standbyServers: servers that are not connected and cannot receive events
	 */
	private CopyOnWriteArraySet<ServerConnection> m_connectedServers = 
			new CopyOnWriteArraySet<ServerConnection>();
	private CopyOnWriteArraySet<ServerConnection> m_standbyServers =
			new CopyOnWriteArraySet<ServerConnection>();
	
	/*
	 * Event queue
	 */
	private Stack<AbstractEvent> m_queue = new Stack<AbstractEvent>();
	
	/* Thread Variables */
	private volatile boolean isStop = false;
	private long SLEEP_TIMEOUT = 5 * 1000; // 5 second
	
	/* Application Variables */
	private Context m_context = null;
	
	/*
	 * Sends the events and helps decide between multicast or direct-server
	 */
	private NetworkDispatch m_networkDispatch = null;
	
	public EventDispatchThread(Context context) {
		if(context == null) {
			throw new NullPointerException();
		}
		
		m_context = context;
		m_networkDispatch = new NetworkDispatch(m_context);
		ServerListManager.addServerListListener(this);
		ServerListManager.getSync(m_context);
	}
	
	@Override
	public void run() {
		logger.trace("ENTRY EventDispatchThread.run");
		
		CheckServerThread checkServerThread = new CheckServerThread(this);
		while(!isStop) {
			if(!m_networkDispatch.hasMulticast()) {
				// Check standby servers if we aren't currently
				if(! checkServerThread.isAlive()) { 
					checkServerThread = new CheckServerThread(this);
					checkServerThread.start();
				}
			}
			
			if(m_queue.size() == 0 || !canDispatchToNetwork()) {
				try {
					sleep(SLEEP_TIMEOUT);
				} catch(InterruptedException e) {}
				continue;
			}
			
			// Get an event
			AbstractEvent event = dequeueEvent();
			if(event == null) {
				continue;
			}
			
			// Dispatch event
			if(m_networkDispatch.hasMulticast()) {
				boolean result = m_networkDispatch.sendEvent(event);
				
				if(result) {
					logger.debug("Event dispatch success (multi): " + event.getEventType().toString());
				}
				else {
					logger.debug("Event dispatch fail (multi): " + event.getEventType().toString());	
				}
			}
			else {
				/*
				 * Attempt to send to multicast relay server.
				 * Loop will break on first success, no need to spam the network
				 * with multiple multicasts.
				 */
				for(ServerConnection server : m_connectedServers) {
					boolean result = m_networkDispatch.sendEvent(event, server);
					if(result == false) {
						logger.debug("Event dispatch fail (tcp): " + event.getEventType().toString());
						// Move to standby
						removeConnectedServer(server);
						addStandByServer(server);
						
						// Let others know of the change
						ServerListManager.updateServer(m_context, server);
					}
					else {
						logger.debug("Event dispatch success (tcp): " + event.getEventType().toString());
						break;
					}
				}
			}
		}
		
		if(checkServerThread != null && checkServerThread.isAlive()) {
			checkServerThread.quit();
		}
		setAllToStandby();
		ServerListManager.removeServerListListener(this);
		logger.trace("EXIT EventDispatchThread.run");
	}
	
	/**
	 * Set the thread to exit.
	 * 
	 * Any existing events in queue will NOT be sent
	 */
	public void quit() {
		isStop = true;
		this.interrupt();
	}
	
	/**
	 * Dispatch an event to servers.
	 * 
	 * Duplicate events won't be added.
	 * 
	 * Call events will not be dispatched if there are no currently
	 * 	connected servers.
	 * 
	 * Unread Missed calls will however be added to the queue regardless
	 * 	of connected servers and will be dispatched once servers connect.
	 * 
	 * @param event An event to dispatch to waiting servers.
	 */
	public synchronized void dispatchEvent(AbstractEvent event) {
		if(canDispatchToNetwork() && !m_queue.contains(event)) {
			logger.info("Event added to dispatch queue: {}", event);
			m_queue.add(event);
			this.interrupt();
		}
		else if((event instanceof MissedCallEvent) && !m_queue.contains(event)) {
			logger.info("Unread Missed Call Event added to dispatch queue: {}", event);
			m_queue.add(event);
		}
		else {
			int size = -1;
			if(m_connectedServers != null) {
				size = m_connectedServers.size();
			}
			logger.debug("Event not dispatched. Connected: " + Integer.toString(size) +
					" Contains: " + Boolean.toString(m_queue.contains(event)));
		}
	}
	
	/**
	 * Global Server List Changed
	 * 
	 * In instances where REMOVE is the action, the supplied ServerConnection will not be
	 * 	a valid connected socket and cannot be used directly to disconnect the server.
	 * 	An equals() should be called on every server to compare the two objects and find
	 * 	the currently active server to remove.
	 * 
	 * @param action An action event that took place
	 * @param server A server connection related to the action.
	 */
	public void onServerListChange(Action action, final ServerConnection server) {
		logger.debug("EventDspatch:ServerList Event: {} {}", action, server);
		switch(action) {
		case SYNC:
		case ADD:
			if(server.getStandbyStatus())
				addStandByServer(server);
			else
				addConnectedServer(server);
			break;
		case REMOVE:
			if(m_connectedServers.contains(server)) { // Connected servers must be shutdown() first
				 for(ServerConnection connection : m_connectedServers) {
					 if(connection.equals(server)) {
						 connection.shutdown(true);
						 break;
					 }
				 }
				 removeConnectedServer(server);
			}
			else if(removeStandByServer(server));
			else {
				logger.error("Failed to remove: {}", server.toString());
			}
			break;	
		default:
			break; // This class issues UPDATEs, action already handled.
		}
		this.interrupt();
	}
	
	/**
	 * Check if an event could be dispatched right now.
	 * 
	 * If multicast is available then this always returns true.
	 * May return true or false depending on current server connections.
	 * 
	 * @return True if a message could be dispatch. False if not.
	 */
	private boolean canDispatchToNetwork() {
		if(m_networkDispatch.hasMulticast()) {
			return true;
		}
		else if(m_connectedServers.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Shutdown all servers and move to standby
	 * 
	 * Called as thread is exiting run()
	 */
	private void setAllToStandby() {
		logger.trace("ENTRY EventDispatchThread.setAllToStandby");
		for(ServerConnection server : m_connectedServers) {
			server.shutdown(true); // Shutdown and send ShutdownEvent
			removeConnectedServer(server);
			addStandByServer(server);
			ServerListManager.updateServer(m_context, server);
		}
		logger.trace("ENTRY EventDispatchThread.setAllToStandby");
	}
	
	/**
	 * Adds a server to the standby set.
	 * 
	 * Servers on standby are not connected but will be checked occasionally
	 * 	to try and reconnect to them.
	 * 
	 * This method will not verify connected state.
	 * @param server A server that isn't connected
	 * @return True if the server was added. False if it wasn't. 
	 */
	private boolean addStandByServer(ServerConnection server) {
		if(m_standbyServers.add(server)) {
			logger.info("Set Standby: " + server.toString());
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a server from the standby set.
	 * 
	 * Servers being removed SHOULD be connected.
	 * 
	 * This method will not verify connected state.
	 * @param server A connected server
	 * @return True if a server was removed. False if it wasn't.
	 */
	private boolean removeStandByServer(ServerConnection server) {
		if(m_standbyServers.remove(server)) {
			logger.info("UnSet Standby: " + server.toString());
			return true;
		}
		return false;
	}

	/**
	 * Add a server to the connected set putting it into the event dispatch
	 * 	rotation.
	 * 
	 * Servers added to this list should be connected and part of the global 
	 * 	server list.
	 * 
	 * This method will not verify connected state.
	 * @param server
	 * @return True if the server was added. False if it wasn't.
	 */
	private synchronized boolean addConnectedServer(ServerConnection server) {
		if(m_connectedServers.add(server)) {
			this.interrupt();
			logger.info("Active Server added: " + server.toString());
			return true;
		}
		return false;
	}
	
	/**
	 * Remove a server from the connected set, removing it from the event
	 * 	dispatch rotation.
	 * 
	 * Servers removed should either be on standby or being removed from the
	 * 	global list. 
	 * @param server
	 * @return
	 */
	private synchronized boolean removeConnectedServer(ServerConnection server) {
		if(m_connectedServers.remove(server)) {
			logger.info("Active Server removed: " + server.toString());
			return true;
		}
		return false;
	}
	
	private AbstractEvent dequeueEvent() {
		AbstractEvent event;
		try {
			event = m_queue.remove(0);
		} catch(ArrayIndexOutOfBoundsException e) {
			event = null;
		}
		return event;
	}
	
	/**
	 * A small thread to check the servers to see 
	 * 	if they are active or can be connected to now.
	 * 
	 * Due to the blocking nature of connect() testing for connection
	 * 	needs to be done inside a thread otherwise it can interfere
	 * 	with message dispatch responsiveness.
	 * 
	 * @author Jeremy May
	 *
	 */
	private class CheckServerThread extends Thread {
		private Thread m_parent = null;
		private volatile boolean isStop = false;
		
		public CheckServerThread(Thread parent) {
			m_parent = parent;
		}
		@Override
		public void run() {
			checkServers();
			
			// Interrupt dispatch, it could be sleeping.
			if(m_parent != null && m_parent.isAlive()) {
				m_parent.interrupt();
			}
		}
		
		public void quit() {
			isStop = true;
			this.interrupt();
		}
		/**
		 * Check servers on standby to see if they have come online.
		 * Move the server from standby to connected.
		 */
		private void checkServers() {
			logger.debug("Checking active servers");
			for(ServerConnection server : m_connectedServers) {
				// Move connected servers that fail heartbeat, let App know
				if(!server.heartbeat()) {
					removeConnectedServer(server);
					addStandByServer(server);
					
					ServerListManager.updateServer(m_context, server);
				}
			}
			
			logger.debug("Checking standby servers.");
			for(ServerConnection server : m_standbyServers) {
				if(isStop) break;
				boolean result = server.connect();
				if(isStop) break;
				
				// Swap standby to connected, let App know
				if(result == true) {
					removeStandByServer(server);
					addConnectedServer(server);
					
					ServerListManager.updateServer(m_context, server);
				}
			}
		}
	};
}
