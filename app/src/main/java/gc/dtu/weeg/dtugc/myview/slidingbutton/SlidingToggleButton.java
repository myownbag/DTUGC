package gc.dtu.weeg.dtugc.myview.slidingbutton;




import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import gc.dtu.weeg.dtugc.R;

/** 
* �������ذ�ť 
*/  
public class SlidingToggleButton extends BaseSlidingToggleButton {

	   
     public SlidingToggleButton(Context context) {  
          //super(context);
         this(context,null);
     }  
      
     public SlidingToggleButton(Context context, AttributeSet attrs) {
         super(context, attrs);
       //  options.inDensity=240;
//        options.inTargetDensity=240;
//         options.inTargetDensity=TypedValue.DENSITY_NONE;
     }



     @Override
     public Bitmap onGetStateNormalBitmap() {

    	 if(SHOEMODE==1)
//    		 return BitmapFactory.decodeResource(getResources(), R.drawable.btn_sliding_state_normalr);
             return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_state_normalr);
    	 else
    		 return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_state_normal);
     }  
  
     @Override  
     public Bitmap onGetStateDisableBitmap() {
      	 if(SHOEMODE==1)
      		 return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_state_disabler);
      	 else
      		 return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_state_disable);
     }  
  
     @Override  
     public Bitmap onGetStateMaskBitmap() {
          return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_state_mask);
     }  
  
     @Override  
     public Bitmap onGetFrameBitmap() {
          return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_frame);
     }  
  
     @Override  
     public Bitmap onGetSliderNormalBitmap() {
          return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_slider_normal);
     }  
  
     @Override  
     public Bitmap onGetSliderPressedBitmap() {
          return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_slider_pressed);
     }  
  
     @Override  
     public Bitmap onGetSliderDisableBitmap() {  
          return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_slider_disable);
     }  
  
     @Override  
     public Bitmap onGetSliderMaskBitmap() {
          return BitmapFactory.decodeResource(getResources(), R.mipmap.btn_sliding_slider_mask);
     }

	@Override
	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.d("zl", "onGestureStarted");
		
	}

	@Override
	public void onGesture(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.d("zl", "onGesture");
		
	}

	@Override
	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.d("zl", "onGestureEnded");
		
	}

	@Override
	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.d("zl", "onGestureCancelled");
		
	}  
}