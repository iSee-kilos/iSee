package com.isee;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.isee.SetColorFragment.OnColorChangedListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.widget.ImageView;

public class DrawActivity extends Activity implements OnColorChangedListener{
	
	private ImageView altered_image;
    private Paint paint;
    private Paint mPaint;
    private Canvas canvas;
    private Canvas real_canvas;
    private Bitmap alterBitmap;
    private Bitmap real_alterBitmap;

    Bitmap newbmp;
    Matrix matrix;
    
    private String imageFilePath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		setContentView(R.layout.activity_draw);
		
		//设置工具和调色的菜单fragment
		FragmentManager fm = getFragmentManager();  
        
        addShowHideListener(R.id.Draw_Tools_Bar_Button, fm.findFragmentById(R.id.draw_tools));  
        addShowHideListener(R.id.Set_Color_Button, fm.findFragmentById(R.id.set_color));  
        
        //初始化画布
        
        altered_image = (ImageView) findViewById(R.id.altered_view);
		
		Intent intent = getIntent();
		String name=  intent.getStringExtra("file_location");
		if (name != null && !name.equals("null") )
			imageFilePath = name;
                
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options(); 
        bmpFactoryOptions.inJustDecodeBounds = true; 
        Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions); 
        bmpFactoryOptions.inJustDecodeBounds = false; 
        bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

        float screenWidth  = getWindowManager().getDefaultDisplay().getWidth();         
		float screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		newbmp = Bitmap.createScaledBitmap(bmp, (int)screenWidth, (int)screenHeight, true);
		
	
        alterBitmap = Bitmap.createBitmap(newbmp.getWidth(),
        		newbmp.getHeight(), newbmp.getConfig());
        real_alterBitmap = Bitmap.createBitmap(newbmp.getWidth(),
        		newbmp.getHeight(), newbmp.getConfig());
        canvas = new Canvas(alterBitmap);
        real_canvas = new Canvas(real_alterBitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);
        
        matrix = new Matrix();
        canvas.drawBitmap(newbmp, matrix, paint);
        
        altered_image.setImageBitmap(alterBitmap);
        altered_image.setOnTouchListener(imageButtonTouchListener);
        
        curr_mode = Mode.plain;
        
        //为橡皮设置单独的paint
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAntiAlias(true);
        
        
	}
	private float downx = 0;
    private float downy = 0;
    private float upx = 0;
    private float upy = 0;

    private long start_time;
    private Mode curr_mode;
    
    //贴图
    private Bitmap picture_source = null;
    private Bitmap altered_picture = null;
    private int on_touch_point = 0;
    private float curr_pic_x = 10;
    private float curr_pic_y = 10;
    private int click_count = 0;
    
    
  //Container Activity must implement this interface
    public void OnColorChanged(int value){
    	paint.setColor(value);
    }
    public void OnWidthChanged(int value){
    	paint.setStrokeWidth(value);
    }
    public void OnToolsChanged(Mode value){
    	curr_mode = value;
    }
    
    public void OnPictureSelected(String Path){
    	FragmentManager fm = getFragmentManager(); 
    	FragmentTransaction ftra = getFragmentManager().beginTransaction();  
        ftra.hide(fm.findFragmentById(R.id.draw_tools));  
        ftra.commit(); 
    	
        InputStream istream = null;
        istream =  getClass().getResourceAsStream("/assets/"+Path);
        
        picture_source = BitmapFactory.decodeStream(istream);
        altered_picture = Bitmap.createBitmap(picture_source);
        
        canvas.drawBitmap(altered_picture, curr_pic_x, curr_pic_y, paint);
        altered_image.invalidate();
    }
    
    
    private OnTouchListener imageButtonTouchListener = new OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
	        int action = event.getAction();
	        int[] location = new int[2];
	        altered_image.getLocationOnScreen(location);
	        
	        if (curr_mode == Mode.picture){
	        	
	        	switch (action) {
			        case MotionEvent.ACTION_DOWN:
			            downx = event.getX();//-location[0];
			            downy = event.getY();//-location[1];
			            on_touch_point = 1;
			            click_count++;
			            if(click_count == 1)
			            start_time = System.currentTimeMillis();
			            break;
			        case MotionEvent.ACTION_MOVE:
			            // 路径画板
			            
			            
			            if (on_touch_point >= 2) {  
			            	curr_pic_x += picture_source.getWidth()/2; 
			            	curr_pic_y += picture_source.getHeight()/2;
			            	float downx_2 = downx - upx;
			            	float downy_2 = downy - upy;
			            	float now_x = event.getX(0) - event.getX(1);  
			                float now_y = event.getY(0) - event.getY(1); 
			                
			                altered_picture = Bitmap.createScaledBitmap(picture_source,(int)(picture_source.getWidth()*now_x/downx_2),
			                		(int)(picture_source.getHeight()*now_y/downy_2), true);
			                curr_pic_x = (event.getX(0) + event.getX(1))/2 - altered_picture.getWidth()/2;  
			                curr_pic_y = (event.getY(0) + event.getY(1))/2 - altered_picture.getHeight()/2; 
			                canvas.drawBitmap(newbmp, matrix, paint);	
			                canvas.drawBitmap(real_alterBitmap, matrix, paint);
			                canvas.drawBitmap(altered_picture, curr_pic_x, curr_pic_y, paint);
			                
			            }  
			            if (on_touch_point == 1) {  
			            	upx = event.getX(); //-location[0];
				            upy = event.getY(); //-location[1];
			            	canvas.drawBitmap(newbmp, matrix, paint);
			            	canvas.drawBitmap(real_alterBitmap, matrix, paint);
			            	curr_pic_x += altered_picture.getWidth()/2; 
			            	curr_pic_y += altered_picture.getHeight()/2;
			            	curr_pic_x = upx - downx + curr_pic_x - picture_source.getWidth()/2;
			            	curr_pic_y = upy - downy + curr_pic_y - picture_source.getHeight()/2;
			            	canvas.drawBitmap(altered_picture, curr_pic_x, curr_pic_y, paint);
			            	downx = upx;
				            downy = upy;
			            } 
			            
			            altered_image.invalidate();// 刷新
			            break;
			        case MotionEvent.ACTION_UP:
			        case MotionEvent.ACTION_POINTER_1_UP:
			            on_touch_point = 0;
			            picture_source = Bitmap.createBitmap(altered_picture);
			            
			            if(click_count == 2 && 400 >= System.currentTimeMillis()-start_time){
			            	click_count = 0;
			            	real_canvas.drawBitmap(altered_picture, curr_pic_x, curr_pic_y, paint);
			            	canvas.drawBitmap(newbmp, matrix, paint);
				    		canvas.drawBitmap(real_alterBitmap, matrix, paint);
				    		altered_image.invalidate();
				    		curr_mode = Mode.plain;
				    		
				    		/*FragmentManager fm = getFragmentManager(); 
				        	FragmentTransaction ftra = getFragmentManager().beginTransaction();  
				            ftra.replace(R.id.DrawContainer, fm.findFragmentById(R.id.draw_tools));  
				            ftra.commit(); */
			            }
			            if(click_count == 2)
			            	click_count = 0;
			            
			            //altered_image.invalidate();// 刷新
			            break;
			        case MotionEvent.ACTION_POINTER_2_UP:  
			        	on_touch_point--;  
			            picture_source = Bitmap.createBitmap(altered_picture);
			            break;  
			        case MotionEvent.ACTION_POINTER_2_DOWN:  
			        	on_touch_point++;
			        	upx = event.getX(1); //-location[0];
			            upy = event.getY(1); //-location[1];
			            break;
			        default:
			            break;
		        }
	        	return true;
	        }
	        
	        switch (action) {
	        case MotionEvent.ACTION_DOWN:
	            downx = event.getX();//-location[0];
	            downy = event.getY();//-location[1];
	            start_time = System.currentTimeMillis();
	            break;
	        case MotionEvent.ACTION_MOVE:
	            // 路径画板
	            upx = event.getX(); //-location[0];
	            upy = event.getY(); //-location[1];
	            
	            DrawLineWithDifferentTool();
	            
	            downx = upx;
	            downy = upy;
	            altered_image.invalidate();// 刷新
	            break;
	        case MotionEvent.ACTION_UP:
	            // 直线画板
	            upx = event.getX();//-location[0];
	            upy = event.getY();//-location[1];

	            DrawLineWithDifferentTool();
	            
	            altered_image.invalidate();// 刷新
	            break;
	       // case MotionEvent.
	
	        default:
	            break;
	        }
	
	        return true;
	    }
    };
    
    private void DrawLineWithDifferentTool(){
    	float maxRadius = paint.getStrokeWidth();
    	    	
    	switch (curr_mode) {
	    	case plain:
		    	canvas.drawLine(downx, downy, upx, upy, paint);
		        canvas.drawCircle(downx, downy, paint.getStrokeWidth()/2, paint);
		        canvas.drawCircle(upx, upy, paint.getStrokeWidth()/2, paint);
		        real_canvas.drawLine(downx, downy, upx, upy, paint);
		        real_canvas.drawCircle(downx, downy, paint.getStrokeWidth()/2, paint);
		        real_canvas.drawCircle(upx, upy, paint.getStrokeWidth()/2, paint);
		        break;
	    	case spray:
		        paint.setStrokeWidth(1);
		        
		        for (int i = 0; i < 300; i++) { 
		        	double angle = (Math.random() * Math.PI * 2); 
		        	float radius = (float)Math.random() * maxRadius; 
		        	float xPos = upx + (float)Math.cos(angle) * radius; 
		        	float yPos = upy + (float)Math.sin(angle) * radius; 
		        	
		        	canvas.drawPoint(xPos, yPos, paint);
		        	real_canvas.drawPoint(xPos, yPos, paint);
		        	} 
		        paint.setStrokeWidth(maxRadius);
		        break;
	    	case brush:
	    		int base_color = paint.getColor();
	    		int delt_color = 0xff/(int)maxRadius;
	    		
	    		for (int i = 0; i+maxRadius/8 < maxRadius/2; i++) {
		        	base_color = (base_color & 0x00ffffff) | ((((int)maxRadius/2-i)*delt_color)<<24) ;
		        	paint.setColor(base_color);
		        	
		    		canvas.drawCircle(downx, downy, maxRadius/8+(2*i), paint);
			        canvas.drawCircle(upx, upy, maxRadius/8+(2*i), paint);
			        real_canvas.drawCircle(downx, downy, maxRadius/8+(2*i), paint);
			        real_canvas.drawCircle(upx, upy, maxRadius/8+(2*i), paint);
			        paint.setStrokeWidth(maxRadius/4+4*i);
			        canvas.drawLine(downx, downy, upx, upy, paint);
			        real_canvas.drawLine(downx, downy, upx, upy, paint);
		        }
		        paint.setStrokeWidth(maxRadius);
		        paint.setColor((base_color & 0x00ffffff) |0xff000000);
		        break;
	    	case advenced_brush:
	    		float advenced_maxRadius =  maxRadius * (System.currentTimeMillis()-start_time)/3000  + maxRadius;
	    		if (advenced_maxRadius>= maxRadius *1.5)
	    			advenced_maxRadius = maxRadius *(float)1.5;
	    		int advenced_base_color = paint.getColor();
	    		int advenced_delt_color = 0xff/(int)advenced_maxRadius;
	    		
		        for (int i = 0; i+advenced_maxRadius/8 < advenced_maxRadius/2; i++) {
		        	paint.setStrokeWidth(1);
		        	advenced_base_color = (advenced_base_color & 0x00ffffff) | ((((int)advenced_maxRadius/2-i)*advenced_delt_color)<<24) ;
		        	paint.setColor(advenced_base_color);
		    		canvas.drawCircle(downx, downy, advenced_maxRadius/8+(2*i), paint);
			        canvas.drawCircle(upx, upy, advenced_maxRadius/8+(2*i), paint);
			        real_canvas.drawCircle(downx, downy, advenced_maxRadius/8+(2*i), paint);
			        real_canvas.drawCircle(upx, upy, advenced_maxRadius/8+(2*i), paint);
			        paint.setStrokeWidth(advenced_maxRadius/4+4*i);
			        canvas.drawLine(downx, downy, upx, upy, paint);
			        real_canvas.drawLine(downx, downy, upx, upy, paint);
		        }
		        paint.setStrokeWidth(maxRadius);
		        paint.setColor((advenced_base_color & 0x00ffffff) |0xff000000);
		        break;
		        
	    	case eraser:
	    		mPaint.setStrokeWidth(maxRadius);
	    		real_canvas.drawLine(downx, downy, upx, upy, mPaint);
	    		real_canvas.drawCircle(downx, downy, paint.getStrokeWidth()/2, mPaint);
	    		real_canvas.drawCircle(upx, upy, paint.getStrokeWidth()/2, mPaint);
	    		canvas.drawBitmap(newbmp, matrix, paint);
	    		canvas.drawBitmap(real_alterBitmap, matrix, paint);
	    		break;
		    default:
		    	break;
        
    	}
    }
    

    void addShowHideListener(int buttonId, final Fragment fragment) {  
        //获取activity中的button  
        final View button = (View)findViewById(buttonId);  
        
        FragmentTransaction ftra = getFragmentManager().beginTransaction();  
        ftra.hide(fragment);  
        ftra.commit();  
        
        button.setOnClickListener(new OnClickListener() {  
        	
            public void onClick(View v) {  
            	FragmentTransaction ft = getFragmentManager().beginTransaction();  
                /*为Fragment设置淡入淡出效果，Android开发网提示这里这两个动画资源是android内部资源无需我们手动定义。*/  
                ft.setCustomAnimations(android.R.animator.fade_in,  
                        android.R.animator.fade_out);    
                  
                if (fragment.isHidden()) {  
                    ft.show(fragment);  
                    //button.setText("隐藏");  
                } else {  
                    ft.hide(fragment);  
                    
                    //button.setText("显示");  
                }  
                ft.commit();  
            }  
        });  
    }  

    public void Save_Button_Onclick(View view){
    	
    	 //Save to file
       //String nname= imageFilePath.replace(".jpg", "_altered.png");
       String name= Environment.getExternalStorageDirectory().getAbsolutePath() +"/data/isee/data/frame.png";
       // String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + nname; 
        try {
        	
        	//String name= DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
        	File fileName = new File(name);
        	FileOutputStream b = new FileOutputStream(fileName);  
        	real_alterBitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件 
            b.flush();
            b.close();
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }catch (IOException e) {  
            e.printStackTrace();  
        }
    	
    	Intent intent = new Intent();
        
		intent.putExtra("file_location", imageFilePath);
        /* 指定intent要启动的类 */
        intent.setClass(DrawActivity.this, MainActivity.class);
        /* 启动一个新的Activity */
        DrawActivity.this.startActivity(intent);
        /* 关闭当前的Activity */
        DrawActivity.this.finish();
	}	
    
}
    
    
    enum Mode{
    	plain,
    	spray,
    	brush,
    	advenced_brush,
    	eraser,
    	picture
    }