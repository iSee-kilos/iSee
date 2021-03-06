// Copyright 2007-2014 metaio GmbH. All rights reserved.
package com.metaio.ar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.isee.R;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.EVISUAL_SEARCH_STATE;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IVisualSearchCallback;
import com.metaio.sdk.jni.ImageStruct;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.sdk.jni.VisualSearchResponseVector;
import com.metaio.tools.io.AssetsManager;

public class ArActivity extends ARViewActivity 
{
	private IGeometry mModel;
	private IGeometry mEarthOcclusion;
	private IGeometry mEarthIndicators;
	private boolean mEarthOpened;
	private MetaioSDKCallbackHandler mSDKCallback;
	private VisualSearchCallbackHandler mVisualSearchCallback;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mEarthOpened = false;
		
		mSDKCallback = new MetaioSDKCallbackHandler();
		mVisualSearchCallback = new VisualSearchCallbackHandler();
		
		if (metaioSDK != null)
		{	
			metaioSDK.registerVisualSearchCallback(mVisualSearchCallback);
		}
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mSDKCallback.delete();
		mSDKCallback = null;
		mVisualSearchCallback.delete();
		mVisualSearchCallback = null;
	}
	
	public void onButtonClick(View v)
	{
		finish();
	}
	
	@Override
	protected int getGUILayout() 
	{
		// Attaching layout to the activity
		return R.layout.activity_ar; 
	}
	
	

	@Override
	protected void loadContents() 
	{
		try
		{
			// Getting a file path for tracking configuration XML file
			String trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "TrackingData_MarkerlessFast.xml");
			
			// Assigning tracking configuration
			boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log("Tracking data loaded: " + result); 
	        
			// Getting a file path for a 3D geometry
			String imagePath = AssetsManager.getAssetPath(getApplicationContext(), "frame.png");			
			if (imagePath != null)
			{
				mModel = metaioSDK.createGeometryFromImage(imagePath);
				if (mModel != null)
				{
					mModel.setScale(3.0f);
					MetaioDebug.log("Loaded geometry "+imagePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+imagePath);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
		
  
	@Override
	protected void onGeometryTouched(IGeometry geometry) 
	{
		MetaioDebug.log("Template.onGeometryTouched: " + geometry);
		
		if (geometry != mEarthOcclusion)
		{
			if (!mEarthOpened)
			{
				mModel.startAnimation("Open", false);
				mEarthIndicators.startAnimation("Grow", false);
				mEarthOpened = true;
			}
			else
			{
				mModel.startAnimation("Close", false);
				mEarthIndicators.startAnimation("Shrink", false);
				mEarthOpened = false;
			}
		}

	}


	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() 
	{
		return mSDKCallback;
	}
	
	final class MetaioSDKCallbackHandler extends IMetaioSDKCallback 
	{

		@Override
		public void onSDKReady() 
		{
			MetaioDebug.log("The SDK is ready");
		}
		
		@Override
		public void onAnimationEnd(IGeometry geometry, String animationName) 
		{
			MetaioDebug.log("animation ended" + animationName);
		}
		
		@Override
		public void onMovieEnd(IGeometry geometry, String name)
		{
			MetaioDebug.log("movie ended" + name);
		}
		
		@Override
		public void onNewCameraFrame(ImageStruct cameraFrame)
		{
			MetaioDebug.log("a new camera frame image is delivered" + cameraFrame.getTimestamp());
		}
		
		@Override 
		public void onCameraImageSaved(String filepath)
		{
			MetaioDebug.log("a new camera frame image is saved to" + filepath);
		}
		
		@Override
		public void onScreenshotImage(ImageStruct image)
		{
			MetaioDebug.log("screenshot image is received" + image.getTimestamp());
		}
		
		@Override
		public void onScreenshotSaved(String filepath)
		{
			MetaioDebug.log("screenshot image is saved to" + filepath);
		}
		
		@Override
		public void onTrackingEvent(TrackingValuesVector trackingValues)
		{
			for (int i=0; i<trackingValues.size(); i++)
			{
				final TrackingValues v = trackingValues.get(i);
				MetaioDebug.log("Tracking state for COS "+v.getCoordinateSystemID()+" is "+v.getState());
			}
		}

		@Override
		public void onInstantTrackingEvent(boolean success, String file)
		{
			if (success)
			{
				MetaioDebug.log("Instant 3D tracking is successful");
			}
		}
	}
	
	final class VisualSearchCallbackHandler extends IVisualSearchCallback
	{

		@Override
		public void onVisualSearchResult(VisualSearchResponseVector response, int errorCode)
		{
			if (errorCode == 0)
			{
				MetaioDebug.log("Visual search is successful");
			}
		}

		@Override
		public void onVisualSearchStatusChanged(EVISUAL_SEARCH_STATE state) 
		{
			MetaioDebug.log("The current visual search state is: " + state);
		}


	}
	
}
