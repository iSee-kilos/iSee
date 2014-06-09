package com.isee;

import com.isee.R;
import com.metaio.ar.ArMainActivity;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ArFragment extends Fragment
{
	private Button btn;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.ar_fragment_layout, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		btn=(Button)view.findViewById(R.id.ar_button);
		btn.setOnClickListener(new Button.OnClickListener(){ 
            public void onClick(View v)
            {
            	Intent intent = new Intent(view.getContext(), ArMainActivity.class);
        		startActivity(intent);
            }
        });
	}
}
