package com.aberdyne.droidnavi.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;
import pctelelog.EventSerializer;
import pctelelog.Packet;
import pctelelog.TeleLogServer;
import pctelelog.events.AbstractEvent;
import pctelelog.events.HeartBeatEvent;

public class MulticastSender extends AsyncTask<String, Void, Boolean>{
	private static final Logger logger = LoggerFactory.getLogger(MulticastSender.class);
	
	private static final int PORT = TeleLogServer.MULTI_LIST_PORT;
	
	@Override
	protected Boolean doInBackground(String...json) {
		boolean result = sendEvent(json[0]);
		return Boolean.valueOf(result);
	}
	
	public static boolean checkMulticastAvailability() {
		return MulticastSender.sendEvent(new HeartBeatEvent());
	}
	
	public static boolean sendEvent(AbstractEvent event) {
		try {
			String json = EventSerializer.serialize(event);
			String[] out = {json};
			
			try {
				return new MulticastSender().execute(out).get().booleanValue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (IOException e) {
			logger.error("Failed to serialize event.");
			return false;
		}
	}
	
	private static boolean sendEvent(String json) {
		try {
			MulticastSocket socket = new MulticastSocket(PORT);
			InetAddress sessAddr = InetAddress.getByName("224.1.1.1");
			socket.setTimeToLive(2);
						
			Packet[] packets = Packet.createPackets(json);
			for(Packet packet : packets) {
				byte[] data = packet.serialize();
				// Build Packet
				DatagramPacket d_pack = new DatagramPacket(data, data.length,
						sessAddr, PORT);
				
				// Broadcast
				socket.send(d_pack);
			}
			
			
			// Cleanup
			socket.close();
		}
		catch(IOException e) {
			logger.error("Failed to send event");
			return false;
		}
		return true;
	}
}
