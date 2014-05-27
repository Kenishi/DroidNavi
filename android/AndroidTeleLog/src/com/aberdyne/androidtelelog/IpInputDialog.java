package com.aberdyne.androidtelelog;

import java.net.InetAddress;

import com.aberdyne.androidtelelog.client.ServerConnection;
import com.aberdyne.androidtelelog.client.ServerListManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

/**
 * This is a wrapper for the IP dialog that will show when the user
 * selects "Manually input IP" in the Pairing List context menu.
 * 
 * @author Jeremy May
 *
 */
public class IpInputDialog {
	/**
	 * Display the IP input dialog
	 * @param callback An object implementing the ListEdibtable interface
	 * @param parent An activity context
	 */
	static public void showDialog(final Context parent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		builder.setTitle("Input IPv4 Address");
		
		final EditText input = new EditText(builder.getContext());
		input.setInputType(InputType.TYPE_CLASS_PHONE);
		
		builder.setView(input);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String ip = input.getText().toString();
				if(validate(ip)) {
					ServerListManager.addServer(parent, new ServerConnection(ip));
				}
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.show();
	}
	
	/** 
	 * Validate that string is a proper IPv4 address
	 * @param ip A string that should contain an IPv4 string address
	 * @return True if the string is valid. False if it is not.
	 */
	static private boolean validate(String ip) {
		// Use InetAddress to validate the string
		try {
			InetAddress.getByName(ip);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
}
