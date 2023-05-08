package gc.dtu.weeg.dtugc.myview;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.slidingbutton.SlidingToggleButton;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class LocalSetaddr222ExtraInfoView extends LinearLayout {
    static  Context ttt;
    String mCursetstr;
    Context mActivity;
    SlidingToggleButton mCameraEn;
    EditText mCameraScan;
    View mView;
    public LocalSetaddr222ExtraInfoView(Context context, String setingstr) {
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

        byte[] test = strinfo2bytes(mCursetstr);

        if(test[0] == 1)
        {
            mCameraEn.setChecked(true);
        }
        else
        {
            mCameraEn.setChecked(false);
        }

        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(test,1,2) ;
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

        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(bytedecode,1,2)  ;
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
        byte[] buts = new byte[3];
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
        buf.get(buts,1,2);
        return  buts;
    }
    static public byte[] strinfo2bytes(String setinfo)
    {
        byte[] but = new byte[3];
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
                    buf.get(but,1,2);
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
