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
    //��������ϵĿ��ӻ����
    //private Button btn_loc, btn_nav;
    //private EditText edt_lng, edt_lat;
    private RadioGroup rg_mapType;
    GoogleMap mMap;
    private CameraPosition cameraPosition;
    private MarkerOptions markerOpt;
    //����LocationManager����
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
    	
		//��ȡ�û���������
		findViews();
		//����LocationManager����,����ȡProvider
		initProvider(context);
		//ȡ�õ�ͼ���
		mMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		updateToNewLocation(location);
		//����ť��Ӽ�����
		//btn_loc.setOnClickListener(new MapClickedListener());
		//ΪRadioGroup��ѡ��״̬�ı���Ӽ�����
		rg_mapType.setOnCheckedChangeListener(new ChangeMapTypeListener());
		// ���ü��������Զ����µ���Сʱ��Ϊ���N��(1��Ϊ1*1000������д��ҪΪ�˷���)����Сλ�Ʊ仯����N
		locManager.requestLocationUpdates(bestProvider,  3 * 1000, 8, new LocationListener() {
		    //��Provider��״̬�ı�ʱ
		    @Override
		    public void onStatusChanged(String provider, int status, Bundle extras) 
		    {
		    }
		    @Override
		    public void onProviderEnabled(String provider) 
		    {
				// ��GPS LocationProvider����ʱ������λ��
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
		    	// ��GPS��λ��Ϣ�����ı�ʱ������λ��
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
	    //����LocationManager����
	    locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    
	    // List all providers:
	    //List<String> providers = locManager.getAllProviders();
	    
	    Criteria criteria = new Criteria();  
        // ��ѯ���ȣ���  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);  
        // �Ƿ��ѯ��������  
        criteria.setAltitudeRequired(false);  
        // �Ƿ��ѯ��λ�� : ��  
        criteria.setBearingRequired(false);  
        // �Ƿ������ѣ�false 
        criteria.setCostAllowed(false);  
        // ����Ҫ�󣺵�  
        criteria.setPowerRequirement(Criteria.POWER_LOW);  
        // ��������ʵķ��������� provider ���� 2 ������Ϊ true ˵�� , ���ֻ��һ�� provider ����Ч�� , �򷵻ص�ǰ  
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
	    	System.out.println("���ȣ�"+location.getLatitude()+"  γ�ȣ�" + location.getLongitude());
	    
	    
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
	//����¼�������
	private class MapClickedListener implements OnClickListener
	{
	    @Override
	    public void onClick(View v) 
	    {
			//�����û����뾭γ�ȶ�λ
			//��ȡ�û�����ľ�γ��
			String lng = edt_lng.getText().toString().trim();
			String lat = edt_lat.getEditableText().toString().trim();
			if(lng.equals("") || lat.equals(""))
			{
			    Toast.makeText(getApplicationContext(), "��������Ч�ľ�γ����Ϣ��" , Toast.LENGTH_LONG).show();
			    location = locManager.getLastKnownLocation(bestProvider);
			    updateToNewLocation(location);
			}
			else
			{
			    location.setLongitude(Double.parseDouble(lng));
			    location.setLatitude(Double.parseDouble(lat));
			    //���÷������µ�ͼ��λ��Ϣ
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
				case R.id.rb_nomal://�����ѡ����"������ͼ"�ĵ�ѡ��ť
				    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				    break;
				case R.id.rb_satellite://�����ѡ����"������ͼ"�ĵ�ѡ��ť
				    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				    break;
			}
	    }
	}

	//��ȡ�û��������
	private void findViews() 
	{
	    //��ȡ�����ϵ�������ť
	    //btn_loc = (Button) findViewById(R.id.btn_loc);
	    //��ȡ�����ϵ������ı���
	    //edt_lng = (EditText) findViewById(R.id.edt_lng);
	    //edt_lat = (EditText) findViewById(R.id.edt_lat);
	    //���RadioGroup
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
	    //��λʯ��ׯ
	    double dLong = 114.51500;
	    double dLat = 38.042000;
	    if(location != null)
	    {
			//��ȡ����
			dLong = location.getLongitude();
			//��ȡγ��
			dLat = location.getLatitude();
	    }
	    Toast.makeText(context, "γ�ȣ�" + location.getLongitude() + " ���ȣ�"+location.getLatitude() , Toast.LENGTH_LONG).show();
	    markerOpt.position(new LatLng(dLat, dLong));
	    markerOpt.draggable(false);
	    markerOpt.visible(true);
	    markerOpt.anchor(0.5f, 0.5f);//��ΪͼƬ����
	    markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
	    mMap.addMarker(markerOpt);
	    
	    setMarker(new LatLng(38.042000, 114.51500));
	    
	    //����Ӱ���ƶ���ָ���ĵ���λ��
	    cameraPosition = new CameraPosition.Builder()
	        .target(new LatLng(dLat, dLong))              // Sets the center of the map to ZINTUN
	        .zoom(17)                   // ���ű���
	        .bearing(0)                // Sets the orientation of the camera to east
	        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	        .build();                   // Creates a CameraPosition from the builder
	        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    }
}
