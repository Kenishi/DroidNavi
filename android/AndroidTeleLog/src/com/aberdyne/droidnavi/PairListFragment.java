package com.aberdyne.droidnavi;

import java.util.Vector;

import com.aberdyne.droidnavi.client.NonActiveServerConnection;
import com.aberdyne.droidnavi.client.ServerConnection;
import com.aberdyne.droidnavi.client.ServerListManager;
import com.aberdyne.droidnavi.client.ServerListManager.ServerListListener;
import com.aberdyne.droidnavi.zxing.IntentIntegrator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PairListFragment extends ListFragment {
	
	public PairListFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ServerListAdapter adapter = new ServerListAdapter(inflater.getContext(),
				android.R.layout.simple_list_item_1);
		this.setListAdapter(adapter);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		registerForContextMenu(getListView());  // Enable the Context Menu for items
				
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		ListItem item = (ListItem)getListView().getItemAtPosition(info.position);
		
		/* Let the manager handle settings up the menus */
		ContextMenuManager.onCreateContextMenu(item, menu, v, menuInfo);

		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		/* Let the manager handle commands */
		ContextMenuManager.onContextItemSelected(item, getActivity());
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Overloaded in order to catch single clicks on "Pairing"
	 * A single click on "Pairing" will open the context menu for it.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ListItem item = (ListItem)this.getListView().getItemAtPosition(position);
		if(item == ListItem.PAIR_ITEM) {
			v.performLongClick();
		}
		
		super.onListItemClick(l, v, position, id);
	}

	public class ServerListAdapter extends ArrayAdapter<ListItem> implements ServerListListener {
		private Vector<ListItem> items = new Vector<ListItem>();
		
		public ServerListAdapter(Context context, int resource) {
			super(context, resource);
			items.add(ListItem.PAIR_ITEM);
			updateList();
			
			ServerListManager.addServerListListener(this);;
			ServerListManager.getSync(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent); 
			ListItem item = items.get(position);
			TextView txtView = (TextView)view.findViewById(android.R.id.text1);
			
			if(item != ListItem.PAIR_ITEM) {
				int color = item.isConnected() ? 
						view.getResources().getColor(android.R.color.holo_green_dark) : 
						view.getResources().getColor(android.R.color.holo_red_dark);
				txtView.setTextColor(color);
			}
			else {
				txtView.setTextColor(view.getResources().getColor(android.R.color.primary_text_dark));
			}
			
			return view;
		}
		
		public void onServerListChange(Action action, ServerConnection server) {
			ListItem newItem = ListItem.createItem(server);
			switch(action) {
			case SYNC:
				if(items.contains(newItem))
					break;
			case ADD:
				if(items.contains(newItem))
					break;
				else
				items.insertElementAt(newItem, 0);
				break;
			case REMOVE:
				items.remove(newItem);
				break;
			case UPDATE:
				// newItem IP = oldItem IP, but state is different now
				int position = items.indexOf(newItem);
				items.set(position, newItem);
				break;
			default:
			}
			updateList();
		}
		
		private void updateList() {
			clear();
			addAll(items);
			notifyDataSetChanged();
		}
	}
	
	public static class ListItem {
		public static final ListItem PAIR_ITEM = new ListItem("Pair with new PC...", false);
		
		private String m_text = null;
		private boolean m_isConnected = false;
		
		private ListItem(String text, boolean isConnected) {
			m_text = text;
			m_isConnected = isConnected;
		}
		
		/**
		 * Factory method for creating a ListItem
		 * @param inet An InetAddress to add to the list
		 * @return
		 */
		static public ListItem createItem(ServerConnection server) {
			if(server == null) {
				throw new NullPointerException("Create List Item: Inet was null.");
			}
			ListItem item = new ListItem(server.toString(), server.isConnected()); 
			return item;
		}
		
		public boolean isConnected() {
			return m_isConnected;
		}
		
		@Override
		public String toString() {
			return m_text;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null) {
				return false;
			}
			try {
				ListItem item = ListItem.class.cast(o);
				return this.toString().equals(item.toString());
			} catch(ClassCastException e) { 
				return false;
			}
		}
	}

	static private class ContextMenuManager {
		/**
		 * An enum containing all the Context Menu items
		 * @author Jeremy May
		 *
		 */
		private enum MenuItems {
			MANUAL_INPUT("Manually input IP"),
			QRCODE("Pair using QR Code"),
			REMOVE("Remove pairing");
			
			private String m_menuText = null;
			
			public String getMenuText() {
				return m_menuText;
			}
			
			/**
			 * Retrieve a string array for the menu items that
			 * should appear for Paired/Ip menu items.
			 * @return A string array holding the text for each context menu item.
			 */
			static public MenuItems[] getIpMenuItems() {
				MenuItems[] list = {
						MenuItems.REMOVE
				};
				return list;
			}
			
			/**
			 * Retrieve the menu items that should appear with the "Pair with a Pc."
			 * option.
			 * @return A string array holding the text for each context menu item.
			 */
			static public MenuItems[] getPairingMenuItems() {
				MenuItems[] list = { 
						MenuItems.MANUAL_INPUT,
						MenuItems.QRCODE
				};
				return list;
			}
			
			static public MenuItems getItemById(int id) {
				MenuItems item = MenuItems.values()[id];
				return item;
			}
			
			public int getItemId() {
				return this.ordinal(); 
			}
			
			private MenuItems(String menuText) {
				m_menuText = menuText;
			}
		}
		
		/**
		 * The actual handler for item selection
		 * @param item The MenuItem selected
		 * @param parent The context for the activity
		 * @param callback An object implementing the ListEditable methods.
		 * @return
		 */
		static public boolean onContextItemSelected(MenuItem item, Context parent) {
			int itemId = item.getItemId();
			MenuItems selected = MenuItems.getItemById(itemId);
			
			switch(selected) {
			case MANUAL_INPUT:
				IpInputDialog.showDialog(parent);
				break;
			case QRCODE:
				IntentIntegrator integrator = new IntentIntegrator((Activity)parent);
				integrator.initiateScan();
				break;
			case REMOVE:
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
				String ip = ((TextView)info.targetView).getText().toString();
				ServerConnection server = new NonActiveServerConnection(ip);
				ServerListManager.removeServer(parent, server);
				break;
			}
			
			return false;
		}
		
		/**
		 * The actual handler for the List's Context Menu creation
		 * @param item The ListItem that triggered the Context Menu
		 * @param menu See android doc.
		 * @param v See android doc.
		 * @param menuInfo See android doc.
		 */
		static public void onCreateContextMenu(ListItem item, ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			MenuItems[] menuItems = null;
			if(item == ListItem.PAIR_ITEM) {
				menuItems = MenuItems.getPairingMenuItems();
			}
			else {
				menuItems = MenuItems.getIpMenuItems();
			}
			
			for(int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, menuItems[i].getItemId(), i, menuItems[i].getMenuText());
			}
		}
	}
}
