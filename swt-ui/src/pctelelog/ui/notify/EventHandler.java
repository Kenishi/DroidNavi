package pctelelog.ui.notify;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import pctelelog.EventListener;
import pctelelog.events.AbstractCallEvent;
import pctelelog.events.AbstractEvent;
import pctelelog.events.ClientConnectEvent;
import pctelelog.events.IncomingCallEvent;
import pctelelog.events.MissedCallEvent;

public class EventHandler implements EventListener {
	private final Logger logger = LogManager.getLogger(EventHandler.class);
	
	private final Display m_display;
	
	public EventHandler(Display display) {
		if(display == null) { throw new NullPointerException("Display cannot be null"); }
		m_display = display;
	}
	
	public void onEvent(final AbstractEvent event) {
		getMainDisplay().asyncExec(new Runnable() {			
			public void run() {
				handleEvent(event);
			}
		});
	}
	
	private void handleEvent(AbstractEvent event) {
		logger.entry(event);
		if(event instanceof ClientConnectEvent) {
			connect(event);
		}
		else if(event instanceof IncomingCallEvent) {
			AbstractCallEvent cast = (AbstractCallEvent)event;
			incomingCall(cast);
		}
		else if(event instanceof MissedCallEvent) {
			AbstractCallEvent cast = (AbstractCallEvent)event;
			missedCall(cast);
		}
		logger.exit();
	}
		
	private void incomingCall(AbstractCallEvent event) {
		EventWindow eventWindow = EventWindow.call(getMainDisplay(), event);
		
		EventWindowEffect effect = eventWindow.getEffect();
		show(effect);
	}
	
	private void missedCall(AbstractCallEvent event) {
		EventWindow eventWindow = EventWindow.call(getMainDisplay(), event);
		
		EventWindowEffect effect = eventWindow.getEffect();
		show(effect);
	}
	
	private void connect(AbstractEvent event) {			
		EventWindow eventWindow = EventWindow.connect(getMainDisplay());
		
		EventWindowEffect effect = eventWindow.getEffect();
		show(effect);
	}
	
	private void show(final EventWindowEffect effect) {
		getMainDisplay().syncExec(effect);
	}
	
	
	private Display getMainDisplay() { 
		return m_display;
	}
}
