package pctelelog;

import java.net.InetAddress;

public class ClientProperties {
	
	private InetAddress m_addr = null;
	
	public ClientProperties(InetAddress address) {
		m_addr = address;
	}
	
	public InetAddress getInetAddress() {
		return m_addr;
	}
}
