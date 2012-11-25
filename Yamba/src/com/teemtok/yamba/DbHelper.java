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
	static final String C_STARTONUNIXTIME = "startOn";
	static final String C_ALERTID = "alertid";
	static final String C_ISACKED = "isAcked";
	static final String C_ACKCOMMENT = "ackComment";
	
	

	
	Context context;

	// Constructor
	public DbHelper(Context context) { //
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	// Called only once, first time the DB is created
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	// Called whenever newVersion != oldVersion
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
	
	}

	
}
