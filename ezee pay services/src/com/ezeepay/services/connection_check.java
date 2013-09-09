package com.ezeepay.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class connection_check
{

	private Context context;

	public connection_check(Context context)
	{
		this.context = context;
	}

	public boolean isnetwork_available()
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}
}