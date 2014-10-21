package pctelelog;

import java.util.Iterator;
import java.util.Vector;

public class PacketSet implements Iterable<Packet> {
	private Vector<Packet> m_set = new Vector<Packet>();
	private int count = 0;
	
	public PacketSet(int maxSequence) {
		m_set.setSize(maxSequence);
	}
	
	public void add(int index, Packet packet) {
		m_set.add(index, packet);
		count++;
	}
	
	public int size() {
		return count;
	}
	
	public Packet get(int index) {
		return m_set.get(index);
	}

	@Override
	public Iterator<Packet> iterator() {
		return m_set.iterator();
	}
	
	
}
