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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Forgotpassword_Activity extends Activity
{
	String URL;
	String qid[] =
	{ "0", "Q001", "Q002" };
	String sques1[] =
	{ "What is your favourite color?" };
	String sques2[] =
	{ "What is your favourite food?" };
	int flag;
	Bundle bundle;
	String errmsg="", x;
	String methodname = "";
	String resultstatus = "";
	connection_check check = new connection_check(this);

	private TextView forgotpassword_questionone_text, forgotpassword_questiontwo_text, forgotpassword_answer1_text,
			forgotpassword_answer2_text, newnewpassword_text, newnewpassword_confirm_text;

	/** Called when the activity is first created. */
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
			setContentView(R.layout.forgotpassword_layout);
			URL = selectwebservice.currentwebservice();
			Intent i1 = getIntent();
			bundle = i1.getExtras();

			forgotpassword_questionone_text = (TextView) findViewById(R.id.forgotpassword_questionone_text);
			forgotpassword_questiontwo_text = (TextView) findViewById(R.id.forgotpassword_questiontwo_text);
			forgotpassword_answer1_text = (EditText) findViewById(R.id.forgotpassword_answer1_text);
			forgotpassword_answer2_text = (EditText) findViewById(R.id.forgotpassword_answer2_text);
			newnewpassword_text = (TextView) findViewById(R.id.newnewpassword_text);
			newnewpassword_confirm_text = (TextView) findViewById(R.id.newnewpassword_confirm_text);

			forgotpassword_questionone_text.setText(sques1[0]);
			forgotpassword_questiontwo_text.setText(sques2[0]);

		}
		catch (Exception e)
		{
			Log.e("fatal", "fatal error in forgotpassword activity");
		}
	}

	public void onforgotpasswordactivity_button_select(View view)
	{
		try
		{

			if (check.isnetwork_available())
			{

				if (forgotpassword_answer1_text.getText().toString().trim().equals("")
						|| forgotpassword_answer2_text.getText().toString().trim().equals(""))
				{
					Toast.makeText(this, "Fill the answers", Toast.LENGTH_LONG).show();
				}
				else
				{
					methodname = "checkanswers";
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
			Log.e("crash", "error on onforgotpasswordactivity_button_select");
		}
	}

	public void onforgotpassword_changepassword_button_select(View view)
	{
		try
		{

			if (check.isnetwork_available())
			{
				if (forgotpassword_answer1_text.getText().toString().trim().equals("")
						|| forgotpassword_answer2_text.getText().toString().trim().equals(""))
				{
					Toast.makeText(this, "Fill the answers", Toast.LENGTH_LONG).show();
				}
				else if ((newnewpassword_text.getText().toString().length() > 5)
						&& (newnewpassword_confirm_text.getText().toString().length() > 5))
				{
					if ((newnewpassword_text.getText().toString().trim()).equals((newnewpassword_confirm_text.getText().toString().trim())))
					{

						methodname = "changepassword";
						new webservicecall_task().execute(methodname);
					}
					else
					{
						Toast.makeText(this, "Both the passwords should be same", Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					Toast.makeText(this, "Password should be 6 characters long", Toast.LENGTH_LONG).show();
				}

			}
			else
			{
				Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on onforgotpassword_changepassword_button_select");
		}
	}

	// ///////////ASYNC FETCH FOR CHECKING ANSWERS @ CHANGING
	// PSSWORD//////////////////////

	class webservicecall_task extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Forgotpassword_Activity.this, "", "Loading...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			x = params[0].toString();
			if (x.equals("checkanswers"))
			{
				try
				{
					// sleep(5000);
					String SOAP_ACTION = "http://services.ezeepay.com/checksecurityanswersmethod";
					String METHOD_NAME = "checksecurityanswersmethod";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("loginid");
					unameProp.setValue(bundle.getString("loginid"));
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					PropertyInfo qoneprop = new PropertyInfo();
					qoneprop.setName("questionone");
					qoneprop.setValue("Q001");
					qoneprop.setType(String.class);
					request.addProperty(qoneprop);

					PropertyInfo answeroneprop = new PropertyInfo();
					answeroneprop.setName("answerone");
					answeroneprop.setValue(forgotpassword_answer1_text.getText().toString().trim());
					answeroneprop.setType(String.class);
					request.addProperty(answeroneprop);

					PropertyInfo qtwoprop = new PropertyInfo();
					qtwoprop.setName("questiontwo");
					qtwoprop.setValue("Q002");
					qtwoprop.setType(String.class);
					request.addProperty(qtwoprop);

					PropertyInfo answertwoprop = new PropertyInfo();
					answertwoprop.setName("answertwo");
					answertwoprop.setValue(forgotpassword_answer2_text.getText().toString().trim());
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
					resultstatus = "checkanswers_executed";
				}

				catch (SocketTimeoutException e)
				{

					flag = 3;
					errmsg = e.toString();
					resultstatus = "checkanswers_executed";

				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "checkanswers_executed";

				}
			}
			else if (x.equals("changepassword"))
			{
				try
				{
					// sleep(5000);
					String SOAP_ACTION = "http://services.ezeepay.com/changepasswordmethod";
					String METHOD_NAME = "changepasswordmethod";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo unameProp = new PropertyInfo();
					unameProp.setName("loginid");
					unameProp.setValue(bundle.getString("loginid"));
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					PropertyInfo answeroneprop = new PropertyInfo();
					answeroneprop.setName("newpassword");
					EditText answer1 = (EditText) findViewById(R.id.newnewpassword_text);
					answeroneprop.setValue(answer1.getText().toString().trim());
					answeroneprop.setType(String.class);
					request.addProperty(answeroneprop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
					SoapPrimitive s = response;
					String str = s.toString();

					flag = Integer.parseInt(str);
					resultstatus = "changepassword_executed";
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
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			progress.dismiss();
			// Toast.makeText(Forgotpassword_Activity.this,
			// "onPostExecute method is:" + result, Toast.LENGTH_SHORT).show();
			if (result.equals("checkanswers_executed"))
			{
				if (flag == 0)
				{
					// Toast.makeText(Forgotpassword_Activity.this,
					// "Answer matches", Toast.LENGTH_SHORT).show();

					TextView tv1 = (TextView) findViewById(R.id.enternewpassword_label);
					tv1.setVisibility(View.VISIBLE);
					TextView tv2 = (TextView) findViewById(R.id.confirmpassword_label);
					tv2.setVisibility(View.VISIBLE);

					EditText ed1 = (EditText) findViewById(R.id.newnewpassword_text);
					ed1.setVisibility(View.VISIBLE);
					EditText ed2 = (EditText) findViewById(R.id.newnewpassword_confirm_text);
					ed2.setVisibility(View.VISIBLE);

					Button b1 = (Button) findViewById(R.id.forgotpassword_changepassword_button);
					b1.setVisibility(View.VISIBLE);
				}
				else if (flag == 1)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Sorry,Answers dosent match", Toast.LENGTH_SHORT).show();
					Log.e("Answers dosent match", errmsg);
				}
				else if (flag == 3)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Conenction Timeout", Toast.LENGTH_SHORT).show();
					Log.e("Conenction Timeout", errmsg);
				}
				else if (flag == 4)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Unknown error", Toast.LENGTH_SHORT).show();
					Log.e("Unknown error", errmsg);
				}
				else
				{
					Toast.makeText(Forgotpassword_Activity.this,
							"Login failed! Please check your credentials or contact admin for account activation", Toast.LENGTH_SHORT)
							.show();
					Log.e("login failed with flag ", String.valueOf(flag));
				}
			}
			else if (result.equals("changepassword_executed"))
			{
				if (flag == 0)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();

					Intent intent1 = new Intent(Forgotpassword_Activity.this, Login_Activity.class);
					startActivity(intent1);
					Forgotpassword_Activity.this.finish();
				}
				else if (flag == 1)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Error from seerverside", Toast.LENGTH_SHORT).show();
					Log.e("Error from seerverside", errmsg);
				}
				else if (flag == 3)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Conenction Timeout", Toast.LENGTH_SHORT).show();
					Log.e("Conenction Timeout", errmsg);
				}
				else if (flag == 4)
				{
					Toast.makeText(Forgotpassword_Activity.this, "Unknown error", Toast.LENGTH_SHORT).show();
					Log.e("Unknown error", errmsg);
				}
				else
				{
					Toast.makeText(Forgotpassword_Activity.this,
							flag + "Login failed! Please check your credentials or contact admin for account activation",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
