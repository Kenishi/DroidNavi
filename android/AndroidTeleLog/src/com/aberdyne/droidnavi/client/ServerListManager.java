package com.aberdyne.droidnavi.client;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import com.aberdyne.droidnavi.client.ServerListManager.ServerListListener.Action;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ServerListManager {
	
	private static boolean isStarted = false; // False till
	private static final String PREF_SERVER_LIST = "serverList";
	
	private ServerListManager() {}
	
	/**
	 * Initialize the ServerList and inform all listeners of servers.
	 * 
	 * This method only needs to be called once but should be called
	 * 	after all listeners have added them to dispatch list.
	 * @param context
	 */
	public static void init(Context context) {
		if(!isStarted) {
			Set<String> serverList = getSet(context);
			for(String server : serverList) {
				ServerConnection connection = new ServerConnection(server);
				fireEvent(Action.ADD, connection, context);
			}
			isStarted = true;
		}
	}
	
	public static void getSync(Context context) {
		Set<String> set = getSet(context);
		for(String ip : set) {
			ServerConnection server = new ServerConnection(ip);
			fireEvent(Action.SYNC, server, context);
		}
	}
	
	public static void addServerListListener(ServerListListener listener) {
		Dispatch.addListener(listener);
	}
	public static void removeServerListListener(ServerListListener listener) {
		Dispatch.removeListener(listener);
	}
	
	public static synchronized boolean addServer(Context context, ServerConnection ip) {
		if(ip == null || context == null) {
			throw new NullPointerException();
		}
		
		String ip_str = ip.toString();
		TreeSet<String> set = getSet(context);
		
		// Add server and Update Preferences
		try {
			if(! set.add(ip_str))
				return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		boolean result = updatePref(context, set);
		
		if(result == true) {
			fireEvent(Action.ADD, ip, context);
		}
		
		return result;
	}
	
	public static synchronized boolean removeServer(Context context, ServerConnection ip) {
		if(context == null || ip == null) {
			throw new NullPointerException();
		}
		String ip_str = ip.toString();
		TreeSet<String> set = getSet(context);
		
		// Remove server and Update Preferences
		try {
			if(! set.remove(ip_str))
				return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		boolean result = updatePref(context, set);
		
		if(result == true) {
			fireEvent(Action.REMOVE, ip, context);
		}
		return result;
	}
	
	public static synchronized void updateServer(Context context, ServerConnection ip) {
		fireEvent(Action.UPDATE, ip, context);
	}
	
	private static boolean updatePref(Context context, TreeSet<String> serverList) {
		assert context != null;
		assert serverList != null;
		
		SharedPreferences pref = 
				context.getSharedPreferences(PREF_SERVER_LIST, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putStringSet(PREF_SERVER_LIST, serverList);
		return editor.commit();		
	}
	
	private static TreeSet<String> getSet(Context context) {
		SharedPreferences pref = 
				context.getSharedPreferences(PREF_SERVER_LIST, Context.MODE_PRIVATE);
		Set<String> set = pref.getStringSet(PREF_SERVER_LIST, null);
		TreeSet<String> returnSet = (set==null) ? new TreeSet<String>() : new TreeSet<String>(set);
		
		return returnSet;
	}
	
	private static void fireEvent(Action action, ServerConnection ip, Context context) {
		Log.d("ServerListManager", "Event: " + action.toString() + " Ip: " + ip.toString());
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Dispatch(action, ip));
		//Thread t = new Thread(new Dispatch(action, ip));
		//t.start();
	}
	
	public interface ServerListListener {
		public enum Action {
			ADD, // New IP added
			REMOVE, // IP removed
			UPDATE, // State of the server has updated	
			SYNC // Get all IPs
		}
		public void onServerListChange(Action action, ServerConnection server);
	}
	
	private static class Dispatch implements Runnable {
		
		private static CopyOnWriteArraySet<ServerListListener> m_listeners = 
				new CopyOnWriteArraySet<ServerListListener>();
		
		private Action m_action = null;
		private ServerConnection m_server = null;
		
		public Dispatch(Action action, ServerConnection server) {
			assert action != null;
			assert server != null;
			m_action = action;
			m_server = server;
		}
			
		/**
		 * Dispatch the new event in a thread.
		 */
		public void run() {
			for(ServerListListener listener : m_listeners) {
				listener.onServerListChange(m_action, m_server);
			}
		}
		
		public static void addListener(ServerListListener listener) {
			m_listeners.add(listener);
		}
		public static void removeListener(ServerListListener listener) {
			m_listeners.remove(listener);
		}
	}
}
