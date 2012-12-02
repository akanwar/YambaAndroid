package com.teemtok.yamba;

import com.teemtok.yamba.PullToRefreshListView;
import com.teemtok.yamba.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class AlertActivity extends Activity implements OnClickListener, OnItemClickListener {

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	PullToRefreshListView listView; //
	//SimpleCursorAdapter adapter; //
	Button textwarnCount;
	Button texterrorCount;
	Button textcriticalCount;
	AlertReceiver receiver;
	IntentFilter alertAutoRefreshFilter;
	ColorLevelAdapter colorAdapter;

	private YambaApplication yamba;
	private static final String TAG = AlertActivity.class.getSimpleName();
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.teemtok.yamba.SEND_ALERT_NOTIFICATIONS";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		// Find your views
		// listTimeline = (ListView) findViewById(R.id.listAlerts); //
		textwarnCount = (Button) findViewById(R.id.textWarnCount);
		texterrorCount = (Button) findViewById(R.id.textErrorCount);
		textcriticalCount = (Button) findViewById(R.id.textCriticalCount);

		textwarnCount.setOnClickListener(this);
		texterrorCount.setOnClickListener(this);
		textcriticalCount.setOnClickListener(this);
		receiver = new AlertReceiver();
		alertAutoRefreshFilter = new IntentFilter("com.teemtok.yamba.NEW_ALERT");

		listView = (PullToRefreshListView) findViewById(R.id.listAlerts);
		
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Your code to refresh the list contents

				refreshAlertData("any");
				listView.onRefreshComplete();
			}
		});
		

		listView.setOnItemClickListener(this);

		// Connect to database

		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		this.yamba = (YambaApplication) getApplication();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
		Log.d(TAG, "You clicked on view : "+ arg1.getId() + " position : " + arg2 + " and id : " + arg3);
		Intent myIntent = new Intent(this, AlertDetailActivity.class);
		myIntent.putExtra("primarykey", arg3);
		startActivity(myIntent);
	}
	

	@Override
	public void onPause() {
		super.onPause();
		super.unregisterReceiver(receiver);
		Log.d(TAG, "in OnPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "in OnStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		Log.d(TAG, "in OnDestroy");

		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			refreshAlertData("any");
			super.registerReceiver(receiver, alertAutoRefreshFilter,
					SEND_TIMELINE_NOTIFICATIONS, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void refreshAlertData(String filterlevel) {
	
		// Get the data from the database
		try {
			if (filterlevel.equals("critical")) {
				cursor = db.query(DbHelper.TABLE, null, "level like " + "'critical'", null, null, null, DbHelper.C_STARTONUNIXTIME + " DESC");
				Log.d(TAG, "filterlevel critical");
			} else if (filterlevel.equals("error")) {
				cursor = db.query(DbHelper.TABLE, null, "level like " + "'error'", null, null, null, DbHelper.C_STARTONUNIXTIME + " DESC");
				Log.d(TAG, "filterlevel error");
			} else if (filterlevel.equals("warn")) {
				cursor = db.query(DbHelper.TABLE, null, "level like " + "'warn'", null, null, null, DbHelper.C_STARTONUNIXTIME + " DESC");
				Log.d(TAG, "filterlevel warn");
			} else {
				cursor = db.query(DbHelper.TABLE, null, null, null, null, null, DbHelper.C_STARTONUNIXTIME + " DESC");
				Log.d(TAG, "filterlevel is "+ filterlevel);
			}	
			
		} catch (SQLiteException e) {
			Log.d(TAG, "SQLiteException in refreshAlertData");
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "exception in refreshAlertData");
			// e.printStackTrace();
		}

		if (cursor != null) {
			startManagingCursor(cursor);
			colorAdapter = new ColorLevelAdapter(this, cursor);
			listView.setAdapter(colorAdapter);
		} else {
			Log.d(TAG, "cursor was NULL");
		}

		textcriticalCount.setText(yamba.getLevelCount("critical"));
		texterrorCount.setText(yamba.getLevelCount("error"));
		textwarnCount.setText(yamba.getLevelCount("warn"));

	}

	public void onClick(View v) {
		Log.d(TAG, "onClicked Level Count button in Alerts Activity" + v.getId());
			
	   	switch ( v.getId()) { //
    	case R.id.textCriticalCount:
			refreshAlertData("critical");
    		break;
    	case R.id.textErrorCount:
			refreshAlertData("error");
    		break;
    	case R.id.textWarnCount:
			refreshAlertData("warn");
    		break;
    	default:
			Log.d(TAG,"onClick called with default");
    		break;
    	
    	}

	}
	
	
	class AlertReceiver extends BroadcastReceiver { //
		@Override
		public void onReceive(Context context, Intent intent) { //
			//cursor.requery(); //
			refreshAlertData("any");
			colorAdapter.notifyDataSetChanged(); //
			Log.d(TAG, "onReceived New Alerts from updaterservice2, doing self refresh");
		}
	}


	
}
