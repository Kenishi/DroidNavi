package com.aberdyne.droidnavi.client;

import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import pctelelog.ContactInfo;
import pctelelog.events.CallEndedEvent;
import pctelelog.events.IncomingCallEvent;
import pctelelog.events.MissedCallEvent;

public class PhoneStateListener extends PhonecallReceiver {

	private EventDispatchThread m_dispatchThread = null;
	
	/**
	 * Default Constructor
	 * @param dispatch
	 * @param resolver A content resolver from a context.
	 * 		this is only used during initialization to check missed calls.
	 */
	public PhoneStateListener(EventDispatchThread dispatch, ContentResolver resolver) {
		assert dispatch != null;
		m_dispatchThread = dispatch;
	}
	
	@Override
	protected void onIncomingCallStarted(String number, Date start) {
		ContactInfo info = ContactInfoPoller.pollInfo(savedContext, number);
		
		IncomingCallEvent event = new IncomingCallEvent(start, info);
		m_dispatchThread.dispatchEvent(event);
	}

	@Override
	protected void onOutgoingCallStarted(String number, Date start) {}

	@Override
	protected void onIncomingCallEnded(String number, Date start, Date end) {
		ContactInfo info = ContactInfoPoller.pollInfo(savedContext, number);
		
		CallEndedEvent event = new CallEndedEvent(end, info);
		m_dispatchThread.dispatchEvent(event);
	}

	@Override
	protected void onOutgoingCallEnded(String number, Date start, Date end) {}

	@Override
	protected void onMissedCall(String number, Date start) {
		// Retrieve the caller's contact info
		ContactInfo info = ContactInfoPoller.pollInfo(savedContext, number);
		
		if(info != null) {
			MissedCallEvent event = new MissedCallEvent(start, info);
			m_dispatchThread.dispatchEvent(event);
		}
	}
	
	/**
	 * Call back for UnreadMissedCallTimer.
	 * 
	 * The timer will call this every time it finds an unread missed call
	 * 	in the call log. This will occur until the "unread" status is cleared
	 * 	by the user by checking the log.
	 * 
	 * @param number A phone number for the missed caller
	 * @param start A date for when the call was missed
	 * @param context A context to user for resolving ContactInfo
	 */
	protected void onUnreadMissedCall(String number, Date start, Context context) {
		// Retrieve the caller's contact info
		ContactInfo info = ContactInfoPoller.pollInfo(context, number);
		
		if(info != null) {
			MissedCallEvent event = new MissedCallEvent(start, info);
			m_dispatchThread.dispatchEvent(event);
		}		
	}
}
