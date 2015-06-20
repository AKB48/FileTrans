package com.example.filetrans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MarkActivity extends Activity {

    private Boolean canLaunch = false;
    private ImageView preView;
    private Button add_photo, launcher;
    private EditText et;
    private String imagePath;
    private Bitmap preImage = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        
        this.canLaunch = false;
        this.preView = (ImageView)this.findViewById(R.id.new_photo);
        this.add_photo = (Button)this.findViewById(R.id.add_photo);
        this.launcher = (Button)this.findViewById(R.id.launcher);
        this.launcher.setClickable(false);
        this.et = (EditText)this.findViewById(R.id.add_text);
        
        this.et.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(""))
                {
                    canLaunch = false;
                    launcher.setClickable(false);
                }
                else
                {
                    canLaunch = true;
                    launcher.setClickable(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {     
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        });
        
        add_photo.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!checkSDCard())
                {
                    Toast.makeText(MarkActivity.this, "No SDCard", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File indexFile = new File(SDCardPath + "/TnT" );
                    if(!indexFile.exists())
                    {
                        indexFile.mkdirs();
                    }
                  
                    imagePath = SDCardPath + "/TnT/temp.jpg";             
                    File imageFile = new File(imagePath);
                    if (imageFile.exists())
                    {
                        imageFile.delete();
                    }
                    try
                    {
                        imageFile.createNewFile();
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                        return;
                    }      
                
                    Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                    intentCam.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    startActivityForResult(intentCam, ActivityFlag.CameraAct);                   
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }
    
    
    private Boolean checkSDCard()
    {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (requestCode==ActivityFlag.CameraAct && resultCode==RESULT_OK)
        {
            Log.i("marktest","success");
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap tempImage = preImage;
            preImage = BitmapFactory.decodeFile(this.imagePath, options);
            preImage = compress(preImage);           
            if (preImage != null)
            {
                Matrix matrix = new Matrix();  
                matrix.postRotate(getCameraPhotoOrientation(imagePath));  
                preImage = Bitmap.createBitmap(preImage, 0, 0, preImage.getWidth(), preImage.getHeight(), matrix, true);
                preView.setImageBitmap(preImage);
                if (tempImage != null)
                {
                    tempImage.recycle();
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            preImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);         
            try {
//                String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                imagePath = SDCardPath + "/TnT/temp2.jpg";
//                File imageFile = new File(imagePath);
//                if (imageFile.exists())
//                {
//                    imageFile.delete();
//                }
//                try
//                {
//                    imageFile.createNewFile();
//                }
//                catch(IOException e)
//                {
//                    e.printStackTrace();
//                    return;
//                }      
                File imageFile = new File(imagePath);
                FileOutputStream out=new FileOutputStream(imageFile);
                out.write(baos.toByteArray());
                out.flush();
                out.close();
            } catch(FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            launcher.setClickable(true);
        }
              
    }   
    
    
    private int getCameraPhotoOrientation(String imagePath){  
        int rotate = 0;  
        try {   
            File imageFile = new File(imagePath);  
      
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());  
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
      
            switch (orientation) {  
            case ExifInterface.ORIENTATION_ROTATE_270:  
                rotate = 270;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_180:  
                rotate = 180;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_90:  
                rotate = 90;  
                break;  
            }  
      
        } 
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
        
        return rotate;  
        
    }
    
    
    private Bitmap compress(Bitmap source)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int new_width = width;
        int new_height = height;
        
        if (width > height && width > 800) 
        {
            double ratio = width /  800.0d;
            new_width =  800;
            new_height = (int) (height / ratio);
        }
        else if (height >= width && height >  800)
        {
            double ratio = height /  800.0d;
            new_height =  800;
            new_width = (int) (width / ratio);
        }
        
        if (new_width != width || new_height != height)
        {
            Bitmap tempBitmap = scale(source, new_width, new_height);
            source.recycle();
            source = tempBitmap;
            tempBitmap = null;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1000 > 100 && options > 10) {   
            baos.reset();
            options -= 10;
            source.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }  
        
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        if (source != null)
            source.recycle();
        source = BitmapFactory.decodeStream(isBm, null, null);
        return source;
    }
    
    
    private Bitmap scale(Bitmap source, int new_width, int new_height)
    {   
        int width = source.getWidth();
        int height = source.getHeight();
        
        float width_ratio = ((float)new_width) / width;
        float height_ratio = ((float)new_height) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(width_ratio, height_ratio);
            
        Bitmap dest = Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
        
        return dest;
    
    }



}
