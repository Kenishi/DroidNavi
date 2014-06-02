package pctelelog;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pctelelog.events.AbstractEvent;
import pctelelog.events.HeartBeat;
import pctelelog.internal.events.ClientSocketClosedEvent;
import py4j.Py4JException;

public class EventOperator {
	
	private Logger logger = LogManager.getLogger(EventOperator.class);
	
	private CopyOnWriteArrayList<EventListener> m_listeners = new CopyOnWriteArrayList<EventListener>();
	private ClientPool m_pool = new ClientPool();
	
	/* Shutdown flag is used to keep the
	 * operator from dispatching events when shutting down.
	 * Not catching this will cause the server to spend 5-10sec
	 * longer shutting down rather than immediately.
	 */
	private boolean isShutdown = false; 
	
	/**
	 * Add/Register the event listener to receive events.
	 * @param listener An event listener to add
	 * @return True if the listener was added. False if it was not (ie: Already added).
	 */
	public boolean addEventListener(EventListener listener) {
		if(m_listeners.addIfAbsent(listener)) {
			logger.info("Event Listener added");
			return true;
		}
		return false;
	}
	
	/**
	 * Remove/Unregister the event listener from the pool
	 * @param listener An event listener to remove
	 * @return True if a listener was removed. False if it was not (ie: Not present).
	 */
	public boolean removeEventListener(EventListener listener) {
		if(m_listeners.remove(listener)) {
			logger.info("Event Listener removed.");
			return true;
		}
		return false;
	}
	
	public Client[] getClientsInPool() {
		return m_pool.getPool();
	}
	
	protected void onEvent(final Client client, AbstractEvent event) {
		if(event == null || client == null)  {
			return;
		}
		
		if(event instanceof ClientSocketClosedEvent) {
			m_pool.removeClient(client);
		}
		else if(event instanceof HeartBeat) {  // Do nothing with heartbeats
			return; 
		}
		event = EventDeviceResolver.resolveDevice(client, event);
		dispatchEvent(event);
	}
	
	protected synchronized void addClient(Client sock) {
		m_pool.addClient(sock);
	}
	
	protected synchronized void removeClient(Client client) {
		m_pool.removeClient(client);
	}
	
	/**
	 * Shuts down the Event Operator 
	 * 
	 */
	protected synchronized void shutdown() {
		isShutdown = true;
		// Shutdown all sockets
		m_pool.shutdown();
	}
	
	private void dispatchEvent(AbstractEvent event) {
		if(isShutdown) return;
		
		if(event != null) {
			for(EventListener listener : m_listeners) {
				try {
					listener.onEvent(event);
				} catch(Py4JException e) {
					logger.catching(e);
					continue;
				}
			}
		}
	}
	

}
