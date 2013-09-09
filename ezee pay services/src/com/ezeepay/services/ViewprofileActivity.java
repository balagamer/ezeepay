package com.ezeepay.services;

import java.net.SocketTimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ViewprofileActivity extends Activity
{

	String user_Name, resultstatus, methodname, x, errmsg;
	int flag;
	connection_check check = new connection_check(this);
	String URL;
	SoapObject response;
	Category C;
	String loginid, main_balance, server_response;

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
		setContentView(R.layout.viewprofile_layout);

		loginid = prefs.getString("loginid", "");
		main_balance = prefs.getString("balance", "");

		URL = selectwebservice.currentwebservice();
		// xuname.setText(user_Name);

		if (check.isnetwork_available())
		{
			methodname = "viewprofile";
			new webservicecall_task().execute(methodname);
		}
		else
		{
			Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
		}

	}

	class webservicecall_task extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(ViewprofileActivity.this, "", "Loading Profile...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			x = params[0].toString();
			if (x.equals("viewprofile"))
			{
				resultstatus = "viewprofile_executed";
				try
				{

					String SOAP_ACTION = "http://services.ezeepay.com/GetCustomerProfile_method";
					String METHOD_NAME = "GetCustomerProfile_method";
					String NAMESPACE = "http://services.ezeepay.com";
					// String NAMESPACE2 = "http://services.ezeepay.com/xsd";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("userid");
					unameProp.setValue(loginid);
					unameProp.setType(String.class);
					// unameProp.setNamespace(NAMESPACE2);
					request.addProperty(unameProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					server_response = response.getPropertyAsString(0).toString();

					flag = 0;

					resultstatus = "viewprofile_executed";
				}
				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "viewprofile_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "viewprofile_executed";
				}
			}
			return resultstatus;
		}

		@Override
		protected void onProgressUpdate(Object... values)
		{
			super.onProgressUpdate(values);
			// progressDialog.setProgress(10);
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			progress.dismiss();
			if (result.equals("viewprofile_executed"))
			{
				Log.e("response", server_response);
				JSONObject response_data;
				try
				{
					response_data = new JSONObject(server_response);
					JSONArray data = response_data.getJSONArray("profile_response");
					JSONObject e = data.getJSONObject(0);
					flag = Integer.parseInt(e.getString("flag"));
					TextView v1 = (TextView) findViewById(R.id.profilename_text);
					v1.setText(e.getString("username"));
					TextView v2 = (TextView) findViewById(R.id.customerid_text);
					v2.setText(e.getString("customer_id"));

					TextView v3 = (TextView) findViewById(R.id.loginid_text);
					v3.setText(e.getString("login_id"));

					TextView v4 = (TextView) findViewById(R.id.emailid_text);
					v4.setText(e.getString("email"));
					TextView v5 = (TextView) findViewById(R.id.phoneno_text);
					v5.setText(e.getString("phone"));
					TextView v6 = (TextView) findViewById(R.id.translimit_text);
					v6.setText("Your transaction limit per day is INR " + e.getString("trans_limit"));

				}
				catch (JSONException e1)
				{
					Log.e("error in json", e1.toString());
				}

				if (flag == 0)
				{
					Toast.makeText(ViewprofileActivity.this, "Profile Loaded", Toast.LENGTH_SHORT).show();

					// TextView v1 = (TextView)
					// findViewById(R.id.profilename_text);
					// v1.setText(C.UserName);
					// TextView v2 = (TextView)
					// findViewById(R.id.customerid_text);
					// v2.setText(C.CusId);
					// TextView v3 = (TextView) findViewById(R.id.loginid_text);
					// v3.setText(C.LoginId);
					//
					// TextView v4 = (TextView) findViewById(R.id.emailid_text);
					// v4.setText(C.Email);
					// TextView v5 = (TextView) findViewById(R.id.phoneno_text);
					// v5.setText(C.Phone);
					// TextView v6 = (TextView)
					// findViewById(R.id.translimit_text);
					// v6.setText("Your transaction limit per day is INR " +
					// C.TransLimit);

				}
				else if (flag == 1)
				{
					Toast.makeText(ViewprofileActivity.this, "Username or Password not correct!", Toast.LENGTH_SHORT).show();
				}
				else if (flag == 3)
				{
					Toast.makeText(ViewprofileActivity.this, "Conenction Timeout", Toast.LENGTH_SHORT).show();
					Log.e("connection timeout", errmsg);
				}
				else if (flag == 4)
				{
					Toast.makeText(ViewprofileActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
					Log.e("Unknown error", errmsg);
				}
			}
		}
	}

	public class Category
	{
		String UserName;
		String CusId;
		String LoginId;
		String Email;
		String Phone;
		String TransLimit;

		public String getUserName()
		{
			return UserName;
		}

		public void setUserName(String username)
		{
			UserName = username;
		}

		public String getCusId()
		{
			return CusId;
		}

		public void setCusId(String customerid)
		{
			CusId = customerid;
		}

		public String getLoginId()
		{
			return LoginId;
		}

		public void setLoginId(String loginid)
		{
			LoginId = loginid;
		}

		public String getEmail()
		{
			return Email;
		}

		public void setEmail(String email)
		{
			Email = email;
		}

		public String getPhone()
		{
			return Phone;
		}

		public void setPhone(String phoneno)
		{
			Phone = phoneno;
		}

		public String getTransLimit()
		{
			return TransLimit;
		}

		public void setTransLimit(String translimit)
		{
			TransLimit = translimit;
		}
	}
}
