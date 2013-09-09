package com.ezeepay.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Fundtransfer_Activity extends Activity
{
	connection_check check = new connection_check(this);
	WebView web;

	String outpu;
	String key, txnid, amount, productinfo, firstname, email, phone, surl, furl, salt;
	String pg, bankcode, ccnum, ccname, ccvv, ccexpmon, ccexpyr, otp, api_version;

	String tempe, t1, t2, t3;
	int flag;

	MessageDigest md;
	// List<NameValuePair> data_pairs;
	// HttpResponse response;

	final String consumerKey = "Argcsd12394cebhved";
	final String consumerSecret = "FHKEFvehedvsdvksnJFC1";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// TODO Auto-generated method stub
		final SharedPreferences prefs_theme = PreferenceManager.getDefaultSharedPreferences(this);
		boolean theme_style = prefs_theme.getBoolean("pref_darktheme", false);
		if (theme_style)
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
		setContentView(R.layout.fundtransfer_layout);

		web = (WebView) findViewById(R.id.sample_webview);
		final Spinner fund_spinner = (Spinner) findViewById(R.id.fund_spinner);

		fund_spinner.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// if
				// (fund_spinner.getSelectedItem().equals("Select Payment mode....."))
				{
					List<String> paymentsmode = new ArrayList<String>();
					paymentsmode.add("My wallet");
					paymentsmode.add("Others");

					ArrayAdapter<String> paymentsadapter = new ArrayAdapter<String>(Fundtransfer_Activity.this,
							android.R.layout.simple_spinner_item, paymentsmode);
					paymentsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					fund_spinner.setAdapter(paymentsadapter);

				}
				return false;
			}
		});
		web.setWebViewClient(new myWebClient());
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("http://www.bing.com");
	}

	public void ontransfer_button_select(View v)
	{

		// String methodname = "logincheck";
		// new webservicecall_task().execute(methodname);
		key = "C0Dr8m";
		Random rand = new Random();
		String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
		txnid = hashCal("SHA-256", rndm).substring(0, 20);
		amount = "100.00";
		productinfo = "dummyrecharge3";
		firstname = "bala";
		email = "ssb.swr@gmail.com";
		phone = "9003818197";
		surl = "www.google.com";
		furl = "www.yahoo.com";
		salt = "3sf0jURk";

		// sample tempe =
		// "C0Dr8m|50032|40.00|testpro!|bala|ssb.swr@gmail.com|||||||||||3sf0jURk";
		tempe = key + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||" + salt;

		String query = "key=" + key + "&txnid=" + txnid + "&amount=" + amount + "&productinfo=" + productinfo + "&firstname=" + firstname
				+ "&email=" + email + "&phone=" + phone + "&surl=" + surl + "&furl=" + furl + "&hash=" + hashCal("SHA-512", tempe);

		outpu = query;

		pg = "CC";
		bankcode = "CC";
		ccnum = "5123456789012346";
		ccname = "demouser";
		ccvv = "123";
		ccexpmon = "MAY";
		ccexpyr = "2017";
		// otp = "";
		// api_version="1";

		String querywithseam = "key=" + key + "&txnid=" + txnid + "&amount=" + amount + "&productinfo=" + productinfo + "&firstname="
				+ firstname + "&email=" + email + "&phone=" + phone + "&surl=" + surl + "&furl=" + furl + "&hash="
				+ hashCal("SHA-512", tempe) + "&pg=" + pg + "&bankcode=" + bankcode + "&ccnum=" + ccnum + "&ccname=" + ccname + "&ccvv="
				+ ccvv + "&ccexpmon=" + ccexpmon + "&ccexpyr=" + ccexpyr;// +
		// "&otp="
		// +
		// otp;

		outpu = querywithseam;

		web.setWebViewClient(new myWebClient());
		web.getSettings().setJavaScriptEnabled(true);
		web.postUrl("https://test.payu.in/_payment", EncodingUtils.getBytes(outpu, "BASE64"));
		Toast.makeText(Fundtransfer_Activity.this, query, Toast.LENGTH_LONG).show();

	}

	public String hashCal(String type, String str)
	{
		byte[] hashseq = str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try
		{
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(hashseq);
			byte messageDigest[] = algorithm.digest();

			for (int i = 0; i < messageDigest.length; i++)
			{
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append("0");
				hexString.append(hex);
			}

		}
		catch (NoSuchAlgorithmException nsae)
		{
		}

		return hexString.toString();

	}

	public class myWebClient extends WebViewClient
	{
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// TODO Auto-generated method stub

			view.loadUrl(url);
			return true;

		}
	}

	// To handle "Back" key press event for WebView to go back to previous
	// screen.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack())
		{
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	class demo_web extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Fundtransfer_Activity.this, "", "Logging in...");
		}

		@Override
		protected String doInBackground(String... params)
		{

/*			RestClient localRestClient = new RestClient("http://api.seatseller.travel/sources");
			localRestClient.AddHeader("consumerKey", "RlS3F30GlN4NjSGg3IoZXfXb1g3qEZ");
			localRestClient.AddHeader("consumerSecret", "RtrQDuOKRAPUo5IRpOn2SSOt9F4EAh");

			try
			{
				localRestClient.Execute(RestClient.RequestMethod.GET);
				String str = localRestClient.getResponse();
				outpu = str;
				// Model.getInstance();
			}
			catch (Exception er)
			{
				outpu = er.toString();
			}
*/
			return null;
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
			// if (flag==1)
			{
				Toast.makeText(Fundtransfer_Activity.this, outpu, Toast.LENGTH_LONG).show();
			}
			// else
			{
				// Toast.makeText(Fundtransfer_Activity.this,
				// "flage work aala\n"+outpu, Toast.LENGTH_LONG).show();
			}

		}
	}
}
