package com.teemtok.yamba;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener { //
	private EditTextPreference username;
	private EditTextPreference company;
	private EditTextPreference password;
	private ListPreference interval;


	@Override
	protected void onCreate(Bundle savedInstanceState) { //
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs); //
		// Get a reference to the preferences
		username = (EditTextPreference) getPreferenceScreen().findPreference("Username");
		company = (EditTextPreference) getPreferenceScreen().findPreference("Company");
		password = (EditTextPreference) getPreferenceScreen().findPreference("Password");
		interval = (ListPreference) getPreferenceScreen().findPreference("interval");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Setup the initial values
		setSummaryforPrefs();

		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Let's do something a preference value changes
		setSummaryforPrefs();


	}
	
	public void setSummaryforPrefs() {
		String textUsername = getPreferenceScreen().getSharedPreferences().getString("Username", "NOT SET");
		//username.setSummary("Currently set to <" + getPreferenceScreen().getSharedPreferences().getString("Username", "NOT SET").concat(">"));
		if (textUsername.equals("")) {
			username.setSummary("Currently set to <NOT SET>");
		} else {
			username.setSummary("Currently set to <" + textUsername + ">");
		}
		
		String textCompany = getPreferenceScreen().getSharedPreferences().getString("Company", "NOT SET");
		//String textCompany = company.setSummary("Currently set to <" + getPreferenceScreen().getSharedPreferences().getString("Company", "NOT SET").concat(">"));
		if (textCompany.equals("")) {
			company.setSummary("Currently set to <NOT SET>");
		} else {
			company.setSummary("Currently set to <" + textCompany + ">");
		}
		
		String textPassword = getPreferenceScreen().getSharedPreferences().getString("Password", "NOT SET");
		if (textPassword.equals("NOT SET") ||  (textPassword.equals(""))) {
			password.setSummary("Currently set to <NOT SET>");
		} else {
			password.setSummary("Currently set to <******>");
		}
		String selected = getPreferenceScreen().getSharedPreferences().getString("interval", "0");
		int index = interval.findIndexOfValue(selected); 
		interval.setSummary("Currently value is <" + interval.getEntries()[index].toString().concat(">"));
	}

}