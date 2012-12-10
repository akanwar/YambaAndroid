package com.teemtok.yamba;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

	static final String[] FROM = { DbHelper.C_HOST, DbHelper.C_STARTONLOCALTIME, DbHelper.C_DATASOURCEINSTANCE, DbHelper.C_LEVEL,
			DbHelper.C_DATAPOINT }; //
	// DbHelper.C_THRESHOLDS, DbHelper.C_VALUE, DbHelper.C_LEVEL,
	// DbHelper.C_ID};
	// DbHelper.C_THRESHOLDS, DbHelper.C_VALUE,

	static final int[] TO = { R.id.textHost, R.id.textStartOnLocal, R.id.textDataSourceInstance, R.id.textAlertLevel, R.id.textDatapoint }; //
	String datefromAPI = null;
	final Calendar apiCalendarObject = Calendar.getInstance();
	final Calendar lastMidnightCalendarObject = Calendar.getInstance();
	private YambaApplication yamba;

	SimpleDateFormat apiformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	SimpleDateFormat time_only_format = new SimpleDateFormat("hh:mm a");
	SimpleDateFormat date_only_format = new SimpleDateFormat("yyyy MMM dd");

	TextView textDate,textHost;

	// R.id.textAlertLevel, R.id.textprimaryKey};
	// R.id.textThreshold, R.id.textValue,

	// Constructor

	public ColorLevelAdapter(Context context, Cursor c) { //
		super(context, R.layout.alert_row, c, FROM, TO);
		lastMidnightCalendarObject.set(Calendar.HOUR_OF_DAY, 0);
		lastMidnightCalendarObject.set(Calendar.MINUTE, 0);
		lastMidnightCalendarObject.set(Calendar.SECOND, 0);	
		this.yamba = (YambaApplication) context.getApplicationContext();
	}

	// This is where the actual binding of a cursor to view happens
	@Override
	public void bindView(View row, Context context, Cursor cursor) { //
		super.bindView(row, context, cursor);
		
		// Set the color of the level text
		String level = cursor.getString(cursor.getColumnIndex(DbHelper.C_LEVEL)); //
		int alertId = cursor.getInt(cursor.getColumnIndex(DbHelper.C_ALERTID)); //
		TextView textLevel = (TextView) row.findViewById(R.id.textAlertLevel); //
		textHost = (TextView) row.findViewById(R.id.textHost);
		if(yamba.getisReadinfo(alertId)) {
			textHost.setTextAppearance(context, R.style.normalText);
		} else {
			textHost.setTextAppearance(context, R.style.boldText);
		}
		// //
		if (level.equals("critical")) {
			textLevel.setTextColor(Color.parseColor("#990000"));
		} else if (level.equals("warn")) {
			textLevel.setTextColor(Color.parseColor("#FFCF33"));
		} else if (level.equals("error")) {
			textLevel.setTextColor(Color.parseColor("#FF9933"));
		} else {
			textLevel.setTextColor(Color.parseColor("#000000"));
		}

		// Set acknowledged icon
		int isAcked = cursor.getInt(cursor.getColumnIndex(DbHelper.C_ISACKED)); //

		ImageView ackimg;
		ackimg = (ImageView) row.findViewById(R.id.imageView1);

		if (isAcked == 1) {
			ackimg.setImageResource(R.drawable.presence_online_blue);

		} else {
			ackimg.setImageResource(R.drawable.presence_invisible);
		}

		// Massage Date text
		textDate = (TextView) row.findViewById(R.id.textStartOnLocal);
		datefromAPI = cursor.getString(cursor.getColumnIndex(DbHelper.C_STARTONLOCALTIME));
		try {
			// Date parsed = format.parse(dateString);
			apiCalendarObject.setTime(apiformat.parse(datefromAPI));

			if (apiCalendarObject.compareTo(lastMidnightCalendarObject) > 0) {
				// C happened before c1

				String todays_Date = time_only_format.format(apiCalendarObject.getTime());
				// Log.d(TAG, "time only format Date: " + todays_Date);
				textDate.setText(todays_Date);

			} else {
				// C1 happened before c
				String nottodays_Date = date_only_format.format(apiCalendarObject.getTime());
				// Log.d(TAG, "date only format todays Date: " +
				// nottodays_Date);
				textDate.setText(nottodays_Date);
			}

		} catch (ParseException pe) {
			Log.d(TAG, "ERROR: Cannot parse \"" + datefromAPI + "\"");
			pe.printStackTrace();
		}

	}
}