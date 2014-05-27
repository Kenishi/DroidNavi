package com.aberdyne.androidtelelog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class QRCodeDialog {
	public static void showDialog(Context parent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		builder.setMessage("The QRCode pairing method is not yet implemented. "
				+ "It will be available in the next minor version.");
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.show();
	}
}
