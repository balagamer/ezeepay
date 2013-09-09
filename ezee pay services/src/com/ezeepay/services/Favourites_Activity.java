package com.ezeepay.services;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Favourites_Activity extends Activity
{

	int flag;

	String selected_number, selected_category, selected_circle, selected_operator, selected_paymentmode, selected_amount, selected_txn_id;
	String x, resultstatus, errmsg, methodname, outp, URL, res, data_type = "";
	ListView favourites_list;
	HashMap<String, String> map_favouritesdata;
	ListAdapter favourites_adapter;
	List<String> list = new ArrayList<String>();
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	connection_check check = new connection_check(this);

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

			setContentView(R.layout.favourites_layout);

			URL = selectwebservice.currentwebservice();

			favourites_list = (ListView) findViewById(R.id.favourites_listview);
			registerForContextMenu(favourites_list);

			favourites_list.setOnItemLongClickListener(new OnItemLongClickListener()
			{

				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
				{

					Log.e("selected favourite", mylist.get(position).toString());

					selected_txn_id = mylist.get(position).get("txn_id").toString();
					selected_number = mylist.get(position).get("txn_account_details").toString();
					selected_category = mylist.get(position).get("txn_category").toString();
					selected_circle = mylist.get(position).get("location").toString();
					selected_operator = mylist.get(position).get("operator").toString();
					// selected_paymentmode =
					// mylist.get(position).get("cancellationPolicy").toString();
					selected_amount = mylist.get(position).get("cust_txn_amount").toString();

					if (check.isnetwork_available())
					{
						// methodname = "tripdetails";
						// new booktickets_task().execute(methodname);
					}
					else
					{
						Toast.makeText(Favourites_Activity.this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
					}
					return false;
				}

			});

			if (check.isnetwork_available())
			{
				methodname = "getfavourites";
				new webservicecall_task().execute(methodname);
			}
			else
			{
				Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
			}

		}
		catch (Exception e)
		{
			Log.e("fatal", "Fatal error in favourties main method" + e.toString());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle("Select Action");
		menu.add(0, 0, 0, "Execute Transaction");
		menu.add(0, 1, 0, "Remove from Favourites");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getItemId() == 0)
		{
			Intent intent1 = new Intent(Favourites_Activity.this, Makepayments_Activity.class);
			intent1.putExtra("from_favourites", "true");
			intent1.putExtra("selected_number", selected_number);
			intent1.putExtra("selected_category", selected_category);
			intent1.putExtra("selected_circle", selected_circle);
			intent1.putExtra("selected_operator", selected_operator);
			intent1.putExtra("selected_amount", selected_amount);
			startActivity(intent1);
			// Toast.makeText(getApplicationContext(), "execute 0",
			// Toast.LENGTH_SHORT).show();
		}

		if (item.getItemId() == 1)
		{
			if (check.isnetwork_available())
			{
				methodname = "remove_favourites";
				new webservicecall_task().execute(methodname);
			}
			else
			{
				Toast.makeText(this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	// @Override
	// public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3)
	// {
	// // TODO Auto-generated method stub
	// return false;
	// }

	class webservicecall_task extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Favourites_Activity.this, "", "Processing...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			if (params[0].toString().equals("getfavourites"))
			{
				try
				{
					// sleep(5000);
					String SOAP_ACTION = "http://services.ezeepay.com/favourites_method";
					String METHOD_NAME = "favourites_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo unameProp = new PropertyInfo();

					unameProp.setName("loginid");
					Intent i = getIntent();
					Bundle bundle = i.getExtras();
					unameProp.setValue(bundle.getString("username_intent"));
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();

					outp = str;
					flag = 0;

					resultstatus = "getfavourites_executed";
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "getfavourites_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "getfavourites_executed";
				}
			}
			else if (params[0].toString().equals("remove_favourites"))
			{
				resultstatus = "remove_favourites_executed";
				try
				{
					// sleep(5000);
					String SOAP_ACTION = "http://services.ezeepay.com/removefavourites_method";
					String METHOD_NAME = "removefavourites_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo txnidprop = new PropertyInfo();
					txnidprop.setName("txn_id");
					txnidprop.setValue(selected_txn_id);
					txnidprop.setType(String.class);
					request.addProperty(txnidprop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();

					outp = str;
					flag = 0;

					resultstatus = "remove_favourites_executed";
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "remove_favourites_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "remove_favourites_executed";
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
			Log.e("taggy", "enter post execute");
			try
			{
				super.onPostExecute(result);
				progress.dismiss();
				if (result.equals("getfavourites_executed"))
				{
					Log.e("taggy", "enter gethistory_executed");

					if (flag == 0)
					{
						// Toast.makeText(Favourites_Activity.this, "Favourites"
						// + outp, Toast.LENGTH_LONG).show();

						JSONObject his_data = new JSONObject(outp);

						if (his_data.length() == 0)
						{
							Toast.makeText(Favourites_Activity.this, "No Transaction found", Toast.LENGTH_SHORT).show();
						}
						else
						{
							JSONArray txns = his_data.getJSONArray("transaction_favourite");
							for (int i = 0; i < txns.length(); i++)
							{
								map_favouritesdata = new HashMap<String, String>();
								JSONObject e = txns.getJSONObject(i);
								map_favouritesdata.put("txn_date", e.getString("txn_date"));
								map_favouritesdata.put("txn_id", e.getString("txn_id"));
								map_favouritesdata.put("txn_type", e.getString("from_service").toString());
								map_favouritesdata.put("dr_cr", e.getString("dr_cr").toString());
								map_favouritesdata.put("cust_txn_amount", e.getString("cust_txn_amount"));
								map_favouritesdata.put("txn_category", e.getString("service_code").toString());
								map_favouritesdata.put("operator", e.getString("operator").toString());
								map_favouritesdata.put("txn_account_details", e.getString("txn_account_details").toString());
								map_favouritesdata.put("txn_status", e.getString("txn_status").toString());
								map_favouritesdata.put("favourite_name", e.getString("favourite_name").toString());
								map_favouritesdata.put("location", e.getString("location").toString());

								mylist.add(map_favouritesdata);
							}
						}

						favourites_adapter = new SimpleAdapter(Favourites_Activity.this, mylist, R.layout.listview_favourite, new String[]
						{ "txn_date", "favourite_name", "txn_type", "dr_cr", "cust_txn_amount", "txn_category", "txn_account_details",
								"operator" }, new int[]
						{ R.id.txntime_label, R.id.favourite_name_label, R.id.txn_type_label, R.id.availableseats_label,
								R.id.txn_amount_label, R.id.txn_category_label, R.id.ref_text, R.id.txn_operator_label });

						favourites_list.setAdapter(favourites_adapter);

					}
					else if (flag == 1)
					{
						Toast.makeText(Favourites_Activity.this, "No records found!\n" + errmsg, Toast.LENGTH_SHORT).show();
					}
					else if (flag == 3)
					{
						Toast.makeText(Favourites_Activity.this, "Conenction TImeout" + errmsg, Toast.LENGTH_SHORT).show();
					}
					else if (flag == 4)
					{
						Toast.makeText(Favourites_Activity.this, "Unknown error" + errmsg, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(Favourites_Activity.this,
								flag + "Login failed! Please check your credentials or contact admin for account activation",
								Toast.LENGTH_SHORT).show();
					}

				}
				else if (result.equals("remove_favourites_executed"))
				{
					Toast.makeText(Favourites_Activity.this, "Transaction removed from favourites!", Toast.LENGTH_SHORT).show();
				}

			}
			catch (Exception e)
			{
				Log.e("error", "error on history post execute");
				Toast.makeText(Favourites_Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
