package pctelelog;

import java.net.InetAddress;
import java.net.Socket;

public interface Client {

	boolean handshake();

	void start();

	InetAddress getInetAddress();

}
