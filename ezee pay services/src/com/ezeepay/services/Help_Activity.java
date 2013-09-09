package com.ezeepay.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Help_Activity extends Activity
{
	private String questions[] =
	{ "Select Question", "What is Ezeepay?", "Highlights of Ezeepay", "Payment Methods", "What can i do with ezeepay?",
			"Is this app safe?", "What if my trasnaction fails?", "What is voice based recahrge?" };
	private Spinner help_spinner;
	private ImageView help_image;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
		if (theme_style)
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
		setContentView(R.layout.help_layout);

		help_spinner = (Spinner) findViewById(R.id.help_spinner);
		help_image = (ImageView) findViewById(R.id.help_image);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_modified, questions);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		help_spinner.setAdapter(dataAdapter);

		final TextView t2 = (TextView) findViewById(R.id.help_text);
		t2.setGravity(Gravity.CENTER_HORIZONTAL);
		t2.setGravity(Gravity.CENTER_VERTICAL);
		// t2.setText(getResources().getString(R.string.faq));

		help_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
			{
				if (position == 1)
				{
					t2.setText(getResources().getString(R.string.ezeepay_platform));
					help_image.setImageResource(R.drawable.ezeepaylogo);
				}
				else if (position == 2)
				{
					t2.setText(getResources().getString(R.string.ezeepay_highlights));
					help_image.setImageResource(R.drawable.ezeepaylogo);
				}
				else if (position == 3)
				{
					t2.setText(getResources().getString(R.string.ezeepay_payments));
					help_image.setImageResource(R.drawable.ezeepay_pay3);
				}
				else if (position == 4)
				{
					t2.setText(getResources().getString(R.string.ezeepay_methods));
					help_image.setImageResource(R.drawable.ezeepay_pay1);
				}
				else if (position == 5)
				{
					t2.setText(getResources().getString(R.string.ezeepay_safe));
				}
				else if (position == 6)
				{
					t2.setText(getResources().getString(R.string.ezeepay_paymenthelp));
				}
				else if (position == 7)
				{
					t2.setText(getResources().getString(R.string.ezeepay_voice));
				}

			}

			public void onNothingSelected(AdapterView<?> arg0)
			{
				Toast.makeText(Help_Activity.this, "nothing selected", Toast.LENGTH_SHORT).show();

			}
		});

	}

}
