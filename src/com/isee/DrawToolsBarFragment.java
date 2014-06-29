package com.isee;


import com.isee.SetColorFragment.OnColorChangedListener;

import android.app.Activity;
import android.app.Fragment;  

import android.os.Bundle;  
import android.os.Environment;
import android.view.LayoutInflater;  
import android.view.View;   
import android.view.ViewGroup;  
import android.widget.TextView; 
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.widget.ScrollView;
public class DrawToolsBarFragment extends Fragment {  
    TextView mTextView;  
    RadioGroup DrawToolsGroup;
    ScrollView Picture_table;

    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        //从文件 example_fragment.xml 加载了一个layout   
        View v = inflater.inflate(R.layout.fragment_draw_tools_bar, container, true);  
          
  
      //通过findViewById获得RadioGroup对象  
        DrawToolsGroup=(RadioGroup)v.findViewById(R.id.Draw_Tools_Group); 
        //DrawToolsGroup.check
        RadioButton temp =  (RadioButton)v.findViewById(R.id.Plain_Button);
        DrawToolsGroup.check(temp.getId());
        //添加事件监听器  
        DrawToolsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
              
            @Override 
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                switch(checkedId){
                case R.id.Plain_Button:  
                	mListener.OnToolsChanged(Mode.plain);
                	Picture_table.setVisibility(View.GONE);
                	break;
                case R.id.Spray_Button:
                	mListener.OnToolsChanged(Mode.spray);
                	Picture_table.setVisibility(View.GONE);
                	break;
                case R.id.Brush_Button:
                	mListener.OnToolsChanged(Mode.brush);
                	Picture_table.setVisibility(View.GONE);
                	break;
                case R.id.Advenced_Brush_Button:
                	mListener.OnToolsChanged(Mode.advenced_brush);
                	Picture_table.setVisibility(View.GONE);
                	break;
                case R.id.Eraser_Button:
                	mListener.OnToolsChanged(Mode.eraser);
                	Picture_table.setVisibility(View.GONE);
                	break;
                case R.id.Picture_Button:
                	mListener.OnToolsChanged(Mode.picture);
                	Picture_table.setVisibility(View.VISIBLE);
                	break;
                	
                
                }  
            }
        });  
        Picture_table = (ScrollView)v.findViewById(R.id.Picture_table);
        Picture_table.setVisibility(View.GONE);
        
        for (int i = 0;i<24;i++){
        	/*final String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/isee/Picture/Picture_"+ i +".png";
        	BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options(); 
            bmpFactoryOptions.inJustDecodeBounds = true; 
            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions); 
            bmpFactoryOptions.inJustDecodeBounds = false; 
            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
            
            //Bitmap temp_bmp = Bitmap.createScaledBitmap(bmp,1,1,true);*/
            
        	//final String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/isee/Picture/Picture_"+ i +".png";
        	final String imageFilePath = "Picture_"+ i +".png";
        	
            ImageView tempImageView = (ImageView)v.findViewById(R.id.Picture_0_0+i);
            
            tempImageView.setOnClickListener(new View.OnClickListener(){
            	public void onClick(View v) {
            		mListener.OnPictureSelected(imageFilePath);
            		mListener.OnToolsChanged(Mode.picture);
            		
            	}
            });
            
            //if (bmp != null && !bmp.isRecycled())            
            	//bmp.recycle();  
        }
        
        
        return v;  
    }  
    
    OnColorChangedListener mListener;
  
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
        	mListener =(OnColorChangedListener)activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");
        }
    }
}  