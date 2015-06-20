package com.example.filetrans;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView tv;
	private Button bt, bt2, launchbt;
	private ImageView imView;
	private Bitmap bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView)this.findViewById(R.id.showtext);
		bt = (Button)this.findViewById(R.id.upload);
		bt2 = (Button)this.findViewById(R.id.uploadimage);
		launchbt = (Button)this.findViewById(R.id.launch);
		imView = (ImageView)this.findViewById(R.id.image);
		bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
		bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("telephone", "123456789");
					jo.put("password", "abcdefg");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String urlStr = "http://10.66.44.41/mini_proj/register.php";
				String urlStr2 = "http://10.66.44.41/mini_proj/upload/temp.jpg";
				try {
					HttpSender.get(urlStr2, new ShowResponse(), HttpRequestType.GET_FILE);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
		bt2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String urlStr = "http://10.66.44.41/mini_proj/uploadpic.php";
				try {
					HttpSender.post(urlStr, bitmap, new TextResponse(), HttpRequestType.POST_FILE);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
		launchbt.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();  
                intent.setClass(MainActivity.this, MarkActivity.class);
                try 
                {
                    startActivityForResult(intent, ActivityFlag.MarkAct);  
                }
                catch (ActivityNotFoundException e) 
                {  
                    return;
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
			imView.setImageBitmap(getRoundedCornerBitmap((Bitmap)respContent));
		    //imView.setImageBitmap((Bitmap)respContent);
		}
	}
	
	class TextResponse implements IResponse
	{

		@Override
		public void onResponse(Object respContent) {
			JSONObject jo = null;
			//Log.i("test", (String)respContent);
//            try {
//                jo = new JSONObject((String)respContent);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//			try {
//				tv.setText(jo.getString("telephone")+" "+jo.getString("password"));
			    tv.setText((CharSequence) respContent);
//			} catch(JSONException e) {
//				e.printStackTrace();
//			}
			
		}
	}
	
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) 
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int w;
		//int deltaX = 0;
		//int deltaY = 0;

		if (width <= height) {
			w = width;
			//deltaY = height - w;
		} 
		else {
			w = height;
			//deltaX = width - w;

		}
		
		Bitmap output = Bitmap.createBitmap(w, w, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        
		final Rect rect = new Rect(0, 0, w, w);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);
	    canvas.drawRoundRect(rectF, radius, radius, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    bitmap.recycle();
	    return output;
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
              
    }   

	
}
