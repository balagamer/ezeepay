package com.ezeepay.services;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class History_Activity extends Activity
{
	connection_check check = new connection_check(this);
	int flag;
	String x, resultstatus = "", errmsg = "", methodname, outp, URL, res, data_type = "";
	String loginid, main_balance;
	ListView history_list;
	HashMap<String, String> map_historydata;
	String report_log, history_type = "";
	ListAdapter history_adapter;
	List<String> list = new ArrayList<String>();
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	Spinner historytype_spinner;
	String selected_txn_date, selected_txn_id, selected_txn_type, selected_dr_cr, selected_txn_amount, selected_txn_category,
			selected_account_details, selected_txn_status;
	String selected_fvname = "";

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
			setContentView(R.layout.history_layout);

			SharedPreferences prefs = this.getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			loginid = prefs.getString("loginid", "");
			main_balance = prefs.getString("balance", "");

			historytype_spinner = (Spinner) findViewById(R.id.historytype_spinner);
			history_list = (ListView) findViewById(R.id.history_listview);
			registerForContextMenu(history_list);

			list = new ArrayList<String>();
			list.add("Select Option.....");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_modified, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			historytype_spinner.setAdapter(dataAdapter);

			URL = selectwebservice.currentwebservice();

			list = new ArrayList<String>();
			list.add("All Transactions");
			list.add("Successfull Transactions");
			list.add("Failed Transactions");
			list.add("Pending Transactions");
			dataAdapter = new ArrayAdapter<String>(History_Activity.this, R.layout.simple_spinner_modified, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			historytype_spinner.setAdapter(dataAdapter);

			historytype_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
				{

					if (historytype_spinner.getSelectedItem().equals("All Transactions"))
					{
						if (check.isnetwork_available())
						{
							history_type = "all";
							methodname = "gethistory";
							new webservicecall_task().execute(methodname);
						}
						else
						{
							Toast.makeText(History_Activity.this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
						}

					}
					else if (historytype_spinner.getSelectedItem().equals("Successfull Transactions"))
					{
						history_type = "success";
						methodname = "gethistory";
						new webservicecall_task().execute(methodname);
						// reset_vendor_spinner();
					}
					else if (historytype_spinner.getSelectedItem().equals("Failed Transactions"))
					{
						history_type = "failure";
						methodname = "gethistory";
						new webservicecall_task().execute(methodname);
						// reset_vendor_spinner();
					}
					else if (historytype_spinner.getSelectedItem().equals("Pending Transactions"))
					{
						history_type = "pending";
						methodname = "gethistory";
						new webservicecall_task().execute(methodname);
						// reset_vendor_spinner();
					}
				}

				public void onNothingSelected(AdapterView<?> arg0)
				{
					Toast.makeText(History_Activity.this, "nothing selected", Toast.LENGTH_SHORT).show();
				}
			});

			history_list.setOnItemLongClickListener(new OnItemLongClickListener()
			{

				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
				{

					Log.e("selected favourite", mylist.get(position).toString());

					selected_txn_date = mylist.get(position).get("txn_date").toString();
					selected_txn_id = mylist.get(position).get("txn_id").toString();
					selected_txn_type = mylist.get(position).get("txn_type").toString();
					selected_dr_cr = mylist.get(position).get("dr_cr").toString();
					selected_txn_amount = mylist.get(position).get("cust_txn_amount").toString();
					selected_txn_category = mylist.get(position).get("txn_category").toString();
					selected_account_details = mylist.get(position).get("txn_account_details").toString();
					selected_txn_status = mylist.get(position).get("txn_status").toString();

					report_log = mylist.get(position).toString();

					if (check.isnetwork_available())
					{
						// methodname = "tripdetails";
						// new booktickets_task().execute(methodname);
					}
					else
					{
						Toast.makeText(History_Activity.this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
					}

					return false;
				}

			});
		}

		catch (Exception e)
		{
			Log.e("fatal", "fatal error in history activity");
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle("Select Action");
		menu.add(0, v.getId(), 0, "Add to Favourites").setIcon(R.drawable.favourites);
		// menu.add(0, v.getId(), 0, "Report");
		menu.add(0, v.getId(), 0, "Claim Dispute");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		try
		{
			// number=new
			// ContactListAdapter(this).numberList.get(info.position);
			if (item.getTitle() == "Add to Favourites")
			{

				final Builder dialog = new AlertDialog.Builder(History_Activity.this);
				dialog.setTitle("Transaction Name");
				// dialog.setMessage(R.string.url);
				final EditText fv = new EditText(this);
				fv.setHint("Enter a name for this transaction");
				dialog.setView(fv);
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface d, int which)
					{
						if (fv.getText().toString().equals(""))
						{
							Toast.makeText(History_Activity.this, "Please enter a valid name", Toast.LENGTH_SHORT).show();

						}
						else
						{
							selected_fvname = fv.getText().toString().trim();
							methodname = "update_favorites";
							new webservicecall_task().execute(methodname);
						}
					}
				});
				dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface d, int which)
					{
						Toast.makeText(History_Activity.this, "k dismiss", Toast.LENGTH_SHORT).show();
						// TODO Auto-generated method stub
						d.dismiss();
					}
				});
				dialog.create();
				dialog.show();

			}

			if (item.getTitle() == "Claim Dispute")
			{

				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				// i.setType("text/html");
				i.putExtra(Intent.EXTRA_EMAIL, new String[]
				{ "support@ezeepayservices.com" });
				i.putExtra(Intent.EXTRA_SUBJECT, "Report Transaction");

				String body = new String("Transaction ID : " + selected_txn_id + "\n" + "Transaction Date : " + selected_txn_date + "\n"
						+ "Transaction Amount : " + selected_txn_amount + "\n" + "Category : " + selected_txn_category + "\n"
						+ "Account Details : " + selected_account_details + "\n" + "Type : " + selected_txn_type + "\n" + "Debit/Credit : "
						+ selected_dr_cr + "\n" + "Status : " + selected_txn_status + "\n");

				i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
				try
				{
					startActivity(Intent.createChooser(i, "Send report..."));
				}
				catch (android.content.ActivityNotFoundException ex)
				{
					Toast.makeText(History_Activity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
				// Toast.makeText(History_Activity.this,
				// "Your report has been submitted, it will be resolved as early as possible ",
				// Toast.LENGTH_SHORT).show();

			}
			else if (item.getTitle() == "Claim Dispute")
			{
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				// smsIntent.putExtra("address", number);
				startActivity(smsIntent);
			}
			else
			{
				return false;
			}
			return true;
		}
		catch (Exception e)
		{
			return true;
		}
	}

	public void onsort_history_button_select(View v)
	{
		PopupMenu popup = new PopupMenu(this, v);
		popup.getMenu().add(0, 0, 0, "Txn id");
		popup.getMenu().add(0, 1, 1, "Amount");
		popup.getMenu().add(0, 2, 2, "Date");
		popup.getMenu().add(0, 3, 3, "Category");
		popup.show();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item)
			{

				if (item.getItemId() == 0)
				{

					history_adapter = new SimpleAdapter(History_Activity.this, mylist, R.layout.listview_history, new String[]
					{ "txn_date", "txn_id", "txn_type", "dr_cr", "cust_txn_amount", "txn_category", "txn_account_details", "txn_status" },
							new int[]
							{ R.id.txntime_label, R.id.txn_id_label, R.id.txn_type_label, R.id.availableseats_label, R.id.txn_amount_label,
									R.id.txn_category_label, R.id.ref_text, R.id.txn_status_label });

					history_list.setAdapter(history_adapter);

					Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
					{
						public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
						{
							return m1.get("txn_id").compareTo(m2.get("txn_id"));
						}
					};

					Collections.sort(mylist, mapComparator);

				}
				else if (item.getItemId() == 1)
				{

					history_adapter = new SimpleAdapter(History_Activity.this, mylist, R.layout.listview_history, new String[]
					{ "txn_date", "txn_id", "txn_type", "dr_cr", "cust_txn_amount", "txn_category", "txn_account_details", "txn_status" },
							new int[]
							{ R.id.txntime_label, R.id.txn_id_label, R.id.txn_type_label, R.id.availableseats_label, R.id.txn_amount_label,
									R.id.txn_category_label, R.id.ref_text, R.id.txn_status_label });

					history_list.setAdapter(history_adapter);

					Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
					{
						public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
						{
							if (Integer.parseInt(m2.get("cust_txn_amount")) > Integer.parseInt((m1.get("cust_txn_amount"))))
							{
								return 1;
							}
							else
							{
								return -1;
							}

						}
					};
					Collections.sort(mylist, mapComparator);

				}
				else if (item.getItemId() == 2)
				{

					history_adapter = new SimpleAdapter(History_Activity.this, mylist, R.layout.listview_history, new String[]
					{ "txn_date", "txn_id", "txn_type", "dr_cr", "cust_txn_amount", "txn_category", "txn_account_details", "txn_status" },
							new int[]
							{ R.id.txntime_label, R.id.txn_id_label, R.id.txn_type_label, R.id.availableseats_label, R.id.txn_amount_label,
									R.id.txn_category_label, R.id.ref_text, R.id.txn_status_label });

					history_list.setAdapter(history_adapter);

					Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
					{
						public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
						{
							return m1.get("txn_date").compareTo(m2.get("txn_date"));

						}
					};

					Collections.sort(mylist, mapComparator);

				}
				else if (item.getItemId() == 3)
				{
					history_adapter = new SimpleAdapter(History_Activity.this, mylist, R.layout.listview_history, new String[]
					{ "txn_date", "txn_id", "txn_type", "dr_cr", "cust_txn_amount", "txn_category", "txn_account_details", "txn_status" },
							new int[]
							{ R.id.txntime_label, R.id.txn_id_label, R.id.txn_type_label, R.id.availableseats_label, R.id.txn_amount_label,
									R.id.txn_category_label, R.id.ref_text, R.id.txn_status_label });

					history_list.setAdapter(history_adapter);

					Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
					{
						public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
						{
							return m1.get("txn_category").compareTo(m2.get("txn_category"));

						}
					};

					Collections.sort(mylist, mapComparator);

				}
				return true;
			}
		});
	}

	class webservicecall_task extends AsyncTask<String, Object, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(History_Activity.this, "", "Processing...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			if (params[0].toString().equals("gethistory"))
			{
				try
				{
					// sleep(5000);
					String SOAP_ACTION = "http://services.ezeepay.com/transactionhistory_method";
					String METHOD_NAME = "transactionhistory_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo unameProp = new PropertyInfo();

					unameProp.setName("loginid");
					unameProp.setValue(loginid);
					unameProp.setType(String.class);
					request.addProperty(unameProp);

					PropertyInfo typeProp = new PropertyInfo();
					typeProp.setName("history_type");
					typeProp.setValue(history_type);
					typeProp.setType(String.class);
					request.addProperty(typeProp);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();

					outp = str;
					flag = 0;

					resultstatus = "gethistory_executed";
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "gethistory_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "gethistory_executed";
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
					txnidprop.setValue(selected_txn_id);
					txnidprop.setType(String.class);
					request.addProperty(txnidprop);

					PropertyInfo nameprop = new PropertyInfo();
					nameprop.setName("favourite_name");
					nameprop.setValue(selected_fvname);
					nameprop.setType(String.class);
					request.addProperty(nameprop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
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
			mylist.clear();
			// Log.e("taggy", "enter post execute");
			try
			{
				super.onPostExecute(result);
				progress.dismiss();
				if (result.equals("gethistory_executed"))
				{
					// Log.e("taggy", "enter gethistory_executed");

					if (flag == 0)
					{
						JSONObject his_data = new JSONObject(outp);
						JSONArray txns = his_data.getJSONArray("transaction_history");
						for (int i = 0; i < txns.length(); i++)
						{
							map_historydata = new HashMap<String, String>();
							JSONObject e = txns.getJSONObject(i);
							map_historydata.put("txn_date", e.getString("txn_date"));
							map_historydata.put("txn_id", e.getString("txn_id"));
							map_historydata.put("txn_type", e.getString("from_service").toString());
							map_historydata.put("dr_cr", e.getString("dr_cr").toString());
							map_historydata.put("cust_txn_amount", e.getString("cust_txn_amount"));
							map_historydata.put("txn_category", e.getString("service_code").toString());
							map_historydata.put("txn_account_details", e.getString("txn_account_details").toString());
							map_historydata.put("txn_status", e.getString("txn_status").toString());

							mylist.add(map_historydata);
						}

						history_adapter = new SimpleAdapter(History_Activity.this, mylist, R.layout.listview_history,
								new String[]
								{ "txn_date", "txn_id", "txn_type", "dr_cr", "cust_txn_amount", "txn_category", "txn_account_details",
										"txn_status" }, new int[]
								{ R.id.txntime_label, R.id.txn_id_label, R.id.txn_type_label, R.id.availableseats_label,
										R.id.txn_amount_label, R.id.txn_category_label, R.id.ref_text, R.id.txn_status_label });

						history_list.setAdapter(history_adapter);

					}
					else if (flag == 1)
					{
						Toast.makeText(History_Activity.this, "No records found!\n", Toast.LENGTH_SHORT).show();
						Log.e("No records found!", errmsg);
					}
					else if (flag == 3)
					{
						Toast.makeText(History_Activity.this, "Conenction Timeout,Please retry later", Toast.LENGTH_SHORT).show();
						Log.e("connection timeout", errmsg);

					}
					else if (flag == 4)
					{
						Toast.makeText(History_Activity.this, "Unknown error,Please retry later", Toast.LENGTH_SHORT).show();
						Log.e("Unknown error", errmsg);
					}
					else
					{
						Toast.makeText(History_Activity.this,
								flag + "Login failed! Please check your credentials or contact admin for account activation",
								Toast.LENGTH_SHORT).show();
					}

				}

				else if (result.equals("update_favorites_executed"))
				{
					Toast.makeText(History_Activity.this, "Added to favourties successfully!", Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e)
			{
				Log.e("error", "error on history post execute");
				Toast.makeText(History_Activity.this, "No records found!\n", Toast.LENGTH_SHORT).show();
				Log.e("Exception in finding records", errmsg);
			}
		}
	}

}
