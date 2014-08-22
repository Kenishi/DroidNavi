package pctelelog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import py4j.GatewayConnection;
import py4j.GatewayServer;
import py4j.GatewayServerListener;

public class TeleLogPy4jLauncher implements GatewayServerListener {
	
	private static Logger logger = LogManager.getLogger(TeleLogPy4jLauncher.class);
	private static TeleLogServer m_telelogServer = null;

	public TeleLogPy4jLauncher() {
		logger.info("Logging started.");
		
		m_telelogServer = new TeleLogServer();
		m_telelogServer.start();
	}
	
	public void addEventListener(EventListener listener) {
		logger.info("Listener added.");
		m_telelogServer.addEventListener(listener);
	}
	
	public void removeEventListener(EventListener listener) {
		logger.info("Listener removed.");
		m_telelogServer.removeEventListener(listener);
	}
	
	public static void main(String[] args) {		
		TeleLogPy4jLauncher launcher = new TeleLogPy4jLauncher();
		GatewayServer gateway = new GatewayServer(launcher);
		gateway.addListener(launcher);
		gateway.start();
	}
	
	@Override
	public void connectionError(Exception arg0) {}
	@Override
	public void connectionStarted(GatewayConnection arg0) {}
	@Override
	public void connectionStopped(GatewayConnection arg0) {}
	@Override
	public void serverError(Exception arg0) {}
	@Override
	public void serverPostShutdown() {}
	@Override
	public void serverPreShutdown() {}
	@Override
	public void serverStarted() {}

	@Override
	public void serverStopped() {
		m_telelogServer.shutdown();
	}
}
