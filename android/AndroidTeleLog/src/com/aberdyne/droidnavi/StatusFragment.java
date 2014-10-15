package com.aberdyne.droidnavi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aberdyne.droidnavi.MulticastTestDialog.MulticastTestCallback;
import com.aberdyne.droidnavi.R;
import com.aberdyne.droidnavi.client.CallMonitorService;
import com.aberdyne.droidnavi.client.MulticastSender;
import com.aberdyne.droidnavi.client.NetworkDispatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * The fragment for the Status tab
 * 
 * @author Jeremy May
 *
 */
public class StatusFragment extends Fragment {
	
	private View m_view = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * View is recycled and removed from parent on each create.
		 * 
		 * This is done to avoid creating a new fragment with the same
		 * id and causing an inflation exception
		 */
		if(m_view != null) { 
			ViewGroup parent = (ViewGroup) m_view.getParent();
			if(parent != null) {
				parent.removeView(m_view);
			}
		}
		try {
			m_view = inflater.inflate(R.layout.layout_status, container, false);
		} catch(InflateException e) {}
		
		return m_view;
	}
	
	/**
	 * The List Fragment implementation
	 * 
	 * @author Jeremy May
	 *
	 */

	public static class StatusListFragment extends ListFragment {
		
		private ServiceStatusView m_service = null;
		private MulticastStatusView m_multi = null;
				
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			List<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();
			// Status
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("title", "status");
			temp.put("desc", "test desc");
			array.add(0, temp);
			
			// Multicast
			temp = new HashMap<String, String>();
			temp.put("title", "multicast");
			temp.put("desc", "multicast desc");
			array.add(1, temp);
			
			StatusAdapter adapter = new StatusAdapter(inflater.getContext(), array,
					android.R.layout.simple_list_item_2,
					new String[] {"title", "desc"},
					new int[] { android.R.id.text1,
								android.R.id.text2 });
			this.setListAdapter(adapter);
			
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			switch(position) {
			case 0: // Service Status
				boolean running = false;
				if(!CallMonitorService.isRunning()) {
					ComponentName name = getActivity().startService(new Intent(getActivity(), CallMonitorService.class));
					running = (name == null) ? false : true;
				}
				else {
					getActivity().stopService(new Intent(getActivity(), CallMonitorService.class));				
				}
				m_service.update(running);
				break;
			case 1: // Multicast Status
				if(MulticastSender.checkMulticastAvailability()) {
					MulticastTestDialog.showDialog(getActivity(), new MulticastTestCallback() {
						public void onTestFinish() {
							m_multi.update();
						}
					});
				}
				break;
			}
			super.onListItemClick(l, v, position, id);
		}
		
		/**
		 * The list adapter
		 * 
		 * @author Jeremy May
		 *
		 */
		class StatusAdapter extends SimpleAdapter {
			
			public StatusAdapter(Context context,
					List<? extends Map<String, String>> data, int resource,
					String[] from, int[] to) {
				super(context, data, resource, from, to);
				
				m_service = new ServiceStatusView(context);
				m_multi = new MulticastStatusView(context);
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				StatusView item = null;

				switch(position) {
				case 0:	
					item = m_service;
					break;
				case 1:
					item = m_multi;
					break;
				}
				return item.getView(position, convertView, parent);
			}
		}
		
		/**
		 * Abstract class for both items on the status list
		 * 
		 * @author Jeremy May
		 *
		 */
		abstract class StatusView extends LinearLayout {
			protected TextView m_status = null;
			protected TextView m_desc = null;
			
			abstract public void update();
			
			private StatusView(Context context) {
				super(context);
								
				this.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 
																	ListView.LayoutParams.MATCH_PARENT));
				this.setOrientation(LinearLayout.VERTICAL);
				
				// Set the padding using screen density
				float scale = getResources().getDisplayMetrics().density;
				int size = (int) (50 * scale * 0.5f);
				int padding_left = (int)(30 * scale * 0.5f);
				this.setPadding(padding_left, size, 0, size);
				
				m_status = new TextView(getContext());
				m_desc = new TextView(getContext());
			}
			
			public StatusView(Context context, String desc) {
				this(context);
				
				m_desc.setText(desc);

				m_status.setTextSize(18);
				m_desc.setTextSize(10);
				
				this.addView(m_status, 0);
				this.addView(m_desc, 1);
			}
			
			public View getView(int position, View convertView, ViewGroup parent) {	
				update();
				return this;
			}
			
			protected Spannable createSpan(String start_text, String color_text, int color) {
				String full = start_text + color_text;
				int start = full.indexOf(color_text);
				int end = full.length();
				Spannable span = new SpannableString(start_text + color_text);
				span.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				
				return span;
			}
		}
		
		/**
		 * The multicast status row view
		 * @author Jeremy May
		 *
		 */
		class MulticastStatusView extends StatusView {
			
			public MulticastStatusView(Context context) {
				super(context, "Click to test multicast network availability");
			}

			@Override
			public void update() {
				String status = getStatusText();
				int status_color = getStatusColor();
				
				Spannable span = createSpan("Multicast Status: ", status, getResources().getColor(status_color));
				m_status.setText(span, TextView.BufferType.SPANNABLE);				
			}

			protected int getStatusColor() {
				if(MulticastSender.checkMulticastAvailability()) {
					SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

					if(pref.getBoolean(NetworkDispatch.MULTI_IS_NETWORK_TESTED_SETTING, false)) {
						return pref.getBoolean(NetworkDispatch.MULTI_NETWORK_TEST_RESULT_SETTING, false)  ? 
								android.R.color.holo_green_dark : android.R.color.holo_red_dark;
					}
					else {
						return android.R.color.holo_blue_dark;
					}
				}

				return android.R.color.primary_text_dark;
			}

			protected String getStatusText() {
				if(MulticastSender.checkMulticastAvailability()) {
					SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
					
					if(pref.getBoolean(NetworkDispatch.MULTI_IS_NETWORK_TESTED_SETTING, false)) {
						return pref.getBoolean(NetworkDispatch.MULTI_NETWORK_TEST_RESULT_SETTING, false) ?
								"Available / Network OK" : "Available / Network FAIL";
					}
					else {
						return "Available / Network not tested";
					}
				}
				else {
					return "Unavailable";
				}
			}
		}
	
		/**
		 * The row view showing the current status of the background
		 * service.
		 * 
		 * @author Jeremy May
		 *
		 */
		class ServiceStatusView extends StatusView {
			public ServiceStatusView(Context context) {
				super(context,"Toggle sending events to PC");
			}
			
			public void update() {
				update(CallMonitorService.isRunning());
			}
			
			public void update(boolean running) {
				String status = getStatusText(running);
				int status_color = getStatusColor(running);
				
				Spannable span = createSpan("Service Status: ", status, getResources().getColor(status_color));
				m_status.setText(span, TextView.BufferType.SPANNABLE);
			}
			
			protected int getStatusColor (boolean running) {
				return running ? android.R.color.holo_green_light : android.R.color.holo_red_dark;
			}
			
			protected String getStatusText(boolean running) {
				return running ? "Running" : "Off";
			}
			
		}
	}
}