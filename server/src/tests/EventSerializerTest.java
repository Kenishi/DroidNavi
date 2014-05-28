package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

import pctelelog.ContactInfo;
import pctelelog.ContactInfo.Email;
import pctelelog.ContactInfo.Name;
import pctelelog.ContactInfo.Photo;
import pctelelog.EventSerializer;
import pctelelog.PhoneNumber;
import pctelelog.Device.InvalidDeviceException;
import pctelelog.events.AbstractEvent;
import pctelelog.events.IncomingCallEvent;

public class EventSerializerTest {
	
	@Test
	public void shouldSerializeFull()
			throws InvalidDeviceException, JsonGenerationException, JsonMappingException, IOException {
		// Setup
		Name name = new Name("Bobby", "Bob", "Johnson");
		Email email = new Email("bobby@gmail.com");
		Photo photo = new Photo("DEADBEEF");
		PhoneNumber number = new PhoneNumber("123-456-7890");
		Date time = new Date();
		ContactInfo info = new ContactInfo(name, number, email, photo);
		
		IncomingCallEvent event = new IncomingCallEvent(time, info);
		
		// Exercise
		System.out.println(EventSerializer.serialize(event));
		
	}
	
	@Test
	public void shouldDeserialize()
			throws JsonParseException, JsonMappingException, IOException, InvalidDeviceException {
		// Setup
		Name name = new Name("Bobby", "Bob", "Johnson");
		Email email = new Email("bobby@gmail.com");
		Photo photo = new Photo("DEADBEEF");
		PhoneNumber number = new PhoneNumber("123-456-7890");
		Date time = new Date();
		ContactInfo info = new ContactInfo(name, number, email, photo);

		IncomingCallEvent event = new IncomingCallEvent(time, info);
		String json = EventSerializer.serialize(event);
		
		// Exercise
		AbstractEvent testEvent = EventSerializer.deserialize(json);
		IncomingCallEvent testRecast = (IncomingCallEvent)testEvent;
		
		// Test
		assertEquals(event, testRecast);
	}

}
