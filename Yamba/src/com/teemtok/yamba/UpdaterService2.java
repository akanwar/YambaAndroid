package com.teemtok.yamba;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class UpdaterService2 extends IntentService {

	private static final String TAG = "UpdaterService2";

	YambaApplication yamba;


	public static final String NEW_ALERT_INTENT = "com.teemtok.yamba.NEW_ALERT";
	public static final String NEW_ALERT_EXTRA_COUNT = "NEW_ALERT_EXTRA_COUNT";
	public static final String RECEIVE_ALERT_NOTIFICATIONS = "com.teemtok.yamba.RECEIVE_ALERT_NOTIFICATIONS";

	public UpdaterService2() {
		super(TAG);
		Log.d(TAG, "UpdaterService constructed");

	}

	@Override
	protected void onHandleIntent(Intent inIntent) {
		Intent intent;
		Log.d(TAG, "onHandleIntent'ing");

		try {
			this.yamba = (YambaApplication) getApplication();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "exception in yambaing");
			e1.printStackTrace();
		}

		Log.d(TAG, "yamba is " + yamba);

		try {
			// Some work goes here...
			if (yamba.isloggedIn()) {
				Log.d(TAG, "Already logged in - calling getLomoAlerts");
				if (yamba.getLomoAlerts()) {
					Log.d(TAG, "Yamba Alerts call successfull");
					int numNewAlerts = yamba.anynewalerts();
					if (numNewAlerts > 0) {
						Log.d(TAG, "We have a new alerts ! numalerts is " + numNewAlerts);
						intent = new Intent(NEW_ALERT_INTENT);
						intent.putExtra(NEW_ALERT_EXTRA_COUNT, numNewAlerts);
						sendBroadcast(intent, RECEIVE_ALERT_NOTIFICATIONS);
						yamba.sendAlertNotification(numNewAlerts);
					}
				} else {
					yamba.setloggedIn(false);
					Log.d(TAG, "Yamba Alerts call unsuccessfull");				
				}

			} else {
				Log.d(TAG,
						"Need to log in  - NOT calling getLomoAlerts, but logging in for next iteration");
				LomoCredentials lomo1 = yamba.getLomoCredentials();
				String ystatus = null;
				if (lomo1 != null) {
					ystatus = yamba.lomoLogin(lomo1);
				} else {
					Log.d(TAG,
							"lomo credemtials are null please enter username pwd or company");
				}

				if (!yamba.isloggedIn()) {
					// if still not logged in after retry
					// some other intervention will be required
					yamba.stopAlarms(getApplicationContext());
				}

				Log.d(TAG, "lomologin status is " + ystatus);
			}

			Log.d(TAG, "Updater ran");

		} catch (Exception e) { //
			// updaterService.runFlag = false;
			Log.d(TAG, "OnHandleIntent Failed");
			e.printStackTrace();
		}

	}


}

