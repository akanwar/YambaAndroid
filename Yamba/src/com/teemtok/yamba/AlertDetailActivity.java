package com.teemtok.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import android.support.v4.app.NavUtils;

public class AlertDetailActivity extends Activity {

	private static final String TAG = AlertDetailActivity.class.getSimpleName();

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	
	TextView idvalalertid;
	TextView idvalhost;
	TextView idvalvalue;
	TextView idvallevel;
	TextView idvaldatapoint;
	TextView idvaldatasource;
	TextView idvaldatasourceinstance;
	TextView idvalthresholds;
	TextView idvalstartonlocaltime;
	

	private YambaApplication yamba;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert_detail);
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		idvalalertid = (TextView) findViewById(R.id.idvalalertid);
		idvalhost = (TextView) findViewById(R.id.idvalhost);
		idvalvalue = (TextView) findViewById(R.id.idvalvalue);
		idvallevel = (TextView) findViewById(R.id.idvallevel);
		idvaldatapoint = (TextView) findViewById(R.id.idvaldatapoint);
		idvaldatasource = (TextView) findViewById(R.id.idvaldatasource);
		idvaldatasourceinstance = (TextView) findViewById(R.id.idvaldatasourceinstance);
		idvalthresholds = (TextView) findViewById(R.id.idvalthresholds);
		idvalstartonlocaltime = (TextView) findViewById(R.id.idvalstartonlocaltime);
		
		
		Intent myIntent = getIntent(); // gets the previously created intent
		long firstKeyName = myIntent.getLongExtra("primarykey", 1);

		Log.d(TAG, "onCreate -- primarykey is " + firstKeyName);

		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		this.yamba = (YambaApplication) getApplication();
		getAlertDetailbyID( firstKeyName );

	}

	private void getAlertDetailbyID(Long key) {

		// Get the data from the database
		try {
			cursor = db.query(DbHelper.TABLE, null, DbHelper.C_ID +"="+ key, null, null, null, null);
			//cursor = db.rawQuery("select * from lomoalerts", null);
			//cursor = db.query(DbHelper.TABLE, null, null, null, null, null, DbHelper.C_STARTONUNIXTIME + " DESC");
			
		} catch (SQLiteException e) {
			Log.d(TAG, "SQLiteException in getAlertDetailbyID");
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "exception in getAlertDetailbyID");
			e.printStackTrace();
		}

		
		if (cursor != null) {
			
			
			startManagingCursor(cursor);
			Log.d(TAG, "cursor was NOT NULL and has X rows : " + cursor.getCount() );
			
			
			cursor.moveToFirst();
			
			int id = cursor.getInt(cursor.getColumnIndex(DbHelper.C_ID)); 
			String host = cursor.getString(cursor.getColumnIndex(DbHelper.C_HOST)); 
			String alertid = cursor.getString(cursor.getColumnIndex(DbHelper.C_ALERTID)); 
			String value = cursor.getString(cursor.getColumnIndex(DbHelper.C_VALUE)); 
			String level = cursor.getString(cursor.getColumnIndex(DbHelper.C_LEVEL)); 
			String datapoint = cursor.getString(cursor.getColumnIndex(DbHelper.C_DATAPOINT));
			String datasource = cursor.getString(cursor.getColumnIndex(DbHelper.C_DATASOURCE));
			String datasourceinstance = cursor.getString(cursor.getColumnIndex(DbHelper.C_DATASOURCEINSTANCE));
			String thresholds = cursor.getString(cursor.getColumnIndex(DbHelper.C_THRESHOLDS));
			String startonlocaltime= cursor.getString(cursor.getColumnIndex(DbHelper.C_STARTONLOCALTIME));
			
			idvalalertid.setText(alertid);
			idvalhost.setText(host);
			idvalvalue.setText(value);
			idvallevel.setText(level);
			idvaldatapoint.setText(datapoint);
			idvaldatasource.setText(datasource);
			idvaldatasourceinstance.setText(datasourceinstance);
			idvalthresholds.setText(thresholds);
			idvalstartonlocaltime.setText(startonlocaltime);
			
			Log.d(TAG, " Got id|host|alertid|value|level: " + id +"|"+ host +"|"+ alertid +"|"+ value +"|"+ level);
			Log.d(TAG, " Got datapoint|datasource|datasourceinstance|threshold|startonlocaltime: " + datapoint +"|"+ datasource +"|"+ datasourceinstance +"|"+ thresholds +"|"+ startonlocaltime);

		} else {
			Log.d(TAG, "cursor was NULL");
		}

	}

	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "in OnStop");
		db.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		Log.d(TAG, "in OnDestroy");

		//db.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "in OnResume");
	}
	
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_alert_detail, menu); return
	 * true; }
	 * 
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home:
	 * NavUtils.navigateUpFromSameTask(this); return true; } return
	 * super.onOptionsItemSelected(item); }
	 */

}
