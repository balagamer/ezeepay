package com.ezeepay.services;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Newregistration_activity extends FragmentActivity
{
	private connection_check check = new connection_check(this);
	private String URL, new_loginid, new_username, new_email, new_password, new_passwordcheck, new_phone, new_answerone, new_answertwo,
			new_dob, new_gender, errmsg;
	private int realage, touch_flag1 = 0, touch_flag2 = 0;
	private String v0 = "", v1 = "", v2 = "", v3 = "", v4 = "", v5 = "", v6 = "", v7 = "", v8 = "";
	private String result, dateval;
	private int flag, dd = 0, mm, yy;

	private EditText username_check, loginid_check, emailid_check, phoneno_check, dob_check, password_check, password2_check,
			answer1_check, answer2_check;

	private Spinner questionone_spinner, questiontwo_spinner, gender_spinner;
	private EditText mEdit;

	private CheckBox agree_checkbox;

	ArrayAdapter<String> adapter;
	List<String> qlist = new ArrayList<String>();
	String qid[] =
	{ "0", "Q001", "Q002" };
	String sques1[] =
	{ "What is your favourite color?" };

	String sques2[] =
	{ "What is your favourtie food?" };

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
		URL = selectwebservice.currentwebservice();
		setContentView(R.layout.newregistration_layout);

		// initialize components

		username_check = (EditText) findViewById(R.id.newusername_text);
		// loginid_check = (EditText) findViewById(R.id.newloginid_text);
		emailid_check = (EditText) findViewById(R.id.newemail_text);
		phoneno_check = (EditText) findViewById(R.id.newphonenumber_text);
		// dob_check = (EditText) findViewById(R.id.newdob_text);
		password_check = (EditText) findViewById(R.id.newuserpassword_text);
		password2_check = (EditText) findViewById(R.id.newuserpassword_check_text);
		questionone_spinner = (Spinner) findViewById(R.id.questionone_spinner);
		answer1_check = (EditText) findViewById(R.id.answerone_text);
		questiontwo_spinner = (Spinner) findViewById(R.id.questiontwo_spinner);
		answer2_check = (EditText) findViewById(R.id.answertwo_text);

		adapter = new ArrayAdapter<String>(Newregistration_activity.this, android.R.layout.simple_spinner_item, sques1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		questionone_spinner.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(Newregistration_activity.this, android.R.layout.simple_spinner_item, sques2);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		questiontwo_spinner.setAdapter(adapter);

		gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
		List<String> genderlist = new ArrayList<String>();
		genderlist.add("Male");
		genderlist.add("Female");
		ArrayAdapter<String> genderadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genderlist);
		gender_spinner.setAdapter(genderadapter);

		agree_checkbox = (CheckBox) findViewById(R.id.agree_checkbox);
	}

	// add date picker

	public void onselectdate_imagebutton_select(View view)
	{
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "Select DOB");
	}

	public void populateSetDate(int year, int month, int day)
	{
		mEdit = (EditText) findViewById(R.id.newdob_text);
		mEdit.setText(day + "/" + month + "/" + year);
		dateval = year + "/" + month + "/" + day;
		realage = getAge(year, month, day);
	}

	@SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{

			if (dd == 0)
			{
				final Calendar calendar = Calendar.getInstance();
				yy = calendar.get(Calendar.YEAR);
				mm = calendar.get(Calendar.MONTH);
				dd = calendar.get(Calendar.DAY_OF_MONTH);
			}
			else
			{
				mEdit = (EditText) findViewById(R.id.newdob_text);
				String x = mEdit.getText().toString();
				String[] y = x.split("/");
				dd = Integer.parseInt(y[0]);
				mm = Integer.parseInt(y[1]);
				yy = Integer.parseInt(y[2]);
			}

			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd)
		{
			populateSetDate(yy, mm + 1, dd);
		}
	}

	public void validateall()
	{
		// v0="",v1="",v2="",v3="",v4="",v5="",v6="";
		String regex = "^[ ]{4,18}$";
		Matcher matcher = Pattern.compile(regex).matcher(username_check.getText().toString().trim());
		Log.e("length is", String.valueOf((username_check.getText().toString().trim().length())));
		if ((username_check.getText().toString().trim().length() < 4) || (username_check.getText().toString().trim().length() > 19))
			v0 = "Username should have a minimum of 4 letters and maximum of 18 letters";
		else
			v0 = "";

		// regex = "^([A-Za-z0-9]+(?:[._-][A-Za-z0-9]+)*){3,8}$";
		// matcher =
		// Pattern.compile(regex).matcher(loginid_check.getText().toString().trim());
		// if (matcher.find())
		// v1 = "";
		// else
		// v1 =
		// "User id should be 3 to 8 characters long.Special characcters .-_ are nly allowed";

		String regex1 = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Matcher matcher1 = Pattern.compile(regex1).matcher(emailid_check.getText().toString().trim());
		if (matcher1.find())
			v2 = "";
		else
			v2 = "Invalid email id";

		regex = "^[7-9][0-9]{9}$";
		matcher = Pattern.compile(regex).matcher(phoneno_check.getText().toString().trim());
		if (matcher.find())
			v3 = "";
		else
			v3 = "Please enter a valid phone number";

		// regex =
		// "^(0?[1-9]|[12][0-9]|3[01])[-/]?(0?[1-9]|1[012])[-/]?(19[\\d]{2}|20[\\d]{2}|2100)$";
		// matcher =
		// Pattern.compile(regex).matcher(dob_check.getText().toString().trim());
		// if (matcher.find())
		// v4 = "";
		// else
		// v4 = "Invalid date";

		regex = "^[^ ]{6,25}$";
		matcher = Pattern.compile(regex).matcher(password_check.getText().toString().trim());
		if (matcher.find())
			v5 = "";
		else
			v5 = "Password should be 6 to 25 characters long";

		regex = "^[^ ]{6,25}$";
		matcher = Pattern.compile(regex).matcher(password2_check.getText().toString().trim());
		if (matcher.find())
			v6 = "";
		else
			v6 = "Password should be 6 to 25 characters long";

		// regex = "^[A-Za-z0-9]{3,15}$";
		// matcher =
		// Pattern.compile(regex).matcher(answer1_check.getText().toString().trim());
		if ((answer1_check.getText().toString().trim().length() < 3) || (answer1_check.getText().toString().trim().length() > 15))
			v7 = "Invalid input for answer1";
		else
			v7 = "";

		// regex = "^[A-Za-z0-9]{3,15}$";
		// matcher =
		// Pattern.compile(regex).matcher(answer2_check.getText().toString().trim());
		if ((answer2_check.getText().toString().trim().length() < 3) || (answer2_check.getText().toString().trim().length() > 15))
			v8 = "Invalid input for answer2";
		else
			v8 = "";
	}

	// ////////////////////VALIDATE_AGE/////////////////////////

	private Integer getAge(int year, int month, int day)
	{
		Calendar dob = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		dob.set(year, month, day);

		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		// if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
		// {
		// age--;
		// }

		// Integer ageInt = new Integer(age);

		return age;
	}

	// /////////////////////////////////////////////VALIDATE NEW USER
	// DATA////////////////////////////////////////////////////

	public void onnewusersubmit_button_select(View view)
	{

		// Toast.makeText(this, "mail api called", Toast.LENGTH_SHORT).show();
		flag = 10;

		new_username = username_check.getText().toString().trim();
		// new_loginid = loginid_check.getText().toString().trim();
		new_email = emailid_check.getText().toString().trim();
		new_phone = phoneno_check.getText().toString().trim();
		// new_dob = dob_check.getText().toString().trim();
		new_password = password_check.getText().toString().trim();
		new_passwordcheck = password2_check.getText().toString().trim();
		new_answerone = answer1_check.getText().toString().trim();
		new_answertwo = answer2_check.getText().toString().trim();

		// if (new_username.equalsIgnoreCase("") ||
		// new_loginid.equalsIgnoreCase("") || new_email.equalsIgnoreCase("")
		// || new_phone.equalsIgnoreCase("") || new_dob.equalsIgnoreCase("") ||
		// new_password.equalsIgnoreCase("")
		// || new_passwordcheck.equalsIgnoreCase("") ||
		// new_answerone.equalsIgnoreCase("") ||
		// new_answertwo.equalsIgnoreCase(""))
		if (new_username.equalsIgnoreCase("") || new_email.equalsIgnoreCase("") || new_phone.equalsIgnoreCase("")
				|| new_password.equalsIgnoreCase("") || new_passwordcheck.equalsIgnoreCase("") || new_answerone.equalsIgnoreCase("")
				|| new_answertwo.equalsIgnoreCase(""))
		{
			flag = 2;
		}
		else if (new_username.matches(new_password) && new_username.matches(new_passwordcheck))
		{
			flag = 3;
		}
		else if (questionone_spinner.getSelectedItem().toString().equals(questiontwo_spinner.getSelectedItem().toString()))
		{
			flag = 4;
		}
		// else if (realage < 18)
		// {
		// flag = 5;
		// }
		else if (new_password.equalsIgnoreCase(new_passwordcheck))
		{
			validateall();
			// if (v0.equals("") && v1.equals("") && v2.equals("") &&
			// v3.equals("") && v4.equals("") && v5.equals("") && v6.equals("")
			// && v7.equals("") && v8.equals(""))
			if (v0.equals("") && v2.equals("") && v3.equals("") && v5.equals("") && v6.equals("") && v7.equals("") && v8.equals(""))
			{

				if (agree_checkbox.isChecked())
				{
					allowAllSSL.allowAllSSL();
					// Toast.makeText(this, "all valid",
					// Toast.LENGTH_SHORT).show();
					AsyncTask insertTask = new insertTask();
					insertTask.execute("");
				}
				else
				{
					Toast.makeText(this, "Please accept the terms & conditions!", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				flag = 7;
			}
		}
		else
		{
			flag = 6;
		}

		if (flag == 2)
		{
			Toast.makeText(Newregistration_activity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
		}
		else if (flag == 3)
		{
			Toast.makeText(Newregistration_activity.this, "Username and Password cannot be same", Toast.LENGTH_SHORT).show();
		}
		else if (flag == 4)
		{
			Toast.makeText(Newregistration_activity.this, "Please select different questions", Toast.LENGTH_SHORT).show();
		}
		else if (flag == 5)
		{
			Toast.makeText(Newregistration_activity.this, "You should be above the age of 18", Toast.LENGTH_SHORT).show();
		}
		else if (flag == 6)
		{
			Toast.makeText(Newregistration_activity.this, "Both Password fields should be same", Toast.LENGTH_SHORT).show();
		}
		else if (flag == 7)
		{
			Toast.makeText(
					Newregistration_activity.this,
					"Please resolve the following" + "\n" + v0 + "\n" + v1 + "\n" + v2 + "\n" + v3 + "\n" + v4 + "\n" + v5 + "\n" + v6
							+ "\n" + v7 + "\n" + v8, Toast.LENGTH_LONG).show();
		}
	}

	public void onregistrationcancel_button_select(View view)
	{

		Newregistration_activity.this.finish();
	}

	// /////////////////////////////////////////////SEND DATA TO WEB SERVICE FOR
	// INSERTION////////////////////////////////////////////////////

	class insertTask extends AsyncTask<Object, Object, Object>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Newregistration_activity.this, "", "Registering User...");
		}

		@Override
		protected Object doInBackground(Object... params)
		{
			try
			{
				String SOAP_ACTION = "http://services.ezeepay.com/addnewusermethod";
				String METHOD_NAME = "addnewusermethod";
				String NAMESPACE = "http://services.ezeepay.com";

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				PropertyInfo unameProp = new PropertyInfo();
				unameProp.setName("username");
				unameProp.setValue(new_username);
				unameProp.setType(String.class);
				request.addProperty(unameProp);

				PropertyInfo loginidProp = new PropertyInfo();
				loginidProp.setName("loginid");
				// loginidProp.setValue(new_loginid);
				loginidProp.setValue(new_email);
				loginidProp.setType(String.class);
				request.addProperty(loginidProp);

				PropertyInfo genderProp = new PropertyInfo();
				genderProp.setName("gender");
				new_gender = gender_spinner.getSelectedItem().toString();
				genderProp.setValue("null");
				genderProp.setType(String.class);
				request.addProperty(genderProp);

				PropertyInfo ageProp = new PropertyInfo();
				ageProp.setName("age");
				// ageProp.setValue(realage);
				ageProp.setValue(0);
				ageProp.setType(Integer.class);
				request.addProperty(ageProp);

				PropertyInfo passwordProp = new PropertyInfo();
				passwordProp.setName("password");
				passwordProp.setValue(new_password);
				passwordProp.setType(String.class);
				request.addProperty(passwordProp);

				PropertyInfo emailProp = new PropertyInfo();
				emailProp.setName("email");
				emailProp.setValue(new_email);
				emailProp.setType(String.class);
				request.addProperty(emailProp);

				PropertyInfo phoneProp = new PropertyInfo();
				phoneProp.setName("mobile");
				phoneProp.setValue(new_phone);
				phoneProp.setType(String.class);
				request.addProperty(phoneProp);

				PropertyInfo qoneprop = new PropertyInfo();
				qoneprop.setName("questionone");
				qoneprop.setValue(qid[1]);
				qoneprop.setType(String.class);
				request.addProperty(qoneprop);

				PropertyInfo answeroneprop = new PropertyInfo();
				answeroneprop.setName("answerone");
				answeroneprop.setValue(new_answerone);
				answeroneprop.setType(String.class);
				request.addProperty(answeroneprop);

				PropertyInfo qtwoprop = new PropertyInfo();
				qtwoprop.setName("questiontwo");
				qtwoprop.setValue(qid[2]);
				qtwoprop.setType(String.class);
				request.addProperty(qtwoprop);

				PropertyInfo answertwoprop = new PropertyInfo();
				answertwoprop.setName("answertwo");
				answertwoprop.setValue(new_answertwo);
				answertwoprop.setType(String.class);
				request.addProperty(answertwoprop);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE ht = new HttpTransportSE(URL);
				ht.call(SOAP_ACTION, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				SoapPrimitive s = response;
				String str = s.toString();
				flag = Integer.parseInt(str);

			}
			catch (SocketTimeoutException e)
			{
				flag = 5;
				errmsg = e.toString();
			}
			catch (Exception e)
			{
				flag = 6;
				errmsg = e.toString();
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
			if (flag == 0)
			{
				Toast.makeText(Newregistration_activity.this, "Registration Successfull, Welcome to Ezeepay!!",
						Toast.LENGTH_LONG).show();
				new Thread(new Runnable()
				{
					public void run()
					{
						// sendEmail();
					}
				}).start();
				Newregistration_activity.this.runOnUiThread(new Runnable()
				{
					public void run()
					{

					}
				});

				Intent intent1 = new Intent(Newregistration_activity.this, Login_Activity.class);
				startActivity(intent1);
				Newregistration_activity.this.finish();
			}
			else if (flag == 1)
			{
				Toast.makeText(Newregistration_activity.this, "email id already used", Toast.LENGTH_SHORT).show();
			}
			else if (flag == 5)
			{
				Toast.makeText(Newregistration_activity.this, "Socket Connection failed, please try after sometime", Toast.LENGTH_SHORT)
						.show();
				Log.e("flag 5,Socket Connection failed", errmsg);
			}
			else if (flag == 6)
			{
				Toast.makeText(Newregistration_activity.this, "Some unknown error", Toast.LENGTH_SHORT).show();
				Log.e("flag 6,Some unknown error", errmsg);
			}
			else if (flag == 22)
			{
				Toast.makeText(Newregistration_activity.this, "Mobile no registered already", Toast.LENGTH_SHORT).show();
			}
			else if (flag == 33)
			{
				Toast.makeText(Newregistration_activity.this, "Email id used already", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
