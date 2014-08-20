package pctelelog;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TeleLogServer {
	 
	private static final Logger logger = LogManager.getLogger(TeleLogServer.class);
	
	public static final int TCP_LISTEN_PORT = 43212;
	public static final int MULTI_LIST_PORT = 43213;
	
	private DefaultChannelGroup m_pool = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ChannelFuture m_serverFuture = null;
	private ChannelFuture m_multiFuture = null;
	
	public void start() {
		logger.entry();
		
		try {
			// TCP Server
			ServerBootstrap server = new ServerBootstrap();
			server.channel(NioServerSocketChannel.class)
				.group(new NioEventLoopGroup())
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
			
			m_serverFuture = server.bind(TCP_LISTEN_PORT).sync();
			
			// MultiCast Server Realy
			InetSocketAddress localSockAddr = 
					new InetSocketAddress(InetAddress.getByName("0.0.0.0"), MULTI_LIST_PORT);
			InetAddress localInetAddr = InetAddress.getByName("0.0.0.0");
			
			InetSocketAddress remoteAddr = 
					new InetSocketAddress(InetAddress.getByName("224.1.1.1"), MULTI_LIST_PORT);
			
			Bootstrap b = new Bootstrap();
			b.group(new NioEventLoopGroup())
			 .option(ChannelOption.SO_BROADCAST, true)
			 .option(ChannelOption.SO_REUSEADDR, true)		
			 .option(ChannelOption.IP_MULTICAST_TTL, 2)
			 .channelFactory(new ChannelFactory<NioDatagramChannel>() {
				 @Override
				public NioDatagramChannel newChannel() {
					return new NioDatagramChannel(InternetProtocolFamily.IPv4);
				}
			});
			b.handler(new ChannelInitializer<NioDatagramChannel>() {
				@Override
				protected void initChannel(NioDatagramChannel ch) throws Exception {
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
				if(nic.isUp() && !nic.isVirtual()) 
					ch.joinGroup(remoteAddr, nic);
			}
			
			EventOperator.instance().setMultiCast(ch); // Set multi in order to relay TCP events
			ch.read();
			
			logger.info("Servers started.");
			
		} catch(Exception e) {
			e.printStackTrace();
			return;
		} 		
		logger.info("Exiting server loop");
		
		logger.exit();
	}
	
	
	protected void addEventListener(EventListener listener) {
		EventOperator.instance().addEventListener(listener);
	}
	
	protected void removeEventListener(EventListener listener) {
		EventOperator.instance().removeEventListener(listener);
	}
	
	protected void shutdown() {
		m_serverFuture.channel().close();
		m_multiFuture.channel().close();
		m_pool.close();
	}
	
}
