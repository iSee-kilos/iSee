package com.isee;
import com.connector.Connector;
import com.metaio.ar.ArMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RatingActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_layout);
		
		Intent intent = getIntent();
        String rating = intent.getStringExtra("rating");
		
		TextView rating_source = (TextView) findViewById(R.id.Rating_Source);
		rating_source.setText("Rating level : " + rating);
	}
	
	public void Rating_Onclick(View view){
		
	}
	
	
	public void Launch_Onclick(View view){
		Intent intent = new Intent();
        intent.setClass(view.getContext(), ArMainActivity.class);
        view.getContext().startActivity(intent);
	}
}
