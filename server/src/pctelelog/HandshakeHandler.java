package pctelelog;

import java.util.Date;

import pctelelog.events.AbstractEvent;
import pctelelog.events.HelloEvent;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class HandshakeHandler extends ChannelInboundHandlerAdapter {
	private boolean helloSent = false;
	private boolean handshakeComplete = false;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ChannelFuture f = ctx.channel().write(new HelloEvent(new Date()));
		
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture fut) throws Exception {
				// Initial send Hello complete now listen for response
				helloSent = true;
			}
		});
		ctx.channel().flush();
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(helloSent) {
			AbstractEvent event = (AbstractEvent)msg;
			if(event instanceof HelloEvent) {
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
	
	public void handshakeTimeout() {
		
	}
}
