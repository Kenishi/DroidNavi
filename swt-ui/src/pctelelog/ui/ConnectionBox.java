package pctelelog.ui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import pctelelog.Device;
import pctelelog.EventListener;
import pctelelog.events.AbstractEvent;
import pctelelog.events.ClientConnectEvent;
import pctelelog.internal.events.ClientSocketClosedEvent;

/**
 * The Connection Box class is wrapper
 * for the Connection List on the Main Window.
 * It serves to show what phones are connected
 * to the desktop via TCP.
 * 
 * @author Jeremy May
 *
 */
public class ConnectionBox implements EventListener {

	/** List instance **/
	private List m_list = null;
	
	/** List Array **/
	Vector<Device> m_connectedDevices = new Vector<Device>();
		
	/** Parent Window/Shell **/
	private Shell m_parent = null;
	
	public ConnectionBox(Shell parent) {
		if(parent == null) {
			throw new NullPointerException("Parent cannot be null.");
		}
		
		m_parent = parent;
		
		m_list = new List(getParent(), SWT.SINGLE | SWT.BORDER );
		
		init();
	}
	
	/**
	 * Triggered when a new Event is received
	 */
	public void onEvent(AbstractEvent event) {
		if(event == null) { return; }
		
		Device eventDevice = event.getDevice();
		
		if(event instanceof ClientConnectEvent) {
			if(m_connectedDevices.contains(eventDevice)) {
				// Remove from List
				int index = m_connectedDevices.indexOf(eventDevice);
				m_list.remove(index);
				m_connectedDevices.remove(eventDevice);
			}

			// Add to list
			m_connectedDevices.add(eventDevice);
			m_list.add(eventDevice.toString());
		}
		else if(event instanceof ClientSocketClosedEvent) {
			int index = m_connectedDevices.indexOf(eventDevice);
			m_list.remove(index);
			m_connectedDevices.remove(eventDevice);
		}
	}
	
	/**
	 * Get the SWT List control instance
	 * @return
	 */
	public List getList() {
		return m_list;
	}
	
	private void init() {
		// Set grid data
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		m_list.setLayoutData(data);
	}
	
	private Shell getParent() {
		return m_parent;
	}
}
