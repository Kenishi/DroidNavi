package pctelelog;

import java.util.Date;

import pctelelog.events.AbstractEvent;
import pctelelog.events.HelloEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.ReferenceCountUtil;

public class HandshakeHandler extends ReadTimeoutHandler {
	
	private final static int HANDSHAKE_TIMEOUT = 10;
	
	private boolean helloSent = false;
	private boolean handshakeComplete = false;
	
	public HandshakeHandler() {
		super(HANDSHAKE_TIMEOUT);
	}	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		HelloEvent event = new HelloEvent(new Date());
		String json = EventSerializer.serialize(event);
		ByteBuf data = Unpooled.copiedBuffer(json.getBytes());
		ChannelFuture f = ctx.write(data);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture fut) throws Exception {
				// Initial send Hello complete now listen for response
				helloSent = true;
			}
		});
		ctx.flush();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(helloSent) {
			AbstractEvent event = (AbstractEvent)msg;
			if(event instanceof HelloEvent) {
				handshakeComplete = true;
				ctx.channel().pipeline().replace(this, "event", new TCPHandler());
			}
			else {
				ctx.channel().close();
			}
		}
		else {
			throw new Exception("Unexpected state.");
		}
		ReferenceCountUtil.release(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
	@Override
	protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		if(!handshakeComplete) {
			ctx.close();
		}
	}

}
