package com.teemtok.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper { //
	static final String TAG = "DbHelper";
	static final String DB_NAME = "lomoalerts.db"; //
	static final int DB_VERSION = 1; //
	static final String TABLE = "lomoalerts"; //
	static final String C_ID = BaseColumns._ID;
	static final String C_DATAPOINT = "dataPoint";
	static final String C_DATASOURCE = "dataSource";
	static final String C_DATASOURCEINSTANCE = "dataSourceInstance";
	static final String C_HOST = "host";
	static final String C_LEVEL = "level";
	static final String C_VALUE = "value";
	static final String C_THRESHOLDS = "thresholds";
	static final String C_STARTONLOCALTIME = "startOnLocal";
	static final String C_ALERTID = "alertid";
	

	
	Context context;

	// Constructor
	public DbHelper(Context context) { //
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	// Called only once, first time the DB is created
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + " (" 
				+ C_ID + " integer primary key, " 
				+ C_DATAPOINT + " text, "
				+ C_DATASOURCE + " text, "
				+ C_DATASOURCEINSTANCE + " text, "
				+ C_HOST + " text, "
				+ C_LEVEL + " text, "
				+ C_VALUE + " text, "
				+ C_THRESHOLDS + " text, "
				+ C_STARTONLOCALTIME + " text, "
				+ C_ALERTID + " int) " ;
		db.execSQL(sql); //
		Log.d(TAG, "onCreated sql: " + sql);
	}

	// Called whenever newVersion != oldVersion
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
		final String TAG1 = TAG.concat("-onUpgrade");
	// Typically do ALTER TABLE statements, but...we're just in development,
	// so:
		db.execSQL("drop table if exists " + TABLE); // drops the old database
		Log.d(TAG1, "onUpgrade");
		onCreate(db); // run onCreate to get new database
	}
	
	public void purgeData (SQLiteDatabase db) { //:
		final String TAG1 = TAG.concat("-purgeData");
		db.execSQL("drop table if exists " + TABLE); // drops the old database
		onCreate(db); // run onCreate to get new database
		Log.d(TAG1, "purgeData set us up the bomb");
	}
	
}

/*
dataPoint String
dataSource String
dataSourceInstance String
host String
level String
value String
thresholds String
startOnLocal String
id Int
*/