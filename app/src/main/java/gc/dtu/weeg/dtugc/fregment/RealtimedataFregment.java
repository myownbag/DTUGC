package gc.dtu.weeg.dtugc.fregment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;


/**
 * Created by Administrator on 2018-03-22.
 */

public class RealtimedataFregment extends BaseFragment  implements View.OnClickListener {
    public View mView;
    public Button mBut;
    public TextView mPress1view;
    public TextView mPress2view;
    byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.real_time_data_layout, container, false);
        initView();
        return  mView;
    }

    private void initView() {
        mBut=mView.findViewById(R.id.realtime_but);
        mPress1view=mView.findViewById(R.id.real_time_press1);
        mPress2view=mView.findViewById(R.id.real_time_press2);
        mBut.setOnClickListener(this);
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
       // Log.d("zl", "OndataCometoParse: "+readOutMsg1);
        if(!mIsatart)
        {
            return;
        }
        int temp=0;
        if(readOutBuf1.length<5)
        {
            ToastUtils.showToast(getActivity(), "数据长度短");
            return;
        }
        else
        {
            if(readOutBuf1[3]!=(readOutBuf1.length-5))
            {
                ToastUtils.showToast(getActivity(), "数据长度异常");
                return;
            }
        }
        MainActivity.getInstance().mDialog.dismiss();
        ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(readOutBuf1,22,4);
        buf1.rewind();
        temp=buf1.getInt();
        if(temp==0)
        {
            mPress1view.setText("未连接");
        }
        else if(temp==0xffffffff)
        {
            mPress1view.setText("传感器故障");
        }
        else
        {

            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(readOutBuf1,22,4);
            buf1.rewind();
            float pressflaot =buf1.getFloat();
            mPress1view.setText(""+pressflaot+" kPa");

        }
        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(readOutBuf1,26,4);
        buf1.rewind();
        temp=buf1.getInt();
        if(temp==0)
        {
            mPress2view.setText("未连接");
        }
        else if(temp==0xffffffff)
        {
            mPress2view.setText("传感器故障");
        }
        else
        {
            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(readOutBuf1,26,4);
            buf1.rewind();
            float pressflaot =buf1.getFloat();
            mPress2view.setText(""+pressflaot+" kPa");
        }
    }

    @Override
    public void onClick(View v) {
        int index=0;
        int i;
        byte[] adsinf0=new byte[2];//={1,3,105, (byte) 0xC7};
        mIsatart=true;
        ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.putInt(6020);
        buf1.rewind();
        buf1.get(sendbufread,14,2);
        CodeFormat.crcencode(sendbufread);

        String readOutMsg = DigitalTrans.byte2hex(sendbufread);
        verycutstatus(readOutMsg);
        mIsatart=true;
        mPress1view.setText("");
        mPress2view.setText("");
    }
    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }
}
