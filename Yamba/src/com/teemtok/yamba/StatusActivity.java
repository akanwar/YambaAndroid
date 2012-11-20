package com.teemtok.yamba;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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


public class StatusActivity extends Activity implements OnClickListener { //

	
	private static final String TAG = "StatusActivity";
    EditText editText;
    Button updateButton;
    SharedPreferences prefs;
    YambaApplication yamba;
    
    
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_status);
    
    	// Find views
    	editText = (EditText) findViewById(R.id.editText); //
    	updateButton = (Button) findViewById(R.id.buttonUpdate);
    	updateButton.setOnClickListener(this); 
    	
    	
    	try {
    		yamba = ((YambaApplication) getApplication()); //
    		LomoCredentials lomo1 = yamba.getLomoCredentials();
    		String ystatus = null;
			if ( lomo1 != null) { ystatus = yamba.lomoLogin(lomo1); }
			Log.d(TAG, "ystatus is " + ystatus);
			
    	} catch (Exception e) {
    		Log.d(TAG, "Exception trying to login");
    	}
    	
    }
    
    // Called when button is clicked
    public void onClick(View v) {
   		String status = editText.getText().toString();
   		Log.d(TAG, "onClicked Starting Alerts Activity");
   		startActivity(new Intent(this, AlertActivity.class));
   		
   	}    	
    	
    	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater(); //
    	inflater.inflate(R.menu.menu, menu); //
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) { //
    	case R.id.itemPrefs:
    		startActivity(new Intent(this, PrefsActivity.class)); //
    		break;
    	case R.id.itemServiceStart:
    		if (!yamba.isServiceRunning()) {
    			startService(new Intent(this, UpdaterService.class)); //
    			Log.d(TAG, "Service started");
    		} else {
    			Log.d(TAG, "Service is already running");
    		}
    		break;
    		case R.id.itemServiceStop:
    		stopService(new Intent(this, UpdaterService.class)); //
    		break;
    	}
    	return true; //
    }
    
    public void onStop(View v) {
    	// TBD never seems to get called.
    	// httpclient.getConnectionManager().shutdown();
    	Log.d(TAG,"httpclient destroyed");
    }
    
    
    
    
}




