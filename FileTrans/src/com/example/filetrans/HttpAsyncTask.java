package com.example.filetrans;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class HttpAsyncTask extends AsyncTask<Void, Void, String> {

	
	private URL url = null;
	private Object uploadObject = null;
	private IResponse httpresponse = null;
	private HttpURLConnection urlConnection = null;
	private Object respObject = null;
	private int requestType = 0;
	
	
	
	public HttpAsyncTask(String urlStr, Object uploadObject, IResponse httpresponse, int requestType) throws MalformedURLException
	{
		this.url = new URL(urlStr);
		this.uploadObject = uploadObject;
		this.httpresponse = httpresponse;
		this.requestType = requestType;
	}
	
	
	@Override
	protected String doInBackground(Void... arg0) {
		switch(this.requestType)
		{
		case HttpRequestType.POST_DATA:
			postData();
			break;
		case HttpRequestType.GET_FILE:
			getBitmapFile();
			break;
		case HttpRequestType.POST_FILE:
			postData();
			break;
		default:
			break;
		}
		return null;
	}
	
	
	@Override  
	protected void onPostExecute(String result) {  
		super.onPostExecute(result);  
		this.httpresponse.onResponse(respObject);
	} 
	
	
	/**
	 * post data to server.
	 */
	private void postData()
	{
		try {
			urlConnection = (HttpURLConnection)this.url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			urlConnection.setRequestProperty("Charsert", "UTF-8");
			urlConnection.setRequestMethod("POST");
			
			OutputStream os = urlConnection.getOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(os);
//			oos.writeObject(this.uploadObject);
//			oos.flush();
//			oos.close();
			os.write(this.uploadObject.toString().getBytes());
			os.flush();
			
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(isr);
			StringBuffer respStr = new StringBuffer();
			
			if(urlConnection.getResponseCode() == HttpStatusCode.HTTP_OK) {
				for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine())
				{
					respStr.append(str);
				}
			}
			respObject = respStr.toString();
			if (is != null)
			{
				is.close();
			}
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null)
			{
				urlConnection.disconnect();
			}
		}
	}

	
	/**
	 * get bitmap file from server.
	 */
	private void getBitmapFile()
	{
		try {
			urlConnection = (HttpURLConnection) this.url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setRequestProperty("Charsert", "UTF-8");
			urlConnection.connect();
			InputStream is = urlConnection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			respObject = bitmap;
			if (is != null)
			{
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null)
			{
				urlConnection.disconnect();
			}
		}
		
	}
}
