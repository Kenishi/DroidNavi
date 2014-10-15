package pctelelog.ui;

import java.io.Serializable;

import pctelelog.events.EventType;
import pctelelog.ui.notify.EventWindowEffect;
import pctelelog.ui.notify.WindowLocation;

public enum PreferenceKey {
	/** Event settings **/
	SHOW_INCOMING("show_incoming", Boolean.class),
	SHOW_MISSED("show_missed", Boolean.class),
	SHOW_CONNECT("show_connect", Boolean.class),
	
	/** General Notify Effect settings **/
	EFFECT_TYPE("notify_effect", EventWindowEffect.class),
	SHOW_TIME("notify_show_time", Integer.class),
	WINDOW_LOCATION("event_window_location", WindowLocation.class),
	
	/** Fade Settings Key **/
	FADE_SPEED("fade_speed", Integer.class),
	
	/** Main Window Settings **/
	WINDOW_X("window_x", Integer.class),
	WINDOW_Y("window_y", Integer.class);
	
	private String m_key = null;
	private Serializable m_class = null;
	
	private PreferenceKey(String k, Serializable clazz) {
		m_key = k;
		m_class = clazz;
	}
	
	public String getKey() {
		return m_key;
	}
	
	public Serializable getValueClass() {
		return m_class;
	}
	
	public static PreferenceKey getKeyFromString(String str) {
		PreferenceKey[] keys = PreferenceKey.values();
		for(PreferenceKey key : keys) {
			if(key.getKey().contains(str)) {
				return key;
			}
		}
		return null;
	}
	
	public static PreferenceKey getKeyForEventType(EventType type) {
		switch(type) {
			case INCOMING_CALL:
				return PreferenceKey.SHOW_INCOMING;
			case MISSED_CALL:
				return PreferenceKey.SHOW_MISSED;
			case CLIENT_CONNECT:
				return PreferenceKey.SHOW_CONNECT;
			default:
				return null;
		}
	}
}
