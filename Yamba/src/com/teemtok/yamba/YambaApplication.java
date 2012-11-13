package com.teemtok.yamba;

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


public class YambaApplication extends Application implements OnSharedPreferenceChangeListener { //

	
		//private HttpClient httpclient = new DefaultHttpClient();
		private CookieStore cookieStore = new BasicCookieStore();
		private HttpContext localContext = new BasicHttpContext();
	
		private static final String TAG = YambaApplication.class.getSimpleName();
		private SharedPreferences prefs;
		
		LomoData lomo = null;
		
	    public CookieStore  getcookieStore () { return cookieStore; }
	    public HttpContext  getlocalContext () { return localContext; }
	    //public HttpClient   gethttpClient () { return httpclient; }
		
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
				
				if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(company)) {
					this.lomo = new LomoData(username, password, company);
				}
			}
			return this.lomo;
		}
		
		public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { //
			this.lomo = null;
			
			try {
			LomoData lomo1 = getLomoData();
			String ystatus = null;
			if ( lomo1 != null) { ystatus = lomoLogin(lomo1); }
			Log.d(TAG, "End of preferred state change");
			Log.d(TAG, ystatus);
			
			} catch (Exception e) {
				Log.d(TAG, "Exception trying to login");
			}
			
		}
		
		
	    public String lomoLogin (LomoData lomo) {
	    	
	    	//public static final String LOMO_URL_STRING = "http://citrix.logicmonitor.com/santaba/rpc/signIn?c=citrix&u=apiuser&p=helloworld";


	    	String loginURL = "http://".concat(lomo.getCompany()).concat(".logicmonitor.com/santaba/rpc/signIn?c=").concat(lomo.getCompany()).concat("&u=").concat(lomo.getUsername()).concat("&p=").concat(lomo.getPassword());
	    	Log.d(TAG,loginURL);
	    	
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
	        } catch (Exception e){
	        	Log.d(TAG, "Login Exception encountered.");
	            e.printStackTrace();
	        } 
	        
	        String status = response.getStatusLine().toString();
	        Log.d(TAG, "end of lomoLogin");
	        Log.d(TAG, status);
	        
	        //TBD modify to return different codes for successful and failed logins
	        
	    	return status;
	    	
	    }
		

}