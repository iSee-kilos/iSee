package com.isee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.isee.R;
import com.metaio.ar.ArMainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;


public class MapFragment extends Fragment 
{
    //定义界面上的可视化组件
    //private Button btn_loc, btn_nav;
    //private EditText edt_lng, edt_lat;
    private RadioGroup rg_mapType;
    GoogleMap mMap;
    private CameraPosition cameraPosition;
    private MarkerOptions markerOpt;
    //定义LocationManager对象
    private LocationManager locManager;
    private Criteria criteria;
    private Location location;
    private String bestProvider;
    private Context context;
    private View view;
    
    private ImageButton loction_Btn;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
    	try 
    	{
    		view = inflater.inflate(R.layout.map_fragment_layout, container, false);
    		return view;
    	} 
    	catch (Exception e) 
    	{
    		System.out.println(e);
        }
    	return null;
	}
    
    
    
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    	
    	if (view == null)
    		return;
    	
		//获取用户界面的组件
		findViews();
		//创建LocationManager对象,并获取Provider
		initProvider(context);
		//取得地图组件
		mMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setInfoWindowAdapter(new MapMarkerClicked());
		mMap.setOnInfoWindowClickListener(new MarkerInfoWindowClickedListener());
		updateToNewLocation(location);
		//给按钮添加监听器
		loction_Btn.setOnClickListener(new MapClickedListener());
		//为RadioGroup的选中状态改变添加监听器
		rg_mapType.setOnCheckedChangeListener(new ChangeMapTypeListener()); 
		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N
		locManager.requestLocationUpdates(bestProvider,  3 * 1000, 8, new LocationListener() {
		    //当Provider的状态改变时
		    @Override
		    public void onStatusChanged(String provider, int status, Bundle extras) 
		    {
		    }
		    @Override
		    public void onProviderEnabled(String provider) 
		    {
				// 当GPS LocationProvider可用时，更新位置
				location = locManager.getLastKnownLocation(provider);
				if (provider != null)
				{
					Toast.makeText(context, "onUpdate:" + provider + "longitude:" + location.getLongitude() + " Latitude:"+location.getLatitude() , Toast.LENGTH_LONG).show();
				}
		    }
		    @Override
		    public void onProviderDisabled(String provider) 
		    {
		    	updateToNewLocation(null);
		    }
		    @Override
		    public void onLocationChanged(Location location) 
		    {
		    	// 当GPS定位信息发生改变时，更新位置
		    	updateToNewLocation(location);
		    }
		    });
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }
    
    
    @Override
    public void onDetach() {
        super.onDetach();
//        FragmentManager fManager = getActivity().getSupportFragmentManager();
//        Fragment fragment = (Fragment) fManager.findFragmentById(R.id.mapView);
//        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }
    
    //listener
    private class MapMarkerClicked implements InfoWindowAdapter
	{	    
		private View mInfoWindowContent = null;
		private LayoutInflater mInflater = LayoutInflater.from(context); 
		
		@Override
		public View getInfoContents(Marker marker) {
			if(mInfoWindowContent == null){
		        mInfoWindowContent = mInflater.inflate(R.layout.map_marker_info, null);
		    }  
			
		    ImageView infoImage = (ImageView)mInfoWindowContent.findViewById(R.id.map_info_image);
		    File sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		    String filename = sdDir.getPath() + "/data/isee/cache/" + marker.getSnippet() + "_target.jpg";
	        Bitmap bitmap = getLoacalBitmap(filename);
	        infoImage .setImageBitmap(bitmap);
		    
		    TextView infoTitle = (TextView)mInfoWindowContent.findViewById(R.id.map_info_title);
		    infoTitle.setText(marker.getTitle());
		        
		    TextView infoSnippet = (TextView)mInfoWindowContent.findViewById(R.id.map_info_snippet);
		    infoSnippet.setText(marker.getSnippet());
		    return mInfoWindowContent;
		}
		
		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
		
		public Bitmap getLoacalBitmap(String url) {
	         try {
	              FileInputStream fis = new FileInputStream(url);
	              return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片        

	           } catch (FileNotFoundException e) {
	              e.printStackTrace();
	              return null;
	         }
	    }
		
	}
    
    //listener
    private class MarkerInfoWindowClickedListener implements OnInfoWindowClickListener
	{
		@Override
		public void onInfoWindowClick(Marker marker) 
		{
			System.out.println("Triggered");
			File sdDir = Environment.getExternalStorageDirectory();//获取跟目录
			
			//delete target
			String fileName1 = sdDir.getPath() + "/data/isee/data/target.jpg";
			File file = new File(fileName1);     
	        if(file.isFile() && file.exists())
	        {     
	            file.delete();
	        }
	        
	      //delete frame
	        String fileName2 = sdDir.getPath() + "/data/isee/data/frame.png";
			file = new File(fileName2);     
	        if(file.isFile() && file.exists())
	        {     
	            file.delete();
	        }
	        
	        
	        String fileName = sdDir.getPath() + "/data/isee/cache/" + marker.getSnippet() + "_target.jpg";
	        file = new File(fileName);     
	        if(file.isFile() && file.exists())
	        {
	        	Copy(file, fileName1);
	        	Toast.makeText(context, "Target settled", Toast.LENGTH_LONG).show();
	        }
	        
	        fileName = sdDir.getPath() + "/data/isee/cache/" + marker.getSnippet() + "_frame.png";
	        file = new File(fileName);     
	        if(file.isFile() && file.exists())
	        {
	        	Copy(file, fileName2);
	        	Toast.makeText(context, "Frame settled", Toast.LENGTH_LONG).show();
	        }	        
		}
		
		public void Copy(File oldfile, String newfile)
		{    
			try     
			{    
				int byteread = 0;    
                  //File     oldfile     =     new     File(oldPath);    
				if (oldfile.exists())
				{      
					InputStream inStream = new FileInputStream(oldfile);     
					FileOutputStream fs = new FileOutputStream(newfile);    
					byte[] buffer = new byte[1444];    
					while ((byteread  = inStream.read(buffer)) != -1)
					{    
						fs.write(buffer, 0, byteread);    
					}    
					inStream.close();
					fs.close();
				}    
			}    
			catch(Exception e)
			{    
				System.out.println("error ");    
				e.printStackTrace();    
			}    
		}     
		
		
		
	}
    
    //listener
	private class MapClickedListener implements OnClickListener
	{
	    @Override
	    public void onClick(View v) 
	    {
	    	bestProvider = locManager.getBestProvider(criteria, true);
	    	location = locManager.getLastKnownLocation(bestProvider);
		    if (location == null)
		    {
		    	System.out.println("No location!!!");
		    	//location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    	Toast.makeText(context, "No location!" , Toast.LENGTH_LONG).show();
		    }
		    else
		    {
		    	Toast.makeText(context, "loc:" + bestProvider + "\n" + "longitude:" + location.getLongitude() + " Latitude:"+location.getLatitude() , Toast.LENGTH_LONG).show();
		    	System.out.println("经度："+location.getLatitude()+"  纬度：" + location.getLongitude());
		    }
	    	updateToNewLocation(location);
	    }
	}
	
	//listener
	private class ChangeMapTypeListener implements OnCheckedChangeListener
	{
	    @Override
	    public void onCheckedChanged(RadioGroup group, int checkedId) 
	    {
			switch(checkedId)
			{
				case R.id.rb_nomal://如果勾选的是"正常视图"的单选按钮
				    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				    break;
				case R.id.rb_satellite://如果勾选的是"卫星视图"的单选按钮
				    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				    break;
			}
	    }
	}

 
	private void initProvider(Context context) 
	{
	    //创建LocationManager对象
	    locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    
	    // List all providers:
	    //List<String> providers = locManager.getAllProviders();
	    
	    criteria = new Criteria();  
        // 查询精度：高  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);  
        // 是否查询海拨：否  
        criteria.setAltitudeRequired(false);  
        // 是否查询方位角 : 否  
        criteria.setBearingRequired(false);  
        // 是否允许付费：false 
        criteria.setCostAllowed(false);  
        // 电量要求：低  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
        if (locManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        		|| 
        	locManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
	    {
	    	System.out.println("source setted!!!!"); 
	    }
	    else
	    {
	    	System.out.println("source???");  
	    }
        
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前  
        // provider
	    bestProvider = locManager.getBestProvider(criteria, true);
	    System.out.println(bestProvider + "!!!");
	    //location
	    location = locManager.getLastKnownLocation(bestProvider);
	    if (location == null)
	    {
	    	System.out.println("No location!!!");
	    	//location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    	Toast.makeText(context, "No location!" , Toast.LENGTH_LONG).show();
	    }
	    else
	    {
	    	Toast.makeText(context, "init:" + bestProvider + "\n" + "longitude:" + location.getLongitude() + " Latitude:"+location.getLatitude() , Toast.LENGTH_LONG).show();
	    	System.out.println("经度："+location.getLatitude()+"  纬度：" + location.getLongitude());
	    }
	    
	}
	
	
	//获取用户界面组件
	private void findViews() 
	{	    
	    rg_mapType = (RadioGroup) view.findViewById(R.id.rg_mapType);
	    loction_Btn = (ImageButton) view.findViewById(R.id.loction);  
	}
	
	private void setMarker(LatLng latlng, String picID)
	{

	    markerOpt = new MarkerOptions();

	    markerOpt.position(latlng);
	    markerOpt.draggable(true);
	    markerOpt.visible(true);
	    markerOpt.title("title");	    
	    markerOpt.snippet(picID);
	    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target));
	    mMap.addMarker(markerOpt);
	}
	
	private void updateToNewLocation(Location location)
	{
	    mMap.clear();
	    markerOpt = new MarkerOptions();
	    //定位石家庄
	    double dLong = 114.51500;
	    double dLat = 38.042000;
	    if(location != null)
	    {
			//获取经度
			dLong = location.getLongitude();
			//获取纬度
			dLat = location.getLatitude();			
	    }	    
	    markerOpt.position(new LatLng(dLat, dLong));
	    markerOpt.draggable(false);
	    markerOpt.visible(true);
	    markerOpt.anchor(0.5f, 0.5f);//设为图片中心
	    markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
	    mMap.addMarker(markerOpt);
	    
	    setMarker(new LatLng(31.0200251, 121.4300024), "1");
	    setMarker(new LatLng(31.0260121, 121.4330014), "2");
	    setMarker(new LatLng(31.0209309, 121.4360022), "3");
	    
	    //将摄影机移动到指定的地理位置
	    cameraPosition = new CameraPosition.Builder()
	        .target(new LatLng(dLat, dLong))              // Sets the center of the map to ZINTUN
	        .zoom(15)                   // 缩放比例
	        .bearing(0)                // Sets the orientation of the camera to east
	        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	        .build();                   // Creates a CameraPosition from the builder
	        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    }
}
