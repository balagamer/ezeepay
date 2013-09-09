package com.ezeepay.services;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class custommenudrawer_adapter extends ArrayAdapter<String>
{
	private String[] spinner_text;
	private int[] spinner_images;
	private final Activity context;

	public custommenudrawer_adapter(Activity context, int textViewResourceId, String[] spinnertext, int[] spinnerimages)
	{
		super(context, textViewResourceId, spinnertext);
		this.spinner_text = spinnertext;
		this.spinner_images = spinnerimages;
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.nvdrawer_rows, parent, false);
		TextView label = (TextView) row.findViewById(R.id.menurow_title);
		ImageView icon = (ImageView) row.findViewById(R.id.menurow_icon);

		label.setText(spinner_text[position]);
		icon.setImageResource(spinner_images[position]);

		return row;
	}
}