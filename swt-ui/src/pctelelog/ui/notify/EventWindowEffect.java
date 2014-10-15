package pctelelog.ui.notify;

import pctelelog.ui.PreferenceKey;
import pctelelog.ui.PreferenceManager;

public abstract class EventWindowEffect implements Runnable {

	/** 
	 * How long the notification should
	 * show for, in milliseconds, before
	 * it disappears.
	 *  
	 */
	protected int SHOW_TIME = 4000;
	
	/**
	 * The event window associated with the effect
	 */
	private final EventWindow m_eventDisplay;
	
	/**
	 * The effect type
	 */
	protected EffectType m_type = EffectType.STANDARD;
	
	public EventWindowEffect(final EventWindow eventDisplay) {
		m_eventDisplay = eventDisplay;
		SHOW_TIME = getCurrentShowTime();
	}
	
	public EventWindow getEventWindow() {
		return m_eventDisplay;
	}
	
	public EffectType getEffectType() {
		return m_type;
	}
	
	abstract public void run();

	private int getCurrentShowTime() {
		PreferenceManager prefManager = PreferenceManager.getPreferenceManager();
		String timeStr = (String) prefManager.get(PreferenceKey.SHOW_TIME, "4000");
		
		int time = 4000;
		try {
			time = Integer.valueOf(timeStr);
		} catch(NumberFormatException e) { 
			e.printStackTrace();
		}
		
		return time;
	}
}
