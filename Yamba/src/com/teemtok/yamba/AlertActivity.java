package com.teemtok.yamba;

import com.teemtok.yamba.PullToRefreshListView;
import com.teemtok.yamba.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class AlertActivity extends Activity implements OnClickListener {

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	PullToRefreshListView listView; //
	SimpleCursorAdapter adapter; //
	Button textwarnCount;
	Button texterrorCount;
	Button textcriticalCount;
	
	ColorLevelAdapter colorAdapter;

	private YambaApplication yamba;
	private static final String TAG = AlertActivity.class.getSimpleName();

	static final String[] FROM = { DbHelper.C_HOST,
			DbHelper.C_STARTONLOCALTIME, DbHelper.C_DATASOURCEINSTANCE,
			DbHelper.C_THRESHOLDS, DbHelper.C_VALUE, DbHelper.C_LEVEL }; //
	static final int[] TO = { R.id.textHost, R.id.textStartOnLocal,
			R.id.textDataSourceInstance, R.id.textThreshold, R.id.textValue,
			R.id.textAlertLevel }; //

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

		listView = (PullToRefreshListView) findViewById(R.id.listAlerts);
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Your code to refresh the list contents

				refreshAlertData("any");
				listView.onRefreshComplete();
			}
		});

		// Connect to database

		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		this.yamba = (YambaApplication) getApplication();
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
			// Set up the adapter
			//adapter = new SimpleCursorAdapter(this, R.layout.alert_row, cursor,	FROM, TO); //
			colorAdapter = new ColorLevelAdapter(this, cursor);
			//listView.setAdapter(adapter); //
			listView.setAdapter(colorAdapter);
		} else {
			Log.d(TAG, "cursor was NULL");
		}

		textcriticalCount.setText(yamba.getLevelCount("critical"));
		texterrorCount.setText(yamba.getLevelCount("error"));
		textwarnCount.setText(yamba.getLevelCount("warn"));

	}

	public void onClick(View v) {
		// String status = editText.getText().toString();
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
    	}
		
//		refreshAlertData("");

	}
}
