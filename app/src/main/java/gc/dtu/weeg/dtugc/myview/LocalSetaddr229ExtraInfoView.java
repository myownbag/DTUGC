package gc.dtu.weeg.dtugc.myview;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.slidingbutton.BaseSlidingToggleButton;
import gc.dtu.weeg.dtugc.myview.slidingbutton.SlidingToggleButton;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class LocalSetaddr229ExtraInfoView extends LinearLayout {
    static  Context ttt;
    String mCursetstr;
    Context mActivity;
    SlidingToggleButton mCameraEn;
    EditText mCameraScan;
    TextView mStatus;
    TextView mLableShowInfo1;
    TextView mLableShowInfo2;
    View mView;
    public LocalSetaddr229ExtraInfoView(Context context, String setingstr) {
        super(context);
        mActivity = context;
        ttt = context;
        mCursetstr = setingstr;
        mView = inflate(context, R.layout.localsetting_addr222_layout,null);
        addView(mView);
        InitView();
    }

    private void InitView() {
        mCameraEn = mView.findViewById(R.id.reg222funenable);
        mCameraScan = mView.findViewById(R.id.reg222_scan_time);
        mStatus = mView.findViewById(R.id.reg222isenableinfo);
        mLableShowInfo1 = mView.findViewById(R.id.lable_show_info1);
        mLableShowInfo2 = mView.findViewById(R.id.lable_show_info2);

        mCameraEn.setOnCheckedChanageListener(new BaseSlidingToggleButton.OnCheckedChanageListener() {
            @Override
            public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
                if(isChecked == true)
                {
                    mStatus.setText("功能使能");
                }
                else
                {
                    mStatus.setText("功能禁止");
                }
            }
        });

        mLableShowInfo1.setText("报警延时");
        mLableShowInfo2.setText("延时时间(分)");

        byte[] test = strinfo2bytes(mCursetstr);

        if(test[0] == 1)
        {
            mCameraEn.setChecked(true);
        }
        else
        {
            mCameraEn.setChecked(false);
        }
        byte[] testbyt = new byte[2];
        testbyt[0] = test[1];
        testbyt[1] = 0;
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(testbyt,0,2) ;
        buf.rewind();
        String info = ""+buf.getShort();
        mCameraScan.setText(info);
    }

    static public String dacodetoStr(byte[] bytedecode)
    {
        String ttt = "";
        if(bytedecode[0] == 1)
        {
            ttt = "使能,";
        }
        else
        {
            ttt = "禁止,";
        }
        byte[] ttst = new byte[2];
        ttst[0] = bytedecode[1];
        ttst[1] = 0;
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(ttst,0,2)  ;
        buf.rewind();
        short test = buf.getShort();
//        if(test>10000 || test<-10000)
//        {
//            test = 0;
//        }
        ttt += test;
        return ttt;
    }
    public byte[] dacodeshowinfo()
    {
        byte[] buts = new byte[2];
        if(mCameraEn.isChecked() == true)
        {
            buts[0] = 1;
        }
        else
        {
            buts[0] = 0;
        }
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort(Short.parseShort(mCameraScan.getText().toString()));
        buf.rewind();
        buf.get(buts,1,1);
        return  buts;
    }
    static public byte[] strinfo2bytes(String setinfo)
    {
        byte[] but = new byte[2];
        if(setinfo !=null)
        {
            int index=-1;
            String temp;

            index = setinfo.indexOf(',');
            if(index>0)
            {
                temp = setinfo.substring(0,index);
                if(temp.equals("使能"))
                {
                    but[0] = 1;
                }
                else
                {
                    but[0] = 0;
                }

                setinfo = setinfo.substring(index+1);
                if(setinfo.matches("^[0-9]*[1-9][0-9]*$") == true)
                {
                    short ts = Short.parseShort(setinfo);

                    ByteBuffer buf = ByteBuffer.allocate(2);
                    buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.putShort(ts) ;
                    buf.rewind();
                    buf.get(but,1,1);
                }
                else
                {
                    ToastUtils.showToast(ttt,"正则判断异常");
                }
            }
        }

        return but;
    }
}
