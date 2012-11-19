package com.teemtok.yamba;

import android.app.Service;
import android.content.ContentValues;
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
	
	//DbHelper dbHelper;
	//SQLiteDatabase db;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.yamba = (YambaApplication) getApplication();
		this.updater = new Updater(); //
		Log.d(TAG, "onCreated");
		
		//dbHelper = new DbHelper(this);
		Log.d(TAG, "onCreated - DBhelper");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true; //
		this.updater.start();
		this.yamba.setServiceRunning(true);
		Log.d(TAG, "onStarted");

		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag = false; //
		this.updater.interrupt(); //
		this.updater = null;
		this.yamba.setServiceRunning(false);
		Log.d(TAG, "onDestroyed Service");
		
	}

	/**
	 * Thread that performs the actual update from the online service
	 */
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
					if (yamba.isloggedIn() ){
						Log.d(TAG, "Already logged in - calling getLomoAlerts");
						yamba.getLomoAlerts();
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
	
	/*
	private void parseJSONandUpdateDB() throws JSONException {
		final String TAG1 = TAG.concat("-parseJSONandUpdateDB");
		
		JSONObject jsonLomoAlertsOuterObject = new JSONObject(alertString);
		
		String responsestatus = jsonLomoAlertsOuterObject.getString("status");
		
		JSONObject alertsrootobject = jsonLomoAlertsOuterObject.getJSONObject("data");
		int totalerts = alertsrootobject.getInt("total");
		
		
		
		 JSONArray alertsobject = alertsrootobject.getJSONArray("alerts");
		
		 
		 Log.d(TAG1,"NUM ALERTS: "+totalerts+"   RESPONSE STATUS: "+responsestatus+"  LENGTH ALERTOBJECT ARRAY: "+alertsobject.length());
		 
		db = dbHelper.getWritableDatabase();
		dbHelper.purgeData(db);
		
		ContentValues values = new ContentValues();
		for(int i=0; i<alertsobject.length(); i++)
        {
			values.clear();
			values.put(DbHelper.C_DATAPOINT, alertsobject.getJSONObject(i).getString("dataPoint") );
			values.put(DbHelper.C_DATASOURCE, alertsobject.getJSONObject(i).getString("dataSource") );
			values.put(DbHelper.C_DATASOURCEINSTANCE, alertsobject.getJSONObject(i).getString("dataSourceInstance") );
			values.put(DbHelper.C_HOST, alertsobject.getJSONObject(i).getString("host") );
			values.put(DbHelper.C_LEVEL, alertsobject.getJSONObject(i).getString("level") );
			values.put(DbHelper.C_VALUE, alertsobject.getJSONObject(i).getString("value") );
			values.put(DbHelper.C_THRESHOLDS, alertsobject.getJSONObject(i).getString("thresholds") );
			values.put(DbHelper.C_STARTONLOCALTIME, alertsobject.getJSONObject(i).getString("startOnLocal") );
			values.put(DbHelper.C_ALERTID, alertsobject.getJSONObject(i).getInt("id") );
		
			
			db.insertOrThrow(DbHelper.TABLE, null, values);
			
			
        }
		
		
		db.close();
		
		
		
	}
	
	*/
	
}

