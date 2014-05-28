package pctelelog;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
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

import pctelelog.events.AbstractEvent;
import pctelelog.events.HelloEvent;
import pctelelog.internal.events.ClientSocketClosedEvent;

/**
 * A class representing the connection to a Client device
 *  
 * @author Kei
 *
 */

public class Client extends Thread {
	private final int READ_TIMEOUT = 30 * 1000; // 30 second timeout on read
	
	private Logger logger = LogManager.getLogger(Client.class);
	
	private Socket m_client = null;
	private String m_ip = null;
	private EventOperator m_operator = null;
	private boolean m_passedHandshake = false;
	private volatile Boolean isShutdown = false;
	
	/* Json Processor */
	private ObjectMapper mapper = null;
	private JsonFactory factory = null;
	
	/**
	 * Constructor
	 * @param client A socket for the client connection.
	 * @param operator An event operator who will receive onEvent callbacks from the client
	 * @throws JsonParseException Thrown when issues arise in creating the JsonParser for the socket stream.
	 * @throws IOException Thrown when issues arise in creating the JsonParser for the socket stream.
	 */
	public Client(Socket client, EventOperator operator) throws JsonParseException, IOException {
		if(client == null) {
			throw new NullPointerException("Client socket can not be null.");
		}
		if(operator == null) {
			throw new NullPointerException("Event Operator can not be null.");
		}
		
		m_client = client;
		m_client.setSoTimeout(READ_TIMEOUT);
		m_ip = client.toString();
		
		m_operator = operator;
		
		mapper = new ObjectMapper();
		factory = mapper.getJsonFactory();
	}
	
	/**
	 * Event thread loop.
	 * 
	 * This should be started using start().
	 * On exiting of the loop, the socket will be closed and dereferenced.
	 */
	public void run() {
		AbstractEvent event = null;
		while(! isShutdown) {
			try {
				event = nextEvent();
				if(event != null) {
					m_operator.onEvent(this, event);
				}
			} catch (InterruptedIOException e) {
				if(isShutdown) break;
				continue; // Read time out or Interrupted
			} catch (SocketException e) {
				// Shutdown otherwise the exception will spam
				shutdown();
			} catch (IOException e) {
				logger.catching(e);
			} catch( NullPointerException e) {
				// Check if its due to socket disconnect
				if(isClosed() || isInputShutdown()) {
					shutdown();
				} 
				else {
					logger.catching(e);
				}
			}
		}
		
		if(!isClosed()) {
			this.close();
		}
	}
	
	@Override
	public synchronized void start() {
		if(this.isAlive())
			return;
		super.start();
	}
	
	/**
	 * Shutdown the thread immediately. 
	 * 
	 * Any blocking reads will be interrupted.
	 * Socket will close and dereference on thread exit automatically.
	 */
	public void shutdown() {
		isShutdown = true;
		interrupt();
	}
	
	/**
	 * Gets the stored IP at time of creation.
	 * 
	 * This is useful because the socket may be dereferenced and its
	 * 	required to know what the client's IP was.
	 * @return
	 */
	public String getIP() {
		return m_ip;
	}
	
	public InetAddress getInetAddress() {
		return m_client.getInetAddress();
	}
	
	public boolean getPassedHandshake() {
		return m_passedHandshake;
	}
	
	/**
	 * Return the next event from the stream.
	 * 
	 * The timeout of the read is specified in READ_TIMEOUT.
	 * @return Returns the event as generic AbstractEvent. Returns null when no event is on the stream.
	 * @throws JsonParseException 
	 * @throws IOException
	 */
	public AbstractEvent nextEvent() throws JsonParseException, IOException, SocketException, SocketTimeoutException {
		logger.entry();
		
		// Build Parser
		JsonParser parser;
		
		AbstractEvent event = null;
		
		logger.info("Reading next event off stream");
		parser = factory.createJsonParser(m_client.getInputStream());
		JsonNode node = parser.readValueAsTree();
		event = EventSerializer.deserialize(node);
	
		
		logger.exit(event);
		return event;
	}

	public boolean isInputShutdown() {
		return (m_client == null) ? true : m_client.isInputShutdown();
	}

	public boolean isClosed() {
		return (m_client == null) ? true : m_client.isClosed();
	}
	
	public boolean handshake() {
		logger.entry();
		if(m_client.isConnected()) {
			logger.info("Starting Handshake: " + m_client.toString());
			AbstractEvent hello = new HelloEvent(new Date());
			try {
				// Send Hello
				String json = EventSerializer.serialize(hello);
				m_client.getOutputStream().write(json.getBytes());
				
				// Get reply
				AbstractEvent event = nextEvent();
				if(event instanceof HelloEvent) {
					logger.info("Handshake PASSED");
					logger.exit();
					m_passedHandshake = true;
					return true;
				}
				else {
					logger.info("Handshake FAILED");
					logger.exit();
					return false;
				}
			} catch (IOException e) {
				logger.info("Handshake Exception");
				logger.exit();
				return false;
			}
		}
		logger.info("Client not connected");
		logger.exit();
		return false;
	}
	
	@Override
	/**
	 * Checks that Clients are equal in data
	 * 
	 * May compare against socket equality, but in the event
	 * 	that sockets are null, compares against stored String IP.
	 */
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		try {
			Client client = Client.class.cast(obj);
			// Compare stored string IP if any sockets are null
			if(this.getSocket() == null || client.getSocket() == null) {
				if(! client.getIP().equals(this.getIP())) {
					return false;
				}
			} // Compare the sockets if available
			else if(this.getSocket() != null && client.getSocket() != null) {
				if(! client.getSocket().equals(this.getSocket())) {
					return false;
				}
			}
		}
		catch(ClassCastException e) {
			return false;
		}
		return true;
	}
	
	private Socket getSocket() {
		return m_client;
	}
	
	private void close() {
		if(m_client == null) return;
		
		try {
			m_client.close();
			logger.info(getIP() + " disconnected");
			m_operator.onEvent(this, new ClientSocketClosedEvent());
			m_client = null;
		} catch(IOException e) {
			m_client = null;
		}
	}
}
