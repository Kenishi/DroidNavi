package com.aberdyne.droidnavi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pctelelog.events.AbstractEvent;

public class NetworkDispatch {
	private static final Logger logger = LoggerFactory.getLogger(NetworkDispatch.class);
	
	private Boolean m_hasMulticast = null;
	
	public NetworkDispatch() {
		m_hasMulticast = MulticastSender.checkMulticastAvailability() ? Boolean.TRUE :
			Boolean.FALSE;
	}
	
	public boolean hasMulticast() {
		return m_hasMulticast.booleanValue();
	}
	
	public boolean sendEvent(AbstractEvent event) {
		if(m_hasMulticast) {
			return MulticastSender.sendEvent(event);
		}
		logger.error("Event dispatch failed: No multicast");
		return false;
	}
	
	public boolean sendEvent(AbstractEvent event, ServerConnection relayServer) {
		if(m_hasMulticast) {
			return MulticastSender.sendEvent(event);
		}
		else if(relayServer != null) {
			return relayServer.sendEvent(event);
		}
		logger.error("Event dispatch failed: No multicast or server");
		return false;
	}
}
