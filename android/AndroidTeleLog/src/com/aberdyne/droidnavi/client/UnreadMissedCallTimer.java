package com.aberdyne.droidnavi.client;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

/**
 * A TimerTask for checking for unread new missed calls.
 * This will dispatch an event every time until the calls are checked
 * 	and no longer considered "new." 
 * @author Kei
 *
 */
public class UnreadMissedCallTimer extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(UnreadMissedCallTimer.class);
	
	private static Context m_context = null;
	private static PhoneStateListener m_stateListener = null;
	
	public UnreadMissedCallTimer(Context context, PhoneStateListener phoneStateListener) {
		logger.info("ENTRY UnreadMissedCallTimer constructor.");
		if(context == null) {
			throw new NullPointerException("Context cannot be null.");
		}
		if(phoneStateListener == null) {
			throw new NullPointerException("PhoneStateListener cannot be null.");
		}
		
		m_context = context;
		m_stateListener = phoneStateListener;
		logger.info("EXIT UnreadMissedCallTimer constructor.");
	}
	
	@Override
	public void run() {
		logger.info("ENTRY UnreadMissedCallTimer.run");
		ContentResolver resolver = m_context.getContentResolver();
		
		String[] projection = { CallLog.Calls.CACHED_NAME,
				CallLog.Calls.CACHED_NUMBER_LABEL,
				CallLog.Calls.TYPE,
				CallLog.Calls.NUMBER,
				CallLog.Calls.DATE };
		
		String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + "=1";        
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, where, null, null);
		
		// Only continue if we have missed calls
		if(cursor.moveToFirst()) {
			int numberIdx = cursor.getColumnIndex(CallLog.Calls.NUMBER);
			int cache_numberIdx = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL);
			int dateIdx = cursor.getColumnIndex(CallLog.Calls.DATE);
			
			do {
				String number = cursor.getString(numberIdx);
				String cache_number = cursor.getString(cache_numberIdx);
				Date date = new Date(cursor.getLong(dateIdx));
				
				// Determine which kind of number we have, or fail with "UNKNOWN"
				if(number.equals(cache_number)) {
					m_stateListener.onUnreadMissedCall(number, date, m_context);
				}  
				else if(number != null && number.length() > 0) {
					m_stateListener.onUnreadMissedCall(number, date, m_context);
				} // Cached number, name will be "UNKNOWN"
				else if(cache_number != null && cache_number.length() > 0) {
					m_stateListener.onUnreadMissedCall(cache_number, date, m_context);
				}
				else {
					m_stateListener.onUnreadMissedCall("UNKNOWN", date, m_context);
				}
			} while(cursor.moveToNext());
		}
		cursor.close();
		logger.info("EXIT UnreadMissedCallTimer.run");
	}

}
