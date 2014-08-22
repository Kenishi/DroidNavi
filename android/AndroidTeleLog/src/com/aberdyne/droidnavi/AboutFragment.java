package com.aberdyne.droidnavi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AboutFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_about, container, false);
		
		// Set App Icon in About window
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
		ImageView app_icon = (ImageView)view.findViewById(R.id.about_app_icon);
		app_icon.setImageDrawable(drawable);
		
		return view;
	}
}
