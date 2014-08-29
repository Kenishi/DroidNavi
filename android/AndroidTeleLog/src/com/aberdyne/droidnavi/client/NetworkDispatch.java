package com.aberdyne.droidnavi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import pctelelog.events.AbstractEvent;

public class NetworkDispatch {
	public static final String MULTI_IS_NETWORK_TESTED_SETTING = "multiNetworkTested";
	public static final String MULTI_NETWORK_TEST_RESULT_SETTING = "multiNetworkTestResult";
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkDispatch.class);
	
	private Context m_context = null;
	
	public NetworkDispatch(Context context) {
		m_context = context;
	}
	
	public boolean hasMulticast() {
		if(MulticastSender.checkMulticastAvailability()) {
			SharedPreferences pref = m_context.getSharedPreferences("settings", Context.MODE_PRIVATE);
			if(pref.getBoolean(MULTI_IS_NETWORK_TESTED_SETTING, false)) {
				boolean result = pref.getBoolean(MULTI_NETWORK_TEST_RESULT_SETTING, false);
				return result;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}
	
	public boolean sendEvent(AbstractEvent event) {
		if(hasMulticast()) {
			return MulticastSender.sendEvent(event);
		}
		logger.error("Event dispatch failed: No multicast");
		return false;
	}
	
	public boolean sendEvent(AbstractEvent event, ServerConnection relayServer) {
		boolean result = false;
		if(hasMulticast()) {
			result = MulticastSender.sendEvent(event);
		}
		else if(relayServer != null) {
			result =  relayServer.sendEvent(event);
		}
		else {
			logger.error("Event dispatch failed: No multicast or server");
			return false;
		}
		return result;
	}
}
