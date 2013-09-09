package com.ezeepay.services;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class Settings_Activity extends PreferenceActivity
{
	int style_value = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			// SharedPreferences prefs =
			// this.getSharedPreferences("com.ezeepay.service",
			// Context.MODE_PRIVATE);
			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo_Light);
			else
				setTheme(android.R.style.Theme_Holo_Light);

			// setContentView(R.layout.preference_layout);
			getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
			// fillmenu();
			ListPreference itemList = (ListPreference) findPreference("pref_darktheme");
			itemList.setSummary(itemList.getEntry());
			itemList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue)
				{
					Toast.makeText(Settings_Activity.this, "second", Toast.LENGTH_LONG).show();
					return true;
				}
			});
		}

		catch (Exception e)
		{
			Log.e("fatal", "error on preference activity" + e);
		}
	}

	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// String xx =l.getItemAtPosition(position).toString();

		if (position == 0)
		{
			Toast.makeText(this, "Bank/Wallet settings", Toast.LENGTH_SHORT).show();
		}
		else if (position == 1)
		{
			Toast.makeText(this, "My Data", Toast.LENGTH_SHORT).show();
		//	Intent intent1 = new Intent(Settings_Activity.this, Editmydata_Activity.class);
			//startActivity(intent1);
		}
		else if (position == 2)
		{
			// Toast.makeText(this, "Themes", Toast.LENGTH_SHORT).show();
		}
		else if (position == 3)
		{

			// Toast.makeText(this, "Advanced", Toast.LENGTH_SHORT).show();
		}
	}

	// sample code for back key press listener
	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // do
	 * something on back.
	 * Toast.makeText(this,"backkey",Toast.LENGTH_SHORT).show(); return true; }
	 * 
	 * return super.onKeyDown(keyCode, event); }
	 */

}
