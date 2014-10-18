package pctelelog;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.*;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TeleLogServer {
	
	public enum RESULT {
		TCP_BIND_EXCEPTION,
		MULTI_BIND_EXCEPTION,
		OTHER_EXCEPTION,
		SUCCESS;
		
		/**
		 * Set the throwable cause for the current status
		 */
		private Throwable m_cause = null;
		public RESULT cause(Throwable cause) {
			this.m_cause = cause;
			return this;
		}
		
		public Throwable getCause() { return m_cause; }
	}
	 
	private static final Logger logger = LogManager.getLogger(TeleLogServer.class);
	
	public static final int TCP_LISTEN_PORT = 43212;
	public static final int MULTI_LIST_PORT = 43213;
	
	private DefaultChannelGroup m_pool = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ChannelFuture m_tcpFuture = null;
	private ChannelFuture m_multiFuture = null;
	private NioEventLoopGroup m_tcpEventLoop = new NioEventLoopGroup();
	private OioEventLoopGroup m_multiEventLoop = new OioEventLoopGroup();
	
	
	public RESULT start() {
		logger.entry();
		RESULT result = RESULT.SUCCESS;
		
		try {
			// TCP Server
			ServerBootstrap server = new ServerBootstrap();
			server.channel(NioServerSocketChannel.class)
				.group(m_tcpEventLoop)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					protected void initChannel(SocketChannel ch) throws Exception {
						m_pool.add(ch);
						ch.pipeline().addFirst(new PacketHandler());
						ch.pipeline().addLast(new HandshakeHandler());
					};
					@Override
					public void channelInactive(ChannelHandlerContext ctx)
							throws Exception {
						m_pool.remove(ctx.channel());
					}
					
				})
				.option(ChannelOption.SO_BACKLOG, 3)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			m_tcpFuture = server.bind(TCP_LISTEN_PORT).sync();
			
			// MultiCast Server
			InetSocketAddress remoteAddr = 
					new InetSocketAddress(InetAddress.getByName("224.1.1.1"), MULTI_LIST_PORT);
			
			Bootstrap b = new Bootstrap();
			b.group(m_multiEventLoop)
			 .option(ChannelOption.SO_BROADCAST, true)
			 .option(ChannelOption.SO_REUSEADDR, true)		
			 .option(ChannelOption.IP_MULTICAST_TTL, 2)
			 .channelFactory(new ChannelFactory<OioDatagramChannel>() {
				 @Override
				public OioDatagramChannel newChannel() {
					OioDatagramChannel ch = new OioDatagramChannel();
					return ch;
				}
			});
			b.handler(new ChannelInitializer<OioDatagramChannel>() {
				@Override
				protected void initChannel(OioDatagramChannel ch) throws Exception {
					ch.pipeline().addFirst(new PacketHandler());
					ch.pipeline().addLast(new MulticastInbound());
				}
			});
			
			m_multiFuture = b.bind(MULTI_LIST_PORT).sync();
			
			DatagramChannel ch = (DatagramChannel)m_multiFuture.channel();
			
			// Join Group on all active interfaces
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			while(nics.hasMoreElements()) {
				NetworkInterface nic = nics.nextElement();
				if(nic.isUp() && !nic.isVirtual()) {
					ch.joinGroup(remoteAddr, nic);
				}
			}
			
			EventOperator.instance().setMultiCast(ch); // Set multi in order to relay TCP events
			
			logger.info("Servers started.");
			
		} catch(BindException e) {
			shutdown();
			
			// If the TCP side was successful then the error must have occurred on
			// UDP multicast
			result = (m_tcpFuture != null && m_tcpFuture.isSuccess()) ? 
					RESULT.MULTI_BIND_EXCEPTION.cause(e) : RESULT.TCP_BIND_EXCEPTION.cause(e);
		}
		catch(Throwable e) {
			shutdown();
			result = RESULT.OTHER_EXCEPTION.cause(e);
		} 		
		logger.exit(result);
		return result;
	}
	
	public void shutdown() {
		logger.entry();
		if(m_tcpFuture != null && m_tcpFuture.channel().isActive()) {
			m_tcpFuture.channel().close();
			logger.debug("TCP server shutdown");
		}
		if(m_multiFuture != null && m_multiFuture.channel().isActive()) {
			m_multiFuture.channel().close();
			logger.debug("UDP socket shutdown");
		}
		if(!m_pool.isEmpty()) {
			m_pool.close();
			logger.debug("TCP Sockets closed");
		}
		if(!m_tcpEventLoop.isShutdown()) {
			m_tcpEventLoop.shutdownGracefully();
			logger.debug("TCP Event loop shutdown");
		}
		if(!m_multiEventLoop.isShutdown()) {
			m_multiEventLoop.shutdownGracefully();
			logger.debug("UDP Event loop shutdown");
		}
		logger.exit();
	}
	
	public void addEventListener(EventListener listener) {
		EventOperator.instance().addEventListener(listener);
	}
	
	public void removeEventListener(EventListener listener) {
		EventOperator.instance().removeEventListener(listener);
	}
		
}
