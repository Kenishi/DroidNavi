package pctelelog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pctelelog.Device.InvalidDeviceException;
import pctelelog.events.AbstractEvent;

/**
 * This class determines who sent an event and adds the Device to the event.
 * 
 * The Device info should be resolved close in time to when the event
 * 	was received.
 * 
 * @author Jeremy May
 *
 */
public class EventDeviceResolver {
	private static final Logger logger = LogManager.getLogger(EventDeviceResolver.class);
	/**
	 * Resolve the device info for the client and attach it to the event.
	 * @param client The client that the event originated from.
	 * @param event The event to attach Device info to.
	 * @return Return the event with the device set
	 */
	static public AbstractEvent resolveDevice(Client client, AbstractEvent event) {
		
		String hostname = client.getInetAddress().getHostName();
		
		Device device;
		try {
			device = new Device(hostname, client.getInetAddress());
		} catch (InvalidDeviceException e) {
			device = Device.UNKNOWN_DEVICE;
			logger.catching(e);
		}
		event.setDevice(device);
		return event;
	}
}
