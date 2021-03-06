package com.aberdyne.droidnavi.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pctelelog.EventSerializer;
import pctelelog.Packet;
import pctelelog.events.AbstractEvent;
import pctelelog.events.ClientConnectEvent;
import pctelelog.events.HeartBeatEvent;
import pctelelog.events.HelloEvent;
import pctelelog.events.ShutdownEvent;

public class ServerConnection implements Comparable<String> {
	private static final Logger logger = LoggerFactory.getLogger(ServerConnection.class);
	
	private static final int SERVER_PORT = 43212;
	private static final int READ_TIMEOUT = 10 * 1000; // 10 second read timeout
	private static final int CONNECT_TIMEOUT = 3 * 1000; // 3 second connect timeout
	private static final int HEARTBEAT_DELAY = 5 * 1000; // 5 second delay between heartbeat checks
	
	private Socket m_server = null;
	private String m_ipStr = null;
	private boolean m_isActiveSocket = false; // Is the socket connect()-able?
	private boolean m_isStandby = true;
	
	private Date m_lastEvent = null; // Timestamp for last event sent, used for heartbeat()

	/**
	 * Protected constructor that allows specifying whether the socket
	 * 	should be active(connect()-able) or not.
	 * 
	 * @param ip A valid IP with no port specified
	 * @param isActiveSocket True if the socket should be connected.
	 * 						 False if the socket should remain unconnected.
	 * @throws ServerConnectionException 
	 */
	protected ServerConnection(String ip, boolean isActiveSocket) throws ServerConnectionException {
		assert ip != null;
		
		m_isActiveSocket = isActiveSocket;
		m_ipStr = ip;	
	}
	
	/**
	 * Main constructor for a server connection.
	 * 
	 * This will not connect to the server automatically so connect()
	 * should be called before attempting to send events.
	 * 
	 * @param ip An ip representing the remote computer.
	 * @throws ServerConnectionException
	 */
	public ServerConnection(String ip) throws ServerConnectionException {
		this(ip, true);
	}
	
	/**
	 * Helper function to validate a host/ip is valid.
	 * 
	 * @param host A string containing a hostname or IP to check.
	 * @return True if the host string is valid. False if it is not.
	 */
	static public boolean validateHost(String host) {
		try {
			InetAddress.getByName(host);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * This will attempt to connect to the remote computer and 
	 * 	perform all the verification.
	 * @return True if the connection was successful and the remote computer passed
	 * 			the handshake.
	 *		   False if the problems occurred or if the computer failed handshake.
	 */
	public boolean connect() {
		return openSocket(SERVER_PORT);
	}
	
	public int compareTo(String another) {
		return this.toString().compareTo(another);
	}
	
	/**
	 * This is not a deep equals of the object.
	 * The method will compare the *STRING* Ips of the two objects ONLY. 
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		
		try {
			ServerConnection server = ServerConnection.class.cast(o);
			
			if(server.toString() != null && this.toString() != null) {
				return server.toString().equals(this.toString());
			}
		} catch(ClassCastException e) {
			return false;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return m_ipStr;
	}
	
	/**
	 * Shutdown the server connection and put socket on standby.
	 */
	public void shutdown(boolean sendShutdownEvent) {
		if(sendShutdownEvent){
			try {
				ShutdownEvent shutdown_event = new ShutdownEvent(new Date());
				sendEvent(shutdown_event);
			} catch(Exception e) { /* Continue closing socket */}
		}
		
		try {
			m_server.close();
		} catch (IOException e) {
			try {
				// Try shutting down the Output
				m_server.shutdownOutput();
				m_server.shutdownInput();
			} catch (IOException e1) {}
		} catch(NullPointerException e) {}
		setStandbyStatus(true);
		m_server = null;
	}
	
	public boolean getStandbyStatus() {
		return m_isStandby;
	}

	public boolean isConnected() {
		if(m_server == null) {
			return false;
		}
		
		return m_server.isConnected();
	}
	
	public boolean isClosed() {
		if(m_server == null) {
			return true;
		}
		
		return m_server.isClosed();
	}
	
	/**
	 * Try sending a heartbeat to the server to check socket state.
	 * 
	 * This is a simple check with no expected reply. If the socket is dead,
	 * 	then an exception should be thrown and the ServerConnection state
	 * 	moved to standby.
	 * 
	 * @return Return true if heartbeat was ok or not needed. False if it failed
	 * 	and the socket is shutdown.
	 */
	public boolean heartbeat() {
		Date now = new Date();
		if(m_lastEvent == null) {
			return sendEvent(new HeartBeatEvent());
		}
		else {
			long diff = now.getTime() - m_lastEvent.getTime();
			if(diff > HEARTBEAT_DELAY) {
				return sendEvent(new HeartBeatEvent());
			}
		}
		return true;
	}
	
	/**
	 * Helper function for sending unserialized events
	 * @param event An event
	 * @return True if no problems occurred while sending, False otherwise.
	 */
	public boolean sendEvent(AbstractEvent event) {
		if(m_isActiveSocket) {
			return sendEvent(m_server, event);
		}
		else {
			if(m_isStandby) { // If on standby try to connect
				if(!connect()) {
					return false;
				}
			}
			return sendEvent(m_server, event);
		}
	}
	
	/**
	 * Send json event to specified server
	 * 
	 * @param server An open and connected socketed 
	 * @param json The serialized event to send
	 * @return True if successful, False otherwise.
	 */
	private synchronized boolean sendEvent(Socket server, AbstractEvent event) {
		logger.trace("ENTRY ServerConnection.sendEvent");
		
		assert server != null;
		
		if(server.isConnected() &&
				!server.isOutputShutdown()) {
			try {
				logger.info("Writing bytes");
				Packet[] packets = Packet.createPackets(event);
				for(Packet packet: packets) {
					server.getOutputStream().write(packet.serialize());
				}
				m_lastEvent = new Date(); // Update last event
			} catch (IOException e) {
				setStandbyStatus(true);
				shutdown(false);
				logger.debug("Catching: {}", e);
				logger.trace("EXIT ServerConnection.sendEvent:{}", Boolean.FALSE);
				return false;
			}
		}
		else {
			logger.info("Server not connected. Handshake Fail.");
			logger.trace("EXIT ServerConnection.sendEvent: {}", Boolean.FALSE);
			return false;
		}
		logger.trace("EXIT ServerConnection.sendEvent: {}", Boolean.TRUE);
		return true;
	}

	private void setStandbyStatus(boolean status) {
		m_isStandby = status;
	}
		
	/**
	 * Open a new socket and handshake with server.
	 * 
	 * This will clear the old socket, so make sure to handle any shutdown routines
	 * 	before calling.
	 * This method will use the IP string stored at creation to connect.
	 * 
	 * On failure, the socket will call shutdown and go on standby.
	 * 
	 * @param port Port to connect to. 
	 * @return True if connection was successful. False otherwise.
	 */
	private boolean openSocket(int port) {
		logger.trace("ENTRY ServerConnection.openSocket({})", port);
		
		if(m_server != null) {
			shutdown(true);
		}
		
		m_server = createSocket(m_ipStr, port);
		if(m_server == null) {
			setStandbyStatus(true);
			System.err.println("Failed to connect to server: " +
								m_ipStr +
								":" + Integer.toString(SERVER_PORT));
			logger.trace("Exiting ServerConnection.openSocket: {}", Boolean.FALSE);
			return false;
		}
		if(!handshake(m_server)) {
			setStandbyStatus(true);
			shutdown(false);
			System.err.println("The server (" + m_ipStr + ") failed the handshake.");
			logger.trace("EXIT ServerConnection.openSocket: {}", Boolean.FALSE);
			return false;
		}
		setStandbyStatus(false);
		sendEvent(new ClientConnectEvent(new Date()));
		logger.trace("EXIT ServerConnection.openSocket: {}", Boolean.TRUE);
		return true;
	}
	
	/**
	 * Creates a socket and sets appropriate settings
	 * @param ip A computer to connect to
	 * @param port A port to connect to
	 * @return Returns an open socket if there are no problem.
	 * 			Returns null if problems occurred and print exceptions.
	 */
	private Socket createSocket(String ip, int port) {
		logger.trace("ENTRY ServerConnection.createSocket");
		logger.debug("IP: {} PORT: {}", ip, port);
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
			socket.setSoTimeout(READ_TIMEOUT);
		} catch (UnknownHostException e) {
			logger.error("Catching UnknownHostException: {}", e.getMessage());
			socket = null;
		} catch (IOException e) {
			logger.error("Catching IOException: {}", e.getMessage());
			socket = null;
		}
		
		logger.trace("EXIT ServerConnection.createSocket: {}", socket);
		return socket;
	}
	
	
	/** 
	 * Handshakes with the specified socket.
	 * 
	 * State of the ServerConnection and supplied socket remains unchanged.
	 *
	 * @return True if successful, False if it wasn't
	 */
	private boolean handshake(Socket server) {
		logger.trace("ENTRY ServerConnection.handshake({})", server);
		if(!server.isConnected() || 
				server.isInputShutdown() ||
				server.isOutputShutdown()) {
			logger.debug("Socket not connected.");
			logger.trace("EXIT ServerConnection.handshake: {}", Boolean.FALSE);
			return false;
		}
		
		// Get Hello
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getJsonFactory();
		AbstractEvent event = null;
		try {
			byte[] data = new byte[3000];
			boolean isFinish = false;
			while(!isFinish) {
				if(server.getInputStream().available() > 0) {
					int read = server.getInputStream().read(data);
					logger.info("Read: " + Integer.toString(read));
				}
				else {
					Thread.sleep(3000);
					logger.info("Available(2): " + Integer.toString(server.getInputStream().available()));
					isFinish = server.getInputStream().available() > 0 ? false : true;
				}
			}
			JsonParser parser = factory.createJsonParser(data);
			logger.info("Reading Hello");
			JsonNode node = parser.readValueAsTree();
			event = EventSerializer.deserialize(node);
			
			logger.info("Received: " + event.toString());
			if(event instanceof HelloEvent) {
				// Send Hello
				sendEvent(server, new HelloEvent(new Date()));
				logger.info("Handshake PASS");
				logger.trace("EXIT ServerConnection.handshake: {}", Boolean.TRUE);
				return true;
			}
			else {
				logger.info("Handshake FAIL");
				logger.trace("EXIT ServerConnection.handshake: {}", Boolean.FALSE);
				return false;
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Catching: {}", e);
		}
		logger.trace("EXIT ServerConnection.handshake: {}", Boolean.TRUE);
		return false;
	}
	
	@SuppressWarnings("serial")
	public class ServerConnectionException extends RuntimeException {
		public ServerConnectionException(String msg) {
			super(msg);
		}
	}
}
