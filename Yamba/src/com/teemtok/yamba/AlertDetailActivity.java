package com.teemtok.yamba;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import android.support.v4.app.NavUtils;

public class AlertDetailActivity extends Activity implements OnClickListener {

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
	TextView idalertacktitle;
	EditText editAckText;
	Button ackButton;
	String ackAlertId;
	TextView idvalAckCommentList;
	TextView idvalAckedList;

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
		idalertacktitle = (TextView) findViewById(R.id.idAlertAckTitle);

		try {
			idvalAckCommentList = (TextView) findViewById(R.id.idvalAckCommentList);
			idvalAckedList = (TextView) findViewById(R.id.idvalAckedList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "ack set up error");
		}

		// Ack Comment Button and
		editAckText = (EditText) findViewById(R.id.idAckComment); //
		ackButton = (Button) findViewById(R.id.idAckButton);
		ackButton.setOnClickListener(this);

		Intent myIntent = getIntent(); // gets the previously created intent
		long firstKeyName = myIntent.getLongExtra("primarykey", 1);

		Log.d(TAG, "onCreate -- primarykey is " + firstKeyName);

		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		this.yamba = (YambaApplication) getApplication();
		getAlertDetailbyID(firstKeyName);

	}

	@SuppressWarnings("deprecation")
	private void getAlertDetailbyID(Long key) {

		// Get the data from the database
		try {
			cursor = db.query(DbHelper.TABLE, null, DbHelper.C_ID + "=" + key,
					null, null, null, null);
			// cursor = db.rawQuery("select * from lomoalerts", null);
			// cursor = db.query(DbHelper.TABLE, null, null, null, null, null,
			// DbHelper.C_STARTONUNIXTIME + " DESC");

		} catch (SQLiteException e) {
			Log.d(TAG, "SQLiteException in getAlertDetailbyID");
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "exception in getAlertDetailbyID");
			e.printStackTrace();
		}

		if (cursor != null) {

			// startManagingCursor(cursor);
			Log.d(TAG,
					"cursor was NOT NULL and has X rows : " + cursor.getCount());

			cursor.moveToFirst();

			int id = cursor.getInt(cursor.getColumnIndex(DbHelper.C_ID));
			String host = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_HOST));
			String alertid = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_ALERTID));
			String value = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_VALUE));
			String level = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_LEVEL));
			String datapoint = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_DATAPOINT));
			String datasource = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_DATASOURCE));
			String datasourceinstance = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_DATASOURCEINSTANCE));
			String thresholds = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_THRESHOLDS));
			String startonlocaltime = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_STARTONLOCALTIME));
			String ackComment = cursor.getString(cursor
					.getColumnIndex(DbHelper.C_ACKCOMMENT));
			int isAcked = cursor.getInt(cursor
					.getColumnIndex(DbHelper.C_ISACKED));

			idvalalertid.setText(alertid);
			idvalhost.setText(host);
			idvalvalue.setText(value);
			idvallevel.setText(level);
			idvaldatapoint.setText(datapoint);
			idvaldatasource.setText(datasource);
			idvaldatasourceinstance.setText(datasourceinstance);
			idvalthresholds.setText(thresholds);
			idvalstartonlocaltime.setText(startonlocaltime);

			if (isAcked == 1)
				idvalAckedList.setText("true");
			else
				idvalAckedList.setText("false");
			idvalAckCommentList.setText(ackComment);

			if (isAcked == 1) {
				idalertacktitle.setVisibility(View.INVISIBLE);
				editAckText.setVisibility(View.INVISIBLE);
				ackButton.setVisibility(View.INVISIBLE);
			}

			Log.d(TAG, " Got id|host|alertid|value|level: " + id + "|" + host
					+ "|" + alertid + "|" + value + "|" + level);
			// Log.d(TAG,
			// " Got datapoint|datasource|datasourceinstance|threshold|startonlocaltime|ackComment|ACKED??: "
			// + datapoint +"|"+ datasource +"|"+ datasourceinstance +"|"+
			// thresholds +"|"+ startonlocaltime+"|"+ackComment+"|"+isAcked);

		} else {
			Log.d(TAG, "cursor was NULL");
		}

		cursor.close();

	}

	// Called when button is clicked //
	public void onClick(View v) {
		// (editAckText.getText().toString()); //
		Log.d(TAG, "onClicked ACK Button");
		getpushAck();

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

		// db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "in OnResume");
	}

	public void getpushAck() {

		final String TAG1 = TAG.concat("-pushAck");
		Context context = getApplicationContext();
		CharSequence text;

		int duration = Toast.LENGTH_LONG;

		// int duration = Toast.LENGTH_SHORT;

		// String ackComment = editAckText.getText().toString().replaceAll(" ",
		// "+");
		String LOMO_ACK_STRING = "https://".concat(yamba.getLomoCredentials().getCompany()).concat(".logicmonitor.com/santaba/rpc/confirmAlerts?ids=")
				.concat(idvalalertid
						.getText()
						.toString()
						.concat("&comment=")
						.concat(editAckText.getText().toString()
								.replaceAll(" ", "+")));
		Log.d(TAG1, "This is ACK URL: " + LOMO_ACK_STRING);
		boolean ackworked = yamba.pushAckandAckComment(LOMO_ACK_STRING);
		Log.d(TAG1, "done calling yamb push ack. success : " + ackworked);
		
		editAckText.setVisibility(View.INVISIBLE);
		ackButton.setVisibility(View.INVISIBLE);

		if (ackworked) {
			text = "Alert id: ".concat(idvalalertid.getText().toString())
					.concat(" is acked.");
			if (yamba.getLomoAlerts()) {
				Log.d (TAG1,"Yamba Alerts call successfull");
			} else {
				Log.d (TAG1,"Yamba Alerts call unsuccessfull");
			}
				
		} else {
			text = "Alert id: ".concat(idvalalertid.getText().toString())
					.concat(" could not be acked.");
		}

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();

	}

}
