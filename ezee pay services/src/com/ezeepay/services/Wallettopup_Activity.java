package com.ezeepay.services;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class Wallettopup_Activity extends Activity
{

	connection_check check = new connection_check(this);
	String banks_cat[] =
	{ "Axis Bank", "HDFC", "ICICI", "State Bank of India" };

	String creditcards_cat[] =
	{ "Visa Card", "Master Card", "American Express" };
	int creditcards_images[] =
	{ R.drawable.cards_visa02, R.drawable.cards_mastercard, R.drawable.cards_americanexpress };

	String debitcards_cat[] =
	{ "Visa Card", "Master Card", "Maestro Card" };
	int debitcards_images[] =
	{ R.drawable.cards_visa02, R.drawable.cards_mastercard, R.drawable.cards_maestro };

	String flagtype = "";

	int pmode_touchflag = 0;
	int poption_touchflag = 0;

	String none[] =
	{};
	private Spinner paymentoption_spinner, paymentmode_spinner;
	ArrayAdapter<String> ptype_adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);
			setContentView(R.layout.wallettopup_layout);

			// View actions = findViewById(R.id.newuser_text);
			// actions.setVisibility(View.GONE);

			paymentmode_spinner = (Spinner) findViewById(R.id.paymentmode_spinner);
			paymentoption_spinner = (Spinner) findViewById(R.id.paymentoption_spinner);

			reset_paymentmode_spinner();

			paymentmode_spinner.setOnTouchListener(new OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (pmode_touchflag == 0)
					{
						List<String> paymentsmode = new ArrayList<String>();
						paymentsmode.add("Credit Cards");
						paymentsmode.add("Debit Cards");
						paymentsmode.add("Bank Accounts");

						ArrayAdapter<String> paymentsadapter = new ArrayAdapter<String>(Wallettopup_Activity.this,
								android.R.layout.simple_spinner_item, paymentsmode);
						paymentsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						paymentmode_spinner.setAdapter(paymentsadapter);
						reset_paymentoption_spinner();
						pmode_touchflag = 10;
						poption_touchflag = 0;

					}
					return false;
				}
			});

			paymentoption_spinner.setOnTouchListener(new OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (poption_touchflag == 0)
					{
						if (paymentmode_spinner.getSelectedItem().toString().equals("Credit Cards"))
						{
							flagtype = "creditcards";
							paymentoption_spinner
									.setAdapter(new MyAdapter(Wallettopup_Activity.this, R.layout.textandicon, creditcards_cat));

							LinearLayout layout = (LinearLayout) findViewById(R.id.userinput_linearlayout);
							for (int i = 0; i < layout.getChildCount(); i++)
							{
								View child = layout.getChildAt(i);
								child.setEnabled(true);
							}
						}
						else if (paymentmode_spinner.getSelectedItem().toString().equals("Debit Cards"))
						{

							flagtype = "debitcards";
							paymentoption_spinner.clearFocus();

							paymentoption_spinner
									.setAdapter(new MyAdapter(Wallettopup_Activity.this, R.layout.textandicon, debitcards_cat));

							LinearLayout layout = (LinearLayout) findViewById(R.id.userinput_linearlayout);
							for (int i = 0; i < layout.getChildCount(); i++)
							{
								View child = layout.getChildAt(i);
								child.setEnabled(true);
							}
						}
						else
						{
							ptype_adapter = new ArrayAdapter<String>(Wallettopup_Activity.this, android.R.layout.simple_spinner_item, none);
							ptype_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							paymentoption_spinner.setAdapter(ptype_adapter);

							LinearLayout layout = (LinearLayout) findViewById(R.id.userinput_linearlayout);
							for (int i = 0; i < layout.getChildCount(); i++)
							{
								View child = layout.getChildAt(i);
								child.setEnabled(false);
							}
						}
						poption_touchflag = 10;
						pmode_touchflag = 0;

					}
					return false;
				}
			});

			paymentmode_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
				{

					/*
					 * EditText ed1 =
					 * (EditText)findViewById(R.id.payment_accountno_text);
					 * ed1.setEnabled(true); EditText ed2 =
					 * (EditText)findViewById(R.id.payment_remarks_text);
					 * ed2.setEnabled(true); EditText ed3 =
					 * (EditText)findViewById(R.id.payment_amount_text);
					 * ed3.setEnabled(true);
					 */
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView)
				{
					// your code here
				}
			});

		}
		catch (Exception e)
		{
			Log.e("fatal", "error on wallettopup activity" + e);
		}
	}

	public void reset_paymentmode_spinner()
	{
		paymentmode_spinner = (Spinner) findViewById(R.id.paymentmode_spinner);
		List<String> list = new ArrayList<String>();
		list.add("Select Payments mode.....");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		paymentmode_spinner.setAdapter(dataAdapter);
	}

	public void reset_paymentoption_spinner()
	{
		paymentoption_spinner = (Spinner) findViewById(R.id.paymentoption_spinner);
		List<String> list = new ArrayList<String>();
		list.add("Payments options.....");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		paymentoption_spinner.setAdapter(dataAdapter);
	}

	public class MyAdapter extends ArrayAdapter<String>
	{
		public MyAdapter(Context context, int textViewResourceId, String[] objects)
		{
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.textandicon, parent, false);
			TextView label = (TextView) row.findViewById(R.id.textView);
			if (flagtype.equals("creditcards"))
			{

				label.setText(creditcards_cat[position]);
				ImageView icon = (ImageView) row.findViewById(R.id.spinnerimage);
				icon.setImageResource(creditcards_images[position]);

			}
			else if (flagtype.equals("debitcards"))
			{
				inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.textandicon, parent, false);
				label = (TextView) row.findViewById(R.id.textView);
				label.setText(debitcards_cat[position]);
				ImageView icon = (ImageView) row.findViewById(R.id.spinnerimage);
				icon.setImageResource(debitcards_images[position]);

			}

			return row;
		}
	}
}
