package com.isee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


public class PhotoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_photo);		
	}
	
	ImageView iv = null; 
    String imageFilePath = "null";
	
    public void Take_Pic_Onclick(View view){
		String name= "/data/isee/"+DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
		imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + name; 
        File imageFile = new File(imageFilePath); 
        Uri imageFileUri = Uri.fromFile(imageFile); 
         
        Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        
        startActivityForResult(it, 0); 
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	if (RESULT_OK == resultCode) { 
            iv = (ImageView) findViewById(R.id.Show_View); 
             
            // Load up the image's dimensions not the image itself 
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options(); 
            bmpFactoryOptions.inJustDecodeBounds = true; 
            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions); 
             
            // Decode it for real 
            bmpFactoryOptions.inJustDecodeBounds = false; 
            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
            Bitmap newbmp = Bitmap.createScaledBitmap(bmp, (int)200, (int)300, true);
            iv.setImageBitmap(newbmp);
            
            try {
            	String name= Environment.getExternalStorageDirectory().getAbsolutePath() +"/data/isee/data/target.jpg";
            	File fileName = new File(name);
            	FileOutputStream b = new FileOutputStream(fileName);  
            	newbmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件 
                b.flush();
                b.close();
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }
            
            
        }    
        
    } 
    
    
    public void Back_Onclick(View view){
		if (imageFilePath.equals("null") ){
			Toast.makeText(getApplicationContext(), "请先拍照！！",
   			     Toast.LENGTH_SHORT).show();
			return;
		}
    	Intent intent = new Intent();
        
		intent.putExtra("file_location", imageFilePath);
        /* 指定intent要启动的类 */
        intent.setClass(PhotoActivity.this, MainActivity.class);
        /* 启动一个新的Activity */
        PhotoActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        PhotoActivity.this.finish();
	}	
	
	public void Draw_Pic_Onclick(View view){
		if (imageFilePath.equals("null") ){
			Toast.makeText(getApplicationContext(), "请先拍照！！",
   			     Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent();
		
        intent.putExtra("file_location", imageFilePath);  
        /* 指定intent要启动的类 */
        intent.setClass(PhotoActivity.this, DrawActivity.class);
        /* 启动一个新的Activity */
        PhotoActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        PhotoActivity.this.finish(); 
	}	
}
