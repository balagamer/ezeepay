package com.ezeepay.services;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.lorecraft.phparser.SerializedPhpParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_recharge extends Fragment implements OnClickListener
{
	connection_check check;

	private String favourite_name = "", txn_id = "", outpu = "", line = "", mihpayid = "", token = "", pref_paymentmode = "", URL = "",
			methodname = "", resultstatus = "", errmsg = "", x = "", selected_circle = "", ptype = "Wallet", bankcd = "none",
			p_state = "SUCCESS", regex = "", e1 = "", e2 = "", e3 = "", e4 = "", e5 = "", main_flag = "Recharge", outp, from_fav = "";

	private String cust_loginid, cust_main_balance, cust_username = "", cust_email = "", cust_phone = "";

	String murl = "";

	private String payuid = "null", payu_status = "none", req_id = "";
	private int pay_flag = 0, retry_count = 0;;
	boolean autofill = false;
	LayoutInflater inflater;
	ListView plans_listview;
	HashMap<String, String> map_mobileplans;
	ListAdapter plans_adapter;
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	WebView web;

	private Spinner payment_categories_spinner, payment_vendors_spinner, payment_modes_spinner, circle_spinner;
	private TextView payment_billno_text, payment_remarks_text, payment_amount_text, rechargehint_text, divider1, vendor_warning_text;
	private Button autoselect_button, plan_option_button, proceed_payment_button;
	private ImageButton speech_imagebutton, number_option_imagebutton;

	ArrayAdapter<String> adapter, adapter1, adapter2;
	View recharge_view;

	int flag;
	Matcher matcher;

	Map<String, String> pa = new HashMap<String, String>();
	List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
	List<NameValuePair> main_parameters = new ArrayList<NameValuePair>(2);

	String recharge_category_names[] =
	{ "Select Category", "Mobile/Datacard Topup", "Recharge DTH" };
	int recharge_category_images[] =
	{ 0, R.drawable.recharge_mobile, R.drawable.recharge_dth };
	// //////circles
	String circles_names[] =
	{ "Select Circle", "Andhra Pradesh", "Assam", "Bihar & Jharkhand", "Chennai", "Delhi", "Gujarat", "Haryana", "Himachal Pradesh",
			"Jammu & Kashmir", "Karnataka", "Kerala", "Kolkata", "Maharashtra & Goa (except Mumbai)", "Madhya Pradesh & Chhattisgarh",
			"Mumbai", "North East", "Orissa", "Punjab", "Rajasthan", "Tamil Nadu", "Uttar Pradesh - East", "Uttar Pradesh - West",
			"West Bengal" };
	int circles_images[] =
	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	int circle_ids[] =
	{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
	// ///// topup vendors
	String topups_vendor_names[] =
	{ "Select Operator", "Aircel", "Airtel", "BSNL 3G", "BSNL Recharge/Validity (RCV)", "BSNL Special (STV)", "BSNL TopUp", "Idea",
			"Loop Mobile", "MTNL Recharge/Special", "MTNL TopUp", "MTS/MTS Blaze/Browse", "Reliance CDMA/NetConnect 1X/+",
			"Reliance GSM/NetConnect 3G", "S Tel", "Tata Docomo", "Tata Docomo Special", "Tata Indicom/Photon +/Whiz", "Uninor",
			"Uninor Special", "Videocon", "Videocon Special", "Virgin CDMA", "Virgin GSM", "Vodafone"

	};
	int topups_vendor_images[] =
	{ 0, R.drawable.mobile_aircel, R.drawable.mobile_airtel, R.drawable.mobile_bsnl3g, R.drawable.mobile_bsnl, R.drawable.mobile_bsnl,
			R.drawable.mobile_bsnl, R.drawable.mobile_idea, R.drawable.mobile_loopmobile, R.drawable.mobile_mtnl, R.drawable.mobile_mtnl,
			R.drawable.mobile_mts, R.drawable.mobile_reliancecdma, R.drawable.datacard_rconnect3g, 0, R.drawable.mobile_docomo,
			R.drawable.mobile_docomo, R.drawable.datacard_photonplus, R.drawable.mobile_uninor, R.drawable.mobile_uninor,
			R.drawable.mobile_videocon, R.drawable.mobile_videocon, R.drawable.mobile_virgin, R.drawable.mobile_virgin,
			R.drawable.mobile_vodafone };
	int topups_vendor_ids[] =
	{ 0, 6, 1, 302, 301, 303, 3, 8, 10, 2501, 25, 13, 4, 5, 15, 11, 1101, 9, 16, 1601, 17, 1701, 12, 14, 2

	};

	String dth_vendor_names[] =
	{ "Select DTH Provider", "Airtel DTH", "Dish TV", "Reliance Big TV", "Sun Direct", "Tata Sky", "Videocon" };
	int dth_vendor_images[] =
	{ 0, R.drawable.dth_airtel, R.drawable.dth_dishtv, R.drawable.dth_reliancebigtv, R.drawable.dth_sundirect, R.drawable.dth_tatasky,
			R.drawable.dth_videocon };
	int dth_vendor_ids[] =
	{ 0, 23, 18, 20, 22, 19, 21 };
	// /////////////
	// String payment_modes_names[] =
	// { "Select Payment Mode", "My wallet", "Credit Cards", "Debit Cards",
	// "Net Banking" };
	// int payment_modes_images[] =
	// { 0, R.drawable.mywallet, R.drawable.creditcards,
	// R.drawable.creditcards2, R.drawable.netbanking };

	String payment_modes_names[] =
	{ "Select Payment Mode", "Credit Cards", "Debit Cards", "Net Banking" };
	int payment_modes_images[] =
	{ 0, R.drawable.creditcards, R.drawable.creditcards2, R.drawable.netbanking };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.e("INSIDE", "inside1");
		recharge_view = inflater.inflate(R.layout.fragment_recharge, container, false);
		try
		{
			SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

			SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			cust_loginid = prefs.getString("loginid", "");
			cust_username = prefs.getString("username", "");
			cust_email = prefs.getString("email", "");
			cust_phone = prefs.getString("phone", "");
			cust_main_balance = prefs.getString("balance", "");

			pref_paymentmode = default_prefs.getString("pref_paymentmode", "");

			check = new connection_check(getActivity());

			// SPINNERS////
			payment_categories_spinner = (Spinner) recharge_view.findViewById(R.id.payment_categories_spinner);
			payment_vendors_spinner = (Spinner) recharge_view.findViewById(R.id.payment_vendors_spinner);
			payment_modes_spinner = (Spinner) recharge_view.findViewById(R.id.payment_modes_spinner);
			circle_spinner = (Spinner) recharge_view.findViewById(R.id.circle_spinner);
			circle_spinner.setPrompt("Select Circle");

			// / TEXTVIEWS////

			payment_billno_text = (TextView) recharge_view.findViewById(R.id.payment_billno_text);
			rechargehint_text = (TextView) recharge_view.findViewById(R.id.rechargehint_text);
			rechargehint_text.setVisibility(View.GONE);
			divider1 = (TextView) recharge_view.findViewById(R.id.divider1);
			divider1.setVisibility(View.GONE);
			vendor_warning_text = (TextView) recharge_view.findViewById(R.id.vendor_warning_text);
			vendor_warning_text.setVisibility(View.GONE);
			payment_remarks_text = (TextView) recharge_view.findViewById(R.id.payment_remarks_text);
			payment_amount_text = (TextView) recharge_view.findViewById(R.id.payment_amount_text);

			// /BUTTONS////

			speech_imagebutton = (ImageButton) recharge_view.findViewById(R.id.speech_imagebutton);
			speech_imagebutton.setOnClickListener(this);
			number_option_imagebutton = (ImageButton) recharge_view.findViewById(R.id.number_option_imagebutton);
			number_option_imagebutton.setOnClickListener(this);
			autoselect_button = (Button) recharge_view.findViewById(R.id.autoselect_button);
			autoselect_button.setVisibility(View.GONE);
			autoselect_button.setOnClickListener(this);
			plan_option_button = (Button) recharge_view.findViewById(R.id.plan_option_button);
			plan_option_button.setOnClickListener(this);
			proceed_payment_button = (Button) recharge_view.findViewById(R.id.proceed_payment_button);

			proceed_payment_button.setEnabled(false);
			proceed_payment_button.setOnClickListener(this);

			// initiate values

			payment_categories_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
					recharge_category_names, recharge_category_images));

			Intent i = getActivity().getIntent();
			if (i.hasExtra("from_favourites"))
			{
				Bundle bundle = i.getExtras();
				if (bundle.containsKey("from_favourites"))
				{
					from_fav = bundle.getString("from_favourites");
					// Log.e("from fav ", "ff" + from_fav);

					if (from_fav.equals("true"))
					{
						payment_billno_text.setText(bundle.getString("selected_number"));
						call_db(bundle.getString("selected_number"));
						payment_amount_text.setText(bundle.getString("selected_amount"));
						payment_modes_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								payment_modes_names, payment_modes_images));
						if (pref_paymentmode.equals("credit_cards"))
							payment_modes_spinner.setSelection(0);
						else if (pref_paymentmode.equals("debit_cards"))
							payment_modes_spinner.setSelection(0);
						else if (pref_paymentmode.equals("net_banking"))
							payment_modes_spinner.setSelection(0);
					}
				}
				else
				{
					Log.e("from fav ", "empty");
				}
			}
			URL = selectwebservice.currentwebservice();
			payment_categories_spinner.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{

					autofill = false;
					payment_billno_text.setText("");
					return false;
				}
			});

			payment_categories_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{

					if (!autofill)
					{
						vendor_warning_text.setVisibility(View.GONE);
						circle_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages, circles_names,
								circles_images));
						payment_modes_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
								payment_modes_names, payment_modes_images));
						if (pref_paymentmode.equals("credit_cards"))
							payment_modes_spinner.setSelection(1);
						else if (pref_paymentmode.equals("debit_cards"))
							payment_modes_spinner.setSelection(2);
						else if (pref_paymentmode.equals("net_banking"))
							payment_modes_spinner.setSelection(3);

						if (payment_categories_spinner.getSelectedItem().equals("Select Category"))
						{
							payment_billno_text.setHint("Select Category");
							// payment_vendors_spinner.setVisibility(View.GONE);

						}

						else if (payment_categories_spinner.getSelectedItem().equals("Mobile/Datacard Topup"))
						{
							payment_billno_text.setHint("Mobile/Datacard Number");
							rechargehint_text.setVisibility(View.VISIBLE);
							divider1.setVisibility(View.VISIBLE);
							autoselect_button.setVisibility(View.VISIBLE);
							circle_spinner.setVisibility(View.VISIBLE);
							payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
									topups_vendor_names, topups_vendor_images));

						}
						else if (payment_categories_spinner.getSelectedItem().equals("Recharge DTH"))
						{
							// payment_billno_text.setText("");
							autoselect_button.setVisibility(View.GONE);
							rechargehint_text.setVisibility(View.GONE);
							divider1.setVisibility(View.GONE);
							circle_spinner.setVisibility(View.GONE);
							circle_spinner.setSelection(1);
							payment_billno_text.setHint("DTH Number");
							payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
									dth_vendor_names, dth_vendor_images));
						}
						else
						{
							rechargehint_text.setVisibility(View.GONE);
							autoselect_button.setVisibility(View.GONE);
							divider1.setVisibility(View.GONE);
							// circle_spinner.setAdapter();
							// payment_vendors_spinner.setAdapter(null);
							// payment_modes_spinner.setAdapter(null);
						}
					}
					else
					{
						autofill = false;
					}
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();

				}
			});

			payment_modes_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{
					set_layout();
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();

				}
			});

			// ///////////////////
		}
		catch (Exception e)
		{
			Log.e("crash", "fatal error in fragment_recharge on create\n" + e.toString());
		}
		// getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);
		return recharge_view;

	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.speech_imagebutton:
			{
				PackageManager pm = getActivity().getPackageManager();
				List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
				if (activities.size() != 0)
				{
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your option");
					startActivityForResult(intent, 123);
					break;
				}
				else
				{
					Toast.makeText(getActivity(), "Recognizer not present", Toast.LENGTH_SHORT);
				}

			}
			case R.id.number_option_imagebutton:

			{
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenu().add(0, 0, 0, "Use my mobile");
				popup.getMenu().add(0, 1, 1, "Select from contacts");
				popup.getMenu().add(0, 2, 2, "My DTH");
				popup.getMenu().add(0, 3, 3, "My DataCard");
				popup.show();

				final SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
				final SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
				{

					@Override
					public boolean onMenuItemClick(MenuItem item)
					{
						if (item.getItemId() == 0)
						{
							String mynum = default_prefs.getString("pref_mymobile", "");
							if (mynum.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_billno_text.setText(mynum);
								call_db(payment_billno_text.getText().toString());
							}
						}
						else if (item.getItemId() == 1)
						{
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
							startActivityForResult(intent, 11);
						}
						else if (item.getItemId() == 2)
						{
							String mynum = default_prefs.getString("pref_mydth", "");
							if (mynum.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{

								payment_billno_text.setText(mynum);
								payment_categories_spinner.setSelection(2, true);
							}
						}
						else if (item.getItemId() == 3)
						{
							String mynum = default_prefs.getString("pref_mydatacard", "");
							if (mynum.equals(""))
							{
								payment_billno_text.setText("");
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{
								payment_billno_text.setText(mynum);
								call_db(payment_billno_text.getText().toString());
							}
						}
						return true;
					}
				});
				break;
			}
			case R.id.autoselect_button:
			{
				if (payment_billno_text.getText().toString().trim().equals("") || payment_billno_text.getText().length() < 9)
				{
					Toast.makeText(getActivity(), "Select/Input a valid number", Toast.LENGTH_SHORT).show();
				}
				else
				{
					call_db(payment_billno_text.getText().toString());
				}
				break;
			}
			case R.id.plan_option_button:
			{
				try
				{
					methodname = "fetchmobile_plans";
					new transactioncall_task().execute(methodname);
				}
				catch (Exception e)
				{
					Log.e("crash", "error on policy button view");
				}

				break;
			}
			case R.id.proceed_payment_button:
			{

				if (payment_categories_spinner.getSelectedItem().toString().equals("Mobile/Datacard Topup"))
				{
					regex = "^[7-9][0-9]{9}$";
					matcher = Pattern.compile(regex).matcher(payment_billno_text.getText().toString().trim());
					if (matcher.find())
					{
						if ((payment_billno_text.getText().toString().trim().length() == 10))
						{
							e1 = "";
						}
						else
						{
							e1 = "Phone number not valid";
						}
					}
					else
					{
						e1 = "Phone number not valid";
					}
				}
				else
				{
					regex = "^[A-Za-z0-9]{5,20}$";
					matcher = Pattern.compile(regex).matcher(payment_billno_text.getText().toString().trim());
					if (matcher.find())
					{
						e1 = "";
					}
					else
					{
						e1 = "Please check your bill number";
						Toast.makeText(getActivity(), e1, Toast.LENGTH_SHORT).show();
					}
				}

				regex = "^[0-9]{0,5}$";
				matcher = Pattern.compile(regex).matcher(payment_amount_text.getText().toString().trim());
				if (matcher.find())
				{
					e3 = "";
				}
				else
				{
					e3 = "Invalid rupee value";
				}

				if ((payment_categories_spinner.getSelectedItemPosition() == 0) || (circle_spinner.getSelectedItemPosition() == 0)
						|| (payment_vendors_spinner.getSelectedItemPosition() == 0)
						|| (payment_modes_spinner.getSelectedItemPosition() == 0)
						|| payment_billno_text.getText().toString().trim().equals("")
						|| payment_amount_text.getText().toString().trim().equals(""))
				{
					Toast.makeText(getActivity(), "Please fill in the required data", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if (e1.equals("") && e2.equals("") && e3.equals(""))
					{
						if (Integer.parseInt(payment_amount_text.getText().toString().trim()) >= 10)
						{

							if (payment_modes_spinner.getSelectedItem().toString().equals("Credit Cards")
									|| payment_modes_spinner.getSelectedItem().toString().equals("Debit Cards")
									|| payment_modes_spinner.getSelectedItem().toString().equals("Net Banking"))
							{
								call_otherpayments();
							}
							else
							{
								if (check.isnetwork_available())
								{
									ptype = "Wallet";
									p_state = "SUCCESS";
									methodname = "transaction_main";
									new transactioncall_task().execute(methodname);
								}
								else
								{
									Toast.makeText(getActivity(), "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
								}

							}
						}
						else
						{
							Toast.makeText(getActivity(), "Minimum amount should be higher than 10 rupees", Toast.LENGTH_SHORT).show();
						}

					}
					else
					{
						Toast.makeText(getActivity(), "Please resolve the following errors\n" + e1 + "\n" + e2 + "\n" + e3,
								Toast.LENGTH_SHORT).show();

					}
				}

				break;
			}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on on click event\n" + e.toString());
		}
	}

	// // speech result activity && contaacts activity

	// /result intent for contacts
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data)
	{
		super.onActivityResult(reqCode, resultCode, data);
		final SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		try
		{

			switch (reqCode)
			{
			case (11):
			{
				if (resultCode == Activity.RESULT_OK)
				{
					Uri contactData = data.getData();
					Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst())
					{
						c = getActivity().getContentResolver().query(contactData, new String[]
						{ ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE }, null, null, null);
						if (c != null && c.moveToFirst())
						{
							String number = c.getString(0);
							number = number.replace(" ", "");
							number = number.replace("+91", "");
							int type = c.getInt(1);
							payment_billno_text.setText(String.valueOf(number));
							call_db(payment_billno_text.getText().toString());
						}
					}
				}
				break;
			}
			case 123:
			{
				if (reqCode == 123 && resultCode == Activity.RESULT_OK)
				{
					int flag = 10;
					ArrayList<String> voice_match = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					// Toast.makeText(getActivity(), "you said " + voice_match,
					// Toast.LENGTH_LONG).show();
					if ((voice_match.get(0).startsWith("recharge my mobile with"))
							|| (voice_match.get(1).startsWith("recharge my mobile with")))
					{
						String match1 = voice_match.get(0).replace("recharge my mobile with", "").trim();
						String match2 = voice_match.get(1).replace("recharge my mobile with", "").trim();
						Log.e("final string", match1);
						try
						{
							int x = Integer.parseInt(match1);
							payment_amount_text.setText(String.valueOf(x));
							flag = 1;
						}
						catch (Exception e)
						{
							Log.e("error", "number not found in string");
						}
						if (flag != 1)
						{
							try
							{
								int x = Integer.parseInt(match2);
								payment_amount_text.setText(String.valueOf(x));
								flag = 1;
							}
							catch (Exception e)
							{
								Log.e("error", "number not found in string");
							}
						}
						String mynum = default_prefs.getString("pref_mymobile", "");
						if (mynum.equals(""))
						{
							Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
						}
						else
						{
							payment_billno_text.setText(mynum);
							call_db(mynum);
						}
					}
					else if ((voice_match.get(0).startsWith("recharge my data card with"))
							|| (voice_match.get(1).startsWith("recharge my data card with")))
					{
						String match1 = voice_match.get(0).replace("recharge my data card with", "").trim();
						String match2 = voice_match.get(1).replace("recharge my data card with", "").trim();
						Log.e("final string", match1);
						try
						{
							int x = Integer.parseInt(match1);
							payment_amount_text.setText(String.valueOf(x));
							flag = 1;
						}
						catch (Exception e)
						{
							Log.e("error", "number not found in string");
						}
						if (flag != 1)
						{
							try
							{
								int x = Integer.parseInt(match2);
								payment_amount_text.setText(String.valueOf(x));
								flag = 1;
							}
							catch (Exception e)
							{
								Log.e("error", "number not found in string");
							}
						}
						String mynum = default_prefs.getString("pref_mydatacard", "");
						if (mynum.equals(""))
						{
							Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
						}
						else
						{
							payment_billno_text.setText(mynum);
							call_db(mynum);
						}
					}
				}
				break;
			}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on result activity\n" + e.toString());
		}
	}

	public void payment_status(String txn_report)
	{
		try
		{
			final Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(R.layout.paymentstatus_layout);
			dialog.setTitle("Transaction Summary");

			TextView summary1 = (TextView) dialog.findViewById(R.id.paymentsummary1_text);
			TextView summary2 = (TextView) dialog.findViewById(R.id.paymentsummary2_text);
			TextView summary3 = (TextView) dialog.findViewById(R.id.paymentsummary3_text);
			final TextView favorite_alias_text = (TextView) dialog.findViewById(R.id.favorite_alias_text);

			final Button b1 = (Button) dialog.findViewById(R.id.finish_button);
			final Button b2 = (Button) dialog.findViewById(R.id.retry_button);

			final TextView tstatus = (TextView) dialog.findViewById(R.id.paymentstatus_text);
			final CheckBox cb1 = (CheckBox) dialog.findViewById(R.id.savetofavorite_checkbox);
			JSONObject his_data = new JSONObject(outp);
			JSONArray txns = his_data.getJSONArray("transaction_report");
			Log.e("report length   ", String.valueOf(txns.length()));
			JSONObject e = txns.getJSONObject(0);
			int flag = Integer.parseInt(e.getString("flag"));
			Log.e("flag   ", String.valueOf(flag));

			if (flag == 0)
			{
				String txn_status = e.getString("txn_status");
				String txn_status_reason = e.getString("txn_status_reason");
				txn_id = e.getString("txn_id");
				if (txn_status.equals("SUCCESS"))
				{
					tstatus.setTextColor(Color.GREEN);
					tstatus.setText(txn_status);
				}
				else
				{
					tstatus.setTextColor(Color.RED);
					tstatus.setText(txn_status + "," + txn_status_reason);
					b2.setEnabled(true);
				}

				summary1.setText("Your Transaction ID :" + txn_id);
				summary2.setText("Bill/Phone Number :" + payment_billno_text.getText().toString().trim());
				summary3.setText("Amount:" + payment_amount_text.getText().toString().trim());

			}
			else if (flag == 10)
			{
				tstatus.setText("Insufficent balance");
			}
			else if (flag == 20)
			{
				tstatus.setText("Zero balance");
			}
			else if (flag == 3)
			{
				tstatus.setTextColor(Color.BLUE);
				tstatus.setText("Conenction Timeout");

			}
			else if (flag == 4)
			{
				tstatus.setTextColor(Color.RED);
				tstatus.setText("Insufficent balance");
			}
			else
			{
				tstatus.setText("Insufficent balance");
				tstatus.setTextColor(Color.RED);
				tstatus.setText("Login failed! Please check your credentials or contact admin for account activation");
			}

			b1.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (cb1.isChecked())
					{
						if (favorite_alias_text.getText().toString().equals(""))
						{
							Toast.makeText(getActivity(), "Please enter a name for this transaction", Toast.LENGTH_SHORT).show();
						}
						else
						{
							favourite_name = favorite_alias_text.getText().toString().trim();
							methodname = "update_favorites";
							new transactioncall_task().execute(methodname);

							// getActivity().finish();
							// dialog.dismiss();
						}
					}
					else
					{
						getActivity().finish();
						dialog.dismiss();
					}

				}
			});
			b2.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
					if (payuid.equals("null"))
					{
						call_otherpayments();
					}
					else
					{
						if (retry_count < 1)
						{
							methodname = "transaction_main";
							new transactioncall_task().execute(methodname);
							retry_count = retry_count + 1;
						}
						else
						{
							methodname = "call_refund";
							new transactioncall_task().execute(methodname);
						}
					}

				}
			});

			cb1.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{

					if (buttonView.isChecked())
					{
						favorite_alias_text.setVisibility(View.VISIBLE);
					}
					else
					{
						Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
					}
				}
			});

			dialog.show();

		}
		catch (Exception e)
		{
			Log.e("crash", "error on payment_method\n" + e);
		}
	}

	// ///// adapter 1/////////////

	public void set_layout()
	{

		try
		{
			// /BUTTONS////

			payment_amount_text.setEnabled(true);
			plan_option_button.setEnabled(true);

			payment_remarks_text.setEnabled(true);
			proceed_payment_button.setEnabled(true);
			// proceed_payment_button.setClickable(true);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on set_layout\n" + e.toString());
		}

	}

	public void call_db(String number)
	{
		DataBaseHelper myDbHelper = new DataBaseHelper(null);
		myDbHelper = new DataBaseHelper(getActivity());

		try
		{
			myDbHelper.createDataBase();
		}
		catch (IOException ioe)
		{
			throw new Error("Unable to create database");
		}

		try
		{
			myDbHelper.openDataBase();

		}
		catch (SQLException sqle)
		{

			Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
			throw sqle;

		}

		SQLiteDatabase sampleDB = null;

		try
		{
			int x = Integer.parseInt(number.substring(0, 4));
			Log.e("number to be checked on databse", String.valueOf(x));
			sampleDB = getActivity().openOrCreateDatabase("mobilecodes_db", 0, null);
			Cursor c = sampleDB.rawQuery("SELECT * FROM mobile_codes where _id=" + x, null);
			String Operator = "";
			String Circle = "";
			Log.e("cursor output", c.toString());
			if (c.moveToFirst())
			{
				Log.e("inside cursor output", c.getString(0));
				Operator = c.getString(c.getColumnIndex("Operator"));
				Circle = c.getString(c.getColumnIndex("Circle"));
				Log.e("operator code ", Operator);
				payment_categories_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
						recharge_category_names, recharge_category_images));
				payment_categories_spinner.setSelection(1, true);

				payment_vendors_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
						topups_vendor_names, topups_vendor_images));

				if (Operator.equals("Reliance CDMA"))

				{
					payment_vendors_spinner.setSelection(12, true);
				}
				else if (Operator.equals("Reliance GSM"))

				{
					payment_vendors_spinner.setSelection(13, true);
				}
				else if (Operator.equals("TATA Docomo"))

				{
					vendor_warning_text.setVisibility(View.VISIBLE);
					vendor_warning_text
							.setText("Docomo Special recharge selected, Please choose Tata Docomo if you are looking for regular plans");
					payment_vendors_spinner.setSelection(16, true);
				}

				else
				{
					payment_vendors_spinner.setSelection(Arrays.asList(topups_vendor_names).indexOf(Operator), true);

				}
				circle_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages, circles_names,
						circles_images));

				circle_spinner.setSelection(Arrays.asList(circles_names).indexOf(Circle), true);

				// payment_modes_spinner.setAdapter(new
				// customspinner_adapter(getActivity(),
				// R.layout.spinneritems_withimages,
				// payment_modes_names, payment_modes_images));
				sampleDB.close();
				autofill = true;

			}
			else
			{
				Toast.makeText(getActivity(), "Number not found in the database!", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception se)
		{
			Log.e(getClass().getSimpleName(), "Could not create or Open the database\n" + se.toString());
		}
	}

	public void call_otherpayments()
	{
		try
		{
			String banks_category[] =
			{ "Select Bank Name", "AXIS Bank NetBanking", "Bank of India", "Bank of Maharashtra", "Central Bank Of India",
					"Corporation Bank", "Development Credit Bank", "Federal Bank", "HDFC Bank", "ICICI Netbanking",
					"Industrial Development Bank of India", "Indian Bank ", "IndusInd Bank", "Indian Overseas Bank",
					"Jammu and Kashmir Bank", "Karnataka Bank", "Karur Vysya ", "State Bank of Bikaner and Jaipur",
					"State Bank of Hyderabad", "State Bank of India", "State Bank of Mysore", "State Bank of Travancore",
					"South Indian Bank", "Union Bank of India", "United Bank Of India", "Vijaya Bank", "Yes Bank", "CityUnion",
					"Canara Bank", "State Bank of Patiala", "Citi Bank NetBanking", "Deutsche Bank" };

			final String banks_category_id[] =
			{ "null", "AXIB", "BOIB", "BOMB", "CBIB", "CRPB", "DCBB", "FEDB", "HDFB", "ICIB", "IDBB", "INDB", "INIB", "INOB", "JAKB",
					"KRKB", "KRVB", "SBBJB", "SBHB", "SBIB", "SBMB", "SBTB", "SOIB", "UBIB", "UNIB", "VJYB", "YESB", "CUBB", "CABB",
					"SBPB", "CITNB", "DSHB" };
			final String creditcards_cat[] =
			{ "Select Credit Card", "Visa Card", "Master Card", "American Express" };
			int creditcards_images[] =
			{ 0, R.drawable.cards_visa02, R.drawable.cards_mastercard, R.drawable.cards_americanexpress };
			final String debitcards_cat[] =
			{ "Select Debit Card", "Visa Card", "Master Card", "Maestro Card", "SBI Maestro Card" };
			int debitcards_images[] =
			{ 0, R.drawable.cards_visa02, R.drawable.cards_mastercard, R.drawable.cards_maestro, R.drawable.cards_maestro };
			final String months_category[] =
			{ "Select Month", "January(01)", "February(02)", "March(03)", "April(04)", "May(05)", "June(06)", "July(07)", "August(08)",
					"Spetember(09)", "October(10)", "November(11)", "December(12)" };
			final int months_id[] =
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
			final String years_category[] =
			{ "Select Year", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025",
					"2026", "2027", "2028", "2029" };

			final TextView cardholder_text, cardno_text, cvvno_text;

			final Spinner paymentoption_spinner, expirymonth_spinner, expiryyear_spinner;

			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View payment_view = inflater.inflate(R.layout.paymentmethod, null);

			// // intialize views
			paymentoption_spinner = (Spinner) payment_view.findViewById(R.id.paymentoption_spinner);

			final LinearLayout userinput_layout = (LinearLayout) payment_view.findViewById(R.id.userinput_linearlayout);
			userinput_layout.setVisibility(View.GONE);

			cardholder_text = (TextView) payment_view.findViewById(R.id.cardholder_text);
			cardno_text = (TextView) payment_view.findViewById(R.id.cardno_text);
			expirymonth_spinner = (Spinner) payment_view.findViewById(R.id.expirymonth_spinner);
			expiryyear_spinner = (Spinner) payment_view.findViewById(R.id.expiryyear_spinner);
			cvvno_text = (TextView) payment_view.findViewById(R.id.cvvno_text);

			// //// reset count
			retry_count = 0;

			web = (WebView) payment_view.findViewById(R.id.sample_webview);
			web.setVisibility(View.GONE);

			final Button pay_button = (Button) payment_view.findViewById(R.id.pay_button);

			ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
					months_category);
			month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			expirymonth_spinner.setAdapter(month_adapter);

			ArrayAdapter<String> year_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
					years_category);
			year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			expiryyear_spinner.setAdapter(year_adapter);

			if (payment_modes_spinner.getSelectedItem().equals("Credit Cards"))
			{
				paymentoption_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages,
						creditcards_cat, creditcards_images));
				ptype = "CC";
				bankcd = "CC";
			}

			else if (payment_modes_spinner.getSelectedItem().equals("Debit Cards"))
			{
				paymentoption_spinner.setAdapter(new customspinner_adapter(getActivity(), R.layout.spinneritems_withimages, debitcards_cat,
						debitcards_images));
				ptype = "DC";
				bankcd = "VISA";

			}
			else
			{
				ArrayAdapter<String> bank_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
						banks_category);
				bank_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				paymentoption_spinner.setAdapter(bank_adapter);
				ptype = "NB";
				bankcd = "NB";
				userinput_layout.setVisibility(View.GONE);
			}

			paymentoption_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{
					if (payment_modes_spinner.getSelectedItem().equals("Net Banking"))
						bankcd = banks_category_id[paymentoption_spinner.getSelectedItemPosition()];
					else
						userinput_layout.setVisibility(View.VISIBLE);
					if (paymentoption_spinner.getSelectedItem().equals("Visa Card"))
						bankcd = "VISA";
					else if (paymentoption_spinner.getSelectedItem().equals("Master Card"))
						bankcd = "MAST";
					else if (paymentoption_spinner.getSelectedItem().equals("Maestro Card"))
						bankcd = "MAES";
					else if (paymentoption_spinner.getSelectedItem().equals("SBI Maestro Card"))
						bankcd = "SMAE ";
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();
				}
			});

			final AlertDialog.Builder payment_dialog_builder = new AlertDialog.Builder(getActivity());
			payment_dialog_builder.setView(payment_view);
			final ProgressDialog pd = new ProgressDialog(getActivity());
			pd.setCancelable(false);
			pd.setMessage("Processing ...");

			final Dialog payment_dialog = payment_dialog_builder.create();

//			payment_dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
//			{
//				@Override
//				public void onCancel(DialogInterface dialog)
//				{
//					Toast.makeText(getActivity(), "cancel called", Toast.LENGTH_SHORT).show();
//				}
//			});
			payment_dialog.setOnKeyListener(new Dialog.OnKeyListener()
			{
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event)
				{
					Boolean x;
					if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() != KeyEvent.ACTION_DOWN))
					{
						x=true;
						AlertDialog.Builder cancel_txn_builder = new AlertDialog.Builder(getActivity());
						cancel_txn_builder.setMessage("Transaction not complete ,are you sure?");
						cancel_txn_builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								payment_dialog.dismiss();
							}
						});
						cancel_txn_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								dialog.cancel();
							}
						});
						AlertDialog cancel_txn_dialog = cancel_txn_builder.create();
						cancel_txn_dialog.show();
					}
					else
					{
						x=false;
					}
					return x;
				}
			});
			pay_button.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					String is_error = "";
					try
					{
						if (ptype.equals("NB"))
						{
							if (paymentoption_spinner.getSelectedItemPosition() == 0)
								is_error = "Please select a valid bank name";
							else
								is_error = "";
						}
						else
						{
							if (cardholder_text.getText().toString().equals("") || cardno_text.getText().toString().equals("")
									|| expirymonth_spinner.getSelectedItemPosition() == 0
									|| expiryyear_spinner.getSelectedItemPosition() == 0 || cvvno_text.getText().toString().equals(""))
							{

								is_error = "Please fill all the fields!!";
							}
							else
							{
								is_error = "";
							}
						}

						if (is_error.equals(""))
						{
							paymentoption_spinner.setVisibility(View.GONE);
							pay_button.setVisibility(View.GONE);
							userinput_layout.setVisibility(View.GONE);
							web.setVisibility(View.VISIBLE);

							String key, txnid, amount, productinfo, firstname, email, phone, surl, furl, curl, salt;
							String pg, bankcode = "", ccnum = "", ccname = "", ccvv = "";
							String ccexpmon = "";
							String ccexpyr = "";
							String tempe = "";

							// parameters values part 1
							key = "asjjZn";
							Random rand = new Random();
							String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
							txnid = hashCal("SHA-256", rndm).substring(0, 20);
							amount = payment_amount_text.getText().toString().trim();
							productinfo = payment_categories_spinner.getSelectedItem().toString().trim();
							firstname = cardholder_text.getText().toString().trim();// loginid;
							email = cust_email;
							phone = cust_phone;
							surl = "www.guru3d.com";
							furl = "www.bing.com";
							curl = "www.fudzilla.com";
							salt = "dHzwOBnl";
							// // hash string
							tempe = key + "|" + txnid + "|" + amount + "|" + productinfo + "|" + firstname + "|" + email + "|||||||||||"
									+ salt;
							// // parameter values part 2
							pg = ptype;
							bankcode = bankcd;
							ccnum = cardno_text.getText().toString().trim();
							ccname = cardholder_text.getText().toString().trim();
							ccvv = cvvno_text.getText().toString().trim();
							ccexpmon = String.valueOf(months_id[expirymonth_spinner.getSelectedItemPosition()]);
							ccexpyr = expiryyear_spinner.getSelectedItem().toString();

							String querywithseam = "key=" + key + "&txnid=" + txnid + "&amount=" + amount + "&productinfo=" + productinfo
									+ "&firstname=" + firstname + "&email=" + email + "&phone=" + phone + "&surl=" + surl + "&furl=" + furl
									+ "&curl=" + curl + "&hash=" + hashCal("SHA-512", tempe) + "&pg=" + pg + "&bankcode=" + bankcode
									+ "&drop_category=DC,CC,NB" + "&ccnum=" + ccnum + "&ccname=" + ccname + "&ccvv=" + ccvv + "&ccexpmon="
									+ ccexpmon + "&ccexpyr=" + ccexpyr;

							web.getSettings().setJavaScriptEnabled(true);
							web.getSettings().setBuiltInZoomControls(true);
							web.getSettings().setRenderPriority(RenderPriority.HIGH);
							web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
							web.requestFocus(View.FOCUS_DOWN|View.FOCUS_UP);
							pay_flag = 0;
							web.postUrl("https://secure.payu.in/_payment", EncodingUtils.getBytes(querywithseam, "BASE64"));
							web.setWebViewClient(new WebViewClient()
							{
								@Override
								public void onPageStarted(WebView view, String url, Bitmap favicon)
								{
									pd.show();
								}

								@Override
								public boolean shouldOverrideUrlLoading(WebView view, String url)
								{
									try
									{
										view.loadUrl(url);
									}
									catch (Exception e)
									{
										Log.e("error", "error in shouldOverrideUrlLoading method\n" + e.toString());
									}
									return true;
								}

								@Override
								public void onPageFinished(WebView view, String url)
								{
									try
									{
										if (url.contains("mihpayid"))
										{
											murl = url;
											Log.e("mihpayid FOUND in pagefinish", url);
										}

										pd.dismiss();

										if (pay_flag == 0)
										{
											if (url.contains("www.guru3d.com"))
											{
												p_state = "SUCCESS";
												payment_dialog.dismiss();
												methodname = "transaction_main";
												new transactioncall_task().execute(methodname);
												pay_flag = 3;
											}
											else if (url.contains("www.bing.com"))
											{
												p_state = "FAILED";
												payment_dialog.dismiss();
												methodname = "transaction_main";
												new transactioncall_task().execute(methodname);
												pay_flag = 3;
											}
										}
										else
										{
											Log.e("secondtime", url);
										}
									}
									catch (Exception e)
									{
										Log.e("error", "error in page finish method\n" + e.toString());
									}
								}
							});
						}
						else
						{
							Toast.makeText(getActivity(), "Please fill the required fields!!", Toast.LENGTH_SHORT).show();
						}
					}
					catch (Exception e)
					{
						Log.e("error", "error in pay_button select\n" + e.toString());
					}
				}
			});
			payment_dialog.show();
		}
		catch (Exception e)
		{
			Log.e("crash", "error on other payments overall method" + e.toString());
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack())
		{
			web.goBack();
			return true;
		}
		return super.getActivity().onKeyDown(keyCode, event);
	}

	// ////////////// ASYNC TASK FOR SENDING DATA TO WEBSERVICE /////////

	class transactioncall_task extends AsyncTask<String, Integer, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(getActivity(), "", "Please wait...");
		}

		@Override
		protected String doInBackground(String... params)
		{

			if (params[0].toString().equals("transaction_main"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/transaction_method";
					String METHOD_NAME = "transaction_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo loginidprop = new PropertyInfo();
					loginidprop.setName("loginid");
					loginidprop.setValue(cust_loginid);
					loginidprop.setType(String.class);
					request.addProperty(loginidprop);

					PropertyInfo categoryprop = new PropertyInfo();
					categoryprop.setName("category");
					categoryprop.setValue(main_flag);
					categoryprop.setType(String.class);
					request.addProperty(categoryprop);
					// /////////////////////////

					PropertyInfo subcategoryprop = new PropertyInfo();
					subcategoryprop.setName("subcategory");
					String cate = "";
					if (payment_categories_spinner.getSelectedItemPosition() == 1)
					{
						cate = "Mobile/Datacard";
					}
					else
					{
						cate = "DTH";
					}
					subcategoryprop.setValue(cate);
					subcategoryprop.setType(String.class);
					request.addProperty(subcategoryprop);

					PropertyInfo circleprop = new PropertyInfo();
					circleprop.setName("circle");
					circleprop.setValue(circle_spinner.getSelectedItem().toString());
					circleprop.setType(String.class);
					request.addProperty(circleprop);

					PropertyInfo circle_idprop = new PropertyInfo();
					circle_idprop.setName("circle_id");
					circle_idprop.setValue(circle_ids[circle_spinner.getSelectedItemPosition()]);
					circle_idprop.setType(Integer.class);
					request.addProperty(circle_idprop);

					PropertyInfo operatorprop = new PropertyInfo();
					operatorprop.setName("operator");
					operatorprop.setValue(payment_vendors_spinner.getSelectedItem().toString());
					operatorprop.setType(String.class);
					request.addProperty(operatorprop);

					PropertyInfo operator_idprop = new PropertyInfo();
					operator_idprop.setName("operator_id");
					if (payment_categories_spinner.getSelectedItem().toString().equals("Mobile/Datacard Topup"))
					{
						operator_idprop.setValue(topups_vendor_ids[payment_vendors_spinner.getSelectedItemPosition()]);
					}
					else
					{
						operator_idprop.setValue(dth_vendor_ids[payment_vendors_spinner.getSelectedItemPosition()]);
					}
					operator_idprop.setType(Integer.class);
					request.addProperty(operator_idprop);

					PropertyInfo accountnoprop = new PropertyInfo();
					accountnoprop.setName("billno");
					accountnoprop.setValue(payment_billno_text.getText().toString().trim());
					accountnoprop.setType(String.class);
					request.addProperty(accountnoprop);

					PropertyInfo remarksprop = new PropertyInfo();
					remarksprop.setName("remarks");
					remarksprop.setValue(payment_remarks_text.getText().toString().trim());
					remarksprop.setType(String.class);
					request.addProperty(remarksprop);

					PropertyInfo charges_prop = new PropertyInfo();
					charges_prop.setName("additional_charges");
					charges_prop.setValue("0");
					charges_prop.setType(String.class);
					request.addProperty(charges_prop);

					PropertyInfo amountprop = new PropertyInfo();
					amountprop.setName("amount");
					amountprop.setValue(payment_amount_text.getText().toString().trim());
					amountprop.setType(String.class);
					request.addProperty(amountprop);

					PropertyInfo payment_type = new PropertyInfo();
					payment_type.setName("payment_type");
					payment_type.setValue(ptype);
					payment_type.setType(String.class);
					request.addProperty(payment_type);

					PropertyInfo payment_state = new PropertyInfo();
					payment_state.setName("payment_state");
					payment_state.setValue(p_state);
					payment_state.setType(String.class);
					request.addProperty(payment_state);

					PropertyInfo payment_through = new PropertyInfo();
					payment_through.setName("payment_through");
					String pmode = "";
					if (payment_modes_spinner.getSelectedItemPosition() == 1)
						pmode = "W";
					else if (payment_modes_spinner.getSelectedItemPosition() == 2)
						pmode = "CC";
					else if (payment_modes_spinner.getSelectedItemPosition() == 3)
						pmode = "DC";
					else if (payment_modes_spinner.getSelectedItemPosition() == 4)
						pmode = "NB";
					payment_through.setValue(pmode);
					payment_through.setType(String.class);
					request.addProperty(payment_through);

					PropertyInfo parameter1_prop = new PropertyInfo();
					parameter1_prop.setName("parameter1");
					parameter1_prop.setValue("null");
					parameter1_prop.setType(String.class);
					request.addProperty(parameter1_prop);

					try
					{
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(murl);
						String responseString = "";
						Log.e("inside", "pay reponse parsing");
						ResponseHandler<String> res = new BasicResponseHandler();
						responseString = httpclient.execute(httppost, res);
						Log.e("Entering jsoup", "1");
						Document doc = Jsoup.parse(responseString);
						Elements inputs = doc.select("input[type=hidden]");
						Log.e("Entering jsoup", "2");
						for (Element el : inputs)
						{
							if (el.attr("name").toString().equals("mihpayid"))
								payuid = el.attr("value");
							if (el.attr("name").toString().equals("unmappedstatus"))
								payu_status = el.attr("value");
						}
					}
					catch (Exception ex)
					{
						Log.e("error in fetching status from gateway", ex.toString());
					}

					PropertyInfo payuid_prop = new PropertyInfo();
					payuid_prop.setName("payuid");
					payuid_prop.setValue(payuid);
					payuid_prop.setType(String.class);
					request.addProperty(payuid_prop);

					PropertyInfo payu_status_prop = new PropertyInfo();
					payu_status_prop.setName("payu_status");
					payu_status_prop.setValue(payu_status);
					payu_status_prop.setType(String.class);
					request.addProperty(payu_status_prop);

					try
					{
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost("https://info.payu.in/merchant/postservice");
						try
						{
							Log.e("inside", "test bg");
							String key = "asjjZn";
							String salt = "dHzwOBnl";
							String command = "check_payment";
							String tempe = "";
							String var1 = payuid;
							tempe = key + "|" + command + "|" + var1 + "|" + salt;
							parameters.add(new BasicNameValuePair("key", key));
							parameters.add(new BasicNameValuePair("command", command));
							parameters.add(new BasicNameValuePair("hash", hashCal("SHA-512", tempe)));
							parameters.add(new BasicNameValuePair("var1", var1));

							httppost.setEntity(new UrlEncodedFormEntity(parameters));
							ResponseHandler<String> res = new BasicResponseHandler();
							String responseString = httpclient.execute(httppost, res);
							SerializedPhpParser serializedPhpParser = new SerializedPhpParser(responseString);
							serializedPhpParser.setAcceptedAttributeNameRegex("transaction_details|request_id");
							Object result = serializedPhpParser.parse();
							String all = result.toString().replace("{", "").replace("}", "");
							String data[] = all.split("=");
							req_id = data[2];
							Log.e("request_id is ", data[2]);
						}
						catch (ClientProtocolException e)
						{
							Log.e("Clientpexcept", e.toString());
						}
						catch (Exception e)
						{
							Log.e("IOexcep", e.toString());
						}
					}
					catch (Exception e)
					{
						Log.e("Error", "Error requesting id");
					}

					PropertyInfo payu_requestid_prop = new PropertyInfo();
					payu_requestid_prop.setName("payu_requestid");
					payu_requestid_prop.setValue(req_id);
					payu_requestid_prop.setType(String.class);
					request.addProperty(payu_requestid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					resultstatus = "transactioncall_executed";

				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "transactioncall_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "transactioncall_executed";
				}
			}
			else if (params[0].toString().equals("update_favorites"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/updatefavourites_method";
					String METHOD_NAME = "updatefavourites_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo txnidprop = new PropertyInfo();
					txnidprop.setName("txn_id");
					txnidprop.setValue(txn_id);
					txnidprop.setType(String.class);
					request.addProperty(txnidprop);

					PropertyInfo nameprop = new PropertyInfo();
					nameprop.setName("favourite_name");
					nameprop.setValue(favourite_name);
					nameprop.setType(String.class);
					request.addProperty(nameprop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					// outp = str;
					resultstatus = "update_favorites_executed";

				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "update_favorites_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "update_favorites_executed";
				}
			}
			else if (params[0].toString().equals("call_refund"))
			{

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("https://info.payu.in/merchant/postservice");
				try
				{
					Log.e("inside", "test bg");
					String key = "asjjZn";
					String salt = "dHzwOBnl";
					String command = "cancel_refund_transaction";
					String tempe = "";
					String var1 = payuid;
					String tok = req_id;
					String amt = payment_amount_text.getText().toString().trim();
					tempe = key + "|" + command + "|" + var1 + "|" + salt;
					parameters.add(new BasicNameValuePair("key", key));
					parameters.add(new BasicNameValuePair("command", command));
					parameters.add(new BasicNameValuePair("hash", hashCal("SHA-512", tempe)));
					parameters.add(new BasicNameValuePair("var1", var1));
					parameters.add(new BasicNameValuePair("var2", tok));
					parameters.add(new BasicNameValuePair("var3", amt));

					httppost.setEntity(new UrlEncodedFormEntity(parameters));
					ResponseHandler<String> res = new BasicResponseHandler();
					String responseString = httpclient.execute(httppost, res);
					SerializedPhpParser serializedPhpParser = new SerializedPhpParser(responseString);
					serializedPhpParser.setAcceptedAttributeNameRegex("request_id");
					Object result = serializedPhpParser.parse();
					String all = result.toString().replace("{", "").replace("}", "");
					Log.e("refund output", all);
					String data[] = all.split("=");
					Log.e("request_id is ", data[1]);

					String SOAP_ACTION = "http://services.ezeepay.com/callrefund_method";
					String METHOD_NAME = "callrefund_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo payuid_prop = new PropertyInfo();
					payuid_prop.setName("payuid");
					payuid_prop.setValue(payuid);
					payuid_prop.setType(String.class);
					request.addProperty(payuid_prop);

					PropertyInfo payu_requestid_prop = new PropertyInfo();
					payu_requestid_prop.setName("payu_refund_status_id");
					payu_requestid_prop.setValue(data[1]);
					payu_requestid_prop.setType(String.class);
					request.addProperty(payu_requestid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					resultstatus = "call_refund_executed";
				}
				catch (ClientProtocolException e)
				{
					Log.e("Clientpexcept", e.toString());
				}
				catch (Exception e)
				{
					Log.e("IOexcep", e.toString());
				}

				resultstatus = "call_refund_executed";
			}
			else if (params[0].toString().equals("fetchmobile_plans"))
			{

				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/fetchmobileplans_method";
					String METHOD_NAME = "fetchmobileplans_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo payuid_prop = new PropertyInfo();
					payuid_prop.setName("Operator");
					payuid_prop.setValue(payment_vendors_spinner.getSelectedItem().toString());
					payuid_prop.setType(String.class);
					request.addProperty(payuid_prop);

					PropertyInfo payu_requestid_prop = new PropertyInfo();
					payu_requestid_prop.setName("Circle");
					payu_requestid_prop.setValue(circle_spinner.getSelectedItem().toString());
					payu_requestid_prop.setType(String.class);
					request.addProperty(payu_requestid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					resultstatus = "fetchmobile_plans_executed";
				}
				catch (ClientProtocolException e)
				{
					Log.e("Clientpexcept", e.toString());
				}
				catch (Exception e)
				{
					Log.e("IOexcep", e.toString());
				}

				resultstatus = "fetchmobile_plans_executed";
			}

			//

			return resultstatus;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				super.onPostExecute(result);
				progress.dismiss();
				Log.e("reached", "post execute");
				if (result.equals("transactioncall_executed"))
				{
					payment_status(outp);
				}
				else if (resultstatus.equals("call_refund_executed"))
				{
					Toast.makeText(getActivity(), "Transaction amount will be refunded to your account shortly", Toast.LENGTH_SHORT).show();
				}
				else if (resultstatus.equals("update_favorites_executed"))
				{
					Toast.makeText(getActivity(), "Favourites added successfully", Toast.LENGTH_SHORT).show();
					getActivity().finish();
				}
				else if (resultstatus.equals("fetchmobile_plans_executed"))
				{

					JSONObject his_data = new JSONObject(outp);
					JSONArray txns = his_data.getJSONArray("plan_history");
					if (txns.length() > 0)
					{
						for (int i = 0; i < txns.length(); i++)
						{

							map_mobileplans = new HashMap<String, String>();
							JSONObject e = txns.getJSONObject(i);
							map_mobileplans.put("plantype", "Plan Type : " + e.getString("plantype"));
							map_mobileplans.put("rate", e.getString("rate"));
							map_mobileplans.put("description", e.getString("description"));
							map_mobileplans.put("validity", e.getString("validity"));
							mylist.add(map_mobileplans);

						}

						plans_adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_mobileplans, new String[]
						{ "plantype", "rate", "description", "validity" }, new int[]
						{ R.id.plantype_label, R.id.amount_label, R.id.description_label, R.id.validity_label });

						inflater = getActivity().getLayoutInflater();
						View pl = inflater.inflate(R.layout.mobileplans_layout, null);
						final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
						b.setView(pl);
						final Dialog plan_dialog = b.create();
						plans_listview = (ListView) pl.findViewById(R.id.mobileplans_listview);
						plans_listview.setAdapter(plans_adapter);
						plans_listview.setOnItemClickListener(new OnItemClickListener()
						{
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								Log.e("selected favourite", mylist.get(position).toString());
								payment_amount_text.setText(mylist.get(position).get("rate").toString()
										.replace("Amount : " + "\u20B9 ", ""));
								plan_dialog.dismiss();
							}
						});
						plan_dialog.show();
					}
					else
					{
						Toast.makeText(getActivity(), "Plans will be added soon for this operator,check back later", Toast.LENGTH_SHORT)
								.show();
					}

				}

			}
			catch (Exception e)
			{
				Log.e("error", "error on transaction post execute" + e);
				Toast.makeText(getActivity(), "Plans will be added soon for this operator,check back later", Toast.LENGTH_SHORT).show();
			}
		}
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

	public ArrayList<HashMap<String, String>> fetch_mobileplans(String Operator, String Circle)
	{
		// Log.e(Operator, Circle);
		//
		// DataBaseHelper myDbHelper = new DataBaseHelper(null);
		// myDbHelper = new DataBaseHelper(getActivity());
		// String locations[] =
		// { "" };
		//
		// try
		// {
		// myDbHelper.createDataBase();
		// }
		// catch (IOException ioe)
		// {
		// throw new Error("Unable to create database");
		// }
		//
		// try
		// {
		// myDbHelper.openDataBase();
		//
		// }
		// catch (SQLException sqle)
		// {
		//
		// Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
		// throw sqle;
		// }
		// SQLiteDatabase sampleDB = null;
		// try
		// {
		// sampleDB = getActivity().openOrCreateDatabase("mobilecodes_db", 0,
		// null);
		// Cursor c =
		// sampleDB.rawQuery("SELECT * FROM mobile_plans where operator_name='"
		// + Operator + "'and circle='" + Circle + "'",
		// null);
		// Log.e("cursor output", c.toString());
		// c.moveToFirst();
		// int i = 0;
		// locations = new String[c.getCount()];
		// Log.e("total plans", String.valueOf(c.getCount()));
		// while (c.moveToNext())
		// {
		// Log.e("rate", c.getString(c.getColumnIndex("rate")));
		// Log.e("description", c.getString(c.getColumnIndex("description")));
		// Log.e("validity", c.getString(c.getColumnIndex("validity")));
		// Log.e("i value", String.valueOf(i));
		// map_mobileplans = new HashMap<String, String>();
		// map_mobileplans.put("plantype", "Plan Type : " +
		// c.getString(c.getColumnIndex("plantype")));
		// map_mobileplans.put("rate", "Amount : " + "\u20B9 " +
		// c.getString(c.getColumnIndex("rate")));
		// map_mobileplans.put("description",
		// c.getString(c.getColumnIndex("description")));
		// map_mobileplans.put("validity", "Validity : " +
		// c.getString(c.getColumnIndex("validity")));
		// mylist.add(map_mobileplans);
		// i++;
		// }
		// // sampleDB.close();
		// }
		// catch (Exception se)
		// {
		// Log.e(getClass().getSimpleName(), "Error in fetching from database\n"
		// + se.toString());
		// }

		return mylist;

	}
}
