package gc.dtu.weeg.dtugc.myview;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ScrollTextView extends AppCompatTextView {

    public ScrollTextView(Context context) {
        super(context);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    float lastScrollY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d("zl","onTouchEvent");
//        Log.d("zl","Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT);
//        Log.d("zl","Build.VERSION_CODES.JELLY_BEAN: "+Build.VERSION_CODES.JELLY_BEAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            Log.d("zl","getLineCount(): "+getLineCount());
//            Log.d("zl","getMaxLines(): "+getMaxLines());
            scrollmothed(ev);
            if (getLineCount() > getMaxLines()) {
//                scrollmothed(ev);
            }
        }
        return super.onTouchEvent(ev);
    }

    private void scrollmothed(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastScrollY = ev.getRawY();
            Log.d("zl","down:"+lastScrollY);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            //滑动到头并且还在继续上滑动,或者滑动到底部就不要再拦截了(有误差)
//            int sum = getLineHeight() * getLineCount() - getLineHeight() * getMaxLines();
           int  sum=10000;
            //计算上次与本次差
            float diff = lastScrollY - ev.getRawY();
            if (diff>0){//下滑动并且到达了底部也不要处理了
                //底部这里用abs的原因是,因为计算sum的时候有些误差
                if (Math.abs(sum - getScrollY())<5) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }else if (diff<0){//上滑动
                if (getScrollY() == 0) {//上滑动并且已经到达了顶部就不要在处理了
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
            lastScrollY = ev.getRawY();
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
}
