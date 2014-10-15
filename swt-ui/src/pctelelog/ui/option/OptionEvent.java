package pctelelog.ui.option;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import pctelelog.events.EventType;
import pctelelog.ui.PreferenceKey;
import pctelelog.ui.PreferenceManager;

public class OptionEvent implements OptionInterface {
	private final TabFolder m_parentFolder;
	private final TabItem m_tabItem;
	
	public OptionEvent(TabFolder m_folder) {
		m_parentFolder = m_folder;
		m_tabItem = new TabItem(m_parentFolder, SWT.NONE);
		m_tabItem.setText("Notify Events");
		
		init();
	}
		
	/**
	 * Get the option state for this panel
	 * 
	 */
	public HashMap<PreferenceKey, Serializable> getOptions() {
		HashMap<PreferenceKey, Serializable> options = new HashMap<PreferenceKey, Serializable>();
		HashMap<EventType, Boolean> settings = getSelectionState();
		
		Set<EventType> keys = settings.keySet();
		for(EventType key : keys) {
			PreferenceKey prefKey = PreferenceKey.getKeyForEventType(key);
			if(prefKey == null) { continue; }
			
			Boolean val = settings.get(key);
			if(val == null) { continue; }
			options.put(prefKey, val);
		}
		
		return options;
	}
	
	/**
	 * Get the selection state for each event type
	 * 
	 * @return a hash map where each event type specifies whether it should be shown or not
	 */
	private HashMap<EventType, Boolean> getSelectionState() {
		HashMap<EventType, Boolean> selections = new HashMap<EventType, Boolean>();
		Group eventGroup = (Group) m_tabItem.getControl();
		for(Control control : eventGroup.getChildren()) {
			try {
				EventType type = (EventType) control.getData();
				if(type == null) continue;
				
				Button check = (Button)control;
				switch(type) {
					case INCOMING_CALL:
					case MISSED_CALL:
					case CLIENT_CONNECT:
						selections.put(type, Boolean.valueOf(check.getSelection()));
					default:
						break;
				}
			} catch (ClassCastException e) { continue; }
		}
		
		return selections;
	}
	
	private void init() {
		// Set up group
		Group eventGroup = new Group(m_parentFolder, SWT.NONE);
		eventGroup.setText("Event Notifications");
		eventGroup.setToolTipText("Events to show notifications for");
		m_tabItem.setControl(eventGroup);
		
		// Set Layout
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		eventGroup.setLayout(layout);
		
		// Preference Manager to get initial vals from
		PreferenceManager pref = PreferenceManager.getPreferenceManager();
		
		// Incoming Event
		Button incoming = new Button(eventGroup, SWT.CHECK);
		incoming.setText("Incoming Calls");
		incoming.setToolTipText("Show notifications for incoming calls");
		incoming.setData(EventType.INCOMING_CALL);
		incoming.setSelection((Boolean) pref.get(PreferenceKey.SHOW_INCOMING, Boolean.TRUE));
		
		// Missed Call event
		Button missed = new Button(eventGroup, SWT.CHECK);
		missed.setText("Missed Calls");
		missed.setToolTipText("Show notifications for missed calls");
		missed.setData(EventType.MISSED_CALL);
		missed.setSelection((Boolean) pref.get(PreferenceKey.SHOW_MISSED, Boolean.TRUE));
		
		// Connect Event
		Button connect = new Button(eventGroup, SWT.CHECK);
		connect.setText("Phone connect");
		connect.setToolTipText("Show notifications when phones connect");
		connect.setData(EventType.CLIENT_CONNECT);
		connect.setSelection((Boolean) pref.get(PreferenceKey.SHOW_CONNECT, Boolean.TRUE));
	}
}
