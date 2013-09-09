package com.ezeepay.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class Quickpaydata_Activity extends Activity implements OnClickListener
{

	private TextView myphonenumber1_text, myphonenumber2_text, myphonenumber3_text, mydth_text, myinsurancepremium_text, mylandline_text,
			mydatacard_text, myinsurancedate_text, mylandlinecustno_text;
	private TextView mynumber1alerts_text, mynumber2alerts_text, mydthalerts_text, mydatacardalerts_text, insurancealerts_text,
			mylandlinealerts_text;

	int mYear, mMonth, mDay;
	private CheckBox[] alerts_check = new CheckBox[6];

	private TextView travelfrom_text, travelto_text;

	Button save_edit_button;
	connection_check check = new connection_check(this);
	SharedPreferences prefs_theme;
	private String selectedDate = "", method = "", method_description = "";
	private Date trans_date = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		try
		{
			prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);
			setContentView(R.layout.quickpaydata_layout);

			travelfrom_text = (TextView) findViewById(R.id.travelfrom_text);
			travelto_text = (TextView) findViewById(R.id.travelto_text);

			myphonenumber1_text = (TextView) findViewById(R.id.myphonenumber1_text);
			mynumber1alerts_text = (TextView) findViewById(R.id.mynumber1alerts_text);
			alerts_check[0] = (CheckBox) findViewById(R.id.mynumber1alerts_checkbox);
			alerts_check[0].setOnClickListener(this);

			myphonenumber2_text = (TextView) findViewById(R.id.myphonenumber2_text);
			mynumber2alerts_text = (TextView) findViewById(R.id.mynumber2alerts_text);
			alerts_check[1] = (CheckBox) findViewById(R.id.mynumber2alerts_checkbox);
			alerts_check[1].setOnClickListener(this);

			mydth_text = (TextView) findViewById(R.id.mydth_text);
			mydthalerts_text = (TextView) findViewById(R.id.mydthalerts_text);
			alerts_check[2] = (CheckBox) findViewById(R.id.mydthalerts_checkbox);
			alerts_check[2].setOnClickListener(this);

			mydatacard_text = (TextView) findViewById(R.id.mydatacard_text);
			mydatacardalerts_text = (TextView) findViewById(R.id.mydatacardalerts_text);
			alerts_check[3] = (CheckBox) findViewById(R.id.mydatacardalerts_checkbox);
			alerts_check[3].setOnClickListener(this);

			myinsurancepremium_text = (TextView) findViewById(R.id.myinsurancepremium_text);
			myinsurancedate_text = (TextView) findViewById(R.id.myinsurancedate_text);
			insurancealerts_text = (TextView) findViewById(R.id.insurancealerts_text);
			alerts_check[4] = (CheckBox) findViewById(R.id.insurancealerts_checkbox);
			alerts_check[4].setOnClickListener(this);

			mylandline_text = (TextView) findViewById(R.id.mylandline_text);
			mylandlinecustno_text = (TextView) findViewById(R.id.mylandlinecustno_text);
			mylandlinealerts_text = (TextView) findViewById(R.id.mylandlinealerts_text);
			alerts_check[5] = (CheckBox) findViewById(R.id.mylandlinealerts_checkbox);
			alerts_check[5].setOnClickListener(this);

			save_edit_button = (Button) findViewById(R.id.quickpay_button);
			save_edit_button.setOnClickListener(button_click);

			travelfrom_text.setText(prefs_theme.getString("pref_source", ""));
			travelto_text.setText(prefs_theme.getString("pref_destination", ""));
			myphonenumber1_text.setText(prefs_theme.getString("pref_mymobile", ""));
			mynumber1alerts_text.setText(prefs_theme.getString("pref_mymobilenumber1_date", ""));
			if (prefs_theme.getString("pref_mymobilenumber1_date", "").length() > 1)
			{
				alerts_check[0].setChecked(true);
			}
			else
			{
				alerts_check[0].setChecked(false);
			}

			myphonenumber2_text.setText(prefs_theme.getString("pref_mymobile2", " "));
			mynumber2alerts_text.setText(prefs_theme.getString("pref_mymobilenumber2_date", ""));
			if (prefs_theme.getString("pref_mymobilenumber2_date", "").length() > 1)
			{
				alerts_check[1].setChecked(true);
			}
			else
			{
				alerts_check[1].setChecked(false);
			}

			mydth_text.setText(prefs_theme.getString("pref_mydth", " "));
			mydthalerts_text.setText(prefs_theme.getString("pref_mydth_date", ""));
			if (prefs_theme.getString("pref_mydth_date", "").length() > 1)
			{
				alerts_check[2].setChecked(true);
			}
			else
			{
				alerts_check[2].setChecked(false);
			}

			mydatacard_text.setText(prefs_theme.getString("pref_mydatacard", " "));
			mydatacardalerts_text.setText(prefs_theme.getString("pref_mydatacard_date", ""));
			if (prefs_theme.getString("pref_mydatacard_date", "").length() > 1)
			{
				alerts_check[3].setChecked(true);
			}
			else
			{
				alerts_check[3].setChecked(false);
			}

			myinsurancepremium_text.setText(prefs_theme.getString("pref_myinsurance", ""));
			myinsurancedate_text.setText(prefs_theme.getString("pref_myinsurancedate", ""));
			insurancealerts_text.setText(prefs_theme.getString("pref_myinsurance_date", ""));
			if (prefs_theme.getString("pref_myinsurance_date", "").length() > 1)
			{
				alerts_check[4].setChecked(true);
			}
			else
			{
				alerts_check[4].setChecked(false);
			}

			mylandline_text.setText(prefs_theme.getString("pref_mylandline", ""));
			mylandlinecustno_text.setText(prefs_theme.getString("pref_mylandlinecustno", ""));
			mylandlinealerts_text.setText(prefs_theme.getString("pref_mylandline_date", ""));
			if (prefs_theme.getString("pref_mylandline_date", "").length() > 1)
			{
				alerts_check[5].setChecked(true);
			}
			else
			{
				alerts_check[5].setChecked(false);
			}

		}
		catch (Exception e)
		{
			Log.e("Fatal", "Fatal error on Quickpaydata activity\n" + e.toString());
		}

	}

	public String addtocalendar(String formatted_date, String selected_date, String event_type, String description)
	{
		String cid = "";
		long calID = 1;
		long startMillis = 0;
		long endMillis = 0;
		Date d3 = null;

		// String formattedDate = new
		// SimpleDateFormat("dd-MMM-yyyy").format(d3);
		// Log.e("actual date", formattedDate);
		try
		{
			SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
			d3 = parserSDF.parse(selected_date);
		}
		catch (Exception e)
		{

		}
		Calendar beginTime = Calendar.getInstance();
		beginTime.setTime(d3);
		startMillis = beginTime.getTimeInMillis();

		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		// values.put(Events.DTEND, endMillis);
		values.put(Events.DURATION, "P3600S");
		values.put(Events.ALL_DAY, 1);
		values.put(Events.TITLE, event_type + "Remainder");
		values.put(Events.HAS_ALARM, 1);
		values.put(Events.DESCRIPTION, description);
		values.put(Events.CALENDAR_ID, calID);
		TimeZone timeZone = TimeZone.getDefault();
		values.put(Events.EVENT_TIMEZONE, timeZone.getID());
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		long eventID = Long.parseLong(uri.getLastPathSegment());

		SharedPreferences.Editor editor = prefs_theme.edit();
		if (event_type.equals("Mobile Number recharge"))
		{
			editor.putString("pref_mymobilenumber1_alert", uri.toString());
			editor.putString("pref_mymobilenumber1_date", formatted_date);
		}
		else if (event_type.equals("Mobile Number 2 recharge"))
		{
			editor.putString("pref_mymobilenumber2_alert", uri.toString());
			editor.putString("pref_mymobilenumber2_date", formatted_date);
		}
		else if (event_type.equals("My DTH"))
		{
			editor.putString("pref_mydth_alert", uri.toString());
			editor.putString("pref_mydth_date", formatted_date);
		}
		else if (event_type.equals("My Datacard"))
		{
			editor.putString("pref_mydatacard_alert", uri.toString());
			editor.putString("pref_mydatacard_date", formatted_date);
		}
		else if (event_type.equals("My Insurance"))
		{
			editor.putString("pref_myinsurance_alert", uri.toString());
			editor.putString("pref_myinsurance_date", formatted_date);
		}
		else if (event_type.equals("My Landline"))
		{
			editor.putString("pref_mylandline_alert", uri.toString());
			editor.putString("pref_mylandline_date", formatted_date);
		}

		editor.commit();

		// add reminder
		ContentValues reminders = new ContentValues();
		reminders.put(Reminders.EVENT_ID, eventID);
		reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		reminders.put(Reminders.MINUTES, 180);

		Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
		Toast.makeText(getBaseContext(), "Added to calendar successfully", Toast.LENGTH_SHORT).show();

		return cid;
	}

	@Override
	public void onClick(View v)
	{
		Log.e("id", String.valueOf(v.getId()));
		switch (v.getId())
		{
		case R.id.mynumber1alerts_checkbox:
		{
			if (alerts_check[0].isChecked())
			{
				method = "Mobile Number recharge";
				method_description = "Notification for mobile recharge";
				calendar(method, method_description);
			}
			else
			{
				if (!prefs_theme.getString("pref_mymobilenumber1_alert", "").equals(""))
				{
					Uri x = Uri.parse(prefs_theme.getString("pref_mymobilenumber1_alert", ""));
					getContentResolver().delete(x, null, null);
					SharedPreferences.Editor editor = prefs_theme.edit();
					editor.putString("pref_mymobilenumber1_alert", "");
					editor.putString("pref_mymobilenumber1_date", "");
					editor.commit();
					mynumber1alerts_text.setText("");
					Toast.makeText(Quickpaydata_Activity.this, "Alert Removed", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}

		case R.id.mynumber2alerts_checkbox:
		{
			if (alerts_check[1].isChecked())
			{
				method = "Mobile Number 2 recharge";
				method_description = "Notification for 2nd mobile recharge";
				calendar(method, method_description);
			}
			else
			{
				if (!prefs_theme.getString("pref_mymobilenumber2_alert", "").equals(""))
				{
					Uri x = Uri.parse(prefs_theme.getString("pref_mymobilenumber2_alert", ""));
					getContentResolver().delete(x, null, null);
					SharedPreferences.Editor editor = prefs_theme.edit();
					editor.putString("pref_mymobilenumber2_alert", "");
					editor.putString("pref_mymobilenumber2_date", "");
					editor.commit();
					mynumber2alerts_text.setText("");
					Toast.makeText(Quickpaydata_Activity.this, "Alert Removed", Toast.LENGTH_SHORT).show();
				}

			}
			break;
		}
		case R.id.mydthalerts_checkbox:
		{
			if (alerts_check[2].isChecked())
			{
				method = "My DTH";
				method_description = "Notification to recharge your DTH";
				calendar(method, method_description);
			}
			else
			{
				if (!prefs_theme.getString("pref_mydth_alert", "").equals(""))
				{
					Uri x = Uri.parse(prefs_theme.getString("pref_mydth_alert", ""));
					getContentResolver().delete(x, null, null);
					SharedPreferences.Editor editor = prefs_theme.edit();
					editor.putString("pref_mydth_alert", "");
					editor.putString("pref_mydth_date", "");
					editor.commit();
					mydthalerts_text.setText("");
					Toast.makeText(Quickpaydata_Activity.this, "Alert Removed", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
		case R.id.mydatacardalerts_checkbox:
		{
			if (alerts_check[3].isChecked())
			{
				method = "My Datacard";
				method_description = "Notification to recharge your data card";
				calendar(method, method_description);
			}
			else
			{
				if (!prefs_theme.getString("pref_mydatacard_alert", "").equals(""))
				{
					Uri x = Uri.parse(prefs_theme.getString("pref_mydatacard_alert", ""));
					getContentResolver().delete(x, null, null);
					SharedPreferences.Editor editor = prefs_theme.edit();
					editor.putString("pref_mydatacard_alert", "");
					editor.putString("pref_mydatacard_date", "");
					editor.commit();
					mydatacardalerts_text.setText("");
					Toast.makeText(Quickpaydata_Activity.this, "Datacard Alert Removed", Toast.LENGTH_SHORT).show();
				}

			}
			break;
		}
		case R.id.insurancealerts_checkbox:
		{
			if (alerts_check[4].isChecked())
			{
				method = "My Insurance";
				method_description = "Notification to pay your insurance premium";
				calendar(method, method_description);
			}
			else
			{
				if (!prefs_theme.getString("pref_myinsurance_alert", "").equals(""))
				{
					Uri x = Uri.parse(prefs_theme.getString("pref_myinsurance_alert", ""));
					getContentResolver().delete(x, null, null);
					SharedPreferences.Editor editor = prefs_theme.edit();
					editor.putString("pref_myinsurance_alert", "");
					editor.putString("pref_myinsurance_date", "");
					editor.commit();
					insurancealerts_text.setText("");
					Toast.makeText(Quickpaydata_Activity.this, "Insurance Alert Removed", Toast.LENGTH_SHORT).show();
				}

			}
			break;
		}
		case R.id.mylandlinealerts_checkbox:
		{
			if (alerts_check[5].isChecked())
			{
				method = "My Landline";
				method_description = "Notification to pay your landline bills";
				calendar(method, method_description);
			}
			else
			{
				if (!prefs_theme.getString("pref_mylandline_alert", "").equals(""))
				{
					Uri x = Uri.parse(prefs_theme.getString("pref_mylandline_alert", ""));
					getContentResolver().delete(x, null, null);
					SharedPreferences.Editor editor = prefs_theme.edit();
					editor.putString("pref_mylandline_alert", "");
					editor.putString("pref_mylandline_date", "");
					editor.commit();
					mylandlinealerts_text.setText("");
					Toast.makeText(Quickpaydata_Activity.this, "Landline Alert Removed", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
		}
	}

	public void calendar(final String method, final String method_description)
	{
		try
		{
			LayoutInflater inflater = Quickpaydata_Activity.this.getLayoutInflater();
			View checkboxLayout = inflater.inflate(R.layout.calendar_layout, null);

			final AlertDialog.Builder b = new AlertDialog.Builder(Quickpaydata_Activity.this);
			b.setTitle("Select date");
			b.setView(checkboxLayout);
			final CalendarView cv = (CalendarView) checkboxLayout.findViewById(R.id.calender_view);
			cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
			{
				public void onSelectedDayChange(CalendarView view, int year, int monthOfYear, int dayOfMonth)
				{
					mYear = year;
					mMonth = monthOfYear;
					mDay = dayOfMonth;
					Date d = new Date();
					Log.e("time", String.valueOf(d.getTime()));
					Log.e("d+m+y", String.valueOf(mDay) + String.valueOf(mMonth) + String.valueOf(mYear));
					{
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
						trans_date = new Date(view.getDate());
						selectedDate = new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay)
								.append(" ").toString();

						cv.setDate(view.getDate());
					}
				}
			});
			b.setPositiveButton("Add", new Dialog.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface d, int which)
				{
					mynumber1alerts_text.setText(selectedDate);
					addtocalendar(selectedDate, trans_date.toString(), method, method_description);
				}
			});

			b.setCancelable(true);
			final Dialog date_dialog = b.create();
			date_dialog.show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on callender view\n" + e.toString());
		}

	}

	OnClickListener button_click = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			try
			{
				SharedPreferences.Editor editor = prefs_theme.edit();
				if (save_edit_button.getText().equals("Edit"))
				{
					Toast.makeText(Quickpaydata_Activity.this, "Please click save after finished editing the data", Toast.LENGTH_SHORT)
							.show();

					for (int i = 0; i < 6; i++)
					{
						alerts_check[i].setEnabled(true);
					}
					travelfrom_text.setEnabled(true);
					travelto_text.setEnabled(true);
					myphonenumber1_text.setEnabled(true);
					myphonenumber2_text.setEnabled(true);
					mydth_text.setEnabled(true);
					mydatacard_text.setEnabled(true);
					myinsurancepremium_text.setEnabled(true);
					myinsurancedate_text.setEnabled(true);
					mylandline_text.setEnabled(true);
					mylandlinecustno_text.setEnabled(true);

					save_edit_button.setText("Save");
				}

				else if (save_edit_button.getText().equals("Save"))
				{
					// travel quick data
					editor.putString("pref_source", travelfrom_text.getText().toString().trim());
					editor.putString("pref_destination", travelto_text.getText().toString().trim());
					// rest quickpay
					editor.putString("pref_mymobile", myphonenumber1_text.getText().toString().trim());
					editor.putString("pref_mymobile2", myphonenumber2_text.getText().toString().trim());
					editor.putString("pref_mydth", mydth_text.getText().toString().trim());
					editor.putString("pref_mydatacard", mydatacard_text.getText().toString().trim());
					editor.putString("pref_myinsurance", myinsurancepremium_text.getText().toString().trim());
					editor.putString("pref_myinsurancedate", myinsurancedate_text.getText().toString().trim());
					editor.putString("pref_mylandline", mylandline_text.getText().toString().trim());
					editor.putString("pref_mylandlinecustno", mylandlinecustno_text.getText().toString().trim());
					editor.commit();
					save_edit_button.setText("Edit");

					travelfrom_text.setEnabled(false);
					travelto_text.setEnabled(false);
					myphonenumber1_text.setEnabled(false);
					myphonenumber2_text.setEnabled(false);
					mydth_text.setEnabled(false);
					mydatacard_text.setEnabled(false);
					myinsurancepremium_text.setEnabled(false);
					myinsurancedate_text.setEnabled(false);
					mylandline_text.setEnabled(false);
					mylandlinecustno_text.setEnabled(false);
					for (int i = 0; i < 6; i++)
					{
						alerts_check[i].setEnabled(false);
					}

					Toast.makeText(Quickpaydata_Activity.this, "Quickpay data saved successfully", Toast.LENGTH_SHORT).show();
				}

			}
			catch (Exception e)
			{
				Log.e("crash", "error on edit/save\n" + e.toString());
			}
		}
	};
}
