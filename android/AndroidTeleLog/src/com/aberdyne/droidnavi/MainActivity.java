package com.aberdyne.droidnavi;

import com.aberdyne.androidtelelog.R;
import com.aberdyne.droidnavi.client.ServerListManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

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
				
			}
		}
		
		UiAdapter adapter = new UiAdapter(getSupportFragmentManager());
		ViewPager pager = (ViewPager)findViewById(R.id.viewPager);
		pager.setAdapter(adapter);
		
		ServerListManager.init(this);
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
		STATUS_PAGE(0),
		PAIR_PAGE(1);
		
		private int page_num;
		
		static public Pages getPage(int n) {
			Pages[] pages = Pages.values();
			for(Pages page : pages) {
				if(page.page_num == n)
					return page;
			}
			return null;
		}
		
		private Pages(int n) {
			page_num = n;
		}
		
	}
}
