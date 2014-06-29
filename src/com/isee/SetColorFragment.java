package com.isee;

import android.app.Fragment;  
import android.os.Bundle;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.TextView; 
import android.widget.SeekBar;
import android.app.Activity;

public class SetColorFragment extends Fragment {  
    TextView mTextView; 
    TextView Tv1;  
  
    private SeekBar Red_Bar = null;
    private SeekBar Green_Bar = null;
    private SeekBar Blue_Bar = null;
    private SeekBar Width_Bar = null;
    
    private int Red_Value = 0;
    private int Green_Value = 0;
    private int Blue_Value = 0;
    private int Width_Value = 0;
    private int Value = 0xFF000000;
    
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        //从文件 example_fragment.xml 加载了一个layout   
        View v = inflater.inflate(R.layout.fragment_set_color, container, true);  
          
        Red_Bar = (SeekBar) v.findViewById(R.id.Red_Bar);
        Green_Bar = (SeekBar) v.findViewById(R.id.Green_Bar);
        Blue_Bar = (SeekBar) v.findViewById(R.id.Blue_Bar);
        Width_Bar = (SeekBar) v.findViewById(R.id.Width_Bar);
        
        Red_Bar.setMax(255);
        Green_Bar.setMax(255);
        Blue_Bar.setMax(255);
        Width_Bar.setMax(200);
       
        Green_Bar.setProgress(255);
        Width_Bar.setProgress(20);
        
        Red_Bar.setOnSeekBarChangeListener(RedBarListener);
        Green_Bar.setOnSeekBarChangeListener(GreenBarListener);
        Blue_Bar.setOnSeekBarChangeListener(BlueBarListener);
        Width_Bar.setOnSeekBarChangeListener(WidthBarListener);
        
        Tv1= (TextView)v.findViewById(R.id.test1);
          
        return v;  
    }  
  
  //Container Activity must implement this interface
    public interface OnColorChangedListener{
        public void OnColorChanged(int value);
        public void OnWidthChanged(int value);
        public void OnToolsChanged(Mode value);
        public void OnPictureSelected(String Path);
    }
    
    OnColorChangedListener mListener;
    
    private SeekBar.OnSeekBarChangeListener RedBarListener = new SeekBar.OnSeekBarChangeListener() {
    	public void onStopTrackingTouch(SeekBar seekBar) {}
    	public void onStartTrackingTouch(SeekBar seekBar) {}
    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    		Red_Value = progress;
    		Value = (Value & 0xFF00FFFF) | Red_Value<<16;
    		Tv1.setText(Integer.toHexString(Value));
    		mListener.OnColorChanged(Value);
	    }
    };
    private SeekBar.OnSeekBarChangeListener GreenBarListener = new SeekBar.OnSeekBarChangeListener() {
    	public void onStopTrackingTouch(SeekBar seekBar) {}
    	public void onStartTrackingTouch(SeekBar seekBar) {}
    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    		Green_Value = progress;
    		Value = (Value & 0xFFFF00FF) | Green_Value<<8;
    		Tv1.setText(Integer.toHexString(Value));
    		mListener.OnColorChanged(Value);
	    }
    };
    private SeekBar.OnSeekBarChangeListener BlueBarListener = new SeekBar.OnSeekBarChangeListener() {
    	public void onStopTrackingTouch(SeekBar seekBar) {}
    	public void onStartTrackingTouch(SeekBar seekBar) {}
    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    		Blue_Value = progress;
    		Value = (Value & 0xFFFFFF00) | Blue_Value;
    		Tv1.setText(Integer.toHexString(Value));
    		mListener.OnColorChanged(Value);
	    }
    };
    
    private SeekBar.OnSeekBarChangeListener WidthBarListener = new SeekBar.OnSeekBarChangeListener() {
    	public void onStopTrackingTouch(SeekBar seekBar) {}
    	public void onStartTrackingTouch(SeekBar seekBar) {}
    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    		Width_Value = progress;
    		//Value = (Value & 0xFFFFFF00) | Blue_Value;
    		Tv1.setText(Integer.toHexString(Width_Value));
    		mListener.OnWidthChanged(Width_Value);
	    }
    };
    
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
        	mListener =(OnColorChangedListener)activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");
        }
    }
}  