package pctelelog;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import pctelelog.events.AbstractEvent;

/**
 * The EventSerializer will serialize an event and deserialize it back
 * into its original concrete event.
 * 
 * @author Kei
 *
 */
public class EventSerializer {
	static private final ObjectMapper mapper = new ObjectMapper();
	
	static public String serialize(AbstractEvent event) 
			throws JsonGenerationException, JsonMappingException, IOException {
		String json = mapper.writeValueAsString(event);
		return json;
	}
	static public AbstractEvent deserialize(String json)
			throws JsonParseException, JsonMappingException, IOException {
		AbstractEvent event = mapper.readValue(json, AbstractEvent.class);
		return event;
	}
	
	static public AbstractEvent deserialize(JsonNode node)
			throws JsonParseException, JsonMappingException, IOException {
		AbstractEvent event = mapper.readValue(node, AbstractEvent.class);
		
		return event;
	}
	
}
