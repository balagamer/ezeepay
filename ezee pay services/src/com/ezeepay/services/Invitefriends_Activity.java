package com.ezeepay.services;

import java.net.SocketTimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Invitefriends_Activity extends Activity implements OnClickListener
{
	connection_check check = new connection_check(this);
	ListView lView;
	String[] d, xx;
	String[] no;
	int j = 1;
	String allnumbers = "";
	String URL;

	Button selectfriends_button, sendinvite_button;
	private String cust_loginid, cust_main_balance, pref_paymentmode = "", cust_username = "", cust_email = "", cust_phone = "";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);
			setContentView(R.layout.invite_layout);
			SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			cust_loginid = prefs.getString("loginid", "");
			cust_username = prefs.getString("username", "");
			cust_email = prefs.getString("email", "");
			cust_phone = prefs.getString("phone", "");
			cust_main_balance = prefs.getString("balance", "");

			selectfriends_button = (Button) findViewById(R.id.selectfriends_button);
			selectfriends_button.setOnClickListener(this);
			sendinvite_button = (Button) findViewById(R.id.sendinvite_button);
			sendinvite_button.setOnClickListener(this);

			lView = (ListView) findViewById(R.id.listView1);

			lView.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View v, int position, long id)
				{
					{
						for (int i = 0; i < xx.length; i++)
						{

							if (((TextView) v).getText().equals(xx[i]))
							{
								if (j <= 10)
								{
									// Toast.makeText(Invitefriends_Activity.this,
									// ((TextView) v).getText() + "\n" + no[i] +
									// "\n" + "count is:" + j,
									// Toast.LENGTH_SHORT).show();
									// Toast.makeText(Invitefriends_Activity.this,
									// "id"+id, Toast.LENGTH_SHORT).show();
									if (allnumbers.equals(""))
										allnumbers = no[position].toString();
									else
										allnumbers = allnumbers + "&" + no[position].toString();
									j = j + 1;
									sendinvite_button.setText("Invite Friends(" + j + ")");
									break;

								}
								else
								{
									Toast.makeText(Invitefriends_Activity.this, "5 is enough", Toast.LENGTH_SHORT).show();
									lView.setItemChecked(position, false);

								}
							}
						}
					}
					// else
					{
						// j = j - 1;
						// Toast.makeText(Invitefriends_Activity.this,
						// "nothing", Toast.LENGTH_SHORT).show();

					}
				}
			});
		}
		catch (Exception e)
		{
			Log.e("fatal", "fatal error in invite friends activity");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		/*
		 * if (data != null) { Uri uri = data.getData();
		 * 
		 * if (uri != null) { Cursor c = null; try { c =
		 * getContentResolver().query(uri, new String[] {
		 * ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
		 * 
		 * if (c != null && c.moveToFirst()) { String number = c.getString(0);
		 * // int type = c.getInt(1); showSelectedNumber(0, number); } } finally
		 * { if (c != null) { c.close(); } } } }
		 */

	}

	public void showSelectedNumber(int type, String number)
	{
		//Toast.makeText(this, number, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.selectfriends_button:

			{

				Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
						Phone.DISPLAY_NAME + " ASC");

				String name = "";
				String phoneNumber = "";
				int n = 0;

				while (cursor.moveToNext())
				{
					int i = 0;
					if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER))) > 0)
					{
						name = name + "&" + cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					}

					phoneNumber = phoneNumber + "&"
							+ cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					i = i + 1;
					i = n;
				}
				d = name.split("&");
				xx = d;
				no = phoneNumber.split("&");
				// Arrays.sort(d);
				lView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, d));
				lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				break;
			}
			case R.id.sendinvite_button:

			{
				//Toast.makeText(getApplicationContext(), allnumbers, Toast.LENGTH_SHORT).show();
				new webservicecall_task().execute();
				break;
			}

			}
		}
		catch (Exception e)
		{
			Log.e("fatal", "fatal error on invite friend buttons\n" + e.toString());
		}

	}

	class webservicecall_task extends AsyncTask<Object, Object, Object>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Invitefriends_Activity.this, "", "Registering User...");
		}

		@Override
		protected Object doInBackground(Object... params)
		{
			try
			{
				
				URL = selectwebservice.currentwebservice();
				
				String SOAP_ACTION = "http://services.ezeepay.com/sendsms_method";
				String METHOD_NAME = "sendsms_method";
				String NAMESPACE = "http://services.ezeepay.com";

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				PropertyInfo unameProp = new PropertyInfo();
				unameProp.setName("all_numbers");
				unameProp.setValue(allnumbers);
				unameProp.setType(String.class);
				request.addProperty(unameProp);

				PropertyInfo nameProp = new PropertyInfo();
				nameProp.setName("username");
				nameProp.setValue(cust_username);
				nameProp.setType(String.class);
				request.addProperty(nameProp);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE ht = new HttpTransportSE(URL);
				ht.call(SOAP_ACTION, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				SoapPrimitive s = response;
				String str = s.toString();

			}
			catch (SocketTimeoutException e)
			{

			}
			catch (Exception e)
			{

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values)
		{
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			progress.dismiss();
			Toast.makeText(getApplicationContext(), "Invitation sent !", Toast.LENGTH_SHORT).show();

		}
	}

}
