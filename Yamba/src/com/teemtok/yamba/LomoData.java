package com.teemtok.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class LomoData { //
	private static final String TAG = LomoData.class.getSimpleName();
	static final String DB_NAME = "lomoalerts.db"; //
	static final int DB_VERSION = 1; //
	static final String TABLE = "lomoalerts"; //
	static final String TABLE_ISREAD = "isReadInfo";
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
	static final String C_ISREAD = "isRead";
	static final String C_ISCURRENT = "isCurrent";
	private static final String GET_ALL_ORDER_BY = C_STARTONUNIXTIME + " DESC";
	private static final String[] MAX_CREATED_AT_COLUMNS = { "max(" + LomoData.C_STARTONUNIXTIME + ")" };
	long tableexists;

	// DbHelper implementations
	class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		boolean doesTableExist(SQLiteDatabase db) {

			String doesTableExistSQL = "SELECT count(name) as count FROM sqlite_master where name = '" + TABLE + "'";
			Cursor c = db.rawQuery(doesTableExistSQL, null);
			c.moveToFirst();
			tableexists = c.getLong(c.getColumnIndex("count"));
			c.close();

			Log.d(TAG, "Chexking on database: " + doesTableExistSQL);
			Log.d(TAG, "table exists :" + tableexists);

			if (tableexists == 0) {
				return false;
			} else {
				return true;
			}

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			if (!doesTableExist(db)) {
				Log.d(TAG, "Creating database: " + DB_NAME);
				String sql = "create table " + TABLE + " (" + C_ID + " integer primary key , " + C_DATAPOINT + " text, " + C_DATASOURCE + " text, "
						+ C_DATASOURCEINSTANCE + " text, " + C_HOST + " text, " + C_LEVEL + " text, " + C_VALUE + " text, " + C_THRESHOLDS
						+ " text, " + C_STARTONLOCALTIME + " text, " + C_STARTONUNIXTIME + " integer, " + C_ALERTID + " integer , " + C_ISACKED
						+ " integer, " + C_ACKCOMMENT + " text, " + C_ISCURRENT + " integer )";
				String isReadsql = "create table " + TABLE_ISREAD + " (" + C_ALERTID + " integer primary key , " + C_ISREAD + " integer )";

				try {
					db.execSQL(sql); //

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Table creation lomoalerts failed due to error in create table query!!");
					// e.printStackTrace();
				}
				Log.d(TAG, "onCreated sql: " + sql);
				try {
					db.execSQL(isReadsql); //

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Table creation for isReadInfo failed due to error in create table query!!");
					// e.printStackTrace();
				}
				Log.d(TAG, "onCreated isRead sql: " + isReadsql);

			} else {
				Log.d(TAG, "Table exists so skipping Create ");
			}

		}

		// Called whenever newVersion != oldVersion
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
			final String TAG1 = TAG.concat("-onUpgrade");
			// Typically do ALTER TABLE statements, but...we're just in
			// development,
			// so:
			db.execSQL("drop table if exists " + TABLE); // drops the old
															// database
			Log.d(TAG1, "onUpgrade");
			onCreate(db); // run onCreate to get new database
		}

		public void purgeData(SQLiteDatabase db) { // :
			final String TAG1 = TAG.concat("-purgeData");
			// db.execSQL("drop table if exists " + TABLE); // drops the old
			// database
			if (doesTableExist(db)) {
				db.delete(TABLE, null, null);
			}
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
		// Log.d(TAG, "insertOrIgnore");
		long rowid = -1;
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //
		try {
			rowid = db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			Log.d(TAG, "-insertOrIgnore: Insert happened and returned rowid = " + rowid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "-insertOrIgnore: Database insert failed. rowid = " + rowid);
			e.printStackTrace();
		} //

	}

	public void insertOrIgnoreisReadInfo(ContentValues values) { //
		// Log.d(TAG, "insertOrIgnore");
		long rowid = -1;
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //
		try {
			rowid = db.insertWithOnConflict(TABLE_ISREAD, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "-insertOrIgnoreisReadInfo: Database insert failed. rowid = " + rowid);
			e.printStackTrace();
		} //
		//Log.d(TAG, "-insertOrIgnoreisReadInfo: Insert happened and returned rowid = " + rowid);

	}

	public boolean zeroCurrentAlerts() {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		boolean ret = false;

		String SQL_STATEMENT = null;
		String[] qparams = null;

		SQL_STATEMENT = "UPDATE  " + TABLE + "  SET " + C_ISCURRENT + " = 0 WHERE " + C_ISCURRENT + " = " + C_ISCURRENT;

		try {
			db.execSQL(SQL_STATEMENT);
			ret = true;
		} catch (Exception e) {
			// db.close();
			ret = false;
			Log.d(TAG, "-zeroCurrentAlerts: Database insert failed on isCurrent for lomoalerts");
			e.printStackTrace();
			return ret;
		}

		Log.d(TAG, "-zeroCurrentAlerts: isCurrent set to 0 for lomoalerts");
		return ret;

	}
	
	
	public boolean deleteStaleAlerts() {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		boolean ret = false;

		String SQL_STATEMENT = null;
		String[] qparams = null;

		SQL_STATEMENT = "DELETE from " + TABLE + " WHERE " + C_ISCURRENT + " = 0 ";

		try {
			db.execSQL(SQL_STATEMENT);
			ret = true;
		} catch (Exception e) {
			// db.close();
			ret = false;
			Log.d(TAG, "-dropStaleAlerts: Database delete failed on isCurrent = 0 for lomoalerts");
			e.printStackTrace();
			return ret;
		}

		Log.d(TAG, "-dropStaleAlerts: isCurrent = 0 is deleted from lomoalerts");
		return ret;

	}

	public Cursor getStatusUpdates() { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}

	public synchronized int getAlertCount(String level) { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		int ret = 0;

		String SQL_STATEMENT = null;
		String[] qparams = null;

		if (level.equals("any")) {
			SQL_STATEMENT = "SELECT COUNT(*) FROM lomoalerts";
		} else {
			SQL_STATEMENT = "SELECT COUNT(*) FROM lomoalerts where level=?";
			qparams = new String[] { level };
		}

		try {
			Cursor cursor = db.rawQuery(SQL_STATEMENT, qparams);

			try {
				ret = cursor.moveToNext() ? cursor.getInt(0) : 0;
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			// db.close();
			e.printStackTrace();
		}

		Log.d(TAG, "-getAlertCount for level " + level + " -- returning " + ret);
		return ret;
		// query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}

	public void purgeDataBeforeInsert() { //
		Log.d(TAG, "purgeDataBeforeInsert");
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //
		try {
			dbHelper.purgeData(db);
		} finally {
			// db.close(); //
		}
	}

	public synchronized boolean getisReadInfofromDb(long alertId) { //
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		int ret = 0;

		String SQL_STATEMENT = null;
		String[] qparams = null;

		SQL_STATEMENT = "SELECT " + C_ISREAD + " FROM " + TABLE_ISREAD + " where " + C_ALERTID + " = ?";
		qparams = new String[] { Long.toString(alertId) };

		try {
			Cursor cursor = db.rawQuery(SQL_STATEMENT, qparams);

			try {
				ret = cursor.moveToNext() ? cursor.getInt(0) : 0;
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			// db.close();
			e.printStackTrace();
		}

		Log.d(TAG, "-getisReadInfofromDb for level " + alertId + " -- returning " + ret);
		if (ret == 1) {
			return true;
		}
		return false;

		// query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
}