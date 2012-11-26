package com.teemtok.yamba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teemtok.yamba.PullToRefreshListView.OnRefreshListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class HostListActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	PullToRefreshListView listView;

	// ColorLevelAdapter colorAdapter;

	private YambaApplication yamba;
	private static final String TAG = HostListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_list);

		/*
		 * listView = (PullToRefreshListView) findViewById(R.id.listAlerts);
		 * 
		 * listView.setOnRefreshListener(new OnRefreshListener() {
		 * 
		 * @Override public void onRefresh() { // Your code to refresh the list
		 * contents
		 * 
		 * refreshAlertData("any"); listView.onRefreshComplete(); } });
		 * 
		 * 
		 * listView.setOnItemClickListener(this);
		 */
		// Connect to database

		this.yamba = (YambaApplication) getApplication();
	}

	/*
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * arg2, long arg3) { // TODO Auto-generated method stub Log.d(TAG,
	 * "You clicked on view : "+ arg1.getId() + " position : " + arg2 +
	 * " and id : " + arg3); Intent myIntent = new Intent(this,
	 * AlertDetailActivity.class); myIntent.putExtra("primarykey", arg3);
	 * startActivity(myIntent); }
	 */

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

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			refreshHostGroupData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void refreshHostGroupData() {

		// listView.setAdapter(colorAdapter);
		String [] gethostresp = yamba.getHostGroups();

		if (gethostresp[0].equals("1")) {
			
			
		} else {
			Log.d(TAG," getHostGroups failed in Yamba");
		}
		
	}

	private void parseJSONHostGroups( String hostgrouprawjson) throws JSONException {
		
		final String TAG1 = TAG.concat("-parseJSONHostGroups");
		
		JSONObject jsonHostGroupsOuterObject = null;
		
		//String alertLevel = null;
		//String responsestatus = null;
		
		JSONObject hostgrouprootobject = null;
		
		//Log.d(TAG1,"Alert Response String is: " + alertString.length());
		
		try {
			jsonHostGroupsOuterObject = new JSONObject(hostgrouprawjson);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1,"Root JSON object (jsonHostGroupsOuterObject) creation failed");
		}
		
		
		
		
		try {
			hostgrouprootobject = jsonHostGroupsOuterObject.getJSONObject("data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1,"creation of JSON object for DATA (hostgrouprootobject) failed: ");
		}
		
		JSONArray alertsobject = alertsrootobject.getJSONArray("alerts");
		Log.d(TAG1,"NUM ALERTS: "+totalerts+"   RESPONSE STATUS: "+responsestatus+"  LENGTH ALERTOBJECT ARRAY: "+alertsobject.length());

		ContentValues values = new ContentValues();
		criticalCount = 0;
		warnCount = 0;
		errorCount = 0;
		int i;
		for(i=0; i<alertsobject.length(); i++)
        {
			values.clear();
			alertLevel = alertsobject.getJSONObject(i).getString("level");
			values.put(LomoData.C_DATAPOINT, alertsobject.getJSONObject(i).getString("dataPoint") );
			values.put(LomoData.C_DATASOURCE, alertsobject.getJSONObject(i).getString("dataSource") );
			values.put(LomoData.C_DATASOURCEINSTANCE, alertsobject.getJSONObject(i).getString("dataSourceInstance") );
			values.put(LomoData.C_HOST, alertsobject.getJSONObject(i).getString("host") );
			values.put(LomoData.C_LEVEL, alertsobject.getJSONObject(i).getString("level") );
			values.put(LomoData.C_VALUE, alertsobject.getJSONObject(i).getString("value") );
			values.put(LomoData.C_THRESHOLDS, alertsobject.getJSONObject(i).getString("thresholds") );
			values.put(LomoData.C_STARTONLOCALTIME, alertsobject.getJSONObject(i).getString("startOnLocal") );
			values.put(LomoData.C_STARTONUNIXTIME, alertsobject.getJSONObject(i).getString("startOn") );
			values.put(LomoData.C_ALERTID, alertsobject.getJSONObject(i).getInt("id") );
			boolean isackedFlag = alertsobject.getJSONObject(i).getBoolean("acked");
			if (isackedFlag)
			{
				values.put(LomoData.C_ISACKED, 1);
			} else {
				values.put(LomoData.C_ISACKED, 0);
			}
			values.put(LomoData.C_ACKCOMMENT, alertsobject.getJSONObject(i).getString("ackComment").replaceAll("\n", "") );
			lomodata.insertOrIgnore(values);
			
        }
		Log.d(TAG, "insertOrIgnore completed. Loop called "+ i +"times");
		
	}
		
	
	
	
	/*
	 * public void onClick(View v) { Log.d(TAG,
	 * "onClicked Level Count button in Alerts Activity" + v.getId());
	 * 
	 * switch ( v.getId()) { // case R.id.textCriticalCount:
	 * refreshAlertData("critical"); break; case R.id.textErrorCount:
	 * refreshAlertData("error"); break; case R.id.textWarnCount:
	 * refreshAlertData("warn"); break; default:
	 * Log.d(TAG,"onClick called with default"); break;
	 * 
	 * }
	 * 
	 * }
	 */

}
