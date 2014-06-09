package com.isee;

import com.isee.R;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// 隐藏android系统的状态栏  
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 // 隐藏应用程序的标题栏，即当前activity的标题栏 this.requestWindowFeature(Window.FEATURE_NO_TITLE)
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
	}

	//private ImageView Search_Buttom;
	private TextView TV1;
	 
	public void Search_Buttom_Onclick(View view){
		TV1 = (TextView) findViewById(R.id.test1);
		TV1.setText("Search");
		
		Intent intent = new Intent();
        intent.putExtra("name","LeiPei");    
        /* 指定intent要启动的类 */
        intent.setClass(MainActivity.this, SearchActivity.class);
        /* 启动一个新的Activity */
        MainActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        //MainActivity.this.finish();
	}	

//	public void Photo_Buttom_Onclick(View view){
//		TV1 = (TextView) findViewById(R.id.test1);
//		TV1.setText("Photo");
//		Intent intent = new Intent();
//        intent.putExtra("name","LeiPei");    
//        /* 指定intent要启动的类 */
//        intent.setClass(MainActivity.this, PhotoActivity.class);
//        /* 启动一个新的Activity */
//        MainActivity.this.startActivity(intent);
//        /* 关闭当前的Activity */
//        MainActivity.this.finish();
//	}	
//	
//	public void Draw_Buttom_Onclick(View view){
//		TV1 = (TextView) findViewById(R.id.test1);
//		TV1.setText("Draw");
//		Intent intent = new Intent();
//        intent.putExtra("name","LeiPei");    
//        /* 指定intent要启动的类 */
//        intent.setClass(MainActivity.this, DrawActivity.class);
//        /* 启动一个新的Activity */
//        MainActivity.this.startActivity(intent);
//        /* 关闭当前的Activity */
//        MainActivity.this.finish(); 
//	}	
//	
//	public void View_Buttom_Onclick(View view){
//		TV1 = (TextView) findViewById(R.id.test1);
//		TV1.setText("View");
//		//Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		  
//        //startActivityForResult(intent, 1); 
//	}	

}