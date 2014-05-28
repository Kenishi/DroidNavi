package pctelelog;

import java.net.InetAddress;

import org.codehaus.jackson.annotate.JsonIgnoreType;

@JsonIgnoreType
public class Device {
	static final public Device NO_DEVICE;
	static final public Device UNKNOWN_DEVICE;
	
	static {
		try {
			NO_DEVICE = new Device("NO DEVICE", InetAddress.getByName("0.0.0.0"));
			UNKNOWN_DEVICE = new Device("UNKNOWN DEVICE", InetAddress.getByName("0.0.0.0"));
		}
		catch(Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	private String m_name = null;
	private InetAddress m_ip = null;
	
	public Device(String name, InetAddress ip) throws InvalidDeviceException {
		if(name == null && ip == null)
			throw new InvalidDeviceException("Device must have a name OR an IP");
		
		m_name = name;
		m_ip = ip;
	}
	
	public String getName() {
		return m_name;
	}
	
	public InetAddress getIP() {
		return m_ip;
	}
	
	public String toString() {
		String name = "";
		String ip = "";
		
		if(m_name != null) { 
			name = "Device: " + m_name;
		}
		if(m_ip != null) {
			ip = "IP: " + m_ip.toString();
		}
		
		if(name != "")
			return name + "(" + m_ip + ")";
		else
			return ip;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		try {
			Device device = Device.class.cast(obj);
			
			// Compare IPs if both have one
			if(device.getIP() != null && this.getIP() != null) {
				if(! device.getIP().equals(this.getIP())) {
					return false;
				}
			}
			else if(device.getIP() != this.getIP()) { // Check if matching nulls
				return false;
			}

			// Compare device name if both have one
			if(device.getName() != null && this.getName() != null) {
				if(! device.getName().equals(this.getName())) {
					return false;
				}
			}
			else if(device.getName() != this.getName()) { // Check if matching nulls
				return false;
			}
		} catch(ClassCastException e) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("serial")
	public class InvalidDeviceException extends Exception {
		public InvalidDeviceException(String msg) {
			super(msg);
		}
	}
}
