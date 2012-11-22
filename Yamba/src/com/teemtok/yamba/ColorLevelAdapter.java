package com.teemtok.yamba;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ColorLevelAdapter extends SimpleCursorAdapter { //

	static final String[] FROM = { DbHelper.C_HOST,
			DbHelper.C_STARTONLOCALTIME, DbHelper.C_DATASOURCEINSTANCE,
			DbHelper.C_THRESHOLDS, DbHelper.C_VALUE, DbHelper.C_LEVEL, DbHelper.C_ID}; //
	static final int[] TO = { R.id.textHost, R.id.textStartOnLocal,
			R.id.textDataSourceInstance, R.id.textThreshold, R.id.textValue,
			R.id.textAlertLevel, R.id.textprimaryKey}; //

	// Constructor

	public ColorLevelAdapter(Context context, Cursor c) { //
		super(context, R.layout.alert_row, c, FROM, TO);
	}

	// This is where the actual binding of a cursor to view happens
	@Override
	public void bindView(View row, Context context, Cursor cursor) { //
		super.bindView(row, context, cursor);
		// Manually bind created at timestamp to its view
		String level = cursor.getString(cursor
				.getColumnIndex(DbHelper.C_LEVEL)); //
		TextView textLevel = (TextView) row
				.findViewById(R.id.textAlertLevel); //
		//textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp)); //
	   if (level.equals("critical")) {
			textLevel.setTextColor(Color.parseColor("#FF0000"));
	   } else if (level.equals("warn")) {
			textLevel.setTextColor(Color.parseColor("#FFFF00"));
	   } else if (level.equals("error")) {
			textLevel.setTextColor(Color.parseColor("#FFA500"));
	   } else  {
			textLevel.setTextColor(Color.parseColor("#FFFFFF"));
	   }  
		   

	}
}