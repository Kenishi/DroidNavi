package pctelelog;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pctelelog.events.AbstractEvent;

/**
 * A helper class that tracks Packets received and
 * rebuilds an event once all are available.  
 * 
 * @author Jeremy May
 *
 */
public class PacketHandler extends ChannelInboundHandlerAdapter {
	private final Logger logger = LogManager.getLogger(PacketHandler.class);
	
	private final int PRUNE_TIME = 5 * (60 * 1000); // 5 minutes
	
	private Hashtable<Long, PacketSet> packets = new Hashtable<Long, PacketSet>();
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = getSenderData(msg);
		byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		
		// Start building the event
		Packet packet = Packet.deserialize(data);
		logger.debug("PACKET: " + packet.getId() + ":" + (packet.getSequence()+1) + "/" + packet.getMaxSequency());
		AbstractEvent event = rebuild(packet);
		if(event != null) {
			InetAddress remote = getSenderAddress(ctx, msg);
			event = EventDeviceResolver.resolveDevice(remote, event);
			ctx.fireChannelRead(event);
			logger.debug("Event Fired: " + event.toString());
		}
		ReferenceCountUtil.release(msg);
	}
	
	private InetAddress getSenderAddress(ChannelHandlerContext ctx, Object msg) {
		InetAddress addr = null;
		if(msg instanceof DatagramPacket) {
			DatagramPacket dgram = (DatagramPacket)msg;
			addr = dgram.sender().getAddress();
		}
		else if(msg instanceof ByteBuf) {
			InetSocketAddress tcp = (InetSocketAddress)ctx.channel().remoteAddress();
			addr = tcp.getAddress();
		}
		return addr;
	}
	
	private ByteBuf getSenderData(Object msg) {
		if(msg instanceof DatagramPacket) { // UDP
			DatagramPacket dgram = (DatagramPacket)msg;
			return dgram.content();
		}
		else if(msg instanceof ByteBuf) { // TCP
			return (ByteBuf)msg;
		}
		else { // No idea
			return null;
		}
	}
	
	public void prune() {
		long time = System.currentTimeMillis();
		Set<Long> ids = packets.keySet();
		for(Long id : ids) {
			long diff = time - id.longValue();
			if(diff > PRUNE_TIME) {
				remove(id);
				logger.debug("Pruned: " + id);
			}
		}
	}
	
	/**
	 * Attempt to rebuild the fragmented JSON Event
	 * 
	 * @param packet A packet to rebuild 
	 * @return An AbstractEvent or null if more packets are still needed
	 */
	private AbstractEvent rebuild(Packet packet) {
		Long id = Long.valueOf(packet.getId());
		
		prune();
		
		// Check if packet id is already in packets
		if(packet.getMaxSequency() == 1) {
			String json = new String(packet.getData());
			try {
				AbstractEvent event = EventSerializer.deserialize(json);
				if(event != null) return event;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(packets.containsKey(id)) {
			put(id, packet);
			
			// Check if we have all packets
			if(packets.get(id).size() == packet.getMaxSequency()) {
				logger.debug("Rebuild Event");
				// Rebuild and return Event
				ByteBuffer buffer = ByteBuffer.allocate((int)Packet.DATA_SPLIT * packet.getMaxSequency());
				PacketSet set = packets.get(id);
				for(int i=0; i < set.size(); i++) {
					Packet p = set.get(i);
					logger.debug("Packet " + p.getSequence() + "=" + p.getData());
					buffer.put(p.getData());
				}
				
				try {
					String json = new String(buffer.array());
					AbstractEvent event = EventSerializer.deserialize(json);
					packets.remove(id);
					return event;
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			logger.debug("Add to set");
			put(id, packet);
		}
		return null;
	}
	
	private void put(Long id, Packet packet) {
		PacketSet pack;
		if(packets.containsKey(id)) {
			pack = packets.get(id); 
		}
		else {
			logger.debug("Creating new packet set for id: " + id);
			pack = new PacketSet(packet.getMaxSequency());
		}
		pack.add(packet.getSequence(), packet);
		logger.debug("ADD Packet Set("+ id+ "): " + (packet.getSequence()+1) + "/" + packet.getMaxSequency() );
		packets.put(id, pack);
	}
	
	private void remove(Long id) {
		packets.remove(id);
	}
	
}
