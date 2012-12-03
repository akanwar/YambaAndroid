package com.teemtok.yamba;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener { //

	private boolean serviceRunning;
	private boolean loggedIn;
	private boolean isAlarmFiring = false;

	public static final int INTERVAL_NEVER = 0;
	// private HttpClient httpclient = new DefaultHttpClient();
	private CookieStore cookieStore = new BasicCookieStore();
	private HttpContext localContext = new BasicHttpContext();

	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;

	private LomoCredentials lomo = null;
	private LomoData lomodata = null;
	private String alertString;
	private int criticalCount = 0;
	private int errorCount = 0;
	private int warnCount = 0;

	private int totalalertcount_new = 0;
	private int totalalertcount_prev = 0;

	private NotificationManager notificationManager; //
	private Notification notification;

	private StringBuilder sb = null;
	SimpleDateFormat apiformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

	// private JSONObject jsonLomoAlertsOuterObject;

	public CookieStore getcookieStore() {
		return cookieStore;
	}

	public HttpContext getlocalContext() {
		return localContext;
	}

	// public HttpClient gethttpClient () { return httpclient; }

	@Override
	public void onCreate() { //
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		lomodata = new LomoData(this);
		Log.d(TAG, "onCreated");

		// TODO check if the DB exists
		totalalertcount_new = lomodata.getAlertCount("any");
		Log.d(TAG, "onCreated ran getalertcount and got " + totalalertcount_new);

	}

	@Override
	public void onTerminate() { //
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}

	public synchronized LomoCredentials getLomoCredentials() { //
		if (this.lomo == null) {
			String username = this.prefs.getString("Username", "");
			String password = this.prefs.getString("Password", "");
			String company = this.prefs.getString("Company", "");

			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
					&& !TextUtils.isEmpty(company)) {
				this.lomo = new LomoCredentials(username, password, company);
			}
		}
		return this.lomo;
	}

	public synchronized int getInterval() { //
		// if (this.lomo != null) {
		String intstr = this.prefs.getString("interval", "0");
		int interval = Integer.parseInt(intstr);
		Log.d(TAG, "getInterval() got the interval as: " + interval);
		return interval;
		// } else {
		// Log.d(TAG,
		// "getInterval failed to get interval from prefs and returning 0");
		// return 0;
		// }

	}

	public synchronized void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key) { //
		// this.lomo = null;

		if (key.equals("interval")) {
			Log.d(TAG, "Only interval changed, not logging in");
			setAlarms(getApplicationContext());

		} else {
			Log.d(TAG, "logging in because you changed " + key);
			this.lomo = null;
			try {
				LomoCredentials lomo1 = getLomoCredentials();
				String ystatus = null;
				if (lomo1 != null) {
					ystatus = lomoLogin(lomo1);
				}
				Log.d(TAG, "End of preference change : " + ystatus);

			} catch (Exception e) {
				Log.d(TAG, "Exception trying to login");
			}
		}

	}

	public String lomoLogin(LomoCredentials lomo) {

		int code;
		int httpcode;

		// public static final String LOMO_URL_STRING =
		// "http://citrix.logicmonitor.com/santaba/rpc/signIn?c=citrix&u=apiuser&p=helloworld";

		String loginURL = "http://".concat(lomo.getCompany())
				.concat(".logicmonitor.com/santaba/rpc/signIn?c=")
				.concat(lomo.getCompany()).concat("&u=")
				.concat(lomo.getUsername()).concat("&p=")
				.concat(lomo.getPassword());
		Log.d(TAG, "beginning of lomologin" + loginURL);

		localContext.removeAttribute(ClientContext.COOKIE_STORE);
		cookieStore.clear();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		String[] httpresp = executeHTTPwithStatus(loginURL);

		Log.d(TAG, "httpresp array HTTPSTATUS|sizeofresponse|errormsg|status"
				+ httpresp[0] + "|" + httpresp[1].length() + "|" + httpresp[2]
				+ "|" + httpresp[3]);
		code = Integer.parseInt(httpresp[3]);
		httpcode = Integer.parseInt(httpresp[0]);

		if (httpcode == 200) {
			Log.d(TAG, "HTTP(header) is 200... good");
		} else {
			Log.d(TAG, "Connection error in HTTP(header) : " + httpcode);
		}

		if (code >= 400 && code < 500) {
			Log.d(TAG,
					"returned HTTP(in body) between 400 and 500 : code|errmsg"
							+ code + "|" + httpresp[2]);
			loggedIn = false;
		} else if (code == 200) {
			Log.d(TAG, "returned HTTP(in body) 200 : code|errmsg" + code + "|"
					+ httpresp[2]);
			loggedIn = true;
		} else {
			Log.d(TAG, "returned strange HTTP(in body): code|errmsg" + code
					+ "|" + httpresp[2]);
			loggedIn = false;
		}

		if (loggedIn) {
			// Send a intent to the service to start
			setAlarms(getApplicationContext());
		} else {
			// no need to run the service
			stopAlarms(getApplicationContext());
		}

		Log.d(TAG, "end of lomoLogin");
		// TBD modify to return different codes for successful and failed logins
		return httpresp[3];

	}

	public String[] executeHTTPwithStatus(String url) {

		StringBuilder sblocal = null;
		String statuslocal;
		int codelocal;
		HttpResponse responselocal = null;

		JSONObject jsonresponselocal = null;
		String responsestatuslocal = null;
		String errmsglocal = null;

		String[] responsearr = { "0", "0", "0", "0" };

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);

		String TAG1 = TAG + "-executeHTTPwithStatus";
		Log.d(TAG1, "url requestd is " + url);

		try {

			responselocal = httpclient.execute(httpget, localContext);
			statuslocal = responselocal.getStatusLine().toString();
			codelocal = responselocal.getStatusLine().getStatusCode(); // TODO
																		// return
			responsearr[0] = Integer.toString(codelocal);

			HttpEntity entitylocal = responselocal.getEntity();

			if (entitylocal != null) {
				Log.d(TAG1, "entity is not null");
				InputStream instream = entitylocal.getContent();
				try {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));

					sblocal = new StringBuilder();

					String line = null;
					while ((line = reader.readLine()) != null) {
						sblocal.append(line + "\n");
						// Log.d(TAG1, line);
					}
					Log.d(TAG1,
							"First 100 chars of the response are: "
									+ sblocal.substring(0,
											Math.min(sblocal.length(), 100)));
				} catch (Exception ex) {
					throw ex;
				} finally {
					instream.close();
				}

			}

		} catch (ClientProtocolException e) {
			Log.d(TAG1, "ClientProtocolException encountered.");
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG1, "Http GET Exception encountered."
					+ responselocal.getStatusLine().toString());
			e.printStackTrace();
		}

		try {
			jsonresponselocal = new JSONObject(sblocal.toString());

			responsestatuslocal = jsonresponselocal.getString("status"); // TODO
																			// return
			errmsglocal = jsonresponselocal.getString("errmsg"); // TODO return

			responsearr[1] = sblocal.toString();
			responsearr[2] = errmsglocal;
			responsearr[3] = responsestatuslocal;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responsearr;

	}

	public boolean isServiceRunning() { //
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) { //
		this.serviceRunning = serviceRunning;
	}

	public boolean isloggedIn() { //
		return loggedIn;
	}

	public void setloggedIn(boolean loggedIn) { //
		this.loggedIn = loggedIn;
	}

	public synchronized boolean getLomoAlerts() {

		// TBD: check for logged in and throw exception if not

		boolean success = false;
		int code = 0;
		int httpcode = 0;

		final String TAG1 = TAG.concat("-getLomoAlerts");
		try {
			Log.d(TAG1,
					"before get lomo_GETALERTS_STRING: " + lomo.getCompany());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.d(TAG1, "excpetion for lomodata");
			e1.printStackTrace();
		}
		// final String LOMO_GETALERTS_STRING =
		// "http://citrix.logicmonitor.com/santaba/rpc/getAlerts";
		final String LOMO_GETALERTS_STRING = "http://"
				.concat(lomo.getCompany()).concat(
						".logicmonitor.com/santaba/rpc/getAlerts");
		Log.d(TAG1, "beginning of Lomo Alerts URL is: " + LOMO_GETALERTS_STRING);

		String[] httpresp = executeHTTPwithStatus(LOMO_GETALERTS_STRING);
		Log.d(TAG1, "httpresp array HTTPSTATUS|sizeofresponse|errormsg|status"
				+ httpresp[0] + "|" + httpresp[1].length() + "|" + httpresp[2]
				+ "|" + httpresp[3]);
		code = Integer.parseInt(httpresp[3]);
		httpcode = Integer.parseInt(httpresp[0]);
		if (httpcode == 200) {
			Log.d(TAG1, "HTTP(header) is 200... good");
		} else {
			Log.d(TAG1, "Connection error in HTTP(header) : " + httpcode);
		}

		if (code >= 400 && code < 500) {
			Log.d(TAG1,
					"returned HTTP(in body) between 400 and 500 : code|errmsg"
							+ code + "|" + httpresp[2]);
			success = false;
		} else if (code == 200) {
			Log.d(TAG1, "returned HTTP(in body) 200 : code|errmsg" + code + "|"
					+ httpresp[2]);
			success = true;
		} else {
			Log.d(TAG1, "returned strange HTTP(in body): code|errmsg" + code
					+ "|" + httpresp[2]);
			success = false;
		}

		// TBD modify to return different codes for successful and failed logins

		// TBD format response into a list generic of some sort
		alertString = httpresp[1];
		if (success) {
			lomodata.purgeDataBeforeInsert();
			try {
				totalalertcount_prev = totalalertcount_new;
				parseJSONandUpdateDB();
				totalalertcount_new = lomodata.getAlertCount("any");
				Log.d(TAG1, "prev alerts | new alerts" + totalalertcount_prev
						+ "|" + totalalertcount_new);
				final Calendar apiCalendarObjectGetAlerts = Calendar.getInstance();
				setLastrefreshTime(apiCalendarObjectGetAlerts.getTime());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return success;

	}

	private void parseJSONandUpdateDB() throws JSONException {
		final String TAG1 = TAG.concat("-parseJSONandUpdateDB");
		JSONObject jsonLomoAlertsOuterObject = null;
		String alertLevel = null;
		String responsestatus = null;
		JSONObject alertsrootobject = null;

		Log.d(TAG1, "Alert Response String is: " + alertString.length());

		try {
			jsonLomoAlertsOuterObject = new JSONObject(alertString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1,
					"Root JSON object (jsonLomoAlertsOuterObject) creation failed: "
							+ jsonLomoAlertsOuterObject);
		}

		try {
			responsestatus = jsonLomoAlertsOuterObject.getString("status");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1, "Read on response status from JSON object failed: "
					+ responsestatus);
		}

		try {
			alertsrootobject = jsonLomoAlertsOuterObject.getJSONObject("data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1,
					"creation of JSON object for DATA (alertsrootobject) failed: "
							+ alertsrootobject);
		}
		int totalerts = alertsrootobject.getInt("total");
		JSONArray alertsobject = alertsrootobject.getJSONArray("alerts");
		Log.d(TAG1, "NUM ALERTS: " + totalerts + "   RESPONSE STATUS: "
				+ responsestatus + "  LENGTH ALERTOBJECT ARRAY: "
				+ alertsobject.length());

		ContentValues values = new ContentValues();
		criticalCount = 0;
		warnCount = 0;
		errorCount = 0;
		int i;
		for (i = 0; i < alertsobject.length(); i++) {
			values.clear();
			alertLevel = alertsobject.getJSONObject(i).getString("level");
			values.put(LomoData.C_DATAPOINT, alertsobject.getJSONObject(i)
					.getString("dataPoint"));
			values.put(LomoData.C_DATASOURCE, alertsobject.getJSONObject(i)
					.getString("dataSource"));
			values.put(LomoData.C_DATASOURCEINSTANCE, alertsobject
					.getJSONObject(i).getString("dataSourceInstance"));
			values.put(LomoData.C_HOST, alertsobject.getJSONObject(i)
					.getString("host"));
			values.put(LomoData.C_LEVEL, alertsobject.getJSONObject(i)
					.getString("level"));
			values.put(LomoData.C_VALUE, alertsobject.getJSONObject(i)
					.getString("value"));
			values.put(LomoData.C_THRESHOLDS, alertsobject.getJSONObject(i)
					.getString("thresholds"));
			values.put(LomoData.C_STARTONLOCALTIME,
					alertsobject.getJSONObject(i).getString("startOnLocal"));
			values.put(LomoData.C_STARTONUNIXTIME, alertsobject
					.getJSONObject(i).getString("startOn"));
			values.put(LomoData.C_ALERTID, alertsobject.getJSONObject(i)
					.getInt("id"));
			boolean isackedFlag = alertsobject.getJSONObject(i).getBoolean(
					"acked");
			if (isackedFlag) {
				values.put(LomoData.C_ISACKED, 1);
			} else {
				values.put(LomoData.C_ISACKED, 0);
			}
			values.put(LomoData.C_ACKCOMMENT, alertsobject.getJSONObject(i)
					.getString("ackComment").replaceAll("\n", ""));
			lomodata.insertOrIgnore(values);

		}
		Log.d(TAG, "insertOrIgnore completed. Loop called " + i + "times");

	}

	public LomoData getLomoData() {
		return lomodata;
	}

	public String getLevelCount(String level) {
		int levelcount = lomodata.getAlertCount(level);
		return Integer.toString(levelcount);
	}

	// Used by alert detail activity
	public boolean pushAckandAckComment(String URL) {

		// TBD: check for logged in and throw exception if not

		int code;
		int httpcode;
		boolean success = false;

		final String TAG1 = TAG.concat("-pushAckandAckComment");
		Log.d(TAG1, "Reached Yamba pushing ACK, URL is: " + URL);

		String[] httpresp = executeHTTPwithStatus(URL);
		Log.d(TAG1, "httpresp array HTTPSTATUS|sizeofresponse|errormsg|status"
				+ httpresp[0] + "|" + httpresp[1].length() + "|" + httpresp[2]
				+ "|" + httpresp[3]);

		code = Integer.parseInt(httpresp[3]);
		httpcode = Integer.parseInt(httpresp[0]);

		if (httpcode == 200) {
			Log.d(TAG1, "HTTP(header) is 200... good");
		} else {
			Log.d(TAG1, "Connection error in HTTP(header) : " + httpcode);
		}

		if (code >= 400 && code < 500) {
			Log.d(TAG1,
					"returned HTTP(in body) between 400 and 500 : code|errmsg"
							+ code + "|" + httpresp[2]);
			success = false;
		} else if (code == 200) {
			Log.d(TAG1, "returned HTTP(in body) 200 : code|errmsg" + code + "|"
					+ httpresp[2]);
			success = true;
		} else {
			Log.d(TAG1, "returned strange HTTP(in body): code|errmsg" + code
					+ "|" + httpresp[2]);
			success = false;
		}

		// TBD modify to return different codes for successful and failed logins

		return success;

	}

	// Used by Host List Activity

	public String[] getHostGroups() {

		int code;
		int httpcode;
		boolean success = false;

		final String TAG1 = TAG.concat("-getHostGroups");

		final String LOMO_GETHOSTGROUPS_STRING = "http://".concat(
				lomo.getCompany()).concat(
				".logicmonitor.com/santaba/rpc/getHostGroups");

		String[] httpresp = executeHTTPwithStatus(LOMO_GETHOSTGROUPS_STRING);
		Log.d(TAG1, "httpresp array HTTPSTATUS|sizeofresponse|errormsg|status"
				+ httpresp[0] + "|" + httpresp[1].length() + "|" + httpresp[2]
				+ "|" + httpresp[3]);

		code = Integer.parseInt(httpresp[3]);
		httpcode = Integer.parseInt(httpresp[0]);

		if (httpcode == 200) {
			Log.d(TAG1, "HTTP(header) is 200... good");
		} else {
			Log.d(TAG1, "Connection error in HTTP(header) : " + httpcode);
		}

		if (code >= 400 && code < 500) {
			Log.d(TAG1,
					"returned HTTP(in body) between 400 and 500 : code|errmsg"
							+ code + "|" + httpresp[2]);
			success = false;
		} else if (code == 200) {
			Log.d(TAG1, "returned HTTP(in body) 200 : code|errmsg" + code + "|"
					+ httpresp[2]);
			success = true;
		} else {
			Log.d(TAG1, "returned strange HTTP(in body): code|errmsg" + code
					+ "|" + httpresp[2]);
			success = false;
		}

		// TBD modify to return different codes for successful and failed logins

		String[] resp = null;
		if (success) {
			resp[0] = "1";
			resp[1] = httpresp[1];
		} else {
			resp[0] = "0";
		}

		return resp;

	}

	int anynewalerts() {
		if (totalalertcount_new > totalalertcount_prev) {
			return totalalertcount_new - totalalertcount_prev;
		} else {
			return 0;
		}
	}

	public boolean setAlarms(Context context) {

		final String TAG1 = TAG.concat("-setAlarms");

		// Check if we should do anything at boot at all

		Log.d(TAG1, "Begin");
		int interval = 0;
		try {
			interval = getInterval();
			Log.d(TAG1, "Interval set to: " + interval);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Fail to get interval from yamba :" + interval);

			e.printStackTrace();
		}

		if (interval == YambaApplication.INTERVAL_NEVER) //
			return false;

		Log.d(TAG, "verified that interval > 0 ");
		// Create the pending intent

		Intent intent = new Intent(context, UpdaterService2.class); //
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT); //
		// Setup alarm service to wake up and start service periodically
		try {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE); //
			alarmManager.cancel(pendingIntent);
			alarmManager.setInexactRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(), interval, pendingIntent); //
			Log.d(TAG1, "Setting alarm succeeded");
			// alarmManager.get
			isAlarmFiring = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1, "Setting alarm failed, your service may not work");
			isAlarmFiring = false;
			e.printStackTrace();
		}

		Log.d(TAG1, "End");
		return true;
	}

	public boolean stopAlarms(Context context) {

		final String TAG1 = TAG.concat("-stopAlarms");

		// Check if we should do anything at boot at all

		Log.d(TAG1, "Begin");

		Intent intent = new Intent(context, UpdaterService2.class); //
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT); //
		// Setup alarm service to wake up and start service periodically
		try {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE); //
			alarmManager.cancel(pendingIntent);

			Log.d(TAG1, "Stopping alarm succeeded");
			isAlarmFiring = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG1, "Stopping alarm failed, your service may not work");
			isAlarmFiring = true;
			e.printStackTrace();
		}

		Log.d(TAG1, "End");
		return true;
	}

	public void sendAlertNotification(int numNewAlerts) {
		Log.d(TAG, "sendAlertNotification begin ");

		Context c = getApplicationContext();
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //
		this.notification = new Notification(
				R.drawable.lomoalertsicon, "", 0);

		PendingIntent pendingIntent = PendingIntent.getActivity(c, -1,
				new Intent(c, AlertActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT); //

		this.notification.when = SystemClock.elapsedRealtime(); //
		this.notification.flags |= Notification.FLAG_AUTO_CANCEL; //
		CharSequence notificationTitle = this
				.getText(R.string.msgNotificationTitle); //
		CharSequence notificationSummary = this.getString(
				R.string.msgNotificationMessage, numNewAlerts);
		this.notification.setLatestEventInfo(this, notificationTitle,
				notificationSummary, pendingIntent); //
		this.notificationManager.notify(0, this.notification);
		Log.d(TAG, "sendAlertNotification- done");
	}
	
	public void setLastrefreshTime(Date dateTime) {
		String time = null;
		try {
			time = apiformat.format(dateTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"-setLastrefreshTime: apiformat fro calkendar onject failed");
			e.printStackTrace();
		}
		final String PREFS_NAME = "MyPrefsFile";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("lastUpdateTime", time);
	      // Commit the edits!
	      editor.commit();
	}
	
	public String getLastrefreshTimeString() {
		final String PREFS_NAME = "MyPrefsFile";
		SharedPreferences settings = null;
		try {
			settings = getSharedPreferences(PREFS_NAME, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"-getLastrefreshTimeString: read from prefs failed");
			e.printStackTrace();
		}
	    //String returntime = settings.getString("lastUpdateTime","1970-01-01 00:00:00 PST");
	    String returntime = settings.getString("lastUpdateTime","a long time ago.");

	    return returntime;
	}
	
	public Date getLastrefreshTimeCalendar() {
		final Calendar apiCalendarObject = Calendar.getInstance();
		final String PREFS_NAME = "MyPrefsFile";
		SharedPreferences settings = null;
		try {
			settings = getSharedPreferences(PREFS_NAME, 0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.d(TAG,"-getLastrefreshTimeCalendar: read from prefs failed");
			e1.printStackTrace();
		}
	    String returntime = settings.getString("lastUpdateTime","1970-01-01 00:00:00 PST");
	    try {
			apiCalendarObject.setTime(apiformat.parse(returntime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"-getLastrefreshTimeCalendar: setTime on calendar object failed");
			e.printStackTrace();
		}
	    return apiCalendarObject.getTime();
	}


	

}
