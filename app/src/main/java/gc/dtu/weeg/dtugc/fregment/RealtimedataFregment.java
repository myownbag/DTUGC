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
import gc.dtu.weeg.dtugc.utils.Constants;
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
    public TextView mWarnview;
    public TextView mTempview;
    public TextView mPressview;
    public TextView mFluxview;
    public TextView mRealFluxview;
    public TextView mVolumeView;
    //public TextView mRealVolumeview;

    public TextView mTempview1;
    public TextView mPressview1;
    public TextView mFluxview1;
    public TextView mRealFluxview1;
    public TextView mVolumeView1;

    public int step=0;

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
        mWarnview=mView.findViewById(R.id.real_time_warn_addr);
        mPress1view=mView.findViewById(R.id.real_time_press1);
        mPress2view=mView.findViewById(R.id.real_time_press2);

        mTempview=mView.findViewById(R.id.real_tem_timeinfo);
        mPressview =mView.findViewById(R.id.real_press_timeinfo);
        mFluxview =mView.findViewById(R.id.real_flux_timeinfo);
        mRealFluxview=mView.findViewById(R.id.real_realflux_timeinfo);
        mVolumeView =mView.findViewById(R.id.real_volume_timeinfo);
//        mRealVolumeview=mView.findViewById(R.id.real_realvolume_timeinfo);
        mTempview1=mView.findViewById(R.id.real_tem1_timeinfo);
        mPressview1 =mView.findViewById(R.id.real_press1_timeinfo);
        mFluxview1 =mView.findViewById(R.id.real_flux1_timeinfo);
        mRealFluxview1=mView.findViewById(R.id.real_realflux1_timeinfo);
        mVolumeView1 =mView.findViewById(R.id.real_volume1_timeinfo);


        mBut.setOnClickListener(this);
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl", "OndataCometoParse: "+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length)+"\r\nStep:"+step);
        ByteBuffer buf1;
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
        if(step==0)
        {


            parsecurrentdata(readOutBuf1,18,mWarnview,"",Constants.PARSE_FLOAT1);
            parsecurrentdata(readOutBuf1,22,mPress1view,"Kpa",Constants.PARSE_FLOAT1);
            parsecurrentdata(readOutBuf1,26,mPress2view,"Kpa",Constants.PARSE_FLOAT1);


            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.putInt(6130);
            buf1.rewind();
            buf1.get(sendbufread,14,2);
            CodeFormat.crcencode(sendbufread);
            String readOutMsg = DigitalTrans.byte2hex(sendbufread);
            verycutstatus(readOutMsg);
            step++;
            return;
        }

        else if(step==1)
        {
            parsecurrentdata(readOutBuf1,18,mTempview,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,22,mPressview,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,26,mFluxview,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,30,mRealFluxview,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,34,mVolumeView,"",Constants.PARSE_INT);
//            parsecurrentdata(readOutBuf1,38,mRealVolumeview,"",Constants.PARSE_FLOAT2);


            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.putInt(6131);
            buf1.rewind();
            buf1.get(sendbufread,14,2);
            CodeFormat.crcencode(sendbufread);
            String readOutMsg = DigitalTrans.byte2hex(sendbufread);
            verycutstatus(readOutMsg);
            step++;
            return;
        }
        else if(step==2)
        {
            parsecurrentdata(readOutBuf1,18,mTempview1,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,22,mPressview1,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,26,mFluxview1,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,30,mRealFluxview1,"",Constants.PARSE_FLOAT2);
            parsecurrentdata(readOutBuf1,34,mVolumeView1,"",Constants.PARSE_INT);

            MainActivity.getInstance().mDialog.dismiss();
        }
    }

    private void parsecurrentdata(byte[] readOutBuf1,int offset,TextView view,String unit,int parsetype) {
        int temp;ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(readOutBuf1,offset,4);
        buf1.rewind();
        temp=buf1.getInt();
        if(parsetype== Constants.PARSE_INT)
        {
            view.setText(""+temp+unit);
            return;
        }
        else
        {
            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(readOutBuf1,offset,4);
            buf1.rewind();
            float pressflaot =buf1.getFloat();
            if(parsetype==Constants.PARSE_FLOAT1)
            {
                if(temp==0xffffffff)
                {
                    view.setText("传感器故障");
                }
                else
                {
                    view.setText(""+pressflaot+unit);
                }
            }
            else if (parsetype==Constants.PARSE_FLOAT2)
            {
                view.setText(""+pressflaot+unit);
            }
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
        clearshow();
        step=0;
    }

    private void clearshow() {
        mWarnview.setText("");
        mPress1view.setText("");
        mPress2view.setText("");

        mTempview.setText("");
        mPressview.setText("");
        mFluxview.setText("");
        mRealFluxview.setText("");
        mVolumeView.setText("");
//        mRealVolumeview.setText("");

        mTempview1.setText("");
        mPressview1.setText("");
        mFluxview1.setText("");
        mRealFluxview1.setText("");
        mVolumeView1.setText("");
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
