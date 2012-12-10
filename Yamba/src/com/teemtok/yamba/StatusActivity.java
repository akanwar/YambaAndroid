package com.teemtok.yamba;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
//import android.app.Activity;
import android.view.Menu;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teemtok.yamba.AlertActivity.AlertReceiver;

public class StatusActivity extends Activity implements OnClickListener { //

	private static final String TAG = "StatusActivity";
	// EditText editText;
	Button updateButton, hostButton;
	TextView alertsRefreshRTextView, loggedinStatus;
	SharedPreferences prefs;
	YambaApplication yamba;
	//
	ImageView loggedinImg;
	PopupWindow popupWindow;
	LayoutInflater layoutInflater;
	View popupView;
	TextView tv1;
	TextView tv2;
	TextView tv3;
	AlertReceiver receiver;

	
	
	private IntentFilter alertAutoRefreshFilter;
	static final String SEND_ALERT_NOTIFICATIONS = "com.teemtok.yamba.SEND_ALERT_NOTIFICATIONS";

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		// Find views
		// editText = (EditText) findViewById(R.id.editText); //
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this);
		alertsRefreshRTextView = (TextView) findViewById(R.id.idlastAlertsRefreshTime);
		loggedinStatus = (TextView) findViewById(R.id.idLoggedinstatus);
		// hostButton = (Button) findViewById(R.id.idbuttonListHosts);
		// hostButton.setOnClickListener(this);
		loggedinImg = (ImageView) findViewById(R.id.imageViewIsLoggedIn);
		
		receiver = new AlertReceiver();
		alertAutoRefreshFilter = new IntentFilter("com.teemtok.yamba.NEW_ALERT");


		tv1 = (TextView) findViewById(R.id.id_status_CriticalCount);
		tv2 = (TextView) findViewById(R.id.id_status_ErrorCount);
		tv3 = (TextView) findViewById(R.id.id_status_WarnCount);

		layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.login_popup, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		try {
			yamba = ((YambaApplication) getApplication()); //
			LomoCredentials lomo1 = yamba.getLomoCredentials();
			String ystatus = null;
			if (lomo1 != null) {
				ystatus = yamba.lomoLogin(lomo1);
			}
			Log.d(TAG, "ystatus is " + ystatus);

		} catch (Exception e) {
			Log.d(TAG, "Exception trying to login");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "-onResume");

		
		try {
			refreshAlertCounters();
			super.registerReceiver(receiver, alertAutoRefreshFilter, SEND_ALERT_NOTIFICATIONS, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	public void refreshAlertCounters() {

		alertsRefreshRTextView.setText("Last update: ".concat(yamba.getPrefsAsString("lastUpdateTime")));

		if (yamba.isloggedIn()) {
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
			loggedinImg.setImageResource(R.drawable.greencheck);
			loggedinStatus.setText("Logged in as : " + yamba.getLomoCredentials().getUsername());
		} else {
			loggedinImg.setImageResource(R.drawable.redexclamation);
			loggedinStatus.setText("Not logged in.");
		}
		//

		tv1.setText(yamba.getLevelCount("critical"));
		tv2.setText(yamba.getLevelCount("error"));
		tv3.setText(yamba.getLevelCount("warn"));

		findViewById(R.id.imageViewIsLogo).post(new Runnable() {
			public void run() {
				if (popWindowPaint()) {
					Log.d(TAG, "Succesfully painted the login pop-up from onResume");
				} else {
					Log.d(TAG, "Could not paint the login pop-up from onResume");
				}
			}
		});

	}
	
	boolean popWindowPaint() {
		boolean success = false;
		try {
			if (!yamba.isloggedIn()) {
				popupWindow.showAtLocation(updateButton, 1, 0, 0);
				Button btnDismiss = (Button) popupView.findViewById(R.id.iddismiss);
				success = true;
				btnDismiss.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
						startActivity(new Intent(getApplicationContext(), PrefsActivity.class));
					}
				});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "painting pop up window failed");
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	// Called when button is clicked
	public void onClick(View v) {

		switch (v.getId()) { //
		case R.id.buttonUpdate:
			Log.d(TAG, "onClicked Starting Alerts Activity");
			startActivity(new Intent(this, AlertActivity.class));
			break;
		/*
		 * case R.id.idbuttonListHosts: Log.d(TAG,
		 * "onClicked Starting Host List Activity"); startActivity(new
		 * Intent(this, HostListActivity.class)); break;
		 */
		default:
			Log.d(TAG, "onClick called with default" + v.getId());
			break;

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater(); //
		inflater.inflate(R.menu.menu, menu); //
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Context context1 = getApplicationContext();

		switch (item.getItemId()) { //
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)); //
			break;
		case R.id.itemServiceStart:
			yamba.setAlarms(context1);
			Log.d(TAG, "Service started check - adb shell dumpsys alarm");
			break;
		case R.id.itemServiceStop:
			yamba.stopAlarms(context1);
			Log.d(TAG, "Service stoped check - adb shell dumpsys alarm");
			break;

		}
		return true; //
	}

	public void onStop(View v) {
		// TBD never seems to get called.
		// httpclient.getConnectionManager().shutdown();
		Log.d(TAG, "httpclient destroyed");
	}


	class AlertReceiver extends BroadcastReceiver { //
		@Override
		public void onReceive(Context context, Intent intent) { //
			// cursor.requery(); //
			//refreshAlertData("any");
			//colorAdapter.notifyDataSetChanged(); //
			refreshAlertCounters();
			Log.d(TAG, "onReceived New Alerts from updaterservice2, doing self refresh");
		}
	}
	
}
