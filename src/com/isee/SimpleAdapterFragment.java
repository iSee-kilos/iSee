package com.isee;

import com.isee.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Environment;





import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class SimpleAdapterFragment extends ListFragment
{
	private static final List<Map<String,String>> items = new ArrayList<Map<String,String>>();
	private static final String[] keys = { "line1", "line2" };
	private static final int[] controlIds = { android.R.id.text1, android.R.id.text2 };
	
	static
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("line1", "Title One");
		map.put("line2", "Subtitle One");
		items.add(map);
		map = new HashMap<String, String>();
		map.put("line1", "Title Two");
		map.put("line2", "Subtitle Two");
		items.add(map);
		map = new HashMap<String, String>();
		map.put("line1", "Title Three");
		map.put("line2", "Subtitle Three");
		items.add(map);
		map = new HashMap<String, String>();
		map.put("line1", "Title Four");
		map.put("line2", "Subtitle Four");
		items.add(map);
		map = new HashMap<String, String>();
		map.put("line1", "Title Five");
		map.put("line2", "Subtitle Five");
		items.add(map);
	}
	
	
	
	public void writeFile(String fileName,String writestr) throws IOException
	{ 
		try
		{
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
			File file = new File(path);
			
			if (!file.exists())
			{
				file.createNewFile();
			}
			
		    FileOutputStream fout = new FileOutputStream(file); 
		
		    byte [] bytes = writestr.getBytes(); 
		
		    fout.write(bytes); 
		
		    fout.close(); 
		    
		    System.out.println("write done!!!");	   
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
	} 

	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.listview, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		try {
			writeFile("123.txt", "123456");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ListAdapter adapter = new SimpleAdapter(getActivity(), items, android.R.layout.simple_list_item_2, keys, controlIds );
		setListAdapter(adapter);
	}
}
