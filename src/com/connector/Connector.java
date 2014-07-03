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
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class Connector {
	private MobileServiceClient mClient;
	private MobileServiceTable<GeoInfo> mGeoInfoTable;
	private GeoInfo geoInfo;
	private ConnectionListener listener;
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
			listener = (ConnectionListener)context;
			
		} catch (MalformedURLException e) {
			listener.OperationCallBack("Connection Fail");
			e.printStackTrace();
		}
	}
	
//----------------------------these are for uploading pictures-------------------------------//	
	public void UploadPicture(double latitude, double longtitude, String targetPath, String framePath){
		if(mGeoInfoTable == null){
			listener.OperationCallBack("Connection Fail");
			return;
		}
		geoInfo = new GeoInfo(latitude, longtitude);
		System.out.println("acquiring...");
		listener.Waiting("Uploading");
		mGeoInfoTable.insert(geoInfo, new InsertOperationCallback(targetPath, framePath));
	}
	
	public class InsertOperationCallback implements TableOperationCallback<GeoInfo>{
		String targetPath;
		String framePath;
		public InsertOperationCallback(String target, String frame){
			this.targetPath = target;
			this.framePath = frame;
		}
		@Override
		public void onCompleted(GeoInfo entity, Exception exception, ServiceFilterResponse response) {
			System.out.println("Come Back");
			if (exception == null) {
				geoInfo = entity;
				String id = entity.getId();
            	if(uploadBlob(id + "_target.jpg", targetPath) < 0){
            		listener.OperationCallBack("Upload Picture Fail");
            		return;
            	}
				System.out.println("uploading frame...");
		        if(uploadBlob(id + "_frame.png", framePath) < 0){
		        	listener.OperationCallBack("Upload Picture Fail");
		        	return;
		        }
		        System.out.println("Upload picture success");
		        geoInfo.setConfirmed(true);
		        mGeoInfoTable.update(geoInfo, new UpdateOperationCallback());
			}
			else {
				listener.OperationCallBack("Upload Picture fail");
			}
		}
	}
	
	public class UpdateOperationCallback implements TableOperationCallback<GeoInfo>{

		@Override
		public void onCompleted(GeoInfo entity, Exception exception, ServiceFilterResponse response) {
			// TODO Auto-generated method stub
			if (exception == null){
				listener.OperationCallBack("Upload Picture Success");
			}
			else listener.OperationCallBack("Upload Picture Fail");
		}
		
	}
	
	public int uploadBlob(String name, String path){
		CloudStorageAccount storageAccount;
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference("pictures");
		//	container.createIfNotExists();
			CloudBlockBlob blob = container.getBlockBlobReference(name);
		//	blob.deleteIfExists();
			File source = new File(path);
			blob.upload(new FileInputStream(source), source.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			return -1;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return -1;
		} catch (StorageException e) {
			// TODO Auto-generated catch block
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
		//	container.createIfNotExists();
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
		mGeoInfoTable.where().field("confirmed").eq(true)
			.and(mGeoInfoTable.where().field("latitude").ge(latitude - radius))
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
	
	public interface ConnectionListener{
		public void Waiting(String message);
		public void OperationCallBack(String message);
	}
}
//----------------------------thing to do with search around-----------------------------------//
	

