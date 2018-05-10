package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.warkiz.widget.IndicatorSeekBar;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class BasicinfoFregment extends BaseFragment {
    private View mView;
    LayoutInflater thisinflater;
    ViewGroup thiscontainer;
    TextView DeviceID;
    TextView Softversion;
    TextView Timeinfo;
    TextView Signalinfo;
    Boolean  m_isTMStart;
    IndicatorSeekBar indicatorSeekBar;
    public Button butsend;
    public int currentposition=0;
    //public int timersec=30;
    //停止标记
    Boolean timerstop=false;
    int mIndexcmd=0;
    byte [][] senddatabuf=new byte[4][18];
    //倒计时
    CountDownTimer mytimer= new CountDownTimer(30000, 1000) {
        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            if(MainActivity.getInstance()!=null)
            {
                String readOutMsg = DigitalTrans.byte2hex(senddatabuf[3]);
                //verycutstatus(readOutMsg);
                BasicinfoFregment.this.butsend.setText("剩余:"+millisUntilFinished/1000);
                verycutstatus1(readOutMsg);
                //BasicinfoFregment.this.timersec--;
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onFinish() {
            ToastUtils.showToast(getActivity(), "已经测试了30秒，如需再测，请读取数据");
            butsend.setEnabled(true);
            butsend.setBackground(getActivity().getResources().getDrawable(R.drawable.round_button));
            BasicinfoFregment.this.butsend.setText("读取数据");
            m_isTMStart=false;
           // BasicinfoFregment.this.timersec=30;
        }
    };

    private void verycutstatus1(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        if(parentActivity1==null)
        {
            return;
        }
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        m_isTMStart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        thisinflater =inflater;
        thiscontainer=container;
        mView = inflater.inflate(R.layout.basicfragmentlayout, container, false);
        initView();
        initdata();
        return  mView;
    }

    private void initdata() {
//        MainActivity.getInstance().setOndataparse(new DataParse());
//        MainActivity.getInstance().SetonPageSelectedinviewpager(new onviewpagerchangedimp());
        butsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsatart=true;
                int index=0;
                byte[] adsinf0={1,3,105, (byte) 0xC7};
                mIndexcmd=0;
                for(int j=0;j<4;j++)
                {
                    senddatabuf[j][index++]= (byte) 0xfd;
                    senddatabuf[j][index++]= (byte) 0x00;
                    senddatabuf[j][index++]= (byte) 0x00;
                    senddatabuf[j][index++]= 13;
                    senddatabuf[j][index++]= (byte) 0x00;
                    senddatabuf[j][index++]= (byte) 0x19;
                    for(int i=0;i<8;i++)
                    {
                        senddatabuf[j][index++]= (byte) 0x00;
                    }
                    senddatabuf[j][index++]= adsinf0[j];
                    senddatabuf[j][index++]= (byte) 0x00;
                    CodeFormat.crcencode(senddatabuf[j]);
                    index=0;
                }
                if(mIndexcmd<3)
                {
                    String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd++]);
                    verycutstatus(readOutMsg);
                }

            }
        });
    }

    private void verycutstatus(String readOutMsg) {
        Log.d("zl",readOutMsg);
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

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        String temp;
        Log.d("zl",CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
        if(mIsatart==false)
        {
            return;
        }
        if(currentposition!=0)
        {
            return;
        }
        int  i=0;
        if(readOutBuf1.length<5)
        {
            ToastUtils.showToast(getActivity(), "数据长度短");
            return;
        }
        else
        {
            if(readOutBuf1[3]!=(readOutBuf1.length-5))
            {
                ToastUtils.showToast(getActivity(), "数据长度异常"+"当前解析："+mIndexcmd);

                return;
            }
        }
        switch (readOutBuf1[14])
        {
            case 1:
                temp="";
                for( i=16;i<24;i++)
                {
                    temp+=(char)readOutBuf1[i];
                }
                DeviceID.setText(temp);
                break;
            case 3:
                temp="";
                for( i=16;i<20;i++)
                {
                    temp+=(char)readOutBuf1[i];
                }
                Softversion.setText(temp);
                break;
            case 105:
                StringBuilder temp1=new StringBuilder();;
                temp1.append(String.format("%x-%x-%x %x %x:%x:%x ", readOutBuf1[16],readOutBuf1[17],readOutBuf1[18],readOutBuf1[19]
                        ,readOutBuf1[20],readOutBuf1[21],readOutBuf1[22]));

                Timeinfo.setText(temp1.toString());
                break;
            case (byte) 0xC7:
//                switch (readOutBuf1[16])
//                {
//                    case 0x01:
//                        indicatorSeekBar.setProgress(25);
//                        break;
//                    case 0x02:
//                        indicatorSeekBar.setProgress(50);
//                        break;
//                    case 0x04:
//                        indicatorSeekBar.setProgress(75);
//                        break;
//                    case (byte) 0x08:
//                        indicatorSeekBar.setProgress(100);
//                        mytimer.cancel();
//                        timerstop=true;
//                        break;
//                    default:
//                        indicatorSeekBar.setProgress(0);
//                        break;
//                }
                int status=0x000000FF&readOutBuf1[16];
                if(status<2)
                {
                    indicatorSeekBar.setProgress(0);
                }
                if(status<=2)
                {
                    indicatorSeekBar.setProgress(25);
                }
                else if(status<=3)
                {
                    indicatorSeekBar.setProgress(50);
                }
                else if(status<=7)
                {
                    indicatorSeekBar.setProgress(75);
                }
                else if(status<=15)
                {
                    indicatorSeekBar.setProgress(100);
                }
                temp=""+readOutBuf1[17];
               // Log.d("zl","temp");
                Signalinfo.setText(temp);
                break;
            default:
                break;
        }
        if(mIndexcmd<3)
        {
            String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd++]);
            verycutstatus(readOutMsg);
        }
        else
        {
            if(timerstop==true)
            {
                timerstop=false;
                mytimer.cancel();
            }
            else
            {
                if(m_isTMStart==false)
                {
                    m_isTMStart=true;
                    mytimer.start();
                }
                butsend.setEnabled(false);
                butsend.setBackgroundColor(this.getResources().getColor(R.color.color_unselected));
            }
            MainActivity activity= (MainActivity) getActivity();
            activity.mDialog.dismiss();
        }

    }

//    private class DataParse implements MainActivity.Ondataparse
//    {
//
//        @Override
//        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {
//
//        }
//    }
//    public class onviewpagerchangedimp implements MainActivity.OnPageSelectedinviewpager
//    {
//
//        @Override
//        public void currentviewpager(int position) {
//            if(position!=0)
//            {
//                mytimer.cancel();
//            }
//        }
//    }

    @SuppressLint("NewApi")
    @Override
    public void Oncurrentpageselect(int index) {
        currentposition=index;
        if(index!=0)
        {
            mytimer.cancel();
            mIsatart=false;
        }
        else
        {
            if(butsend.getText().equals("读取数据")==false)
            {
                butsend.setEnabled(true);
                butsend.setBackground(getActivity().getResources().getDrawable(R.drawable.round_button));
                BasicinfoFregment.this.butsend.setText("读取数据");
                m_isTMStart=false;
            }
        }
    }

    private void initView() {
        CharSequence [] mylabe={"开始","接收","检卡","连接","握手"};
        indicatorSeekBar = mView.findViewById(R.id.discrete);
        indicatorSeekBar.setTextArray(mylabe);
        indicatorSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
//        float a=indicatorSeekBar.getMax();
//        indicatorSeekBar.setProgress(a*3/5);
        DeviceID=mView.findViewById(R.id.tv_basic_sernum);
        Softversion=mView.findViewById(R.id.tv_basic_softversion);
        Timeinfo=mView.findViewById(R.id.tv_basic_time);
        Signalinfo=mView.findViewById(R.id.tv_basic_signal);
        butsend = mView.findViewById(R.id.tv_basic_btn_write);
    }
}
