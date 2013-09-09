package com.ezeepay.services;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login_Activity extends Activity
{

	int flag, mpinid;
	Editable mpin;
	private String errmsg, user_Name, user_Password, qid1, qid2, ans1, ans2, x, resultstatus = "", methodname = "", mpin2 = "", damn = "",
			URL, pinstore = "", server_response = "", username = "", email = "", phone = "";

	Bundle b = new Bundle();
	String[] questionArr;
	String[] answerArr;
	String[] logincheck_result;
	Float balance;

	private TextView username_text, userpassword_text;
	private CheckBox keepmelogged_checkbox, showpassword_checkbox;

	connection_check check = new connection_check(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		try
		{
			SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
			boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
			if (theme_style)
				setTheme(android.R.style.Theme_Holo);
			else
				setTheme(android.R.style.Theme_Holo_Light);
			setContentView(R.layout.login_layout);

			new Eula(this).show();

			username_text = (TextView) findViewById(R.id.username_text);
			username_text.setText("");
			userpassword_text = (TextView) findViewById(R.id.userpassword_text);
			userpassword_text.setText("");
			keepmelogged_checkbox = (CheckBox) findViewById(R.id.keepmelogged_checkbox);

			keepmelogged_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				SharedPreferences prefs = Login_Activity.this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					// TODO Auto-generated method stub
					if (isChecked)
					{
						userpassword_text.setInputType(129);
						showpassword_checkbox.setChecked(false);
						showpassword_checkbox.setEnabled(false);
						SharedPreferences.Editor editor = prefs.edit();

						user_Name = username_text.getText().toString();
						user_Password = userpassword_text.getText().toString();

						editor.putString("loginid", user_Name);
						editor.putString("pwd", user_Password);
						editor.putBoolean("keepmesigned_checked", true);
						editor.putBoolean("showpassword_checked", false);
						editor.commit();
					}
					else
					{
						username_text.setText("");
						userpassword_text.setText("");
						showpassword_checkbox.setEnabled(true);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("loginid", "");
						editor.putString("pwd", "");
						editor.putBoolean("keepmesigned_checked", false);
						editor.putBoolean("showpassword_checked", false);
						editor.commit();
					}
				}

			});

			showpassword_checkbox = (CheckBox) findViewById(R.id.showpassword_checkbox);

			String name = prefs.getString("loginid", "");
			String pwd = prefs.getString("pwd", "");
			Boolean c = prefs.getBoolean("keepmesigned_checked", false);
			Boolean c1 = prefs.getBoolean("showpassword_checked", false);

			if (c == true)
			{
				Toast.makeText(getApplicationContext(), "keep me signed checked", Toast.LENGTH_SHORT);
				if (!name.equalsIgnoreCase(""))
				{

					username_text.setText(name);
					userpassword_text.setText(pwd);

					keepmelogged_checkbox.setChecked(c);
					{

						showpassword_checkbox.setEnabled(c1);
					}

					if (check.isnetwork_available())
					{
						methodname = "logincheck";
						new webservicecall_task().execute(methodname);
					}
					else
					{
						Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
					}

				}
			}
			else
			{
				username_text.setText("");
				userpassword_text.setText("");
			}
			URL = selectwebservice.currentwebservice();

		}
		catch (Exception e)
		{
			Log.e("Fatal", "Fatal error on Login activity\n" + e.toString());
		}

	}

	private boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onResume()
	{
		super.onResume();
		// .... other stuff in my onResume ....
		this.doubleBackToExitPressedOnce = false;
	}

	@Override
	public void onBackPressed()
	{
		if (doubleBackToExitPressedOnce)
		{
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
	}

	public void hi(View v)
	{
		try
		{
			String l = SHA1(username_text.getText().toString(), userpassword_text.getText().toString());
			Log.e("hashed  ", l);
			Toast.makeText(getApplicationContext(), l, Toast.LENGTH_SHORT).show();
		}
		catch (InvalidKeyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String SHA1(String s, String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException
	{

		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);

		byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

		return new String(Base64.encode(bytes, Base64.DEFAULT));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.loginpage_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		this.finish();
		return true;
	}

	public void quitjob(View view)
	{
		Toast.makeText(Login_Activity.this, "Thanks for using the application", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	public void calendarclick(View v)
	{

		startVoiceRecognitionActivity();
		if (check.isnetwork_available())
		{
			Toast.makeText(getBaseContext(), "yes", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
		}
	}

	public void startVoiceRecognitionActivity()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
		startActivityForResult(intent, 1234);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (requestCode == 1234 && resultCode == RESULT_OK)
		{
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			// if (matches.contains("information"))
			{
				Toast.makeText(getApplicationContext(), "you said " + matches, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onloginsubmit_button_select(View view)
	{
		try
		{

			if (check.isnetwork_available())
			{
				user_Name = username_text.getText().toString().trim();
				user_Password = userpassword_text.getText().toString();
				if (!user_Name.equals("") && !user_Password.equals(""))
				{
					methodname = "logincheck";
					// String s,dd;
					flag = 100;
					new webservicecall_task().execute(methodname);
				}
				else
				{
					Toast.makeText(this, "Username and/or password cannot be empty!", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
			}
			// servicecall();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onloginsubmit_button_select");
		}
	}

	public void onnewuserreg_button_select(View view)
	{
		try
		{

			if (check.isnetwork_available())
			{
				Intent intent1 = new Intent(Login_Activity.this, Newregistration_activity.class);
				startActivity(intent1);
			}
			else
			{
				Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onnewuserreg_button_select");
		}
	}

	public void onforgotpassword_button_select(View view)
	{
		try
		{

			if (check.isnetwork_available())
			{
				user_Name = username_text.getText().toString();
				if (user_Name.equals(""))
				{
					Toast.makeText(this, "Enter userid first", Toast.LENGTH_LONG).show();
				}
				else
				{
					methodname = "forgotpassword";
					new webservicecall_task().execute(methodname);
				}
			}
			else
			{
				Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onforgotpassword_button_select");
		}

	}

	public void onshowpassword_checkbox_select(View view)
	{
		try
		{
			boolean checked = ((CheckBox) view).isChecked();
			if (checked)
			{
				userpassword_text.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			}
			else
			{
				userpassword_text.setInputType(129);
				// 129 is the input type set when setting
				// android:inputType="textPassword"
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "error on onshowpassword_checkbox_select");
		}
	}

	// /////////////////////////////////////////////ASYNC FETCH FOR CHECKING
	// LOGIN////////////////////////////////////////////////////

	class webservicecall_task extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Login_Activity.this, "", "Logging in...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			x = params[0].toString();
			allowAllSSL.allowAllSSL();
			if (x.equals("logincheck"))
			{
				// resultstatus = "logincheck_executed";
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/logincheckmethod";
					String METHOD_NAME = "logincheckmethod";
					String NAMESPACE = "http://services.ezeepay.com";
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					EditText userName = (EditText) findViewById(R.id.username_text);
					user_Name = userName.getText().toString().trim();
					EditText userPassword = (EditText) findViewById(R.id.userpassword_text);
					// String user_Password =
					// SHA1(userPassword.getText().toString(),"ddd");
					// String user_Password = "bala";
					user_Password = userPassword.getText().toString();

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("loginid");
					unameProp.setValue(username_text.getText().toString().trim());
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					PropertyInfo passwordProp = new PropertyInfo();
					passwordProp.setName("password");
					passwordProp.setValue(userpassword_text.getText().toString().trim());
					passwordProp.setType(String.class);
					request.addProperty(passwordProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);
					SoapObject response = (SoapObject) envelope.bodyIn;

					server_response = response.getProperty(0).toString();

					resultstatus = "logincheck_executed";
				}
				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "logincheck_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "logincheck_executed";
				}
			}

			else if (x.equals("forgotpassword"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/checkloginidmethod";
					String METHOD_NAME = "checkloginidmethod";
					String NAMESPACE = "http://services.ezeepay.com";
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
					EditText userName = (EditText) findViewById(R.id.username_text);
					user_Name = userName.getText().toString().trim();

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("username");
					unameProp.setValue(username_text.getText().toString().trim());
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);
					SoapObject response = (SoapObject) envelope.bodyIn;

					String str = response.getProperty(0).toString();

					flag = Integer.parseInt(str);
					resultstatus = "forgotpassword_executed";
				}
				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "forgotpassword_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "forgotpassword_executed";
				}
			}
			else if (x.equals("insertmpin"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/insertmpinmethod";
					String METHOD_NAME = "insertmpinmethod";
					String NAMESPACE = "http://services.ezeepay.com";
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					EditText userName = (EditText) findViewById(R.id.username_text);
					user_Name = userName.getText().toString().trim();

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("username");
					unameProp.setValue(username_text.getText().toString().trim());
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					PropertyInfo mpinProp = new PropertyInfo();
					mpinProp.setName("mpin");
					mpinProp.setValue(mpinid);
					mpinProp.setType(Integer.class);
					request.addProperty(mpinProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);
					SoapObject response = (SoapObject) envelope.bodyIn;
					// SoapPrimitive s = response;
					String str = response.getProperty(0).toString();

					flag = Integer.parseInt(str);
					resultstatus = "insertmpin_executed";
				}
				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
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
			if (result.equals("logincheck_executed"))
			{
				JSONObject response_data;
				try
				{
					response_data = new JSONObject(server_response);
					JSONArray data = response_data.getJSONArray("login_response");
					JSONObject e = data.getJSONObject(0);
					flag = Integer.parseInt(e.getString("flag"));
					username = e.getString("username");
					email = e.getString("email");
					phone = e.getString("phone");
					balance = Float.parseFloat(e.getString("balance"));
					mpin2 = e.getString("mpin");

				}
				catch (JSONException e1)
				{
					e1.printStackTrace();
				}
				if (flag == 0)
				{
					Toast.makeText(Login_Activity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
					Intent intent1 = new Intent(Login_Activity.this, Mainpage_Activity.class);
					SharedPreferences prefs = Login_Activity.this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();

					editor.putString("loginid", user_Name);
					editor.putString("username", username);
					editor.putString("email", email);
					editor.putString("phone", phone);
					editor.putString("main_balance", String.valueOf(balance));

					if (mpin2.equals(""))
						pinstore = null;
					else
						pinstore = Base64.encodeToString(mpin2.getBytes(), Base64.DEFAULT);
					editor.putString("pinstore", pinstore);
					editor.commit();
					startActivity(intent1);
					Boolean c = prefs.getBoolean("keepmesigned_checked", false);
					if (c == true)
					{

					}
					else
					{
						username_text.setText("");
						userpassword_text.setText("");
					}

				}
				else if (flag == 1)
				{
					Toast.makeText(Login_Activity.this, "Username or Password not correct!", Toast.LENGTH_LONG).show();
				}
				else if (flag == 3)
				{
					Toast.makeText(Login_Activity.this, "Conenction Timeout,check your internet connection", Toast.LENGTH_LONG).show();
					Log.e("flag 3,connection timeout", errmsg);
				}
				else if (flag == 4)
				{
					Toast.makeText(Login_Activity.this, "Unknown error,please try after sometime", Toast.LENGTH_LONG).show();
					Log.e("flag 4,Unknown error", errmsg);
				}
				else if (flag == 22)
				{
					Toast.makeText(Login_Activity.this, flag + "Login failed! Please contact admin for account activation",
							Toast.LENGTH_LONG).show();
				}
				else if (flag == 33)
				{
					// Toast.makeText(MainActivity.this,flag+"Welcome to mpin wizard",Toast.LENGTH_SHORT).show();
					mpin_create();
				}
			}
			else if (result.equals("forgotpassword_executed"))
			{
				if (flag == 0)
				{
					Intent intent1 = new Intent(Login_Activity.this, Forgotpassword_Activity.class);
					intent1.putExtra("loginid", user_Name);
					startActivity(intent1);
				}
				else if (flag == 1)
				{
					Toast.makeText(Login_Activity.this, "ID dosent exist!", Toast.LENGTH_LONG).show();
				}
				else if (flag == 3)
				{
					Toast.makeText(Login_Activity.this, "Conenction Timeout,check your internet connection", Toast.LENGTH_LONG).show();
					Log.e("flag 3,connection timeout", errmsg);
				}
				else if (flag == 4)
				{
					Toast.makeText(Login_Activity.this, "Unknown error,please try after sometime", Toast.LENGTH_LONG).show();
					Log.e("flag 4,Unknown error", errmsg);
				}
				else
				{
					Toast.makeText(Login_Activity.this, flag + "Unidentified error", Toast.LENGTH_LONG).show();
				}
			}
			else if (result.equals("insertmpin_executed"))
			{
				if (flag == 0)
				{
					SharedPreferences prefs = Login_Activity.this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					if (String.valueOf(mpinid).equals(""))
						pinstore = null;
					else
						pinstore = Base64.encodeToString(String.valueOf(mpinid).getBytes(), Base64.DEFAULT);
					editor.putString("pinstore", pinstore);
					editor.commit();
					Toast.makeText(Login_Activity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
					Intent intent1 = new Intent(Login_Activity.this, Mainpage_Activity.class);
					intent1.putExtra("usernameintent", user_Name);
					startActivity(intent1);
				}
				else if (flag == 1)
				{
					Toast.makeText(Login_Activity.this, "Some error!", Toast.LENGTH_SHORT).show();
				}
				else if (flag == 3)
				{
					Toast.makeText(Login_Activity.this, "Conenction Timeout,check your internet connection" + errmsg, Toast.LENGTH_LONG)
							.show();
				}
				else if (flag == 4)
				{
					Toast.makeText(Login_Activity.this, "Unknown error,please try after sometime" + errmsg, Toast.LENGTH_LONG).show();
				}
				else if (flag == 22)
				{
					Toast.makeText(Login_Activity.this, flag + "Login failed! Please contact admin for account activation",
							Toast.LENGTH_LONG).show();
				}
				else if (flag == 33)
				{
					// Toast.makeText(Login_Activity.this, flag +
					// "Welcome to mpin wizard",
					// Toast.LENGTH_SHORT).show();
					mpin_create();
				}
			}

		}
	}

	// ///////////////////////////////////////FIRSTTIME_MPIN/////////////////////////////////////////
	public void mpin_create()
	{
		try
		{
			LayoutInflater inflater = getLayoutInflater();
			View checkboxLayout = inflater.inflate(R.layout.mpin_layout, null);

			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Welcome to mpin wizard");
			// b.setMessage("mpin serves as a master password"
			// +
			// " for all your transaction.Confirm your mpin to start making transactions");

			b.setView(checkboxLayout);
			final EditText et1 = (EditText) checkboxLayout.findViewById(R.id.editText1);
			final EditText et2 = (EditText) checkboxLayout.findViewById(R.id.editText2);

			b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int whichButton)
				{
					if (et1.getText().toString().equals("") || et2.getText().toString().equals(""))
					{
						Toast.makeText(Login_Activity.this, "mpin cannot be empty", Toast.LENGTH_SHORT).show();
						mpin_create();
					}
					else
					{
						if (et1.getText().toString().length() < 6 || et2.getText().toString().length() < 6)
						{
							Toast.makeText(Login_Activity.this, "mpin should be atleast 6 digit long", Toast.LENGTH_SHORT).show();
							mpin_create();
						}
						else
						{
							int x = Integer.parseInt(et1.getText().toString());
							int y = Integer.parseInt(et2.getText().toString());

							if (x == y)
							{
								mpinid = x;
								if (check.isnetwork_available())
								{
									methodname = "insertmpin";
									new webservicecall_task().execute(methodname);
								}
								else
								{
									Toast.makeText(Login_Activity.this, "Please check your internet connectivity!", Toast.LENGTH_SHORT)
											.show();
								}

							}
							else
							{
								Toast.makeText(Login_Activity.this, "Both pin numbers should be same", Toast.LENGTH_SHORT).show();
								mpin_create();
							}
						}
					}
				}
			});
			b.setCancelable(false);
			b.setNegativeButton("CANCEL", null);
			b.create().show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on mpin_create");
		}
	}

}
