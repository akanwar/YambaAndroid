package com.teemtok.yamba;

import android.app.IntentService;
import android.content.Intent;
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
		
		this.yamba = (YambaApplication) getApplication();
		
		
		try {
			// Some work goes here...
			if (yamba.isloggedIn() ){
				Log.d(TAG, "Already logged in - calling getLomoAlerts");
				if (yamba.getLomoAlerts()) {
					Log.d (TAG,"Yamba Alerts call successfull");
					int numNewAlerts = yamba.anynewalerts(); 
					if ( numNewAlerts > 0 ){
						Log.d(TAG, "We have a new alerts !");
						intent = new Intent(NEW_ALERT_INTENT);
						intent.putExtra(NEW_ALERT_EXTRA_COUNT, numNewAlerts);
						sendBroadcast(intent, RECEIVE_ALERT_NOTIFICATIONS);
					}
				} else {
					Log.d (TAG,"Yamba Alerts call unsuccessfull");
				}
					
			} else {
				Log.d(TAG, "Need to log in  - NOT calling getLomoAlerts");			
			
			}
		
			Log.d(TAG, "Updater ran");

		} catch (Exception e) { //
			//updaterService.runFlag = false;
			Log.d(TAG, "OnHandleIntent Failed");
			e.printStackTrace();
		}
		
		
		/*
		 * YambaApplication yamba = (YambaApplication) getApplication();
		 
		int newUpdates = yamba.fetchStatusUpdates();
		if (newUpdates > 0) { //
			Log.d(TAG, "We have a new status");
			intent = new Intent(NEW_STATUS_INTENT);
			intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
			sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
		}
		*/
		
	}

}
