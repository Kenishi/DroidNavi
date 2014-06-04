package com.aberdyne.droidnavi;

import com.aberdyne.droidnavi.R;
import com.aberdyne.droidnavi.client.CallMonitorService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class StatusFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_status, container, false);
		
		RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.statusLayout);
		RelativeLayout.LayoutParams params = 
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
												RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		
		StatusToggleButton toggleButton = new StatusToggleButton(view.getContext());
		toggleButton.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
											ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.addView(toggleButton, params);
	
		return view;
	}
	
	static public class StatusToggleButton extends ToggleButton {

		public StatusToggleButton(Context context) {
			super(context);
			setTextOn("Service Running");
			setTextOff("Service Off");
			setChecked(false);
		}
		
		@Override
		public void toggle() {
			super.toggle();
			if(isChecked()) {
				getContext().startService(new Intent(getContext(), CallMonitorService.class));
			}
			else {
				getContext().stopService(new Intent(getContext(), CallMonitorService.class));
			}
		}
		
	}
}
