package pctelelog.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferenceManager {
	
	private static PreferenceManager m_manager = new PreferenceManager();	
	private Preferences m_pref = null;
	
	private PreferenceManager() {
		m_pref = Preferences.userNodeForPackage(PreferenceManager.class);
	}
	
	/**
	 * Get an instance of the Preference Manager
	 * 
	 * @return the preference manager
	 */
	public static PreferenceManager getPreferenceManager() {
		return m_manager;
	}
	
	/**
	 * Clear the preferences
	 * 
	 */
	protected void clear() {
		try {
			m_pref.clear();
		} catch (BackingStoreException e) { e.printStackTrace(); }
	}
	
	/**
	 * Clear preferences
	 * 
	 */
	@SuppressWarnings("unused")
	private void debugClear() {
		try {
			m_pref.clear();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the preference value
	 * @param key a lookup key for the preference
	 * @param fallback the fallback value to return if the preference doesn't exit
	 * @return the preference if it exists, or return fallback if it doesn't
	 */
	public Object get(PreferenceKey key, Object fallback) {
		if(key==null) { return fallback; }
		
		Object result = (lookup(key)==null) ? fallback : lookup(key);
		return result;
	}
	
	/**
	 * Set a preference
	 * @param key a lookup key for the preference
	 * @param value the value to set
	 */
	public void set(PreferenceKey key, Serializable value) {
		if(key == null) { throw new NullPointerException("Key cannot be null."); }
		if(value == null) { throw new NullPointerException("Value cannot be null."); }
		String keyStr = key.getKey();
		try {
			ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
			ObjectOutputStream writer = new ObjectOutputStream(streamOut);
			writer.writeObject(value);
			m_pref.putByteArray(keyStr, streamOut.toByteArray());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Actual lookup method that returns the preference
	 * value
	 * 
	 * @param key the lookup key given by the user
	 * @return the preference value if it exists, otherwise null
	 */
	private Object lookup(PreferenceKey key) {
		String keyStr = key.getKey();
		byte[] val = m_pref.getByteArray(keyStr, null); // Lookup pref
		Object prefObj = null;
		
		if(val != null) {
			ObjectInputStream stream = null;
			try {
				stream = new ObjectInputStream(new ByteArrayInputStream(val));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(stream == null) return null;
			
			try {
				prefObj = stream.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return prefObj;
	}
	
}
