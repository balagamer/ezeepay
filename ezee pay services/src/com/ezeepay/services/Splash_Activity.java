package com.ezeepay.services;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Splash_Activity extends Activity
{
    
    private static String TAG = Splash_Activity.class.getName();
    private static long SLEEP_TIME = 1; // Sleep for some time
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	// try
	{
	    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes title bar
	    this.getWindow()
		    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Removes
	    
	    final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
	    boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
	    if (theme_style)
		setTheme(android.R.style.Theme_Holo);
	    else
		setTheme(android.R.style.Theme_Holo_Light);
	    
	    setContentView(R.layout.splash);
	    
	    // Start timer and launch main activity
	    IntentLauncher launcher = new IntentLauncher();
	    launcher.start();
	}
	// catch (Exception e)
	{
	    // Log.e("fatal", "error on splash activity" + e);
	}
    }
    
    private class IntentLauncher extends Thread
    {
	@Override
	public void run()
	{
	    try
	    {
		// Sleeping
		Thread.sleep(SLEEP_TIME * 3000);
	    }
	    catch (Exception e)
	    {
		Log.e(TAG, e.getMessage());
	    }
	    
	    // Start main activity
	    Intent intent = new Intent(Splash_Activity.this, Login_Activity.class);
	    Splash_Activity.this.startActivity(intent);
	    Splash_Activity.this.finish();
	}
    }
}
