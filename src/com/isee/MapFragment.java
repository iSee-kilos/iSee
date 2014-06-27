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
    //��������ϵĿ��ӻ����
    //private Button btn_loc, btn_nav;
    //private EditText edt_lng, edt_lat;
    private RadioGroup rg_mapType;
    GoogleMap mMap;
    private CameraPosition cameraPosition;
    private MarkerOptions markerOpt;
    //����LocationManager����
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
    	
		//��ȡ�û���������
		findViews();
		//����LocationManager����,����ȡProvider
		initProvider(context);
		//ȡ�õ�ͼ���
		mMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapView)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setInfoWindowAdapter(new MapMarkerClicked());
		mMap.setOnInfoWindowClickListener(new MarkerInfoWindowClickedListener());
		updateToNewLocation(location);
		//����ť��Ӽ�����
		loction_Btn.setOnClickListener(new MapClickedListener());
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
		    File sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼
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
	              return BitmapFactory.decodeStream(fis);  ///����ת��ΪBitmapͼƬ        

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
			File sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼
			
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
		    	System.out.println("���ȣ�"+location.getLatitude()+"  γ�ȣ�" + location.getLongitude());
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
				case R.id.rb_nomal://�����ѡ����"������ͼ"�ĵ�ѡ��ť
				    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				    break;
				case R.id.rb_satellite://�����ѡ����"������ͼ"�ĵ�ѡ��ť
				    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				    break;
			}
	    }
	}

 
	private void initProvider(Context context) 
	{
	    //����LocationManager����
	    locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    
	    // List all providers:
	    //List<String> providers = locManager.getAllProviders();
	    
	    criteria = new Criteria();  
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
        
        // ��������ʵķ��������� provider ���� 2 ������Ϊ true ˵�� , ���ֻ��һ�� provider ����Ч�� , �򷵻ص�ǰ  
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
	    	System.out.println("���ȣ�"+location.getLatitude()+"  γ�ȣ�" + location.getLongitude());
	    }
	    
	}
	
	
	//��ȡ�û��������
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
	    markerOpt.position(new LatLng(dLat, dLong));
	    markerOpt.draggable(false);
	    markerOpt.visible(true);
	    markerOpt.anchor(0.5f, 0.5f);//��ΪͼƬ����
	    markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
	    mMap.addMarker(markerOpt);
	    
	    setMarker(new LatLng(31.0200251, 121.4300024), "1");
	    setMarker(new LatLng(31.0260121, 121.4330014), "2");
	    setMarker(new LatLng(31.0209309, 121.4360022), "3");
	    
	    //����Ӱ���ƶ���ָ���ĵ���λ��
	    cameraPosition = new CameraPosition.Builder()
	        .target(new LatLng(dLat, dLong))              // Sets the center of the map to ZINTUN
	        .zoom(15)                   // ���ű���
	        .bearing(0)                // Sets the orientation of the camera to east
	        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	        .build();                   // Creates a CameraPosition from the builder
	        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    }
}
