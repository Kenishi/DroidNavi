package com.aberdyne.droidnavi;

import com.aberdyne.droidnavi.client.ServerConnection;
import com.aberdyne.droidnavi.client.ServerListManager;

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
	public static void showDialog(final Context parent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		builder.setTitle("Input IPv4 Address");
		
		final EditText input = new EditText(builder.getContext());
		input.setInputType(InputType.TYPE_CLASS_PHONE);
		
		builder.setView(input);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String ip = input.getText().toString();
				if(ServerConnection.validateHost(ip)) {
					ServerListManager.addServer(parent, new ServerConnection(ip));
				}
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.show();
	}
}
