package pctelelog;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TeleLogServer extends Thread {
	
	private enum STATUS { SUCCESS, FAIL } 
	
	private static final Logger logger = LogManager.getLogger(TeleLogServer.class);
	
	/* Note: If changed, must be updated in android app's ServerConnection.java */
	private static final int LISTEN_PORT = 43212; 
	private final int ACCEPT_TIMEOUT = 30 * 1000; // Accept timeout = 30 seconds
	
	private ServerSocket serverSocket = null;
	private volatile boolean m_isListening = false;
	private volatile boolean isStop = false;
	
	private EventOperator m_operator = new EventOperator();
	
	static public int getServerPort() {
		return LISTEN_PORT;
	}
	
	@Override
	public void run() {
		logger.entry();
		STATUS status = init();
		if(status == STATUS.FAIL) {
			return;
		}
		
		// Begin listen
		logger.info("Entering Server Loop");
		while(!isStop) {
			try {
				m_isListening = true;
				Socket sock = serverSocket.accept();
				m_isListening = false;
				Client client = (Client)new TCPClient(sock, m_operator);
				if(client.handshake()) {
					client.start(); // Start the event reader loop
					logger.info(client.toString() + " connected.");;
					m_operator.addClient(client);
				}
			} 
			catch(InterruptedIOException e) {
				m_isListening = false;
				continue;
			}
			catch (IOException e) {
				m_isListening = false;
				e.printStackTrace();
				quit();
			}
		}
		logger.info("Exiting server loop");
		
		shutdown();
		try {
			serverSocket.close();
		} catch(IOException e) { serverSocket = null; }
		logger.exit();
	}
	
	public boolean isBound() {
		if(serverSocket == null) {
			return false;
		}
		return serverSocket.isBound();
	}
	
	public boolean isClosed() {
		if(serverSocket == null) {
			return true;
		}
		return serverSocket.isClosed();
	}
	
	public boolean isListening() {
		return m_isListening;
	}
	
	public void quit() {
		isStop = true;
		interrupt();
	}
	
	protected void addEventListener(EventListener listener) {
		m_operator.addEventListener(listener);
	}
	
	protected void removeEventListener(EventListener listener) {
		m_operator.removeEventListener(listener);
	}
	
	private STATUS init() {
		try {
			serverSocket = new ServerSocket(LISTEN_PORT);
			serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
		}
		catch (Exception e) {
			return STATUS.FAIL;
		}

		return STATUS.SUCCESS;
	}
	
	private synchronized void shutdown() {
		m_operator.shutdown();
	}
	
}
