package com.teemtok.yamba;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
//import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ColorLevelAdapter extends SimpleCursorAdapter { //
	
	private static final String TAG = ColorLevelAdapter.class.getSimpleName();
	
	//private String format ="yyyy-MM-dd HH:mm:ss z";
	private String format ="yyyy";
	private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

	static final String[] FROM = { DbHelper.C_HOST,
			DbHelper.C_STARTONLOCALTIME, DbHelper.C_DATASOURCEINSTANCE,
			DbHelper.C_LEVEL, DbHelper.C_DATAPOINT }; //
	// DbHelper.C_THRESHOLDS, DbHelper.C_VALUE, DbHelper.C_LEVEL,
	// DbHelper.C_ID};
	// DbHelper.C_THRESHOLDS, DbHelper.C_VALUE,

	static final int[] TO = { R.id.textHost, R.id.textStartOnLocal,
			R.id.textDataSourceInstance, R.id.textAlertLevel,
			R.id.textDatapoint }; //

	// R.id.textAlertLevel, R.id.textprimaryKey};
	// R.id.textThreshold, R.id.textValue,

	// Constructor

	public ColorLevelAdapter(Context context, Cursor c) { //
		super(context, R.layout.alert_row, c, FROM, TO);
		
	}

	// This is where the actual binding of a cursor to view happens
	@Override
	public void bindView(View row, Context context, Cursor cursor) { //
		super.bindView(row, context, cursor);
		
		
		
		// Set the color of the level text
		String level = cursor
				.getString(cursor.getColumnIndex(DbHelper.C_LEVEL)); //
		TextView textLevel = (TextView) row.findViewById(R.id.textAlertLevel); //
		// textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
		// //
		if (level.equals("critical")) {
			textLevel.setTextColor(Color.parseColor("#FF0000"));
		} else if (level.equals("warn")) {
			textLevel.setTextColor(Color.parseColor("#FFFF00"));
		} else if (level.equals("error")) {
			textLevel.setTextColor(Color.parseColor("#FFA500"));
		} else {
			textLevel.setTextColor(Color.parseColor("#FFFFFF"));
		}

		// Set acknowledged icon
		int isAcked = cursor.getInt(cursor.getColumnIndex(DbHelper.C_ISACKED)); //

		ImageView ackimg;
		ackimg = (ImageView) row.findViewById(R.id.imageView1);

		if (isAcked == 1) {
			ackimg.setImageResource(R.drawable.presence_online);

		} else {
			ackimg.setImageResource(R.drawable.presence_invisible);
		}
		
		/*
		// Massage Date text
		String datefromAPI = cursor.getString(cursor.getColumnIndex(DbHelper.C_STARTONLOCALTIME));
         
		//yyyy-MM-dd HH:mm:ss z
		datefromAPI = "10";

	
		
		Date alertdate = null;
		try {
			alertdate = sdf.parse(datefromAPI);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"Parse exception");
			//e.printStackTrace();
		} catch (Exception e) {
			Log.d(TAG,"Some exception");
		}

		
		
		Log.d(TAG, "Date (from API) is :" + datefromAPI );
		if (alertdate != null) {
			Log.d(TAG, "Date (parsed) is : " + alertdate.getYear() +"|"+ alertdate.getMonth() +"|"+ alertdate.getDay() +"|"+ alertdate.getHours() +"|"+ alertdate.getMinutes()+"|"+ alertdate.getSeconds() );
		}
		
		*/

	}
}