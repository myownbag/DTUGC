package gc.dtu.weeg.dtugc.myview.slidingbutton;


import android.annotation.SuppressLint;
import android.content.Context;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * 滑动开关按钮
 */
public abstract class BaseSlidingToggleButton extends View implements OnGestureListener, OnDoubleTapListener, GestureDetector.OnGestureListener{
    private static final int DURATION = 300;
    private static final int MIN_ROLLING_DISTANCE = 30;//滚动最小生效距离
    private GestureDetector gestureDetector;//手势识别器
    private Scroller scroller;//滚动器
    private Bitmap stateNormalBitmap;//正常状态时的状态图片
    private Bitmap stateDisableBitmap;//禁用状态时的状态图片
    private Bitmap stateMaskBitmap;//状态遮罩图片
    private Bitmap frameBitmap;//框架图片
    private Bitmap sliderNormalBitmap;//正常状态时的滑块图片
    private Bitmap sliderPressedBitmap;//按下状态时的滑块图片
    private Bitmap sliderDisableBitmap;//禁用状态时的滑块图片
    private Bitmap sliderMaskBitmap;//滑块遮罩图片
    private Paint paint;//颜料
    private PorterDuffXfermode porterDuffXfermode;//遮罩类型
    private boolean checked;//状态，true：开启；false：关闭
    private int currentLeft;//当前状态图以及滑块图的X坐标
    private int checkedLeft;//当状态为开启时状态图以及滑块图的X坐标
    private int uncheckedLeft;//当状态为关闭时状态图以及滑块图的X坐标
    private int scrollDistanceCount;//滚动距离计数器
    private boolean needHandle;//当在一组时件中发生了滚动操作时，在弹起或者取消的时候就需要根据滚动的距离来切换状态或者回滚
    private boolean down;//是否按下，用来在弹起的时候，恢复状态图以及滑块的状态
    private boolean enabled;//是否可用，表示当前视图的激活状态
    private OnCheckedChanageListener onCheckedChanageListener;//状态改变监听器
    private boolean pendingSetState;//在调用setState()来设置初始状态的时候，如果onLeft字段还没有初始化（在Activity的onCreate()中调用此setState的时候就会出现这种情况），那么就将此字段标记为true，等到在onDraw()方法中初始化onLeft字段时，会检查此字段，如果为true就会再次调用setState()设置初始状态
    private boolean pendingChecked;//记录默认状态值

    //imgaview 的尺寸
    public BitmapFactory.Options options = new BitmapFactory.Options();
//    public  int mViewHigh;
//    public  int mViewWith;

    //显示模式
    final public int  SHOEMODE=0;  //1 apple style mode others android mode
  
    public BaseSlidingToggleButton(Context context) {  
        //super(context);
      this(context,null);
    }  
      
    public BaseSlidingToggleButton(Context context, AttributeSet attrs) {  
        super(context, attrs);
        init();
    }  
      
    private final void init(){  
        gestureDetector = new GestureDetector(getContext(), this);  
        gestureDetector.setOnDoubleTapListener(this);  
          
        stateNormalBitmap = onGetStateNormalBitmap();  
        if(stateNormalBitmap == null){  
            throw new RuntimeException("onGetStateNormalBitmap() The return value cannot be null");  
        }  
//        Log.d("zl","bitmap:"+stateNormalBitmap.getWidth());
        stateDisableBitmap = onGetStateDisableBitmap();  
        if(stateDisableBitmap == null){  
            stateDisableBitmap = stateNormalBitmap;  
        }  
          
        stateMaskBitmap = onGetStateMaskBitmap();  
        if(stateMaskBitmap == null){  
            throw new RuntimeException("onGetStateMasklBitmap() The return value cannot be null");  
        }  
          
        frameBitmap = onGetFrameBitmap();  
        if(frameBitmap == null){  
            throw new RuntimeException("onGetFrameBitmap() The return value cannot be null");  
        }  
          
        sliderNormalBitmap = onGetSliderNormalBitmap();  
        if(sliderNormalBitmap == null){  
            throw new RuntimeException("onGetSliderNormalBitmap() The return value cannot be null");  
        }  
          
        sliderPressedBitmap = onGetSliderPressedBitmap();  
        if(sliderPressedBitmap == null){  
            sliderPressedBitmap = sliderNormalBitmap;  
        }  
          
          
        sliderDisableBitmap = onGetSliderDisableBitmap();  
        if(sliderDisableBitmap == null){  
            sliderDisableBitmap = sliderNormalBitmap;  
        }  
          
        sliderMaskBitmap = onGetSliderMaskBitmap();  
        if(sliderMaskBitmap == null){  
            throw new RuntimeException("onGetSliderMaskBitmap() The return value cannot be null");  
        }  
          
        paint = new Paint();  
        paint.setFilterBitmap(false);  
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);  
        scroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());  
        enabled = isEnabled();  
    }  
      
    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas) {
        //初始化状态为开启时状态图以及滑块图的X坐标
        if(checkedLeft == 0){  
            checkedLeft = -1 * (stateNormalBitmap.getWidth() - frameBitmap.getWidth());//ѡ��ʱ��X�������״̬��Ŀ�ȼ�ȥ��ܲ�Ŀ�ȵĸ�ֵ  
            //�������Ҫ���õ�״̬  
            if(pendingSetState){  
                pendingSetState = false;  
                setChecked(pendingChecked, 0);  
//                Log.d("slidebut","the checkedleft at onDraw init= "+checkedLeft);
            }  
        }

        //创建一个新的全透明图层，大小同当前视图的大小一样，这一步绝对不可缺少，要不然最周绘制出来的图片背景会是黑色的
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);  
//        if(SHOEMODE==1)
//        {
//        	currentLeft=checkedLeft;
//        }
        //绘制状态层
        canvas.drawBitmap(enabled?stateNormalBitmap:stateDisableBitmap, currentLeft, 0, paint);  
        paint.setXfermode(porterDuffXfermode);  
        canvas.drawBitmap(stateMaskBitmap, 0, 0, paint);//使用遮罩模式只显示状态层中和状态遮罩重合的部分
        paint.setXfermode(null);//因为是共用一个Paint，所以要立马清除掉遮罩效果

        //绘制框架层
        canvas.drawBitmap(frameBitmap, 0, 0, paint);

        //绘制滑块层
        if(enabled){  
        	canvas.drawBitmap(down?sliderPressedBitmap:sliderNormalBitmap, currentLeft, 0, paint);  
        }else{  
            canvas.drawBitmap(sliderDisableBitmap, currentLeft, 0, paint);  
        }  
        paint.setXfermode(porterDuffXfermode);  
        canvas.drawBitmap(sliderMaskBitmap, 0, 0, paint);//使用遮罩模式只显示滑块层中和滑块遮罩重合的部分
        paint.setXfermode(null);//因为是共用一个Paint，所以要立马清除掉遮罩效果
          
        //合并图层
        canvas.restore();  
  
        super.onDraw(canvas);  
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        //������
//       int mViewWith = MeasureSpec.getSize(widthMeasureSpec);
//      int  mViewHigh = MeasureSpec.getSize(heightMeasureSpec);
//        options.outHeight = MeasureSpec.getSize(heightMeasureSpec);
//        options.outWidth = MeasureSpec.getSize(widthMeasureSpec);
//
//        Log.d("zl","mView:"+mViewWith);

        int realWidthSize = 0;  
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//������Ȳο�����  
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);//������ȳߴ�  
        switch (widthMode) {  
            case MeasureSpec.AT_MOST://���widthSize�ǵ�ǰ��ͼ��ʹ�õ������  
                realWidthSize = frameBitmap.getWidth();  
                break;  
            case MeasureSpec.EXACTLY://���widthSize�ǵ�ǰ��ͼ��ʹ�õľ��Կ��  
                realWidthSize = widthSize;  
                break;  
            case MeasureSpec.UNSPECIFIED://���widthSize�Ե�ǰ��ͼ��ȵļ���û���κβο�����  
                realWidthSize = frameBitmap.getWidth();  
                break;  
        }  
          
        //����߶�  
        int realHeightSize = 0;  
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);//�����ο�����  
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);//�����߶ȳߴ�  
        switch (heightMode) {  
            case MeasureSpec.AT_MOST://���heightSize�ǵ�ǰ��ͼ��ʹ�õ����߶�  
                realHeightSize = frameBitmap.getHeight();  
                break;  
            case MeasureSpec.EXACTLY://���heightSize�ǵ�ǰ��ͼ��ʹ�õľ��Ը߶�  
                realHeightSize = heightSize;  
                break;  
            case MeasureSpec.UNSPECIFIED://���heightSize�Ե�ǰ��ͼ�߶ȵļ���û���κβο�����  
                realHeightSize = frameBitmap.getHeight();  
                break;  
        }  
          
        setMeasuredDimension(realWidthSize, realHeightSize);  
    }  
      
    @Override  
    public void computeScroll() {  
        //��������ڹ�������ô�͸���X���겢ˢ��  
        if(scroller.computeScrollOffset()){  
            currentLeft = scroller.getCurrX();  
//            Log.d("slidebut", "currentleft at on computeScorll = "+currentLeft);
            invalidate();  
        }  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if(enabled){  
            //�Ⱦ�������ʶ�����Ĵ���  
            gestureDetector.onTouchEvent(event);  
              
            //�����ǰ�¼�ʹ�������ȡ��  
            if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP){  
                //���֮ǰ�����˰����¼�����ô��ʱһ��Ҫ�ָ���ʾ�Ļ���ͼƬΪ����״̬ʱ��ͼƬ  
                if(down){  
                    down = false;  
                    invalidate();  
                }  
                  
                //��������¼��з����˻�������ô��ʱ��Ҫ�ж��Ƿ���Ҫ�л�״̬������Ҫ�ع���ԭ����λ��  
                if(needHandle){  
                    //������ι����ľ��볬������С��Ч���룬���л�״̬������ͻع�  
                    if(Math.abs(scrollDistanceCount) >= MIN_ROLLING_DISTANCE){  
                        setChecked(scrollDistanceCount > 0, currentLeft, DURATION);  
                    }else{  
                        setChecked(isChecked(), currentLeft, DURATION);  
                    }  
                    needHandle = false;  
                }  
            }  
        }  
        return true;  
    }  
  
    @Override  
    public boolean onDown(MotionEvent e) {  
        scrollDistanceCount = 0;  
        needHandle = false;  
          
        //�л�����ͼƬ��״̬  
        down = true;  
        invalidate();  
        return true;  
    }  
  
    @Override  
    public void onShowPress(MotionEvent e) {  
    }  
  
    @Override  
    public boolean onSingleTapUp(MotionEvent e) {  
        toggle();  
        return true;  
    }  
  
    @Override  
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {  
        needHandle = true;//����ڵ����ȡ����ʱ����Ҫ����  
        scrollDistanceCount += distanceX;//��¼�����ܵĻ����ľ���  
        currentLeft -= distanceX;//���������״̬���Լ���������X����  
//        Log.d("slidebut", "scrollDistanceCount at onScroll = "+scrollDistanceCount);
//        Log.d("slidebut", "currentLeft at onScroll = "+currentLeft);
        //��ֹ�����Ĺ����г�����Χ  
        if(currentLeft >= uncheckedLeft){  
            currentLeft = uncheckedLeft;  
        }else if(currentLeft <= checkedLeft){  
            currentLeft = checkedLeft;  
        }  
        invalidate();  
        return true;  
    }  
  
    @Override  
    public void onLongPress(MotionEvent e) {  
    }  
  
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
        needHandle = false;//����ڵ����ȡ��ʱ���ٴ���  
        setChecked(e2.getX() < e1.getX(), currentLeft, DURATION);//����ǰ������X����Ĵ�С���жϽ�����˭Ҫ�л�Ϊ����״̬���ǹر�״̬  
        return true;  
    }  
  
    @Override  
    public boolean onSingleTapConfirmed(MotionEvent e) {  
        return true;  
   }  
  
   @Override  
   public boolean onDoubleTap(MotionEvent e) {  
        return true;  
   }  
  
   @Override  
   public boolean onDoubleTapEvent(MotionEvent e) {  
        return true;  
   }  
     
    @Override  
    public void setEnabled(boolean enabled) {  
        super.setEnabled(enabled);  
        this.enabled = enabled;  
    }  
  
    public boolean isChecked() {  
        return checked;  
    }  
      
    /** 
     * ���� 
     * @param startX ��ʼX���� 
     * @param endX ����Y���� 
     * @param duration ����ʱ�� 
     */  
    private void scroll(int startX, int endX, int duration){  
        //����ʼλ�úͽ���λ��һ��ʱ������  
        if(startX != endX){  
            scroller.startScroll(startX, 0, endX - startX, 0, duration);  
            invalidate();  
        }  
    }  
      
    /** 
     * ����״̬ 
     * @param isChecked �������ǹر� 
     * @param startX ��ʼ������λ�� 
     * @param duration ����ʱ�� 
     */  
    private void setChecked(boolean isChecked, int startX, int duration){  
        this.checked = isChecked;  
        //�����Ҫ����  
        if(isChecked()){  
            scroll(startX, checkedLeft, duration);  
        }else{  
            scroll(startX, uncheckedLeft, duration);  
        }  
        //����ѡ��״̬�ı�ص�  
        if(onCheckedChanageListener != null){  
            onCheckedChanageListener.onCheckedChanage(this, isChecked());  
        }  
    }  
      
    /** 
     * ����״̬ 
     * @param isChecked �������ǹر� 
     * @param duration ����ʱ�� 
     */  
    public void setChecked(boolean isChecked, int duration){  
        //�����δ��ɳ�ʼ�������������ӳ٣��ȴ���ʼ�����֮���ٴ���  
        if(checkedLeft == 0){  
            pendingSetState = true;  
            pendingChecked = isChecked;  
        }else{  
            setChecked(isChecked, isChecked?uncheckedLeft:checkedLeft, duration);  
        }  
    }  
      
    /** 
     * ����״̬ 
     * @param isChecked �������ǹر� 
     */  
    public void setChecked(boolean isChecked){  
        setChecked(isChecked, DURATION);  
    }  
      
    /** 
     * �л�״̬ 
     * @param duration ����ʱ�� 
     */  
    public void toggle(int duration){  
        setChecked(!isChecked(), duration);  
    }  
      
    /** 
     * �л�״̬ 
     */  
    public void toggle(){  
        setChecked(!isChecked());  
    }  
      
    /** 
     * ����ѡ��״̬�ı������ 
     * @param onCheckedChanageListener ѡ��״̬�ı������ 
     */  
    public void setOnCheckedChanageListener(OnCheckedChanageListener onCheckedChanageListener) {  
        this.onCheckedChanageListener = onCheckedChanageListener;  
    }  
  
    /** 
     * ѡ��״̬�ı������ 
     */  
    public interface OnCheckedChanageListener{  
        /** 
         * ��ѡ��״̬�����ı� 
         * @param slidingToggleButton 
         * @param isChecked �Ƿ�ѡ�� 
         */  
        public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked);  
    }  
      
    /** 
     * ��ȡ����״̬ʱ��״̬ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetStateNormalBitmap();  
    /** 
     * ��ȡ����״̬ʱ��״̬ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetStateDisableBitmap();  
    /** 
     * ��ȡ״̬����ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetStateMaskBitmap();  
    /** 
     * ��ȡ���ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetFrameBitmap();  
    /** 
     * ��ȡ����״̬ʱ�Ļ���ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetSliderNormalBitmap();  
    /** 
     * ��ȡ����״̬ʱ�Ļ���ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetSliderPressedBitmap();  
    /** 
     * ��ȡ����״̬ʱ�Ļ���ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetSliderDisableBitmap();  
    /** 
     * ��ȡ��������ͼƬ 
     * @return 
     */  
    public abstract Bitmap onGetSliderMaskBitmap();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}