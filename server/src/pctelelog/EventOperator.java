package pctelelog;

import io.netty.channel.socket.DatagramChannel;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pctelelog.events.AbstractEvent;
import pctelelog.events.HeartBeatEvent;
import py4j.Py4JException;

public class EventOperator {
	
	private Logger logger = LogManager.getLogger(EventOperator.class);
	
	private static final EventOperator instance = new EventOperator();
	private CopyOnWriteArrayList<EventListener> m_listeners = new CopyOnWriteArrayList<EventListener>();
	private DatagramChannel m_multi = null;
	
	/* Shutdown flag is used to keep the
	 * operator from dispatching events when shutting down.
	 * Not catching this will cause the server to spend 5-10sec
	 * longer shutting down rather than immediately.
	 */
	private boolean isShutdown = false; 
	
	public static EventOperator instance() {
		return instance;
	}
	
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
	
	public void setMultiCast(DatagramChannel ch) {
		m_multi = ch;
	}
	
	protected void onEvent(AbstractEvent event, boolean sendMulti) {
		if(event == null)  {
			return;
		}
		else if(event instanceof HeartBeatEvent) {
			return;
		}
		
		if(sendMulti && m_multi != null) { // Relay to multicast
			m_multi.writeAndFlush(event);
		}
		
		dispatchEvent(event);
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
