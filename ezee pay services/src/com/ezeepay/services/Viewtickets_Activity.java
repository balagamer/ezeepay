package com.ezeepay.services;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Viewtickets_Activity extends Activity
{

	String loginid = "", main_balance = "", methodname = "", resultstatus = "", errmsg = "", URL, outp = "", selected_pickuptime = "",
			selected_travels = "", selected_doj = "", selected_pickup_location = "", selected_destination = "", selected_status = "",
			selected_cancellation = "", received_txn_id = "", status = "", status_reason = "", available_tin = "", selected_tin = "",
			tvl_names = "", tvl_seats = "", tvl_primary = "";
	int flag = 99, seatscount = 0;
	float total_fare = 0;
	JSONObject get_tin_response = new JSONObject();
	JSONArray get_tin_report = new JSONArray();

	JSONObject get_ticket_response = new JSONObject();
	JSONArray get_ticket_report = new JSONArray();

	ListAdapter tin_adapter;
	ListView tin_list;
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> map_tindata;
	String tinid[];
	String seat_name[];
	String final_names[];
	String finalnamesall = "";
	List<String> list = new ArrayList<String>();

	Spinner viewbookedtickets_spinner;
	connection_check check = new connection_check(this);

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
		setContentView(R.layout.viewtickets_layout);

		viewbookedtickets_spinner = (Spinner) findViewById(R.id.viewbookedtickets_spinner);
		tin_list = (ListView) findViewById(R.id.viewbookedtickets_listview);
		registerForContextMenu(tin_list);
		loginid = prefs.getString("loginid", "");
		main_balance = prefs.getString("balance", "");
		URL = selectwebservice.currentwebservice();
		if (check.isnetwork_available())
		{
			methodname = "get_tin_data";
			new viewtickets_task().execute(methodname);

		}
		else
		{
			Toast.makeText(Viewtickets_Activity.this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
		}

		viewbookedtickets_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
			{
				if (!(viewbookedtickets_spinner.getSelectedItemPosition() == 0))
				{
					Toast.makeText(Viewtickets_Activity.this, viewbookedtickets_spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT)
							.show();

					selected_tin = viewbookedtickets_spinner.getSelectedItem().toString();
					methodname = "get_ticket_data";
					new viewtickets_task().execute(methodname);
				}
				else
				{
					mylist.clear();
					tin_list.setAdapter(null);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0)
			{
				Toast.makeText(Viewtickets_Activity.this, "nothing selected", Toast.LENGTH_SHORT).show();

			}
		});

		tin_list.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{

				Log.e("selected favourite", mylist.get(position).toString());
				selected_travels = mylist.get(position).get("travels").toString();
				selected_doj = mylist.get(position).get("dojandtime").toString();
				selected_pickup_location = mylist.get(position).get("pickuplocationaddress").toString();
				selected_destination = mylist.get(position).get("destination").toString();
				selected_status = mylist.get(position).get("status").toString();
				selected_cancellation = mylist.get(position).get("partialcancellation").toString();
				Log.e(selected_travels + selected_doj + "time:" + selected_pickuptime, selected_pickup_location + selected_destination);
				if (check.isnetwork_available())
				{
					// methodname = "tripdetails";
					// new booktickets_task().execute(methodname);
				}
				else
				{
					Toast.makeText(Viewtickets_Activity.this, "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle("Select Action");
		if (selected_status.equals("BOOKED"))
		{
			menu.add(0, 0, 0, "Cancel Ticket");
			menu.add(0, 1, 0, "View Cancellation Policy");
			menu.add(0, 2, 0, "Add to Calendar");
		}
		else
		{

		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getItemId() == 0)
		{
			if (selected_cancellation.trim().equals("true"))
			{
				finalnamesall = "";
				final List<String> mSelectedItems = new ArrayList();
				final String seat_names[] = tvl_seats.split(",");
				final String final_names[] = new String[seatscount];
				Log.e("seatscount", String.valueOf(seatscount));
				final String primary_names[] = tvl_primary.split(",");
				Log.e("primary now ", tvl_primary);
				// Log.e("total ", primary_names[0] + primary_names[1] +
				// primary_names[2]);
				final AlertDialog.Builder builder = new AlertDialog.Builder(Viewtickets_Activity.this);
				builder.setTitle("Select the seat names to cancel")
						.setMultiChoiceItems(seat_names, null, new DialogInterface.OnMultiChoiceClickListener()
						{
							@Override
							public void onClick(final DialogInterface dialog, final int which, final boolean isChecked)
							{
								if (isChecked)
								{
									if (primary_names[which].trim().equals("true"))
									{
										final AlertDialog.Builder builder2 = new AlertDialog.Builder(Viewtickets_Activity.this);
										builder2.setTitle("Confirm Cancellation");
										builder2.setMessage("Cancelling the primary ticket will automatically cancel all the tickets,Are you sure?");
										builder2.setPositiveButton("Okay", new DialogInterface.OnClickListener()
										{
											public void onClick(DialogInterface dialog, int id)
											{
												// final_names[which] =
												// seat_names[which];
												mSelectedItems.add(seat_names[which]);
												dialog.dismiss();
											}
										});
										builder2.setNegativeButton("Uncheck", new DialogInterface.OnClickListener()
										{
											public void onClick(DialogInterface dialoginside, int id)
											{
												((AlertDialog) dialog).getListView().setItemChecked(which, false);
												dialoginside.dismiss();
											}
										});
										AlertDialog dialog2 = builder2.create();
										dialog2.show();
									}
									else
									{
										Log.e("seat names", seat_names[which]);
										// final_names[which] =
										// seat_names[which];
										mSelectedItems.add(seat_names[which]);
									}
								}
								else if (mSelectedItems.contains(seat_names[which]))
								{
									mSelectedItems.remove(seat_names[which]);
									// final_names[which] = "";
								}
							}
						}).setPositiveButton("Proceed", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								Log.e("selected data", mSelectedItems.toString());

								for (String s : mSelectedItems)
								{
									Log.e("loopin", s);
									if (finalnamesall.equals(""))
									{
										finalnamesall = s + "&&";
									}
									else
									{
										finalnamesall = finalnamesall + s + "&&";
									}
								}
								methodname = "cancel_ticket";
								new viewtickets_task().execute(methodname);
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
							}
						});

				builder.create();
				builder.show();
			}
			else
			{
				final AlertDialog.Builder builder3 = new AlertDialog.Builder(Viewtickets_Activity.this);
				builder3.setTitle("Confirm Cancellation");
				builder3.setMessage("Partial Cancellation not allowed for this bus,Cancel all the tickets?");
				builder3.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						finalnamesall = "";
						String[] m=tvl_seats.split(",");
						for(int k=0;k<m.length;k++)
						{
							if (finalnamesall.equals(""))
							{
								finalnamesall = m[k] + "&&";
							}
							else
							{
								finalnamesall = finalnamesall + m[k] + "&&";
							}
						}
						
						methodname = "cancel_ticket";
						new viewtickets_task().execute(methodname);
					}
				});
				builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialoginside, int id)
					{
						dialoginside.dismiss();
					}
				});
				AlertDialog dialog3 = builder3.create();
				dialog3.show();
			}
		}

		if (item.getItemId() == 1)
		{
			// Toast.makeText(getApplicationContext(), "execute 1",
			// Toast.LENGTH_SHORT).show();
		}
		else if (item.getItemId() == 2)
		{
			long calID = 1;
			long startMillis = 0;
			long endMillis = 0;
			Date d3 = null;
			String event_date = selected_doj + " " + selected_pickuptime;
			try
			{
				d3 = new SimpleDateFormat("E MMM dd yyyy HH:mm").parse(event_date);
				Log.e("actual date", d3.toString());
				String formatted_dob = new SimpleDateFormat("E MMM dd yyyy | HH:mm:ss ").format(d3);
			}
			catch (Exception e)
			{

			}
			Calendar beginTime = Calendar.getInstance();
			beginTime.setTime(d3);
			// beginTime.set(2013, 6, 12, 7, 30);
			startMillis = beginTime.getTimeInMillis();
			// Calendar endTime = Calendar.getInstance();
			// endTime.set(2013, 6, 12, 8, 45);
			// endMillis = endTime.getTimeInMillis();

			ContentResolver cr = getContentResolver();
			ContentValues values = new ContentValues();
			values.put(Events.DTSTART, startMillis);
			// values.put(Events.DTEND, endMillis);
			values.put(Events.DURATION, "P3600S");
			values.put(Events.TITLE, "Journey on" + selected_travels);
			values.put(Events.HAS_ALARM, 1);
			values.put(Events.DESCRIPTION, "Travel from " + selected_pickup_location + " to " + selected_destination);
			values.put(Events.CALENDAR_ID, calID);
			TimeZone timeZone = TimeZone.getDefault();
			values.put(Events.EVENT_TIMEZONE, timeZone.getID());
			Uri uri = cr.insert(Events.CONTENT_URI, values);
			long eventID = Long.parseLong(uri.getLastPathSegment());

			// add reminder
			ContentValues reminders = new ContentValues();
			reminders.put(Reminders.EVENT_ID, eventID);
			reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
			reminders.put(Reminders.MINUTES, 180);

			Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
			Toast.makeText(getBaseContext(), "Added to calendar successfully", Toast.LENGTH_SHORT).show();
		}
		else
		{
			return false;
		}
		return true;
	}

	public void reset_bookedtickets_spinner()
	{
		try
		{
			list = new ArrayList<String>();
			list.add("Select Ticket Number.....");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Viewtickets_Activity.this, R.layout.simple_spinner_modified, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			viewbookedtickets_spinner.setAdapter(dataAdapter);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on reset_subcat_spinner\n" + e.toString());
		}
	}

	class viewtickets_task extends AsyncTask<String, Integer, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(Viewtickets_Activity.this, "Fetching Data", "Please wait...");
		}

		@Override
		protected String doInBackground(String... params)
		{
			if (params[0].toString().equals("get_tin_data"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/gettin_method";
					String METHOD_NAME = "gettin_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo tripid_prop = new PropertyInfo();
					tripid_prop.setName("loginid");
					tripid_prop.setValue(loginid);
					tripid_prop.setType(String.class);
					request.addProperty(tripid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					get_tin_response = new JSONObject(outp);
					get_tin_report = get_tin_response.getJSONArray("tin_report");
					Log.e("response length   ", String.valueOf(get_tin_report.length()));
					JSONObject e = get_tin_report.getJSONObject(0);
					flag = Integer.parseInt(e.getString("flag"));
					Log.e("flag   ", String.valueOf(flag));
					status = e.getString("status");
					status_reason = e.getString("status_reason");

					JSONArray available_tin = e.getJSONArray("available_tin");
					tinid = new String[available_tin.length() + 1];

					for (int j = 0; j < available_tin.length(); j++)
					{
						JSONObject e1 = available_tin.getJSONObject(j);

						tinid[0] = "Select TIN";
						tinid[j + 1] = e1.getString("tin_id");

					}

					resultstatus = "get_tin_data_executed";
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "get_tin_data_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "get_tin_data_executed";
					Log.e("error on tin backgorung\n", e.toString());
				}

			}

			else if (params[0].toString().equals("get_ticket_data"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/viewtickets_method";
					String METHOD_NAME = "viewtickets_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo tripid_prop = new PropertyInfo();
					tripid_prop.setName("tin");
					tripid_prop.setValue(selected_tin);
					tripid_prop.setType(String.class);
					request.addProperty(tripid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					String dt[] = str.split("&&&");
					received_txn_id = dt[0];
					outp = dt[1];
					resultstatus = "get_ticket_data_executed";
					// flag = 1;
				}

				catch (Exception e)
				{
					Log.e("Exception occured in availabletrips_main ", e.toString());
					flag = 4;
					errmsg = e.toString();
					resultstatus = "get_ticket_data_executed";
				}
			}
			else if (params[0].toString().equals("cancel_ticket"))
			{
				try
				{

					String SOAP_ACTION = "http://services.ezeepay.com/cancelticket_method";
					String METHOD_NAME = "cancelticket_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo tinid_prop = new PropertyInfo();
					tinid_prop.setName("tin");
					tinid_prop.setValue(selected_tin);
					tinid_prop.setType(String.class);
					request.addProperty(tinid_prop);

					PropertyInfo seatnames_prop = new PropertyInfo();
					seatnames_prop.setName("seatnames");
					seatnames_prop.setValue(finalnamesall);
					seatnames_prop.setType(String.class);
					request.addProperty(seatnames_prop);

					PropertyInfo loginid_prop = new PropertyInfo();
					loginid_prop.setName("loginid");
					loginid_prop.setValue(loginid);
					loginid_prop.setType(String.class);
					request.addProperty(loginid_prop);

					PropertyInfo txn_id_prop = new PropertyInfo();
					txn_id_prop.setName("txn_id");
					txn_id_prop.setValue(received_txn_id);
					txn_id_prop.setType(String.class);
					request.addProperty(txn_id_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;

					resultstatus = "cancel_ticket_executed";
					// flag = 1;
				}

				catch (Exception e)
				{
					Log.e("Exception occured in availabletrips_main ", e.toString());
					flag = 4;
					errmsg = e.toString();
					resultstatus = "cancel_ticket_executed";
				}
			}

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
				if (resultstatus.equals("get_tin_data_executed"))
				{
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Viewtickets_Activity.this,
							R.layout.simple_spinner_modified, tinid);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// dataAdapter.createFromResource(this,i,
					// R.layout.spinneritems);
					viewbookedtickets_spinner.setAdapter(dataAdapter);
				}
				else if (resultstatus.equals("get_ticket_data_executed"))
				{
					mylist.clear();
					get_ticket_response = new JSONObject(outp);
					total_fare = 0;
					// get_ticket_report =
					// get_ticket_response.getJSONArray("tin_report");
					map_tindata = new HashMap<String, String>();
					Log.e("response length   ", String.valueOf(get_ticket_response.length()));
					Log.e("status   ", get_ticket_response.getString("status"));
					map_tindata.put("travels", get_ticket_response.getString("travels"));
					map_tindata.put("bustype", get_ticket_response.getString("busType"));
					map_tindata.put("pnr", get_ticket_response.getString("pnr"));
					Date d = new SimpleDateFormat("E MMM dd hh:mm:ss 'IST' yyyy").parse(get_ticket_response.getString("doj"));
					String formatted_doj = new SimpleDateFormat("E MMM dd yyyy").format(d);

					int t1 = Integer.parseInt(get_ticket_response.getString("pickupTime").toString());
					long hrs = TimeUnit.MINUTES.toHours(t1);
					long min = t1 - TimeUnit.HOURS.toMinutes(hrs);
					String pickuptime = String.format("%02d", hrs) + ":" + String.format("%02d", min);
					map_tindata.put("dojandtime", formatted_doj + " | " + pickuptime);
					d = new SimpleDateFormat("E MMM dd hh:mm:ss 'IST' yyyy").parse(get_ticket_response.getString("dateOfIssue"));
					String formatted_dob = new SimpleDateFormat("E MMM dd yyyy | HH:mm:ss ").format(d);
					map_tindata.put("dob", formatted_dob);
					map_tindata.put("pickuplocation&landmark", get_ticket_response.getString("pickupLocation") + " | "
							+ get_ticket_response.getString("pickupLocationLandmark"));
					map_tindata.put("pickuplocationaddress", get_ticket_response.getString("pickUpLocationAddress"));
					map_tindata.put("destination", fetch_name(get_ticket_response.getString("destinationCityId")));
					map_tindata.put("source", fetch_name(get_ticket_response.getString("sourceCityId")));
					map_tindata.put("status", get_ticket_response.getString("status"));
					map_tindata.put("partialcancellation", get_ticket_response.getString("partialCancellationAllowed"));

					JSONArray inventory_data = get_ticket_response.getJSONArray("inventoryItems");
					Log.e("inventory", inventory_data.toString());
					for (int j = 0; j < inventory_data.length(); j++)
					{
						JSONObject e1 = inventory_data.getJSONObject(j);
						Log.e("seatnames", e1.getString("seatName"));
						if (j == 0)
						{
							tvl_seats = e1.getString("seatName");
						}
						else
						{
							tvl_seats = tvl_seats + "," + e1.getString("seatName");
						}
						JSONObject e3 = e1.getJSONObject("passenger");
						if (j == 0)
						{
							tvl_names = e3.getString("name");
							tvl_primary = e3.getString("primary");

						}
						else
						{
							tvl_names = tvl_names + " , " + e3.getString("name");
							tvl_primary = tvl_primary + " , " + e3.getString("primary");
						}
						seatscount = j;
						total_fare = total_fare + Float.parseFloat(e1.getString("fare"));
					}
					map_tindata.put("totalfare", "\u20B9 " + String.valueOf(total_fare));
					map_tindata.put("seatnames", tvl_seats);
					map_tindata.put("psnnames", tvl_names);
					mylist.add(map_tindata);

					tin_adapter = new SimpleAdapter(Viewtickets_Activity.this, mylist, R.layout.listview_viewtickets, new String[]
					{ "travels", "bustype", "pnr", "dojandtime", "dob", "pickuplocationaddress", "source", "pickuplocation&landmark",
							"destination", "totalfare", "status", "seatnames", "psnnames" }, new int[]
					{ R.id.travels_label, R.id.bustype_text, R.id.pnr_text, R.id.doj_text, R.id.dob_text, R.id.pickuplocation_text,
							R.id.pickup_address_text, R.id.pickuplandmark_text, R.id.destination_text, R.id.totalfare_text,
							R.id.status_text, R.id.seatnames_text, R.id.pnames_text });

					tin_list.setAdapter(tin_adapter);
				}
				else if (resultstatus.equals("cancel_ticket_executed"))
				{
					Toast.makeText(Viewtickets_Activity.this, outp, Toast.LENGTH_SHORT).show();
					mylist.clear();
					tin_list.setAdapter(null);
				}
				else
				{
					Toast.makeText(Viewtickets_Activity.this, status + " " + status_reason, Toast.LENGTH_SHORT).show();
					mylist.clear();
					tin_list.setAdapter(null);
					viewbookedtickets_spinner.setSelection(0);
				}
			}
			catch (Exception e)
			{
				Log.e("error", "error on transaction post execute\n" + e);
				Toast.makeText(Viewtickets_Activity.this, "No Tickets Found", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public String fetch_name(String destination_id)
	{
		Log.d("name to be fetched", destination_id);
		DataBaseHelper myDbHelper = new DataBaseHelper(null);
		myDbHelper = new DataBaseHelper(Viewtickets_Activity.this);
		String destination_name = "";

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

			Toast.makeText(Viewtickets_Activity.this, "error", Toast.LENGTH_SHORT).show();
			throw sqle;
		}

		SQLiteDatabase sampleDB = null;

		try
		{
			sampleDB = Viewtickets_Activity.this.openOrCreateDatabase("mobilecodes_db", 0, null);
			Cursor c = sampleDB.rawQuery("SELECT name FROM all_sources where _id=" + destination_id, null);
			Log.e("cursor output", c.toString());
			c.moveToFirst();
			if (c.moveToFirst())
			{
				Log.e("inside cursor output", c.getString(0));
				destination_name = c.getString(c.getColumnIndex("name"));

				sampleDB.close();

			}
			else
			{
				destination_name = "notfound";
				Log.e("error", "Destination name not found in the database!");
			}
		}

		catch (Exception se)
		{
			Log.e(getClass().getSimpleName(), "Error in fetching from database\n" + se.toString());
		}

		return destination_name;
	}

}
