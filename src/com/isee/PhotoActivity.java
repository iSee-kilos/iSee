package com.isee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
	LocationManager locationManager;
	private Location location = null;
	private Criteria criteria;
	private String bestProvider;
	private double latitude;
	private double longitude;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_photo);
		initProvider();
		
	}
	
	private void UpdateLocation(Location location){
		this.location = location;
	}
	
	private boolean LocationDetected(){
		if(location != null){
			return true;
		}
		return false;
	}
	
	ImageView iv = null; 
    String imageFilePath = "null";
	
    public void Take_Pic_Onclick(View view){
    	if(!LocationDetected()){
    		Toast.makeText(getApplicationContext(), "无法确定位置，请打开GPS或网络！！",
      			     Toast.LENGTH_SHORT).show();
    		return;
    	}
		String name= "/data/isee/"+DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
		imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + name; 
        File imageFile = new File(imageFilePath); 
        Uri imageFileUri = Uri.fromFile(imageFile); 
         
        Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        
        startActivityForResult(it, 0); 
        
	}
	
    Bitmap bmp ;
    Bitmap newbmp;
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	if (RESULT_OK == resultCode) { 
            iv = (ImageView) findViewById(R.id.Show_View); 
             
            // Load up the image's dimensions not the image itself 
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options(); 
            bmpFactoryOptions.inJustDecodeBounds = true; 
             bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions); 
             
            // Decode it for real 
            bmpFactoryOptions.inJustDecodeBounds = false; 
            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
             newbmp = Bitmap.createScaledBitmap(bmp, (int)200, (int)300, true);
            iv.setImageBitmap(newbmp);
            
            try {
            	String name= Environment.getExternalStorageDirectory().getAbsolutePath() +"/data/isee/data/target.jpg";
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
            
              
        }    
        
    } 
    
    
    public void Back_Onclick(View view){
		if (imageFilePath.equals("null") ){
			Toast.makeText(getApplicationContext(), "请先拍照！！",
   			     Toast.LENGTH_SHORT).show();
			return;
		}
		if(newbmp != null && !newbmp.isRecycled())
			newbmp.recycle();
		if(bmp != null && !bmp.isRecycled())
			bmp.recycle();
    	Intent intent = new Intent();
        
		intent.putExtra("file_location", imageFilePath);
		intent.putExtra("photo_latitude", location.getLatitude());
		intent.putExtra("photo_longitude", location.getLongitude());
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
		
		if(newbmp != null && !newbmp.isRecycled())
			newbmp.recycle();
		if(bmp != null && !bmp.isRecycled())
			bmp.recycle();
		Intent intent = new Intent();
		
        intent.putExtra("file_location", imageFilePath);  
        intent.putExtra("photo_latitude", location.getLatitude());
		intent.putExtra("photo_longitude", location.getLongitude());
        /* 指定intent要启动的类 */
        intent.setClass(PhotoActivity.this, DrawActivity.class);
        /* 启动一个新的Activity */
        PhotoActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        PhotoActivity.this.finish(); 
	}	
	
	private void initProvider() 
	{
	    locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);    
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
        	System.out.println("GPS open");
        }
        if (locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
	    {
	    	System.out.println("NETWORK open"); 
	    }
        criteria = new Criteria();  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);  
        criteria.setAltitudeRequired(false);  
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
	    bestProvider = locationManager.getBestProvider(criteria, true);
	    if(bestProvider != null){
	    	location = locationManager.getLastKnownLocation(bestProvider);
	    	System.out.println(bestProvider);
	    }
	    else {
	    	return;
	    }
	    locationManager.requestLocationUpdates(bestProvider, 1000, 0,
			new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					UpdateLocation(location);
					if(location == null){
						System.out.println("!No Location!");
					}
					else {
						System.out.println(location.getLatitude());
						System.out.println(location.getLatitude());
					}
				}
				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					bestProvider = locationManager.getBestProvider(criteria, true);
					location = locationManager.getLastKnownLocation(bestProvider);
					Toast.makeText(getApplicationContext(), provider + "was opened",
			   			     Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), provider + "was closed",
			   			     Toast.LENGTH_SHORT).show();
					bestProvider = locationManager.getBestProvider(criteria, true);
				}
			});
	}
}
