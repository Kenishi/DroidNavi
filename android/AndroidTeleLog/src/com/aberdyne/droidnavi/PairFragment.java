package com.aberdyne.droidnavi;

import com.aberdyne.droidnavi.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PairFragment extends Fragment {
 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	 	View view = inflater.inflate(R.layout.layout_pair, container, false);
		return view;
	}
 
 	@Override
 		public void onDestroyView() {
			/*
			 * Remove child list fragment
			 * Note: This is required as android will not
			 * 	clean up statically created (ie: layout xml) child
			 *  fragments.
			 *  This will cause an exception when it recreates as it will
			 *  see a duplicate ID already exists.
			 */
 			FragmentManager fm = getFragmentManager();
 			Fragment pairList = fm.findFragmentById(R.id.pairListFragment);
 			if(pairList != null) {
 				fm.beginTransaction().remove(pairList).commit();
 			}
 			super.onDestroyView();
 		}
}
