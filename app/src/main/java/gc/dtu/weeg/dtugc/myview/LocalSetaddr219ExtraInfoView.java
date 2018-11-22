package gc.dtu.weeg.dtugc.myview;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import gc.dtu.weeg.dtugc.R;

public class LocalSetaddr219ExtraInfoView extends LinearLayout {

    Context mActivity;
    View myview;
    public LocalSetaddr219ExtraInfoView(Context context) {
        super(context);
        mActivity = context;
        myview = View.inflate(mActivity,R.layout.localsetting_addr219_layout,null);
        addView(myview);
    }
}
