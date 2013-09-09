package com.ezeepay.services;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

public class PrefFragment extends PreferenceFragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
		if (theme_style)
			getActivity().setTheme(android.R.style.Theme_Holo);
		else
			getActivity().setTheme(android.R.style.Theme_Holo_Light);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}

	@Override
	public void onResume()
	{
		super.onResume();

		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
		if (theme_style)
			setBackgroundColorForViewTree((ViewGroup) getActivity().getWindow().getDecorView(),
					getResources().getColor(R.color.background_dark));
		else
			setBackgroundColorForViewTree((ViewGroup) getActivity().getWindow().getDecorView(),
					getResources().getColor(R.color.background_light));

	}

	private static void setBackgroundColorForViewTree(ViewGroup viewGroup, int color)
	{
		for (int i = 0; i < viewGroup.getChildCount(); i++)
		{
			View child = viewGroup.getChildAt(i);
			if (child instanceof ViewGroup)
				setBackgroundColorForViewTree((ViewGroup) child, color);
			child.setBackgroundColor(color);
		}
		viewGroup.setBackgroundColor(color);
	}

}