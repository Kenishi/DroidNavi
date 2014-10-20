package com.aberdyne.droidnavi;

import java.util.Date;

import pctelelog.ContactInfo;
import pctelelog.PhoneNumber;
import pctelelog.ContactInfo.Email;
import pctelelog.ContactInfo.Name;
import pctelelog.events.IncomingCallEvent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.view.View;

import com.aberdyne.droidnavi.client.MulticastSender;
import com.aberdyne.droidnavi.client.NetworkDispatch;

public class MulticastTestDialog {

	
	public static void showDialog(final Context context, final MulticastTestCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle("Multicast Network Test")
		.setMessage("First, make sure the desktop app is running.\n"
				+ "Next, press \"Test\".\n"
				+ "If you receive an Incoming Call event with the name \"Multicast Test\", then you can use Multicast.\n\n"
				+ "Did you see the multicast test event?")
		.setNegativeButton("No", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				setNetworkTestResult(context, false);
				callback.onTestFinish();
			}
		})
		.setPositiveButton("Yes", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				setNetworkTestResult(context, true);
				callback.onTestFinish();
			}
		})
		.setNeutralButton("Test", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}});
		
		AlertDialog alert = builder.create();
		alert.show();
		alert.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				fireMulticastTest();
			}
		});
	}
	
	private static void setNetworkTestResult(Context context, boolean result) {
		SharedPreferences pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(NetworkDispatch.MULTI_IS_NETWORK_TESTED_SETTING, true);
		editor.putBoolean(NetworkDispatch.MULTI_NETWORK_TEST_RESULT_SETTING, result);
		editor.commit();
	}

	private static void fireMulticastTest() {
		ContactInfo info = new ContactInfo(
				new Name("Multicast Test", "Multicast", "Test"),
				new PhoneNumber("000-000-0000"),
				new Email("multicast_test"));
		IncomingCallEvent event = new IncomingCallEvent(new Date(), info);
		MulticastSender.sendEvent(event);
	}
	
	interface MulticastTestCallback {
		public void onTestFinish();
	}

}
