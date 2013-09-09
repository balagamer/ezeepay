package com.ezeepay.services;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_bustickets extends Fragment implements OnClickListener
{

	connection_check check;
	View tickets_view;
	List<String> list;

	JSONObject trips_response = new JSONObject();
	JSONArray trips_report = new JSONArray();
	private String loginid, main_balance, URL, available_trips, data_type = "", outp, allsource, status = "", status_reason = "",
			allboarding = "", alldrop = "", from_id, to_id, selected_tripid, selected_bus, selected_cpolicy, selected_callowed,
			selected_bustype, errmsg, resultstatus, methodname, result, dateval;

	long[] trip_id = new long[500];
	String[] bus_details = new String[500];
	private int boarding_locations_count, drop_locations_count, total_boardpoints = 0, mYear, mMonth, mDay;;

	private String[] boarding_list, drop_list, bus_type;

	LinearLayout panel_layout;

	ListAdapter bus_adapter;
	private int flag, dd = 0, mm, yy;

	ListView tickets_listview;

	private HashMap<String, String> map_busdata, map_boardinglocation, map_droplocation;
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

	private Spinner tickets_spinner, seats_spinner;
	private Button proceed_tickets_button, filter_button, datepicker_button;
	private ImageButton options_imagebutton, showpanel_imagebutton;
	private AutoCompleteTextView destination_from_text, destination_to_text;
	private TextView date_text;
	private RelativeLayout panel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		tickets_view = inflater.inflate(R.layout.fragment_bustickets, container, false);
		try
		{
			SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
			loginid = prefs.getString("loginid", "");
			main_balance = prefs.getString("balance", "");

			check = new connection_check(getActivity());

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,
					fetch_locations());

			// intitialize buttons and others

			panel_layout = (LinearLayout) tickets_view.findViewById(R.id.panel_layout);
			panel_layout.setVisibility(View.GONE);

			panel = (RelativeLayout) tickets_view.findViewById(R.id.panel);

			destination_from_text = (AutoCompleteTextView) tickets_view.findViewById(R.id.destination_from_text);
			destination_to_text = (AutoCompleteTextView) tickets_view.findViewById(R.id.destination_to_text);
			datepicker_button = (Button) tickets_view.findViewById(R.id.datepicker_button);
			datepicker_button.setOnClickListener(this);

			proceed_tickets_button = (Button) tickets_view.findViewById(R.id.proceed_tickets_button);
			proceed_tickets_button.setOnClickListener(this);

			options_imagebutton = (ImageButton) tickets_view.findViewById(R.id.options_imagebutton);
			options_imagebutton.setOnClickListener(this);
			showpanel_imagebutton = (ImageButton) tickets_view.findViewById(R.id.showpanel_imagebutton);
			showpanel_imagebutton.setOnClickListener(this);

			filter_button = (Button) tickets_view.findViewById(R.id.filter_button);
			filter_button.setOnClickListener(this);

			tickets_listview = (ListView) tickets_view.findViewById(R.id.tickets_listview);

			URL = selectwebservice.currentwebservice();

			destination_from_text.setAdapter(adapter);
			destination_to_text.setAdapter(adapter);

			tickets_listview.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{

					Log.e("selected trip", mylist.get(position).toString());

					selected_tripid = mylist.get(position).get("tripid").toString();
					selected_bus = mylist.get(position).get("travels").toString();
					selected_bustype = mylist.get(position).get("bustype").toString();
					boarding_list = new String[boarding_locations_count];
					drop_list = new String[drop_locations_count];
					selected_cpolicy = mylist.get(position).get("cancellationPolicy").toString();
					selected_callowed = mylist.get(position).get("partialCancellationAllowed").toString();
					boarding_list = mylist.get(position).get("boardinglocations").toString().split("&&");
					drop_list = mylist.get(position).get("droplocations").toString().split("&&");
					// Toast.makeText(
					// getActivity(),
					// "Bus ID\n" + selected_tripid + "\nBus Name\n" +
					// selected_bus + "\nBoarding points is\n"
					// + String.valueOf(boarding_list.length) + "\nBusType\n" +
					// selected_bustype, Toast.LENGTH_SHORT).show();

					if (check.isnetwork_available())
					{
						methodname = "tripdetails";
						new booktickets_task().execute(methodname);
					}
					else
					{
						Toast.makeText(getActivity(), "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
					}

				}
			});
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on fragment_tickets on create\n" + e.toString());
		}

		return tickets_view;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try
		{
			switch (requestCode)
			{
			case 1:
			{
				Bundle bundle = data.getExtras();
				datepicker_button.setText(bundle.getString("dateSelected"));
				break;
			}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on onActivityResult\n" + e.toString());
		}
	}

	public void reset_seats_spinner()
	{
		try
		{
			list = new ArrayList<String>();
			list.add("No Of Seats");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_modified, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			seats_spinner.setAdapter(dataAdapter);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on reset_circle_spinner\n" + e.toString());
		}
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.proceed_tickets_button:

			{
				if (destination_from_text.getText().toString().equals("") || destination_to_text.getText().toString().equals("")
						|| datepicker_button.getText().toString().equals("Select Date"))
				{
					Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if (check.isnetwork_available())
					{
						methodname = "availabletrips_main";
						new booktickets_task().execute(methodname);
					}
					else
					{
						Toast.makeText(getActivity(), "Please check your internet connectivity!", Toast.LENGTH_SHORT).show();
					}

				}
				break;
			}
			case R.id.datepicker_button:
			{
				// try
				// {
				// LayoutInflater inflater = getActivity().getLayoutInflater();
				// View checkboxLayout =
				// inflater.inflate(R.layout.calendar_layout, null);
				//
				// final AlertDialog.Builder b = new
				// AlertDialog.Builder(getActivity());
				// b.setTitle("Select travel date");
				// b.setView(checkboxLayout);
				// b.setNegativeButton("CLOSE", null);
				// final Dialog date_dialog = b.create();
				// Calendar cal = Calendar.getInstance();
				// CalendarView cv = (CalendarView)
				// checkboxLayout.findViewById(R.id.calender_view);
				// cv.setMinDate(cal.getTimeInMillis() - 1000);
				// cv.setOnDateChangeListener(new
				// CalendarView.OnDateChangeListener()
				// {
				// public void onSelectedDayChange(CalendarView view, int year,
				// int monthOfYear, int dayOfMonth)
				// {
				// mYear = year;
				// mMonth = monthOfYear;
				// mDay = dayOfMonth;
				//
				// DateFormat dateFormat = new
				// SimpleDateFormat("yyyy-MM-dd,HH:mm");
				// Date trans_date = new Date(view.getDate());
				// datepicker_button.setTag(dateFormat.format(trans_date));
				// Log.e("tagged date", dateFormat.format(trans_date));
				// String selectedDate = new
				// StringBuilder().append(mYear).append("-").append(mMonth +
				// 1).append("-").append(mDay)
				// .append(" ").toString();
				// datepicker_button.setText(selectedDate);
				// date_dialog.dismiss();
				// }
				// });
				// date_dialog.show();
				// }
				// catch (Exception e)
				// {
				// Log.e("crash", "error on callender view");
				// }
				try
				{
					LayoutInflater inflater = getActivity().getLayoutInflater();
					View checkboxLayout = inflater.inflate(R.layout.calendar_layout, null);

					final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
					b.setTitle("Select date");
					b.setView(checkboxLayout);
					b.setNegativeButton("CLOSE", null);
					final Dialog date_dialog = b.create();
					@SuppressWarnings("deprecation")
					Date min = new Date(2013 - 1900, 4, 21);
					Calendar cal = Calendar.getInstance();
					CalendarView cv = (CalendarView) checkboxLayout.findViewById(R.id.calender_view);
					// cv.setMinDate(cal.getTimeInMillis() - 1000);

					cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
					{
						public void onSelectedDayChange(CalendarView view, int year, int monthOfYear, int dayOfMonth)
						{
							mYear = year;
							mMonth = monthOfYear;
							mDay = dayOfMonth;

							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
							Date trans_date = new Date(view.getDate());
							// mynumberalerts_text.setTag(dateFormat.format(trans_date));
							// Log.e("tagged date",
							// dateFormat.format(trans_date));
							String selectedDate = new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay)
									.append(" ").toString();
							datepicker_button.setText(selectedDate);
							// date_dialog.dismiss();
						}
					});

					date_dialog.show();
				}
				catch (Exception e)
				{
					Log.e("crash", "error on callender view");
				}
				break;
			}
			case R.id.options_imagebutton:
			{
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenu().add(0, 0, 0, "My favourite");
				// popup.getMenu().add(0, 1, 1, "from saved");
				// popup.getMenu().add(0, 2, 2, "others");
				// popup.getMenu().add(0, 3, 3, "tbd");
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

							// destination_from_text.setText("Coimbatore");
							// destination_to_text.setText("Madurai");
							String from = default_prefs.getString("pref_source", "");
							String to = default_prefs.getString("pref_destination", "");
							if (from.equals("") || to.equals(""))
							{
								Toast.makeText(getActivity(), "No data found in QuickPay preferences", Toast.LENGTH_SHORT).show();
							}
							else
							{
								destination_from_text.setText(from);
								destination_to_text.setText(to);
							}
						}
						else if (item.getItemId() == 1)
						{

						}
						else if (item.getItemId() == 2)
						{

						}
						else if (item.getItemId() == 3)
						{

						}
						return true;
					}
				});
				break;
			}
			case R.id.filter_button:
			{
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenu().add(0, 0, 0, "fare");
				popup.getMenu().add(0, 1, 1, "name");
				popup.getMenu().add(0, 2, 2, "available seats");
				popup.getMenu().add(0, 3, 3, "running time");
				popup.show();

				final SharedPreferences prefs = getActivity().getSharedPreferences("com.ezeepay.service", Context.MODE_PRIVATE);
				final SharedPreferences default_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
				{

					@Override
					public boolean onMenuItemClick(MenuItem item)
					{
						try
						{
							if (item.getItemId() == 0)
							{
								bus_adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_tickets, new String[]
								{ "travels", "traveltime", "bustype", "availableseats", "boardingTimes", "droppingTimes", "ticketfare",
										"runtime" }, new int[]
								{ R.id.flightname_label, R.id.traveltime_label, R.id.flighttype_label, R.id.availableseats_label,
										R.id.boardpoint_label, R.id.droppoint_label, R.id.ticketfare_label, R.id.runtime_label });

								tickets_listview.setAdapter(bus_adapter);

								Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
								{
									public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
									{
										return m1.get("ticketfare").compareTo(m2.get("ticketfare"));
									}
								};
								Collections.sort(mylist, mapComparator);

							}
							else if (item.getItemId() == 1)
							{
								bus_adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_tickets, new String[]
								{ "travels", "traveltime", "bustype", "availableseats", "boardingTimes", "droppingTimes", "ticketfare",
										"runtime" }, new int[]
								{ R.id.flightname_label, R.id.traveltime_label, R.id.flighttype_label, R.id.availableseats_label,
										R.id.boardpoint_label, R.id.droppoint_label, R.id.ticketfare_label, R.id.runtime_label });

								tickets_listview.setAdapter(bus_adapter);

								Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
								{
									public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
									{
										return m1.get("travels").compareTo(m2.get("travels"));
									}
								};
								Collections.sort(mylist, mapComparator);

							}
							else if (item.getItemId() == 2)
							{
								bus_adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_tickets, new String[]
								{ "travels", "traveltime", "bustype", "availableseats", "boardingTimes", "droppingTimes", "ticketfare",
										"runtime" }, new int[]
								{ R.id.flightname_label, R.id.traveltime_label, R.id.flighttype_label, R.id.availableseats_label,
										R.id.boardpoint_label, R.id.droppoint_label, R.id.ticketfare_label, R.id.runtime_label });

								tickets_listview.setAdapter(bus_adapter);

								Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
								{
									public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
									{
										if (Integer.parseInt(m2.get("availableseats")) > Integer.parseInt((m1.get("availableseats"))))
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
							else if (item.getItemId() == 3)
							{
								bus_adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_tickets, new String[]
								{ "travels", "traveltime", "bustype", "availableseats", "boardingTimes", "droppingTimes", "ticketfare",
										"runtime" }, new int[]
								{ R.id.flightname_label, R.id.traveltime_label, R.id.flighttype_label, R.id.availableseats_label,
										R.id.boardpoint_label, R.id.droppoint_label, R.id.ticketfare_label, R.id.runtime_label });

								tickets_listview.setAdapter(bus_adapter);

								Comparator<HashMap<String, String>> mapComparator = new Comparator<HashMap<String, String>>()
								{
									public int compare(HashMap<String, String> m1, HashMap<String, String> m2)
									{
										return m1.get("runtime").compareTo(m2.get("runtime"));
									}
								};

								Collections.sort(mylist, mapComparator);

							}
						}
						catch (Exception e)
						{
							Log.e("fatal", "Error in sort method");
						}

						return true;
					}

				});
				break;

			}
			case R.id.showpanel_imagebutton:
			{
				panel_layout.setVisibility(View.GONE);
				panel.setVisibility(View.VISIBLE);
				break;
			}
			}
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on onClick\n" + e.toString());
		}
	}

	class booktickets_task extends AsyncTask<String, Integer, String>
	{
		ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progress = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...");
		}

		@Override
		protected String doInBackground(String... params)
		{

			if (params[0].toString().equals("availabletrips_main"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/availabletrips_method";
					String METHOD_NAME = "availabletrips_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo from_prop = new PropertyInfo();
					from_prop.setName("from");
					from_prop.setValue(destination_from_text.getText().toString().trim());
					from_prop.setType(String.class);
					request.addProperty(from_prop);

					PropertyInfo to_prop = new PropertyInfo();
					to_prop.setName("to");
					to_prop.setValue(destination_to_text.getText().toString().trim());
					to_prop.setType(String.class);
					request.addProperty(to_prop);

					PropertyInfo date_prop = new PropertyInfo();
					date_prop.setName("date");
					date_prop.setValue(datepicker_button.getText().toString().trim());
					date_prop.setType(String.class);
					request.addProperty(date_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;
					String str = response.getPropertyAsString(0).toString();
					outp = str;
					trips_response = new JSONObject(outp);
					trips_report = trips_response.getJSONArray("trips_report");
					Log.e("report length   ", String.valueOf(trips_report.length()));
					JSONObject e = trips_report.getJSONObject(0);
					flag = Integer.parseInt(e.getString("flag"));
					Log.e("flag   ", String.valueOf(flag));
					status = e.getString("status");
					status_reason = e.getString("status_reason");
					available_trips = e.getString("available_trips");
					data_type = "availabletrips";
					from_id = e.getString("from_id");
					to_id = e.getString("to_id");
					allsource = e.getString("all_sources");

					DataBaseHelper myDbHelper = new DataBaseHelper(null);
					myDbHelper = new DataBaseHelper(getActivity());

					try
					{
						myDbHelper.createDataBase();
					}
					catch (IOException ioe)
					{
						Log.e("Unable to create database", ioe.toString());
					}
					try
					{
						myDbHelper.openDataBase();

					}
					catch (SQLException sqle)
					{

						Log.e("error openenig database", sqle.toString());

					}

					SQLiteDatabase sampleDB = null;

					try
					{
						sampleDB = getActivity().openOrCreateDatabase("mobilecodes_db", 0, null);

						JSONObject jObject = new JSONObject(allsource);
						JSONArray cities = jObject.getJSONArray("cities");
						for (int i = 0; i < cities.length(); i++)
						{
							ContentValues map = new ContentValues();
							JSONObject et = cities.getJSONObject(i);
							// map.put("id", String.valueOf(i));
							map.put("_id", et.getString("id"));
							map.put("name", et.getString("name"));

							sampleDB.insert("all_sources", null, map);
						}
						Log.e("database", "record insert success");
					}
					catch (Exception se)
					{
						Log.e(getClass().getSimpleName(), "Could not create or Open the database");
					}

					resultstatus = "availabletrips_executed";
					// flag = 1;
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "availabletrips_executed";
				}
				catch (Exception e)
				{
					Log.e("Exception occured in availabletrips_main ", e.toString());
					flag = 4;
					errmsg = e.toString();
					resultstatus = "availabletrips_executed";
				}
			}

			else if (params[0].toString().equals("tripdetails"))
			{
				try
				{
					String SOAP_ACTION = "http://services.ezeepay.com/tripdetails_method";
					String METHOD_NAME = "tripdetails_method";
					String NAMESPACE = "http://services.ezeepay.com";

					allowAllSSL.allowAllSSL();
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					PropertyInfo tripid_prop = new PropertyInfo();
					tripid_prop.setName("tripid");
					tripid_prop.setValue(selected_tripid);
					tripid_prop.setType(String.class);
					request.addProperty(tripid_prop);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);

					ht.call(SOAP_ACTION, envelope);

					SoapObject response = (SoapObject) envelope.bodyIn;

					String tempdata[];
					String tem;
					tem = response.getPropertyAsString(0).toString();
					tempdata = tem.split("&&&");
					available_trips = tempdata[0];
					outp = available_trips;
					data_type = tempdata[1];
					// flag = 1;
					resultstatus = "tripdetails_executed";
				}

				catch (SocketTimeoutException e)
				{
					flag = 3;
					errmsg = e.toString();
					resultstatus = "tripdetails_executed";
				}
				catch (Exception e)
				{
					flag = 4;
					errmsg = e.toString();
					resultstatus = "tripdetails_executed";
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
				if (result.equals("availabletrips_executed"))
				{
					Log.e("flag_value ", String.valueOf(flag));
					Log.e("result is ", result);
					Log.e("datatype is ", data_type);

					try
					{
						if (flag == 0)
						{
							if (data_type.equals("allcities"))
							{
								JSONObject jObject = new JSONObject(available_trips);
								JSONArray cities = jObject.getJSONArray("cities");
								for (int i = 0; i < cities.length(); i++)
								{
									HashMap<String, String> map = new HashMap<String, String>();
									JSONObject e = cities.getJSONObject(i);
									map.put("id", String.valueOf(i));
									map.put("name", "City ID:" + e.getString("id"));
									map.put("magnitude", "City name: " + e.getString("name"));
									mylist.add(map);
								}
								ListAdapter adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_tickets, new String[]
								{ "name", "magnitude" }, new int[]
								{ R.id.flightname_label, R.id.flighttype_label });

								// bus_list.setAdapter(adapter);
							}
							else if (data_type.equals("availabletrips"))
							{
								Log.e("inside availabletrips ", "1");
								tickets_listview.setAdapter(null);
								mylist.clear();
								JSONObject ticket_data = new JSONObject(available_trips);
								JSONArray trips = ticket_data.getJSONArray("availableTrips");
								bus_type = new String[trips.length()];
								for (int i = 0; i < trips.length(); i++)
								{
									map_busdata = new HashMap<String, String>();
									JSONObject e = trips.getJSONObject(i);
									map_busdata.put("tripid", e.getString("id"));
									map_busdata.put("travels", e.getString("travels"));
									int t1 = Integer.parseInt(e.getString("departureTime"));
									long hrs = TimeUnit.MINUTES.toHours(t1);
									long min = t1 - TimeUnit.HOURS.toMinutes(hrs);
									String deptime = String.format("%02d", hrs) + ":" + String.format("%02d", min);
									int t2 = Integer.parseInt(e.getString("arrivalTime"));
									int val1 = (t2 % (24 * 60)) / 60;
									int val2 = (t2 % (24 * 60)) % 60;
									String arrtime = String.format("%02d", val1) + ":" + String.format("%02d", val2);
									int rtime = t2 - t1;
									int val11 = (rtime % (24 * 60)) / 60;
									int val22 = (rtime % (24 * 60)) % 60;
									String runtime = String.format("%01d", val11) + ":" + String.format("%02d", val22);
									// DecimalFormat df = new
									// DecimalFormat("##.##");
									map_busdata.put("runtime", String.valueOf(runtime));
									map_busdata.put("traveltime", deptime + "-" + arrtime);
									map_busdata.put("bustype", e.getString("busType"));
									map_busdata.put("availableseats", e.getString("availableSeats"));
									map_busdata.put("ticketfare", e.getString("fares").replaceAll("\\[", "").replaceAll("\\]", ""));
									map_busdata.put("cancellationPolicy", e.getString("cancellationPolicy"));
									map_busdata.put("partialCancellationAllowed", e.getString("partialCancellationAllowed"));
									JSONArray board_points = e.getJSONArray("boardingTimes");
									allboarding = "";
									boarding_locations_count = board_points.length();
									for (int j = 0; j < board_points.length(); j++)
									{
										JSONObject e1 = board_points.getJSONObject(j);
										map_busdata.put("boardingTimes", "Start Point : " + e1.getString("location"));
										int bp1 = Integer.parseInt(e1.getString("time"));
										long bp_hrs = TimeUnit.MINUTES.toHours(bp1);
										long bp_min = bp1 - TimeUnit.HOURS.toMinutes(bp_hrs);
										String bp_time = String.format("%02d", bp_hrs) + ":" + String.format("%02d", bp_min);
										if (allboarding.equals(""))
										{
											allboarding = e1.getString("location") + " (" + bp_time + ") " + "@@" + e1.getString("bpId");
										}
										else
										{
											allboarding = allboarding + "&&" + e1.getString("location") + " (" + bp_time + ") " + "@@"
													+ e1.getString("bpId");
										}
									}
									map_busdata.put("boardinglocations", allboarding);

									try
									{
										JSONArray drop_points = e.getJSONArray("droppingTimes");
										alldrop = "";
										drop_locations_count = drop_points.length();
										for (int k = 0; k < drop_points.length(); k++)
										{
											JSONObject e2 = drop_points.getJSONObject(k);

											map_busdata.put("droppingTimes", "End Point : " + e2.getString("location"));

											if (alldrop.equals(""))
											{
												alldrop = e2.getString("location");
											}
											else
											{
												alldrop = alldrop + "&&" + e2.getString("location");
											}
										}
										map_busdata.put("droplocations", alldrop);
									}
									catch (Exception ed)
									{
										Log.e("no drop points there", ed.toString());
									}
									mylist.add(map_busdata);
								}

								bus_adapter = new SimpleAdapter(getActivity(), mylist, R.layout.listview_tickets, new String[]
								{ "travels", "traveltime", "bustype", "availableseats", "boardingTimes", "droppingTimes", "ticketfare",
										"runtime" }, new int[]
								{ R.id.flightname_label, R.id.traveltime_label, R.id.flighttype_label, R.id.availableseats_label,
										R.id.boardpoint_label, R.id.droppoint_label, R.id.ticketfare_label, R.id.runtime_label });

								// ///show hide panel
								panel.setVisibility(View.GONE);
								panel_layout.setVisibility(View.VISIBLE);
								tickets_listview.setAdapter(bus_adapter);

								Toast.makeText(getActivity(), String.valueOf(trips.length()) + " buses available", Toast.LENGTH_SHORT)
										.show();
							}
						}
						else
						{
							Toast.makeText(getActivity(), status + " " + status_reason, Toast.LENGTH_SHORT).show();
						}

					}
					catch (JSONException e)
					{
						Log.e("log_tag", "Error parsing data " + e.toString());
						Toast.makeText(getActivity(), "Error parsing data " + e.toString(), Toast.LENGTH_SHORT).show();
					}
				}

				else if (result.equals("tripdetails_executed"))
				{
					if (data_type.equals("tripdetails"))
					{
						// Bundle location_bundle=new Bundle();
						// location_bundle.putStringArray("boarding_locations",
						// boarding_locations);
						// Log.e("outp", outp);
						Intent seat_intent = new Intent(getActivity(), Booktickets_Activity.class);
						seat_intent.putExtra("trip_id", selected_tripid);
						Log.e("frommmmmmmmm id", from_id.toString());
						seat_intent.putExtra("from_id", from_id.toString());

						Log.e("tooooooooo id", to_id.toString());
						seat_intent.putExtra("to_id", to_id.toString());

						seat_intent.putExtra("trip_date", datepicker_button.getText().toString());
						seat_intent.putExtra("trip_from", destination_from_text.getText().toString());
						seat_intent.putExtra("trip_to", destination_to_text.getText().toString());

						seat_intent.putExtra("boarding_list", boarding_list);
						seat_intent.putExtra("drop_list", drop_list);

						seat_intent.putExtra("seat_details", outp);
						seat_intent.putExtra("bus_details", selected_bus);
						seat_intent.putExtra("bus_type", selected_bustype);
						seat_intent.putExtra("cancellation_policy", selected_cpolicy);
						seat_intent.putExtra("cancellation_allowed", selected_callowed);

						startActivity(seat_intent);

					}
				}
				else
				{
					Toast.makeText(getActivity(), "flag is not returning 1", Toast.LENGTH_SHORT).show();
				}

			}
			catch (Exception e)
			{
				Log.e("error", "error on transaction post execute" + e);
				Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void populateSetDate(int year, int month, int day)
	{

		try
		{
			datepicker_button.setText(year + "-" + month + "-" + day);
		}
		catch (Exception e)
		{
			Log.e("crash", "crash on populateSetDate\n" + e.toString());
		}
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
				String x = datepicker_button.getText().toString();
				String[] y = x.split("-");
				yy = Integer.parseInt(y[0]);
				mm = Integer.parseInt(y[1]);
				dd = Integer.parseInt(y[2]);

			}
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd)
		{
			populateSetDate(yy, mm + 1, dd);
		}
	}

	public String[] fetch_locations()
	{

		DataBaseHelper myDbHelper = new DataBaseHelper(null);
		myDbHelper = new DataBaseHelper(getActivity());
		String locations[] =
		{ "" };

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
			sampleDB = getActivity().openOrCreateDatabase("mobilecodes_db", 0, null);
			Cursor c = sampleDB.rawQuery("SELECT name FROM all_sources", null);
			Log.e("cursor output", c.toString());
			c.moveToFirst();
			int i = 0;
			locations = new String[c.getCount()];
			while (c.isAfterLast() == false)
			{
				// Log.e("i value  ", String.valueOf(i));
				// Log.e("inside cursor output",
				// c.getString(c.getColumnIndex("name")));
				locations[i] = c.getString(c.getColumnIndex("name"));
				c.moveToNext();
				i++;

			}
			sampleDB.close();
			myDbHelper.close();
		}

		catch (Exception se)
		{
			Log.e(getClass().getSimpleName(), "Error in fetching from database\n" + se.toString());
		}

		return locations;
	}

}
