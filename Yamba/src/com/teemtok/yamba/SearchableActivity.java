package com.teemtok.yamba;

import com.teemtok.yamba.PullToRefreshListView.OnRefreshListener;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Context;

public class SearchableActivity extends Activity implements OnItemClickListener {
	private static final String TAG = AlertActivity.class.getSimpleName();
	PullToRefreshListView listViewSearch; //
	ColorLevelAdapter colorAdapter;
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	String query = null;
	private YambaApplication yamba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable);
		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		this.yamba = (YambaApplication) getApplication();
		listViewSearch = (PullToRefreshListView) findViewById(R.id.listSearchAlerts);

		listViewSearch.setOnItemClickListener(this);
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
			suggestions.saveRecentQuery(query, null);
			doMySearch(query);
		}

		listViewSearch.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Your code to refresh the list contents

				doMySearch(query);
				listViewSearch.onRefreshComplete();
			}

		});
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "in OnPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "in OnStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		Log.d(TAG, "in OnDestroy");

		db.close();
	}

	private void doMySearch(String query) {
		// TODO Auto-generated method stub
		Log.d(TAG, "search being performed for: " + query);
		String arg = DbHelper.C_HOST + " like '%".concat(query).concat("%' OR ") + DbHelper.C_DATAPOINT + " like '%".concat(query).concat("%' OR ")
				+DbHelper.C_DATASOURCE + " like '%".concat(query).concat("%' OR ") + DbHelper.C_DATASOURCEINSTANCE + " like '%".concat(query).concat("%' OR ")
				+DbHelper.C_VALUE + " like '%".concat(query).concat("%'");

		try {
			Log.d(TAG, "search argument for search query: " + arg);
			cursor = db.query(DbHelper.TABLE, null, arg, null, null, null, DbHelper.C_STARTONUNIXTIME + " DESC");		

		} catch (SQLiteException e) {
			Log.d(TAG, "SQLiteException in refreshAlertData");
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "exception in refreshAlertData");
			// e.printStackTrace();
		}

		if (cursor != null) {
			startManagingCursor(cursor);
			colorAdapter = new ColorLevelAdapter(this, cursor);
			listViewSearch.setAdapter(colorAdapter);
		} else {
			Log.d(TAG, "cursor was NULL");
		}

		Toast.makeText(SearchableActivity.this, "Search resuts for " + query, Toast.LENGTH_LONG).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_searchable, menu);
		// SearchManager searchManager = (SearchManager)
		// getSystemService(Context.SEARCH_SERVICE);
		// SearchView searchView = (SearchView)
		// menu.findItem(R.id.menu_search).getActionView();
		// searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		// searchView.setIconifiedByDefault(false); // Do not iconify the
		// widget; expand it by default
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Log.d(TAG, "You clicked on view : " + arg1.getId() + " position : " + arg2 + " and id : " + arg3);
		yamba.insertisReadinfo(arg3);
		Intent myIntent = new Intent(this, AlertDetailActivity.class);
		myIntent.putExtra("primarykey", arg3);
		startActivity(myIntent);

	}

}
