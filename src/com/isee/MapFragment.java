package com.isee;

import com.isee.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
    private Location location;
    private String bestProvider;
    private Context context;
    private View view;
    
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
		updateToNewLocation(location);
		//给按钮添加监听器
		//btn_loc.setOnClickListener(new MapClickedListener());
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
    public void onDestroy() {  
        super.onDestroy();  
    }
    
    @Override  
    public void onPause() {  
        super.onPause();  
    }  
  
  
    @Override  
    public void onResume() {  
        super.onResume();  
    }  
    
    @Override
    public void onDetach() {
        super.onDetach();
        FragmentManager fManager = getActivity().getSupportFragmentManager();
        Fragment fragment = (Fragment) fManager.findFragmentById(R.id.mapView);
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
  
    @Override  
    public void onStop() {  
        super.onStop();  
    }  
 
	private void initProvider(Context context) 
	{
	    //创建LocationManager对象
	    locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    
	    // List all providers:
	    //List<String> providers = locManager.getAllProviders();
	    
	    Criteria criteria = new Criteria();  
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
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前  
        // provider
	    bestProvider = locManager.getBestProvider(criteria, true);
	    System.out.println(bestProvider + "!!!");
	    //location
	    location = locManager.getLastKnownLocation(bestProvider);
	    if (location == null)
	    {
	    	System.out.println("No location!!!");	    	
	    	location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    }
	    else
	    	System.out.println("经度："+location.getLatitude()+"  纬度：" + location.getLongitude());
	    
	    
	  //open GPS
	    if (locManager  
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)  
                || locManager  
                        .isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)  
        )
	    {
	    	System.out.println("source setted!!!!"); 
	    }
	    else
	    	System.out.println("source???");  
	    	    
	    
	}
	
	

/*
	//点击事件监听器
	private class MapClickedListener implements OnClickListener
	{
	    @Override
	    public void onClick(View v) 
	    {
			//根据用户输入经纬度定位
			//获取用户输入的经纬度
			String lng = edt_lng.getText().toString().trim();
			String lat = edt_lat.getEditableText().toString().trim();
			if(lng.equals("") || lat.equals(""))
			{
			    Toast.makeText(getApplicationContext(), "请输入有效的经纬度信息！" , Toast.LENGTH_LONG).show();
			    location = locManager.getLastKnownLocation(bestProvider);
			    updateToNewLocation(location);
			}
			else
			{
			    location.setLongitude(Double.parseDouble(lng));
			    location.setLatitude(Double.parseDouble(lat));
			    //调用方法更新地图定位信息
			    updateToNewLocation(location);
			}
	    }
	}
*/
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

	//获取用户界面组件
	private void findViews() 
	{
	    //获取界面上的两个按钮
	    //btn_loc = (Button) findViewById(R.id.btn_loc);
	    //获取界面上的两个文本框
	    //edt_lng = (EditText) findViewById(R.id.edt_lng);
	    //edt_lat = (EditText) findViewById(R.id.edt_lat);
	    //获得RadioGroup
	    rg_mapType = (RadioGroup) view.findViewById(R.id.rg_mapType);
	}
	
	private void setMarker(LatLng latlng)
	{

	    markerOpt = new MarkerOptions();

	    markerOpt.position(latlng);
	    markerOpt.draggable(true);
	    markerOpt.visible(true);
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
	    Toast.makeText(context, "纬度：" + location.getLongitude() + " 经度："+location.getLatitude() , Toast.LENGTH_LONG).show();
	    markerOpt.position(new LatLng(dLat, dLong));
	    markerOpt.draggable(false);
	    markerOpt.visible(true);
	    markerOpt.anchor(0.5f, 0.5f);//设为图片中心
	    markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
	    mMap.addMarker(markerOpt);
	    
	    setMarker(new LatLng(38.042000, 114.51500));
	    
	    //将摄影机移动到指定的地理位置
	    cameraPosition = new CameraPosition.Builder()
	        .target(new LatLng(dLat, dLong))              // Sets the center of the map to ZINTUN
	        .zoom(17)                   // 缩放比例
	        .bearing(0)                // Sets the orientation of the camera to east
	        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	        .build();                   // Creates a CameraPosition from the builder
	        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    }
}
