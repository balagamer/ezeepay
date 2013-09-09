package com.ezeepay.services;

public class selectwebservice
{
    String address;
    public static String currentwebservice()
    {
    		//final String URL = "https://192.168.1.33:8443/ezeepayservices/services/ezeepaydataservice?wsdl";
    		final String URL = "https://www.ezeepayservices.com:8443/ezeepayservices/services/ezeepaydataservice?wsdl";
	return URL; 
    }
}
