package com.example.filetrans;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView tv;
	private Button bt, bt2;
	private ImageView imView;
	private Bitmap bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView)this.findViewById(R.id.showtext);
		bt = (Button)this.findViewById(R.id.upload);
		bt2 = (Button)this.findViewById(R.id.uploadimage);
		imView = (ImageView)this.findViewById(R.id.image);
		bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
		bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("aa", 1);
					jo.put("bb", 11);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String urlStr = "http://www.whatsact.com:10087/json";
				String urlStr2 = "http://pica.nipic.com/2007-11-09/2007119124513598_2.jpg";
				try {
					HttpSender.post(urlStr, jo, new TextResponse(), HttpRequestType.POST_DATA);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
		bt2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String urlStr = "";
				try {
					HttpSender.post(urlStr, bitmap, new TextResponse(), HttpRequestType.POST_FILE);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	class ShowResponse implements IResponse
	{

		@Override
		public void onResponse(Object respContent) {
			//tv.setText(respContent.toString());
			imView.setImageBitmap((Bitmap)respContent);
		}
	}
	
	class TextResponse implements IResponse
	{

		@Override
		public void onResponse(Object respContent) {
			JSONObject jo = null;
            try {
                jo = new JSONObject((String)respContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
			try {
				tv.setText(jo.getInt("cmd")+"");
			} catch(JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
