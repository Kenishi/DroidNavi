package com.aberdyne.droidnavi;

import com.aberdyne.droidnavi.R;
import com.aberdyne.droidnavi.client.ServerConnection;
import com.aberdyne.droidnavi.client.ServerListManager;
import com.aberdyne.droidnavi.zxing.IntentIntegrator;
import com.aberdyne.droidnavi.zxing.IntentResult;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	public  final String PREF_SERVER_LIST = "serverList";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Check Intent
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if(Intent.ACTION_SEND.equals(action)) {
			if("text/plain".equals(type)) {
				String data = intent.getDataString();
				System.out.println(data);
			}
		}
		
		UiAdapter adapter = new UiAdapter(getSupportFragmentManager());
		final ViewPager pager = (ViewPager)findViewById(R.id.viewPager);
		
		// Register Tab bar and listeners
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}
			
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				
				pager.setCurrentItem(tab.getPosition());
				
			}
			
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};
		
		for(Pages page: Pages.values()) {
			actionBar.addTab(actionBar.newTab()
					.setText(page.getName())
					.setTabListener(tabListener));
		}
		
		// Make selected tab change with swipes
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				getActionBar().setSelectedNavigationItem(position);
			}
		});
		
		pager.setAdapter(adapter);
		
		ServerListManager.init(this);
	}
	
	/**
	 * Called when Barcode Scanner completes scanning. Adds IP to pairing.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = 
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if(scanResult != null) {
			String ip = scanResult.getContents();
			if(ServerConnection.validateHost(ip)) {
				ServerListManager.addServer(this, new ServerConnection(ip));
				Toast.makeText(this, "IP added via QRCode", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(this, "Invalid host or IP supplied", Toast.LENGTH_LONG).show();
			}
		}
		else {
			Toast.makeText(this, "Failed to add IP via QRCode", Toast.LENGTH_SHORT).show();
		}
	}
	
	public PreferenceStore getPreferenceStore() {
		return PreferenceStore.createPreferenceStore(this);
	}
	
	public class UiAdapter extends FragmentPagerAdapter {

		public UiAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int n) {
			Pages page = Pages.getPage(n);
			switch(page) {
			case STATUS_PAGE:
				return new StatusFragment();
			case PAIR_PAGE:
				return new PairFragment();
			case ABOUT_PAGE:
				return new AboutFragment();
			}
			
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Pages.values().length;
		}
		
	}
	
	public enum Pages {
		STATUS_PAGE(0, "Status"),
		PAIR_PAGE(1, "Pair"),
		ABOUT_PAGE(2, "About");
		
		private int m_page_num;
		private String m_page_name;
		
		static public Pages getPage(int n) {
			Pages[] pages = Pages.values();
			for(Pages page : pages) {
				if(page.m_page_num == n)
					return page;
			}
			return null;
		}
		
		public String getName() {
			return m_page_name;
		}
		
		public int getNum() {
			return m_page_num;
		}
		
		private Pages(int page_num, String page_name) {
			m_page_num = page_num;
			m_page_name = page_name;
		}
		
	}
}
