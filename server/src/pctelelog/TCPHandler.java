package pctelelog;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pctelelog.events.AbstractEvent;

/**
 * A class representing the connection to a Client device
 *  
 * @author Jeremy May
 *
 */

public class TCPHandler extends ChannelInboundHandlerAdapter {	
	private Logger logger = LogManager.getLogger(TCPHandler.class);
	
	private EventOperator m_operator = null;
	
	/**
	 * Constructor
	 */
	public TCPHandler() {
		EventOperator operator = EventOperator.instance();
		
		if(operator == null) {
			throw new NullPointerException("Event Operator can not be null.");
		}
		
		m_operator = operator;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			AbstractEvent event = (AbstractEvent)msg;
			if(event != null) {
				// Get remote address
				m_operator.onEvent(event, true);
			}
		} catch(Exception e) {
			logger.catching(e);
		}
		ReferenceCountUtil.release(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
}
