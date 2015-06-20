package com.example.filetrans;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;


import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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
			postJsonData();
			break;
		case HttpRequestType.GET_FILE:
			getBitmapFile();
			break;
		case HttpRequestType.POST_FILE:
			postFile();
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
	
	
	private void postJsonData2()
	{
	    HttpClient client = new DefaultHttpClient();
        String httpUrl = "http://10.66.44.41/mini_proj/register.php";
        HttpPost httpPost = new HttpPost(httpUrl);
        StringBuilder str = new StringBuilder();
        
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("telephone","132"));
        params.add(new BasicNameValuePair("password", "311"));
        BufferedReader buffer = null;
        try
        {
            // 请求超时
            //client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20 * 1000);
            // 读取超时
            //client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20 * 1000);
            
            httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            HttpResponse httpRes = client.execute(httpPost);
            
            Log.i("test", ""+httpRes.getStatusLine().getStatusCode());
            if(httpRes.getStatusLine().getStatusCode() == 200)
            {
                buffer = new BufferedReader(new InputStreamReader(httpRes.getEntity().getContent()));
                for(String s = buffer.readLine(); s != null ; s = buffer.readLine())
                {
                    str.append(s);
                }
                respObject = str.toString();
                buffer.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {  
            client.getConnectionManager().shutdown();  
        }
	}
	
	
	private void postJsonData()
	{
	    JSONParser jsonParser = new JSONParser();
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("telephone","132"));
        params.add(new BasicNameValuePair("password", "311"));
        String urlStr = "http://10.66.44.41/mini_proj/register.php";
	    try {
            JSONObject json = jsonParser.makeHttpRequest(urlStr, "POST",params);
            Log.i("test", json.toString());
            respObject = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	}
	
	
	private void postFile()
	{
	    String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String imagePath = SDCardPath + "/TnT/temp.jpg";
        String urlStr = "http://10.66.44.41/mini_proj/uploadpic.php";
        Log.i("test", imagePath);
        uploadFile(urlStr, imagePath);
	}
	
	/**
	 * post data to server.
	 */
	private void postData()
	{
	    String end = "\r\n";  
	    String twoHyphens = "--";  
	    String boundary = "******";
		try {
			urlConnection = (HttpURLConnection)this.url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			//urlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			urlConnection.setRequestProperty("Charsert", "UTF-8");
			urlConnection.setRequestMethod("POST");
			urlConnection.setChunkedStreamingMode(128 * 1024);
			
			OutputStream os = urlConnection.getOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(os);
//			oos.writeObject(this.uploadObject);
//			oos.flush();
//			oos.close();
			if (this.uploadObject instanceof Bitmap)
			{
			    DataOutputStream dos = new DataOutputStream(os);  
			        dos.writeBytes(twoHyphens + boundary + end);  
			        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\""
			            + end);  
			        dos.writeBytes(end);
			        
			        
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//				((Bitmap) this.uploadObject).compress(CompressFormat.JPEG, 100, baos);  
//				dos.write(baos.toByteArray());
			        
			    String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			    String imagePath = SDCardPath + "/TnT/temp.jpg";
			    FileInputStream fis = new FileInputStream(imagePath);  
			    byte[] buffer = new byte[8192]; // 8k  
			    int count = 0;  
			    while ((count = fis.read(buffer)) != -1)  
			    {  
			        dos.write(buffer, 0, count);  
			    }  
			    fis.close();     
				
				dos.writeBytes(end);  
			    dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
			    dos.flush();
			    dos.close();
				
				
			}
			else {
			    Log.i("testTask", "notBitmap");
				//os.write(this.uploadObject.toString().getBytes());
			    DataOutputStream dos = new DataOutputStream(os);
			    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(new BasicNameValuePair("telephone","132"));
		        params.add(new BasicNameValuePair("password", "311"));
		        dos.writeBytes(params.toString());
			}
			
			//os.flush();
			
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(isr);
			StringBuffer respStr = new StringBuffer();
			
			Log.i("task", urlConnection.getResponseCode()+"");
			if(urlConnection.getResponseCode() == HttpStatusCode.HTTP_OK) {
				for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine())
				{
					respStr.append(str);
				}
			}
			respObject = respStr.toString();
			Log.i("task", (String) respObject);
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
	
	
	private void uploadFile(String uploadUrl, String srcPath)  
	  {  
	    String end = "\r\n";  
	    String twoHyphens = "--";  
	    String boundary = "******";  
	    try  
	    {  
	      URL url = new URL(uploadUrl);  
	      HttpURLConnection httpURLConnection = (HttpURLConnection) url  
	          .openConnection(); 
	      httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
	      // 允许输入输出流  
	      httpURLConnection.setDoInput(true);  
	      httpURLConnection.setDoOutput(true);  
	      httpURLConnection.setUseCaches(false);  
	      // 使用POST方法  
	      httpURLConnection.setRequestMethod("POST");  
	      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
	      httpURLConnection.setRequestProperty("Charset", "UTF-8");  
	      httpURLConnection.setRequestProperty("Content-Type",  
	          "multipart/form-data;boundary=" + boundary);  
	  
	      DataOutputStream dos = new DataOutputStream(  
	          httpURLConnection.getOutputStream());  
	      dos.writeBytes(twoHyphens + boundary + end);  
	      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""  
	          + srcPath.substring(srcPath.lastIndexOf("/") + 1)  
	          + "\""  
	          + end);  
	      dos.writeBytes(end);  
	  
	      FileInputStream fis = new FileInputStream(srcPath);  
	      byte[] buffer = new byte[8192]; // 8k  
	      int count = 0;  
	      // 读取文件  
	      while ((count = fis.read(buffer)) != -1)  
	      {  
	        dos.write(buffer, 0, count);  
	      }  
	      fis.close();  
	  
	      dos.writeBytes(end);  
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
	      dos.flush();  
	  
//	      InputStream is = httpURLConnection.getInputStream();  
//	      InputStreamReader isr = new InputStreamReader(is, "utf-8");  
//	      BufferedReader br = new BufferedReader(isr);  
//	      String result = br.readLine(); 
//	      respObject = result;
	      
	      InputStream is = httpURLConnection.getInputStream();
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader bufferedReader = new BufferedReader(isr);
          StringBuffer respStr = new StringBuffer();
          
          if(httpURLConnection.getResponseCode() == HttpStatusCode.HTTP_OK) {
              for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine())
              {
                  respStr.append(str);
              }
          }
          respObject = respStr.toString();
	  
	      dos.close();  
	      is.close();  
	  
	    } catch (Exception e)  
	    {  
	      e.printStackTrace();  
	    }  
	  }  
}
