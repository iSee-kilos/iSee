package com.isee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.isee.R;

import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window; 
import android.view.WindowManager; 

public class MainActivity extends FragmentActivity 
{
	public SurfaceHolder holder;
	private String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/isee/default.jpg";
	private ImageView image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_main);
		
		InputStream istream = null;
        istream =  getClass().getResourceAsStream("/assets/default.jpg");
        Bitmap bmp = BitmapFactory.decodeStream(istream);
        try {
        	String name= Environment.getExternalStorageDirectory().getAbsolutePath() +"/data/isee/default.jpg";
        	//imageFilePath = name;
        	File fileName = new File(name);
        	FileOutputStream b = new FileOutputStream(fileName);  
        	bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件 
            b.flush();
            b.close();
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }catch (IOException e) {  
            e.printStackTrace();  
        }
		Intent intent = getIntent();
		String name=  intent.getStringExtra("file_location");
		if (name != null && !name.equals("null") ){
			imageFilePath = name;
		
			// Load up the image's dimensions not the image itself 
	        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options(); 
	        bmpFactoryOptions.inJustDecodeBounds = true; 
	        bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions); 
	        // Decode it for real 
	        bmpFactoryOptions.inJustDecodeBounds = false; 
	        bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
        }

        float screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）  
		float screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		Bitmap newbmp = Bitmap.createScaledBitmap(bmp, (int)screenWidth, (int)screenHeight, true);
		
		image = (ImageView)findViewById(R.id.background);
		image.setImageBitmap(newbmp);
		
	}

	 private Bitmap blurImageAmeliorate(Bitmap bmp) 
	    { 
	        // 高斯矩阵 
	        int[] gauss = new int[]{1,2,4,8,4,2,1, 2,4,8,16,8,4,2, 4,8,16,32,16,8,4, 8,16,32,64,32,16,8,4,
	        						4,8,16,32,16,8,4, 2,4,8,16,8,4,2, 1,2,4,8,4,2,1};
	        int width = bmp.getWidth(); 
	        int height = bmp.getHeight(); 
	        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565); 
	         
	        int pixR = 0; 
	        int pixG = 0; 
	        int pixB = 0; 
	         
	        int pixColor = 0; 
	         
	        int newR = 0; 
	        int newG = 0; 
	        int newB = 0; 
	         
	        int delta = 484; // 值越小图片会越亮，越大则越暗 
	         
	        int idx = 0; 
	        int[] pixels = new int[width * height]; 
	        bmp.getPixels(pixels, 0, width, 0, 0, width, height); 
	        for (int i = 3, length = height - 3; i < length; i++) 
	        { 
	            for (int k = 3, len = width - 3; k < len; k++) 
	            { 
	                idx = 0; 
	                for (int m = -3; m <= 3; m++) 
	                { 
	                    for (int n = -3; n <= 3; n++) 
	                    { 
	                        pixColor = pixels[(i + m) * width + k + n]; 
	                        pixR = Color.red(pixColor); 
	                        pixG = Color.green(pixColor); 
	                        pixB = Color.blue(pixColor); 
	                         
	                        newR = newR + (int) (pixR * gauss[idx]); 
	                        newG = newG + (int) (pixG * gauss[idx]); 
	                        newB = newB + (int) (pixB * gauss[idx]); 
	                        idx++; 
	                    } 
	                } 
	                 
	                newR /= delta; 
	                newG /= delta; 
	                newB /= delta; 
	                 
	                newR = Math.min(255, Math.max(0, newR)); 
	                newG = Math.min(255, Math.max(0, newG)); 
	                newB = Math.min(255, Math.max(0, newB)); 
	                 
	                pixels[i * width + k] = Color.argb(80, newR, newG, newB); 
	                 
	                newR = 0; 
	                newG = 0; 
	                newB = 0; 
	            } 
	        } 
	        bitmap.setPixels(pixels, 0, width, 0, 0, width, height); 
	        //long end = System.currentTimeMillis(); 
	        return bitmap; 
       }
	
	public void Search_Buttom_Onclick(View view){
		Intent intent = new Intent();
        /* 指定intent要启动的类 */
        intent.setClass(MainActivity.this, SearchActivity.class);
        /* 启动一个新的Activity */
        MainActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        MainActivity.this.finish();
	}	
	
	public void Photo_Buttom_Onclick(View view){
		Intent intent = new Intent();
        /* 指定intent要启动的类 */
        intent.setClass(MainActivity.this, PhotoActivity.class);
        /* 启动一个新的Activity */
        MainActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        MainActivity.this.finish();
	}	
	
	public void Draw_Buttom_Onclick(View view){
		Intent intent = new Intent();
		intent.putExtra("file_location", imageFilePath);   
        /* 指定intent要启动的类 */
        intent.setClass(MainActivity.this, DrawActivity.class);
        /* 启动一个新的Activity */
        MainActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        MainActivity.this.finish(); 
	}	
	
	public void Upload_Buttom_Onclick(View view){
		
		//Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  
        //startActivityForResult(intent, 1); 
	}	
	
}