package pctelelog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pctelelog.events.AbstractEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MulticastInbound extends ChannelInboundHandlerAdapter {
	private final Logger logger = LogManager.getLogger(MulticastInbound.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		AbstractEvent event = (AbstractEvent)msg;
		EventOperator.instance().onEvent(event, false);
		logger.debug("Event Fired: " + event.toString());
		ReferenceCountUtil.release(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}
}
