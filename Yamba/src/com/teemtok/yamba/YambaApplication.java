package com.teemtok.yamba;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener { //

	private boolean serviceRunning;
	private boolean loggedIn;

	// private HttpClient httpclient = new DefaultHttpClient();
	private CookieStore cookieStore = new BasicCookieStore();
	private HttpContext localContext = new BasicHttpContext();

	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;

	private LomoData lomo = null;
	
	private StringBuilder sb = null;
	
	//private JSONObject jsonLomoAlertsOuterObject;

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
		Log.i(TAG, "onCreated");
	}

	@Override
	public void onTerminate() { //
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}

	public synchronized LomoData getLomoData() { //
		if (this.lomo == null) {
			String username = this.prefs.getString("Username", "");
			String password = this.prefs.getString("Password", "");
			String company = this.prefs.getString("Company", "");

			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
					&& !TextUtils.isEmpty(company)) {
				this.lomo = new LomoData(username, password, company);
			}
		}
		return this.lomo;
	}

	public synchronized void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key) { //
		this.lomo = null;

		try {
			LomoData lomo1 = getLomoData();
			String ystatus = null;
			if (lomo1 != null) {
				ystatus = lomoLogin(lomo1);
			}
			Log.d(TAG, "End of preferred state change");
			Log.d(TAG, ystatus);
		} catch (Exception e) {
			Log.d(TAG, "Exception trying to login");
		}

	}

	public String lomoLogin(LomoData lomo) {

		// public static final String LOMO_URL_STRING =
		// "http://citrix.logicmonitor.com/santaba/rpc/signIn?c=citrix&u=apiuser&p=helloworld";

		String loginURL = "http://".concat(lomo.getCompany())
				.concat(".logicmonitor.com/santaba/rpc/signIn?c=")
				.concat(lomo.getCompany()).concat("&u=")
				.concat(lomo.getUsername()).concat("&p=")
				.concat(lomo.getPassword());
		Log.d(TAG, loginURL);

		localContext.removeAttribute(ClientContext.COOKIE_STORE);
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		HttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(loginURL);

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget, localContext);
			httpclient.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			Log.d(TAG, "ClientProtocolException encountered.");
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG, "Login Exception encountered.");
			e.printStackTrace();
		}

		String status = response.getStatusLine().toString();

		int code = response.getStatusLine().getStatusCode();

		if (code >= 400 && code < 500) {
			Log.d(TAG, "end of lomoLogin returned HTTP between 400 and 500");
			loggedIn = false;
		} else if (code == 200) {
			Log.d(TAG, "end of lomoLogin returned HTTP 200");
			loggedIn = true;
		} else {
			Log.d(TAG, "end of lomoLogin returned strange HTTP");
			loggedIn = false;
		}

		Log.d(TAG, "end of lomoLogin");
		Log.d(TAG, status);

		// TBD modify to return different codes for successful and failed logins

		return status;

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

	public String getLomoAlerts() {

		// TBD: check for logged in and throw exception if not
		
		
		
		final String TAG1 = TAG.concat("-getLomoAlerts");

		final String LOMO_GETALERTS_STRING = "http://citrix.logicmonitor.com/santaba/rpc/getAlerts";

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(LOMO_GETALERTS_STRING);
		HttpResponse response = null;
		String status = null;

		try {

			response = httpclient.execute(httpget, localContext);

			status = response.getStatusLine().toString();
			Log.d(TAG1, status);

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();

			
			
			
			
			
			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {

				Log.d(TAG1, "entity is not null");
				InputStream instream = entity.getContent();
				try {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));

					sb = new StringBuilder();

					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
						//Log.d(TAG1, line);
					}

				} catch (Exception ex) {

					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					throw ex;

				} finally {

					// Closing the input stream will trigger connection release
					instream.close();
				}
				
				// 
			
	
			}

		} catch (ClientProtocolException e) {
			Log.d(TAG1, "ClientProtocolException encountered.");
			e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG1, "GetAlerts Exception encountered.");
			e.printStackTrace();
		}

		// TBD format response into a list generic of some sort
		return sb.toString();

	}
	
	

	
	// ///////////////////////////////

}

