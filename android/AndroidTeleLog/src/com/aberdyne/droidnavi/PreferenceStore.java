package com.aberdyne.droidnavi;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.SharedPreferences;

public class PreferenceStore {
	private static PreferenceStore m_instance = null;
	
	private final String PREF_SERVER_LIST = "serverList";
	private Activity m_parent = null;
	
	private PreferenceStore(Activity activity) {
		m_parent = activity;
	}
	
	static public PreferenceStore createPreferenceStore(Activity activity) {
		if(m_instance == null) {
			m_instance = new PreferenceStore(activity);
		}
		return m_instance;
	}
	
	/**
	 * Add a new server IP to the stored preferences for later retrieval
	 * @param serverIp A string to a valid IP address
	 * @return True if everything went ok. False if something went wrong or if serverIp
	 * 		was null.
	 */
	public synchronized boolean addServer(String serverIp) {
		if(serverIp == null) return false;
		
		// Get the current serverIps or create a new one if it doesn't exist
		SharedPreferences pref = m_parent.getPreferences(Activity.MODE_PRIVATE);
		Set<String> set = pref.getStringSet(PREF_SERVER_LIST, null);
		set = (set == null) ? new TreeSet<String>() : set;
		try {
			set.add(serverIp);
		} catch (Exception e) { 
			return false;
		}
		
		// Replace set
		SharedPreferences.Editor editor = pref.edit();
		editor.putStringSet(PREF_SERVER_LIST, set);
		editor.commit();
		
		return true;
	}
	
	/**
	 * Remove the IP from the server list
	 * @param serverIp A valid IP that should be on the list
	 */
	public synchronized void removeServer(String serverIp) {
		if(serverIp == null) return;
		
		SharedPreferences pref = m_parent.getPreferences(Activity.MODE_PRIVATE);
		Set<String> set = pref.getStringSet(PREF_SERVER_LIST, null);
		if(set != null) {
			set.remove(serverIp);
			

			SharedPreferences.Editor editor = pref.edit();
			editor.putStringSet(PREF_SERVER_LIST, set);
			editor.commit();
		}
	}
	
	/**
	 * Retrieve a copy of the currently stored server list
	 * @return An array of string IPs
	 */
	public ArrayList<String> getServerList() {
		SharedPreferences pref = m_parent.getPreferences(MainActivity.MODE_PRIVATE);
		Set<String> serverSet = pref.getStringSet(PREF_SERVER_LIST, null);
		if(serverSet == null) {
			return null;
		}
		ArrayList<String> list = new ArrayList<String>(serverSet);
		return list;
	}
}