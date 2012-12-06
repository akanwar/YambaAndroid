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
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class AlertActivity extends Activity implements OnClickListener, OnItemClickListener {

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	PullToRefreshListView listView; //
	// SimpleCursorAdapter adapter; //
	Button textwarnCount;
	Button texterrorCount;
	Button textcriticalCount;
	AlertReceiver receiver;
	IntentFilter alertAutoRefreshFilter;
	ColorLevelAdapter colorAdapter;
	TextView alertsRefreshRTextView;

	private YambaApplication yamba;
	private static final String TAG = AlertActivity.class.getSimpleName();
	static final String SEND_ALERT_NOTIFICATIONS = "com.teemtok.yamba.SEND_ALERT_NOTIFICATIONS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		// Find your views
		// listTimeline = (ListView) findViewById(R.id.listAlerts); //
		textwarnCount = (Button) findViewById(R.id.textWarnCount);
		texterrorCount = (Button) findViewById(R.id.textErrorCount);
		textcriticalCount = (Button) findViewById(R.id.textCriticalCount);
		alertsRefreshRTextView = (TextView) findViewById(R.id.idalert_activity_lastAlertsRefreshTime);
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
		
		
		findViewById(R.id.textCriticalCount).post(new Runnable() {
			public void run() {
				
				String showHelp = null;
				try {
					showHelp = yamba.getPrefsAsString("helpBubbleInvocations");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d (TAG, "Failed to read prefs");
					e.printStackTrace();
				}
				if (showHelp.equals("false")) {
					showPopup(AlertActivity.this);
				} else {
					Log.d(TAG, "Not showing bubble help for returning user");
				}
				yamba.setPrefs("true", "helpBubbleInvocations");
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d(TAG, "You clicked on view : " + arg1.getId() + " position : " + arg2 + " and id : " + arg3);
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
			super.registerReceiver(receiver, alertAutoRefreshFilter, SEND_ALERT_NOTIFICATIONS, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		alertsRefreshRTextView.setText("Alerts updated: ".concat(yamba.getPrefsAsString("lastUpdateTime")));
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
				Log.d(TAG, "filterlevel is " + filterlevel);
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

		switch (v.getId()) { //
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
			Log.d(TAG, "onClick called with default");
			break;

		}

	}
	
	
	
	
	private void showPopup(final Activity context) {
			   int popupWidth = 400;
			   int popupHeight = 280;
			   //int y = (textcriticalCount.getLeft()+ textcriticalCount.getRight())/2;
			   
			   LinearLayout l = (LinearLayout) context.findViewById(R.id.alert_activity_middle);
			   int x = l.getLeft();
			   int y = l.getTop()+75;
			   
			   Log.d(TAG, " alert_activity_middle coordinates" + x + " " + y);
			   
			   // Inflate the popup_layout.xml
			   LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.idHelpPopup);
			   LayoutInflater layoutInflater = (LayoutInflater) context
			     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   View layout = layoutInflater.inflate(R.layout.bubble_layout, viewGroup);
			 
			   // Creating the PopupWindow
			   final PopupWindow popup = new PopupWindow(context);
			   popup.setContentView(layout);
			   popup.setWidth(popupWidth);
			   popup.setHeight(popupHeight);
			   popup.setFocusable(true);
			 
			   // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
			   int OFFSET_X = 30;
			   int OFFSET_Y = 30;
			 
			   // Clear the default translucent background
			   popup.setBackgroundDrawable(new BitmapDrawable());
			 
			   // Displaying the popup at the specified location, + offsets.
			   popup.showAtLocation(layout, Gravity.TOP, 0,y);
		   // Getting a reference to Close button, and close the popup when clicked.
			   Button close = (Button) layout.findViewById(R.id.close);
			   close.setOnClickListener(new OnClickListener() {
			 
			     @Override
			     public void onClick(View v) {
			       popup.dismiss();
			     }
			   });

			}

	class AlertReceiver extends BroadcastReceiver { //
		@Override
		public void onReceive(Context context, Intent intent) { //
			// cursor.requery(); //
			refreshAlertData("any");
			colorAdapter.notifyDataSetChanged(); //
			Log.d(TAG, "onReceived New Alerts from updaterservice2, doing self refresh");
		}
	}

}
