package com.ezeepay.services;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_flighttickets extends Fragment implements OnClickListener
{

	String adults_numbers[] =
	{ "Adults", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	String children_numbers[] =
	{ "Children", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	String infants_numbers[] =
	{ "Infants", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	String cabintype_category[] =
	{ "Select Class", "Economy", "Business" };

	View flighttickets_view;
	String methodname = "", outpu = "", resultstatus = "";

	Button show_button;
	ListView flights_listview;
	Spinner adults_spinner, children_spinner, infants_spinner, cabintype_spinner;

	TextView flightfrom_text, flightto_text;

	ArrayAdapter<String> adults_adapter, children_adapter, infants_adapter, cabintype_adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		// //declare
		flighttickets_view = inflater.inflate(R.layout.fragment_flighttickets, container, false);

		flightfrom_text = (TextView) flighttickets_view.findViewById(R.id.flightfrom_text);
		flightto_text = (TextView) flighttickets_view.findViewById(R.id.flightto_text);
		adults_spinner = (Spinner) flighttickets_view.findViewById(R.id.adults_spinner);
		children_spinner = (Spinner) flighttickets_view.findViewById(R.id.children_spinner);
		infants_spinner = (Spinner) flighttickets_view.findViewById(R.id.infants_spinner);
		cabintype_spinner = (Spinner) flighttickets_view.findViewById(R.id.cabintype_spinner);

		// /initialize

		adults_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, adults_numbers);
		adults_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adults_spinner.setAdapter(adults_adapter);

		children_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, children_numbers);
		children_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		children_spinner.setAdapter(children_adapter);

		infants_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, infants_numbers);
		infants_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		infants_spinner.setAdapter(infants_adapter);

		cabintype_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cabintype_category);
		cabintype_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cabintype_spinner.setAdapter(cabintype_adapter);

		show_button = (Button) flighttickets_view.findViewById(R.id.show_button);
		show_button.setOnClickListener(this);

		flights_listview = (ListView) flighttickets_view.findViewById(R.id.flights_listview);

		flightfrom_text.setText("IXM");
		flightto_text.setText("MAA");
		cabintype_spinner.setSelection(1);
		adults_spinner.setSelection(1);
		children_spinner.setSelection(1);
		infants_spinner.setSelection(1);

		return flighttickets_view;
	}

	class webservicecall_task extends AsyncTask<String, Object, String>
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
			if (params[0].toString().equals("getsource"))
			{
				resultstatus = "getsource_executed";

				// RestClient c0 = new
				// RestClient("http://www.cleartrip.com/air/1.0/airports");
				//
				// c0.AddHeader("X-CT-API-KEY",
				// "faac74d003a54e6abbdfeca11be78838");
				// try
				// {
				// c0.Execute(RestClient.RequestMethod.GET);
				// String str = c0.getResponse();
				// String em = "bla", ec = "bla";
				// em = c0.getErrorMessage();
				// Log.e("errormsg", em);
				// ec = String.valueOf(c0.getResponseCode());
				// Log.e("responsecode", ec);
				// outpu = str;
				// Log.e("airports",outpu);
				// }
				// catch (Exception er)
				// {
				// outpu = er.toString();
				// }

				RestClient c1 = new RestClient("http://api.staging.cleartrip.com/air/1.0/search");
				c1.AddParam("from", flightfrom_text.getText().toString().trim());
				c1.AddParam("to", flightto_text.getText().toString().trim());
				c1.AddParam("depart-date", "2013-08-20");
				c1.AddParam("adults", adults_spinner.getSelectedItem().toString());
				c1.AddParam("children", children_spinner.getSelectedItem().toString());
				c1.AddParam("infants", infants_spinner.getSelectedItem().toString());
				c1.AddParam("cabin-type", cabintype_spinner.getSelectedItem().toString());
				c1.AddHeader("X-CT-API-KEY", "faac74d003a54e6abbdfeca11be78838");
				try
				{
					c1.Execute(RestClient.RequestMethod.GET);
					String str = c1.getResponse();
					String em = "bla", ec = "bla";
					em = c1.getErrorMessage();
					Log.e("errormsg", em);
					ec = String.valueOf(c1.getResponseCode());
					Log.e("responsecode", ec);
					outpu = str;
					// int maxLogSize = 1000;
					// for (int i = 0; i <= outpu.length() / maxLogSize; i++)
					// {
					// int start = i * maxLogSize;
					// int end = (i + 1) * maxLogSize;
					// end = end > outpu.length() ? outpu.length() : end;
					// Log.e("xml", outpu.substring(start, end));
					// }

				}
				catch (Exception er)
				{
					outpu = er.toString();
				}

				// HttpClient httpclient = new DefaultHttpClient();
				// HttpResponse response;
				// HttpPost httppost = new
				// HttpPost("http://api.staging.cleartrip.com/air/1.0/search");
				// List<NameValuePair> parameters = new
				// ArrayList<NameValuePair>(2);
				// String responseString="";
				// try
				// {
				// Log.e("inside", "test bg");
				// parameters.add(new BasicNameValuePair("from", "MAA"));
				// parameters.add(new BasicNameValuePair("to", "DEL"));
				// parameters.add(new BasicNameValuePair("depart-date",
				// "2013-08-20"));
				// parameters.add(new BasicNameValuePair("career", "IC"));
				// parameters.add(new BasicNameValuePair("cabin-type",
				// "Economy"));
				//
				// httppost.addHeader("X-CT-API-KEY",
				// "faac74d003a54e6abbdfeca11be78838");
				// httppost.setEntity(new UrlEncodedFormEntity(parameters));
				//
				// //ResponseHandler<String> res = new BasicResponseHandler();
				// //responseString = httpclient.execute(httppost, res);
				// response=httpclient.execute(httppost);
				// Log.e("output response",
				// response.getStatusLine().toString());
				// //Log.e("output response", response.toString());
				// BufferedReader rd = new BufferedReader(new
				// InputStreamReader(response.getEntity().getContent()));
				// String body = "";
				// while ((body = rd.readLine()) != null)
				// {
				// Log.e("HttpResponse", body);
				// }
				// }
				// catch (Exception e)
				// {
				// Log.e("ee", e.toString());
				//
				//
				//
				// }

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
			if (result.equals("getsource_executed"))
			{
				try
				{
					Log.e("entered post execute", "1");
					ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
					Document doc = getDomElement(outpu);
					NodeList nl = doc.getElementsByTagName("solution");
					Log.e("nllength", String.valueOf(nl.getLength()));
					for (int i = 0; i < nl.getLength(); i++)
					{
						HashMap<String, String> map = new HashMap<String, String>();
						Element e = (Element) nl.item(i);

						map.put("departure-airport", getValue(e, "departure-airport"));
						map.put("arrival-airport", getValue(e, "arrival-airport"));

						String date_d = getValue(e, "departure-date-time");
						String date_a = getValue(e, "arrival-date-time");
						SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
						try
						{
							map.put("time", "Travel Time : " + date_d.substring(11, 16) + " - " + date_a.substring(11, 16));
						}
						catch (Exception e1)
						{
							e1.printStackTrace();
						}

						map.put("departure-date-time", "Dep@ " + getValue(e, "departure-date-time"));
						map.put("arrival-date-time", "Arr@ " + getValue(e, "arrival-date-time"));
						map.put("flight-number", getValue(e, "flight-number"));
						map.put("airline", call_db(getValue(e, "airline")) + "(" + getValue(e, "stops") + " Stops)");
						map.put("operating-airline", call_db(getValue(e, "operating-airline")));
						map.put("arrival-airport", getValue(e, "arrival-airport"));
						map.put("stops", getValue(e, "stops"));
						map.put("equipment", getValue(e, "equipment"));
						map.put("duration", getValue(e, "duration"));
						map.put("total-fare", getValue(e, "total-fare"));

						menuItems.add(map);
					}

					ListAdapter adapter = new SimpleAdapter(getActivity(), menuItems, R.layout.listview_flighttickets, new String[]
					{ "departure-airport", "arrival-airport", "flight-number", "airline", "duration", "time", "total-fare" }, new int[]
					{ R.id.boardpoint_label, R.id.droppoint_label, R.id.flighttype_label, R.id.flightname_label, R.id.runtime_label,
							R.id.traveltime_label, R.id.ticketfare_label });
					flights_listview.setAdapter(adapter);
				}
				catch (Exception e)
				{
					Log.e("Exception in dom parser", e.toString());
				}
			}

		}

		public Document getDomElement(String xml)
		{
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try
			{

				DocumentBuilder db = dbf.newDocumentBuilder();

				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				doc = db.parse(is);

			}
			catch (ParserConfigurationException e)
			{
				Log.e("Error: ", e.getMessage());
				return null;
			}
			catch (SAXException e)
			{
				Log.e("Error: ", e.getMessage());
				return null;
			}
			catch (IOException e)
			{
				Log.e("Error: ", e.getMessage());
				return null;
			}
			// return DOM
			Log.e("outputdom", doc.toString());
			return doc;
		}

		public String getValue(Element item, String str)
		{
			NodeList n = item.getElementsByTagName(str);
			return this.getElementValue(n.item(0));
		}

		public final String getElementValue(Node elem)
		{
			Node child;
			if (elem != null)
			{
				if (elem.hasChildNodes())
				{
					for (child = elem.getFirstChild(); child != null; child = child.getNextSibling())
					{
						if (child.getNodeType() == Node.TEXT_NODE)
						{
							return child.getNodeValue();
						}
					}
				}
			}
			return "";
		}
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
			case R.id.show_button:
			{
				methodname = "getsource";
				new webservicecall_task().execute(methodname);
				break;
			}
			}

		}
		catch (Exception e)
		{
		}
	}

	public String call_db(String airline_code)
	{
		String output = "";
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
			Log.e("Airline name to be checked on databse", airline_code);
			sampleDB = getActivity().openOrCreateDatabase("mobilecodes_db", 0, null);
			Cursor c = sampleDB.rawQuery("SELECT * FROM airline_operators where iata_code= '" + airline_code.trim() + "'", null);
			String name = "";
			Log.e("cursor output", c.toString());
			if (c.moveToFirst())
			{
				Log.e("output", c.getString(0));
				name = c.getString(c.getColumnIndex("airline_name"));
				output = name;
				sampleDB.close();
			}
			else
			{
				// Toast.makeText(getActivity(),
				// "Number not found in the database!",
				// Toast.LENGTH_SHORT).show();
				output = airline_code;
			}
		}
		catch (Exception se)
		{
			Log.e(getClass().getSimpleName(), "Could not create or Open the database\n" + se.toString());
			output = airline_code;
		}

		return output;
	}

}
