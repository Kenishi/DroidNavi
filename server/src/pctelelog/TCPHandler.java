package pctelelog;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.ietf.jgss.ChannelBinding;

import pctelelog.events.AbstractEvent;
import pctelelog.events.HelloEvent;
import pctelelog.internal.events.ClientSocketClosedEvent;

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
				InetSocketAddress i_addr = (InetSocketAddress)ctx.channel().remoteAddress();
				m_operator.onEvent(new ClientProperties(i_addr.getAddress()), event, true);
			}
		} catch(Exception e) {
			logger.catching(e);
		}
		ReferenceCountUtil.release(msg);
	}
	
}
