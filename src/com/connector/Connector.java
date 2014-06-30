package com.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.isee.MapFragment;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.core.storage.StorageException;

public class Connector {
	private MobileServiceClient mClient;
	private MobileServiceTable<GeoInfo> mGeoInfoTable;
	public GeoInfo geoInfo;
	public static final String storageConnectionString = 
			"DefaultEndpointsProtocol=http;" + 
		    "AccountName=iseepics;" + 
		    "AccountKey=VwWSXbBD4pMm7MiEfVc9vYoS7fceKG2UuyhvtGZYVBs4rLotUPFUVwERkgD4kBjH4v7VlEWD0to9ZJ4yNWX5yA==";

	public Connector(Context context){
		try{
			mClient = new MobileServiceClient(
					"https://iseegeo.azure-mobile.net/",
					"bYRKjuesCORnxldwytFXBSVvBkKzPn39",
					context);
			mGeoInfoTable = mClient.getTable(GeoInfo.class);			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
//----------------------------these are for uploading pictures-------------------------------//	
	public int UploadPicture(double latitude, double longtitude, String targetPath, String framePath){
		if(mGeoInfoTable == null){
			return -1;
		}
		geoInfo = null;
		CountDownLatch latch = new CountDownLatch(1);
		Thread req = new Thread(new GeoInfoRequirer(0, null, latitude, longtitude, latch));
		req.start();
		System.out.println("acquiring...");
		try {
			latch.await();
			System.out.println(latch.getCount());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(geoInfo == null){
			return -1;
		}
		System.out.println("getGeoInfo");
		//----------------------------
		System.out.println("uploading target...");
		String id = geoInfo.getId();
		if(uploadBlob(id + "_target.jpg", targetPath) < 0)
        	return -1;
		System.out.println("uploading fram...");
        if(uploadBlob(id + "_frame.png", framePath) < 0)
        	return -1;
        System.out.println("2success");
        //-------------------------------------
        System.out.println("confirming...");
		latch = new CountDownLatch(1);
		req = new Thread(new GeoInfoRequirer(1, geoInfo.getId(), latitude, longtitude, latch));
		geoInfo = null;
		req.start();
		try {
			latch.await(3 ,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(geoInfo == null){
			return -1;
		}
		return 0;
	}
	
	public class GeoInfoRequirer implements Runnable{
		CountDownLatch latch;
		double latitude;
		double longtitude;
		int type;
		String id;
		public GeoInfoRequirer(int type, String id, double latitude, double longtitude, CountDownLatch latch){
			this.latch = latch;
			this.latitude = latitude;
			this.longtitude = longtitude;
			this.type = type;
			this.id = id;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			GeoInfo gi = new GeoInfo(latitude, longtitude);
			if(type == 0){
				System.out.println("Inserting... ");
				mGeoInfoTable.insert(gi, new MyTableOperationCallback(latch));
			}else{
				gi.setId(id);
				System.out.println("updating... ");
				mGeoInfoTable.update(gi, new MyTableOperationCallback(latch));
			}
		}
	}
	
	public class MyTableOperationCallback implements TableOperationCallback<GeoInfo>{
		CountDownLatch myLatch;
		public MyTableOperationCallback(CountDownLatch latch){
			this.myLatch = latch;
			System.out.println(myLatch.getCount());
		}
		@Override
		public void onCompleted(GeoInfo entity, Exception exception, ServiceFilterResponse response) {
			System.out.println("Come Back");
			if (exception == null) {
				geoInfo = entity;
			}
			myLatch.countDown();
			System.out.println(myLatch.getCount());
		}
	}
	
	public int uploadBlob(String name, String path){
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference("pictures");
			container.createIfNotExist();
			CloudBlockBlob blob = container.getBlockBlobReference(name);
			blob.deleteIfExists();
			File source = new File(path);
			blob.upload(new FileInputStream(source), source.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	public int downloadBlob(String name, String savePath){
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference("pictures");
			container.createIfNotExist();
			CloudBlockBlob blob = container.getBlockBlobReference(name);
			if(!blob.exists()){
				return -1;
			}
			File dest = new File(savePath);
			blob.download(new FileOutputStream(dest));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
//----------------------------downloading!----------------------------------------------//
	public int DownloadPicture(String id, String savePath){
		if(downloadBlob(id + "_target.jpg", savePath + "_target.jpg") < 0)
			return -1;
		if(downloadBlob(id + "_frame.png", savePath + "_frame.png") < 0)
			return -1;
		return 0;
	}
//----------------------------thing to do with search around-----------------------------------//
	public void SearchAround(double latitude, double longtitude, double radius, MapFragment mf){
		// TODO Auto-generated method stub
		if(mGeoInfoTable == null){
			return;
		}
		mGeoInfoTable.where().field("latitude").ge(latitude - radius)
			.and(mGeoInfoTable.where().field("latitude").le(latitude + radius))
			.and(mGeoInfoTable.where().field("longtitude").ge(longtitude - radius))
			.and(mGeoInfoTable.where().field("longtitude").le(longtitude + radius))
			.execute(new MyTableQueryCallback(mf));
	}
	
	public class MyTableQueryCallback implements TableQueryCallback<GeoInfo>{
		private MapFragment mf;
		
		public MyTableQueryCallback(MapFragment mf){
			this.mf = mf;
		}
		@Override
		public void onCompleted(List<GeoInfo> result, int count, Exception exception, ServiceFilterResponse response) {
			// TODO Auto-generated method stub
			System.out.println("yeah....");
			if(exception == null){
				for(GeoInfo gi : result){
					mf.setMarker(new LatLng(gi.getLatitude(), gi.getLongtitude()), gi.getId());
					System.out.println(gi.getId());
				}
			}
		}
	}
}
//----------------------------thing to do with search around-----------------------------------//
	

