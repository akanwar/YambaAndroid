package com.teemtok.yamba;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;

public class UpdaterService extends Service {

	String alertString;

	private static final String TAG = "UpdaterService";
	static final int DELAY = 60000; // a minute
	private boolean runFlag = false; //
	private Updater updater;
	private YambaApplication yamba;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.yamba = (YambaApplication) getApplication();
		// this.updater = new Updater(); //
		Log.d(TAG, "onCreated");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		// this.runFlag = true; //
		// this.updater.start();
		// this.yamba.setServiceRunning(true);
		Log.d(TAG, "onStartCommand");

		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		// this.runFlag = true; //
		// this.updater.start();
		// this.yamba.setServiceRunning(true);
		Log.d(TAG, "onStart");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// this.runFlag = false; //
		// this.updater.interrupt(); //
		// this.updater = null;
		// this.yamba.setServiceRunning(false);
		Log.d(TAG, "onDestroyed Service");

	}

	// Thread that performs the actual update from the online service
	private class Updater extends Thread { //
		public Updater() {
			super("UpdaterService-Updater"); //
		}

		@Override
		public void run() { //
			UpdaterService updaterService = UpdaterService.this; //
			while (updaterService.runFlag) { //
				Log.d(TAG, "Updater running");
				try {
					// Some work goes here...
					if (yamba.isloggedIn()) {
						Log.d(TAG, "Already logged in - calling getLomoAlerts");
						if (yamba.getLomoAlerts()) {
							Log.d(TAG, "Yamba Alerts call successfull");
						} else {
							Log.d(TAG, "Yamba Alerts call unsuccessfull");
						}

					} else {
						Log.d(TAG, "Need to log in  - NOT calling getLomoAlerts");

					}

					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY); //
				} catch (InterruptedException e) { //
					updaterService.runFlag = false;
				}
			}
		}
	} // Updater

}
