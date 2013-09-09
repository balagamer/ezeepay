package com.ezeepay.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Instapay_Activity extends Activity
{

	connection_check check = new connection_check(this);
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
		if (theme_style)
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
		setContentView(R.layout.instapay_layout);

	}

}
