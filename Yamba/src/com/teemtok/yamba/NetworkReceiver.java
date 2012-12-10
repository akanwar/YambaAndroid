package com.teemtok.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver { //
	public static final String TAG = "NetworkReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false); //

		if (isNetworkDown) {
			Log.d(TAG, "Network is DOWN unsetting alarm ");
			if (((YambaApplication) context.getApplicationContext()).stopAlarms(context)) {
				Log.d(TAG, "stopalarm was sucessful");
			} else {
				Log.d(TAG, "stopalarm did not work");
			}
		} else {
			Log.d(TAG, "Network is UP setting alarm ");
			if (((YambaApplication) context.getApplicationContext()).setAlarms(context)) {
				Log.d(TAG, "setalarm was sucessful");
			} else {
				Log.d(TAG, "setalarm did not work");
			}
		}
	}
}
