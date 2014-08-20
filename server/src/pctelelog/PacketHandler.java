package pctelelog;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import pctelelog.events.AbstractEvent;

/**
 * A helper class that tracks Packets received and
 * rebuilds an event once all are available.  
 * 
 * @author Jeremy May
 *
 */
public class PacketHandler extends ChannelDuplexHandler {
	private final int PRUNE_TIME = 5 * (60 * 1000); // 5 minutes
	
	private Hashtable<Long, Vector<Packet>> packets = new Hashtable<Long, Vector<Packet>>();
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// Read the packet data into a byte array
		DatagramPacket dgram = (DatagramPacket)msg;
		ByteBuf buf = dgram.content();
		byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		
		// Start building the event
		Packet packet = Packet.deserialize(data);
		AbstractEvent event = rebuild(packet);
		if(event != null) {
			ctx.fireChannelRead(event);
		}
		ReferenceCountUtil.release(msg);
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		AbstractEvent event = (AbstractEvent)msg;
		Packet[] packets = Packet.createPackets(event);
		for(Packet packet : packets) {
			ByteBuf buf = Unpooled.copiedBuffer(packet.serialize());
			ctx.writeAndFlush(buf, promise);
			buf.release();
		}
	}
	
	public void prune() {
		long time = System.currentTimeMillis();
		Set<Long> ids = packets.keySet();
		for(Long id : ids) {
			long diff = time - id.longValue();
			if(diff > PRUNE_TIME)
				remove(id);
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
		else if(packets.contains(id)) {
			put(id, packet);
			
			// Check if we have all packets
			if(packets.get(id).size() == packet.getMaxSequency()) {
				// Rebuild and return Event
				ByteBuffer buffer = ByteBuffer.allocate(Packet.DATA_SPLIT * packet.getMaxSequency());
				for(Packet p : packets.get(id)) {
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
			put(id, packet);
		}
		return null;
	}
	
	private void put(Long id, Packet packet) {
		Vector<Packet> pack = packets.contains(id) ? packets.get(id) : new Vector<Packet>(packet.getMaxSequency()); 
		pack.add(packet.getSequence(), packet);
		packets.put(id, pack);
	}
	
	private void remove(Long id) {
		packets.remove(id);
	}
	
}
