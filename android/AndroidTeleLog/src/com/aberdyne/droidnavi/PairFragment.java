package com.aberdyne.droidnavi;

import com.aberdyne.droidnavi.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PairFragment extends Fragment {
	
	private View m_view = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
			 m_view = inflater.inflate(R.layout.layout_pair, container, false);
	 	} catch(InflateException e) {}
	 	
		return m_view;
	}
}
