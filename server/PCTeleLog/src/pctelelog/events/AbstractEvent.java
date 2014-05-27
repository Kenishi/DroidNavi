package pctelelog.events;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import pctelelog.Device;

@JsonTypeInfo( use = Id.NAME )
@JsonSubTypes(
		{
			@Type(name = "ClientConnect", value = ClientConnectEvent.class),
			@Type(name = "MissedCall", value = MissedCallEvent.class),
			@Type(name = "IncomingCall", value = IncomingCallEvent.class),
			@Type(name = "CallEnded", value = CallEndedEvent.class),
			@Type(name = "Shutdown", value = ShutdownEvent.class),
			@Type(name = "Hello", value = HelloEvent.class),
		}
)

public abstract class AbstractEvent {
	
	private EventType m_eventType = null;
	@JsonProperty("time") private Date m_time = null;
	
	private Device m_device = Device.NO_DEVICE;
	
	public AbstractEvent(EventType type, Date time) {
		m_eventType = type;
		m_time = time;
	}
	
	public AbstractEvent(AbstractEvent event) {
		this.m_eventType = event.m_eventType;
		this.m_time = event.m_time;
		this.m_device = event.m_device;
	}
	
	public EventType getEventType() {
		return m_eventType;
	}
	
	public Device getDevice() {
		return m_device;
	}
	
	public void setDevice(Device device) {
		m_device = device;
	}
	/**
	 * Return the time when the event occurred.
	 * @return A Date object
	 */
	@JsonProperty("time")
	public Date getEventTime() {
		return m_time;
	}
	
	public String toString() {
		return 	m_eventType.toString() + ": " +
				m_time.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		try {
			// If cast attempt fails, then not an Event and not equal
			AbstractEvent event = AbstractEvent.class.cast(obj);
			
			// Compare Devices if both have one
			if(event.getDevice() != null && this.getDevice() != null) {
				if(! event.getDevice().equals(this.getDevice()))
					return false;
			}
			else if(event.getDevice() != this.getDevice()) { // Check if matching nulls
				return false;
			}
			// Compare EventTime if both have one
			if(event.getEventTime() != null && this.getEventTime() != null) {
				if(! event.getEventTime().equals(this.getEventTime()))
					return false;
			}
			// Check if matching nulls
			else if(event.getEventTime() != this.getEventTime()) {
				return false; 
			}
			else {
				return false;
			}
		
			// Compare EventType if both have one
			if(event.getEventType() != null && this.getEventType() != null) {
				if(! event.getEventType().equals(this.getEventType()))
					return false;
			}
			// Check if matching nulls
			else if(event.getEventType() != this.getEventType()) {
				return false;
			}
		} catch(ClassCastException e) {
			return false;
		}
		
		return true;
	}
	
}
