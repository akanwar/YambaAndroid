package com.teemtok.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class LomoData { //
	private static final String TAG = LomoData.class.getSimpleName();
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
	private static final String GET_ALL_ORDER_BY = C_STARTONUNIXTIME + " DESC";
	private static final String[] MAX_CREATED_AT_COLUMNS = { "max("
			+ LomoData.C_STARTONUNIXTIME + ")" };
	//private static final String[] DB_TEXT_COLUMNS = { C_TEXT };

	// DbHelper implementations
	class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating database: " + DB_NAME);
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
					+ C_STARTONUNIXTIME + " integer, "
					+ C_ALERTID + " integer) " ;
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

	private final DbHelper dbHelper; // Created only once

	public LomoData(Context context) { //
		this.dbHelper = new DbHelper(context);
		Log.d(TAG, "Initialized LomoData class");
	}

	public void close() { //
		this.dbHelper.close();
	}

	public void insertOrIgnore(ContentValues values) { //
		//Log.d(TAG, "insertOrIgnore");
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //
		try {
			db.insertWithOnConflict(TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE); //
		} finally {
			db.close(); //
		}
	}

	public Cursor getStatusUpdates() { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}

	public long getLatestStatusCreatedAtTime() { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null, null,
					null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}


	public int getAlertCount(String level) { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		int ret = 0;

		final String SQL_STATEMENT = "SELECT COUNT(*) FROM lomoalerts where level=?";

		try {
			Cursor cursor = db.rawQuery(SQL_STATEMENT, new String[] { level });

			try {
				ret = cursor.moveToNext() ? cursor.getInt(0) : 0;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}

		Log.d(TAG, "getAlertCount for level " + level + "returning " + ret);
		return ret;
		// query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}

	/*
	public long getLatestStatusCreatedAtTime() { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null, null,
					null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	
	

	public String getStatusTextById(long id) { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id,
					null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	*/

	public void purgeDataBeforeInsert() { //
		Log.d(TAG, "purgeDataBeforeInsert");
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //
		try {
			dbHelper.purgeData(db);
		} finally {
			db.close(); //
		}
	}
}