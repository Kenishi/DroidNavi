package pctelelog;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPool {
	private static final Logger logger = LogManager.getLogger(ClientPool.class);
	
	private CopyOnWriteArrayList<Client> m_pool = new CopyOnWriteArrayList<Client>();
	
	/**
	 * Shuts down the entire pool.
	 * 
	 * Calls into this may take time as the method will call .join() on every
	 * 	client as it shuts down.
	 */
	public synchronized void shutdown() {
		for(Client client : m_pool) {
			client.shutdown();
			client.interrupt();
			try {
				client.join(10000);
			} catch(InterruptedException e) {
				logger.catching(e);
				logger.error("Client Thread: " + client.getIP() + " failed to shutdown.");
			}
		}
	}
	
	protected Client[] getPool() {
		Client[] clients = (Client[]) m_pool.toArray();
		return clients;
	}
	
	protected boolean addClient(Client client) {
		if(client == null ||
				client.isClosed() ||
				client.isInputShutdown() ||
				!client.isAlive()) {
			return false;
		}
		if(m_pool.addIfAbsent(client)) {
			logger.info(client.getIP() + " added to ClientPool.");
			return true;
		}
		else { // Compare thread states
			int index = m_pool.indexOf(client);
			Client currentClient = m_pool.get(index);
			if(currentClient.isAlive()) {
				return false;
			}
			else if(client.isAlive()) {
				logger.info(client.getIP() + " swapped in ClientPool.");
				m_pool.set(index, client);
			}
		}
		
		logger.error(client.getIP() + " already in the pool!");
		return false;
	}
	
	protected boolean removeClient(Client client) {
		if(client == null) {
			return false;
		}
		else if(m_pool.remove(client)) {
			// Shutdown Client, don't want to strand the socket/thread
			client.shutdown();
			return true;
		}
		return false;
	}
}
