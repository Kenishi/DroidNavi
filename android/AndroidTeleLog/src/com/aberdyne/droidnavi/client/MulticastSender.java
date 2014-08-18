package com.aberdyne.droidnavi.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pctelelog.EventSerializer;
import pctelelog.events.AbstractEvent;
import pctelelog.events.HeartBeatEvent;

public class MulticastSender {
	private static final Logger logger = LoggerFactory.getLogger(MulticastSender.class);
	
	public static boolean checkMulticastAvailability() {
		return sendEvent(new HeartBeatEvent());
	}
	
	public static boolean sendEvent(AbstractEvent event) {
		try {
			return sendEvent(EventSerializer.serialize(event));
		} catch (IOException e) {
			logger.error("Failed to serialize event.");
			return false;
		}
	}
	
	private static boolean sendEvent(String json) {
		try {
			MulticastSocket socket = new MulticastSocket(5008);
			InetAddress sessAddr = InetAddress.getByName("224.1.1.1");
			socket.joinGroup(sessAddr);
			
			// Build Packet
			DatagramPacket packet = new DatagramPacket(json.getBytes(), json.getBytes().length,
					sessAddr, 5008);
			
			// Broadcast
			socket.send(packet);
			
			// Cleanup
			socket.leaveGroup(sessAddr);
			socket.close();
		}
		catch(IOException e) {
			logger.error("Failed to send event");
			return false;
		}
		return true;
	}
}
