// Copyright 2007-2014 metaio GmbH. All rights reserved.
package com.metaio.ar;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.isee.BuildConfig;
import com.isee.R;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

public class ArMainActivity extends Activity
{
	//TODO: Select the template, i.e. Native Android or AREL
	public static final boolean NATIVE = false;
	
	/**
	 * Task that will extract all the assets
	 */
	private AssetsExtracter mTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ar_main);
		
		// Enable metaio SDK debug log messages based on build configuration
		MetaioDebug.enableLogging(BuildConfig.DEBUG);
		
		// extract all the assets
		mTask = new AssetsExtracter();
		mTask.execute(0);

	}

	/**
	 * This task extracts all the assets to an external or internal location
	 * to make them accessible to metaio SDK
	 */
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
	{

		@Override
		protected void onPreExecute() 
		{
		}
		
		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try 
			{
				// Extract all assets and overwrite existing files if debug build
				AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
			} 
			catch (IOException e) 
			{
				MetaioDebug.log(Log.ERROR, "Error extracting assets: "+e.getMessage());
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) 
		{
			if(NATIVE)
			{
				// create native template and present it
				Intent intent = new Intent(getApplicationContext(), ArActivity.class);
				startActivity(intent);
			}
			else
			{
				// create AREL template and present it
				String arelConfigFilePath = null;
					
				File sdDir = null; 
				boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); 
				if (sdCardExist)   
				{                               
					sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼
					arelConfigFilePath = sdDir.getPath() + "/data/isee/data/arelConfig.xml";
					System.out.println(arelConfigFilePath + "!!!");
				}
				else
				{
					arelConfigFilePath = AssetsManager.getAssetPath(getApplicationContext(), "arelConfig.xml");					
				}
				
				
				MetaioDebug.log("arelConfig to be passed to intent: "+arelConfigFilePath);
				System.out.println(arelConfigFilePath + "!!!");
				Intent intent = new Intent(getApplicationContext(), ARELViewActivity.class);
				intent.putExtra(getPackageName()+".AREL_SCENE", arelConfigFilePath);
				startActivity(intent);
				
			}
			
			finish();
	    }
		
	}
	
}

