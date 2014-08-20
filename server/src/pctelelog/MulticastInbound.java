package pctelelog;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import pctelelog.events.AbstractEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MulticastInbound extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		InetAddress addr = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress();
		AbstractEvent event = (AbstractEvent)msg;
		EventOperator.instance().onEvent(new ClientProperties(addr), event, false);
		ReferenceCountUtil.release(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}
}
