package com.example.robot.mechanical;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Httpreader
{
	public static void main(String[] args)
			throws Exception
	{
		new Httpreader();
	}

	public String GetUrlData(String desiredUrl)
			throws Exception
	{
		URL url = null;
		BufferedReader reader = null;
		StringBuilder stringBuilder;

		try
		{
			// create the HttpURLConnection

			if (!desiredUrl.contains("http://")){   	

				desiredUrl=MainActivity.ServerUrl +desiredUrl;

			}



			url = new URL(desiredUrl);

			if (MainActivity.debugmode)    Log.e("Mechanical", "Http Request " + desiredUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// just want to do an HTTP GET here
			connection.setRequestMethod("GET");

			// uncomment this if you want to write output to this url
			//connection.setDoOutput(true);

			if (MainActivity.debugmode)    // give it 15 seconds to respond
				Log.e("Mechanical", "Set 60 seconds timeout");
			connection.setReadTimeout(60*1000);

			if (MainActivity.debugmode)    Log.e("Mechanical", "connect");
			connection.connect();
			if (MainActivity.debugmode)    Log.e("Mechanical", "connected. try read");
			// read the output from the server
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			stringBuilder = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				stringBuilder.append(line + "\n");
			}
			if (MainActivity.debugmode)  Log.e("Mechanical","Server Response" + stringBuilder.toString());
			MainActivity.requesttimeout=false;
			return stringBuilder.toString();
		}
		catch (Exception e)
		{
			MainActivity.requesttimeout=false;
			if (MainActivity.debugmode)   Log.e("Mechanical", "Http error " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally
		{
			// close the reader; this can throw an exception too, so
			// wrap it in another try/catch block.
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException ioe)
				{
					if (MainActivity.debugmode)  Log.e("Mechanical","Server Error" + ioe.getMessage() + " CAUSE " +ioe.getCause().toString());
					ioe.printStackTrace();
				}
			}
		}
	}
}