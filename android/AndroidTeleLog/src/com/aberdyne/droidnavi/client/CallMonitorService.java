package com.aberdyne.droidnavi.client;

import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class CallMonitorService extends Service {
	private EventDispatchThread m_eventDispatchThread = null;
	private PhoneStateListener m_stateListener = null;
	
	/* Service Status */
	private static boolean m_isRunning = false;
	
	/* Static Timer Variables for Unread Missed Calls Checking */
	private Timer m_timer = null;
	private UnreadMissedCallTimer m_checkMissCallsTask = null;
	private final long CHECK_MISS_CALL_PERIOD = 1000 * 30 * 1; // Every 30 sec. 
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Event and State handling
		m_eventDispatchThread = new EventDispatchThread(this);
		m_stateListener = new PhoneStateListener(m_eventDispatchThread, getContentResolver());
		
		// Setup timer to check unread Missed calls
		m_timer = new Timer(true);
		m_checkMissCallsTask = new UnreadMissedCallTimer(this, m_stateListener);
		m_timer.schedule(m_checkMissCallsTask, 0, CHECK_MISS_CALL_PERIOD);
		 
		m_eventDispatchThread.start();
		
		// Setup intent filter and register the phone call receiver to handle events
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PHONE_STATE");
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(m_stateListener, filter);
		
		m_isRunning = true;
	}
	
	@Override
	public void onDestroy() {
		m_isRunning = false;
		
		// Stop Unread Miss Call Timer
		m_timer.cancel();
		m_timer = null;
		m_checkMissCallsTask = null;
		
		// Unregister the Call's receivers
		unregisterReceiver(m_stateListener);
		
		// Shutdown dispatch thread
		m_eventDispatchThread.quit();
		
		super.onDestroy();
	}
	
	/**
	 * Return whether the service is running or not
	 * @return True if it is, False if it isn't
	 */
	public static boolean isRunning() {
		return m_isRunning;
	}

}
