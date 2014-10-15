package pctelelog.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * The pairing assistant for the android app
 * 
 * @author Jeremy May
 *
 */
public class PairingWindow implements WindowWidget, SelectionListener, DisposeListener {

	/** Event Loop **/
	private Display m_mainDisplay = null;
	private Shell m_windowShell = null;
	
	private ArrayList<String> m_ips = null;
	private Label m_qrcode = null;
	
	public PairingWindow(Display display) {
		if(display == null) { throw new NullPointerException("Display cannot be null"); }
		
		m_mainDisplay = display;
		m_windowShell = new Shell(getMainDisplay(), SWT.CLOSE | SWT.APPLICATION_MODAL);
		m_windowShell.setSize(270, 300);		
		
		init();
		m_windowShell.pack();
	}
	
	public Shell getWindowShell() {
		return m_windowShell;
	}

	public Display getMainDisplay() {
		return m_mainDisplay;
	}

	/**
	 * Open/Show the pairing window
	 * 
	 */
	public void open() {
		getWindowShell().open();
	}
	
	/**
	 * Handler for Network LIst selections
	 * 
	 */
	public void widgetSelected(SelectionEvent e) {
		// Get selected item text
		List source = (List) e.getSource();
		int index = source.getSelectionIndex();
		String ip = m_ips.get(index);
		
		if(ip == "" || ip == null) { return; }
		else {
			displayQRImage(ip);
		}
	}

	/**
	 * Handle cleanup of resources on window dispose
	 * 
	 */
	public void widgetDisposed(DisposeEvent e) {
		clearQRImage();
	}
	
	/**
	 * Get the Array List of IPs
	 * 
	 * @return an array list of ips
	 */
	private ArrayList<String> getIPs() {
		return m_ips;
	}
	
	/**
	 * Get the QR Code Label for the window
	 * 
	 * @return the label
	 */
	private Label getQRCodeLabel() {
		return m_qrcode;
	}
	
	/**
	 * Set up the Window
	 * 
	 */
	private void init() {
		m_ips = enumInterfaces();
		
		createLayout();
		createNetworkList();
		createQRCodeLabel();
		createCloseButton();
	}
	
	/**
	 * Get the IP of all non-loopback
	 * devices
	 * 
	 * @return an array of IP Strings
	 */
	private ArrayList<String> enumInterfaces() {
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		ArrayList<NetworkInterface> list = Collections.list(interfaces);
		ArrayList<String> strInterface = new ArrayList<String>();
		for(NetworkInterface inter : list) {
			try {
				if(inter.isLoopback()) { continue; }
				else {
					for(InetAddress addr : Collections.list(inter.getInetAddresses())) {
						strInterface.add(addr.getHostAddress());
					}
				}
			} catch(SocketException e) { continue; }
		}
		
		return strInterface;
	}
	
	/**
	 * Create the layout
	 * 
	 */
	private void createLayout() {
		GridLayout layout = new GridLayout(1, true);
		getWindowShell().setLayout(layout);
	}
	
	/**
	 * Create the Network List
	 * 
	 */
	private void createNetworkList() {
		Label listTxt = new Label(getWindowShell(), SWT.LEFT);
		listTxt.setText("Select address to create QRCode:");
		listTxt.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		List list = new List(getWindowShell(), SWT.SINGLE);
		list.addSelectionListener(this);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// Add IPs to list
		for(String ip : getIPs()) {
			list.add(ip);
		}
	}
	
	/**
	 * Create and Assignthe QR Code label that will
	 * be used for the pairing window
	 * 
	 */
	private void createQRCodeLabel() {
		m_qrcode = new Label(getWindowShell(), SWT.CENTER | SWT.BORDER);
		m_qrcode.setSize(250, 250);
		m_qrcode.setBackground(getMainDisplay().getSystemColor(SWT.COLOR_WHITE));
		m_qrcode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		m_qrcode.setText("No Address Selected");
	}
	
	private void createCloseButton() {
		Button close = new Button(getWindowShell(), SWT.PUSH);
		close.setText("Close");
		close.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		close.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWindowShell().dispose();
			}
		});
		
		getWindowShell().setDefaultButton(close);
	}

	/**
	 * Show the QR Code for the IP
	 * 
	 * @param ip an IP
	 */
	private void displayQRImage(String ip) {
		// Dispose of the old image
		clearQRImage();
		
		// Generate QRCode
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		QRCode.from(ip).to(ImageType.PNG).withSize(250, 250).writeTo(stream);
		ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
		
		// Generate Image and Show it
		if(getQRCodeLabel().getImage() != null) { getQRCodeLabel().getImage().dispose(); }
		Image img = new Image(getMainDisplay(), input);
		getQRCodeLabel().setImage(img);
		getQRCodeLabel().setToolTipText("QR for: " + ip);
		getWindowShell().pack();		
	}
	
	/**
	 * Dispose of the old QR image if it exists
	 * 
	 */
	private void clearQRImage() {
		Image image = getQRCodeLabel().getImage();
		if(image != null) {
			image.dispose();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {}
}
