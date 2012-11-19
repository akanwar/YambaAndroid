package com.teemtok.yamba;

import com.teemtok.yamba.PullToRefreshListView;
import com.teemtok.yamba.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AlertActivity extends Activity  {
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	PullToRefreshListView listView; //
	SimpleCursorAdapter adapter; //
	TextView textwarnCount;
	TextView texterrorCount;
	TextView textcriticalCount;
	
	private YambaApplication yamba;
	
	static final String[] FROM = { DbHelper.C_HOST, DbHelper.C_STARTONLOCALTIME,
			DbHelper.C_DATASOURCEINSTANCE,DbHelper.C_THRESHOLDS,DbHelper.C_VALUE, DbHelper.C_LEVEL }; //
	static final int[] TO = { R.id.textHost, R.id.textStartOnLocal, R.id.textDataSourceInstance, R.id.textThreshold, R.id.textValue, R.id.textAlertLevel}; //

	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		// Find your views
		//listTimeline = (ListView) findViewById(R.id.listAlerts); //
		textwarnCount = (TextView) findViewById(R.id.textWarnCount);
		texterrorCount = (TextView) findViewById(R.id.textErrorCount);
		textcriticalCount = (TextView) findViewById(R.id.textCriticalCount);
		
		listView = (PullToRefreshListView) findViewById(R.id.listAlerts);		
		listView.setOnRefreshListener(new OnRefreshListener() {

		    @Override
		    public void onRefresh() {
		        // Your code to refresh the list contents

		        // ...

		        // Make sure you call listView.onRefreshComplete()
		        // when the loading is done. This can be done from here or any
		        // other place, like on a broadcast receive from your loading
		        // service or the onPostExecute of your AsyncTask.
				refreshAlertData();
		        listView.onRefreshComplete();
		    }
		});
		
		// Connect to database
		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		this.yamba = (YambaApplication) getApplication();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshAlertData();

	}
	
	private void refreshAlertData () {
		// Get the data from the database
		cursor = db.query(DbHelper.TABLE, null, null, null, null, null,
				DbHelper.C_STARTONUNIXTIME + " DESC");
		startManagingCursor(cursor);
		// Set up the adapter
		adapter = new SimpleCursorAdapter(this, R.layout.alert_row, cursor, FROM, TO); //
		//listTimeline.setAdapter(adapter); //		
		listView.setAdapter(adapter); //
	    textcriticalCount.setText(yamba.getLevelCount("critical"));
	    texterrorCount.setText(yamba.getLevelCount("error"));
	    textwarnCount.setText(yamba.getLevelCount("warn"));
		
	}
}
