package pctelelog;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import pctelelog.events.AbstractEvent;

public class Packet {
	
	public static final double DATA_SPLIT = 1200; 
	
	@JsonProperty("id") private long m_id;
	@JsonProperty("seq") private int m_seq;
	@JsonProperty("max_seq") private int m_seq_max;
	@JsonProperty("data") public byte[] m_data;
	
	@JsonCreator
	private Packet(@JsonProperty("id")long id, @JsonProperty("seq")int seq,
			@JsonProperty("max_seq")int seq_max, @JsonProperty("data")byte[] data) {
		m_id = id;
		m_seq = seq;
		m_seq_max = seq_max;
		m_data = data;
	}
	
	public static Packet[] createPackets(String json) {
		int count = (int)Math.ceil(json.getBytes().length / DATA_SPLIT);
		Vector<Packet> packets = new Vector<Packet>(count);
		long id = System.currentTimeMillis();
		int size = json.getBytes().length;
		
		for(int i=0; i < count; ++i) {
			int start = i * (int)DATA_SPLIT;
			int end = (count == 1) ? size : start + (int)DATA_SPLIT;
			byte[] pack = Arrays.copyOfRange(json.getBytes(), start, end);
			packets.add(new Packet(id, i, count, pack));
		}
		return packets.toArray(new Packet[count]);
	}
	
	public static Packet[] createPackets(AbstractEvent event) {
		String json = null;
		try {
			json = EventSerializer.serialize(event);
		} catch(Exception e) {
			return null;
		}
		
		if(json == null) {
			return null;
		}
		
		return createPackets(json);
	}
	
	@JsonProperty("id")
	public long getId() {
		return m_id;
	}
	
	@JsonProperty("seq")
	public int getSequence() {
		return m_seq;
	}
	
	@JsonProperty("max_seq")
	public int getMaxSequency() {
		return m_seq_max;
	}
	
	@JsonProperty("data")
	public byte[] getData() {
		return m_data;
	}
	
	public byte[] serialize() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(this);
			return json.getBytes();
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Packet deserialize(byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.readValue(data, Packet.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
			return null;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
