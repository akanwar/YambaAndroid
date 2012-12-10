package com.teemtok.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	// YambaApplication yamba;
	private static final String TAG = BootReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent callingIntent) {
		// Check if we should do anything at boot at all
		if (((YambaApplication) context.getApplicationContext()).setAlarms(context)) {
			Log.d(TAG, "setalarm was sucessful");
		} else {
			Log.d(TAG, "setalarm did not work");
		}
	}
}