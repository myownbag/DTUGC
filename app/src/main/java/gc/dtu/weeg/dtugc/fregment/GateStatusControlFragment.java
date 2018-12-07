package gc.dtu.weeg.dtugc.fregment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;



import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class GateStatusControlFragment extends BaseFragment {

    View mView;
    TextView mResultinfotextview;
    Button  mButton;
    RadioGroup mSelectitem;
    int mCheckedindex=0;
    byte responecontrol[] = {(byte)0xFD,0x00,0x00,0x0F,0x00,0x16,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte)0xC5,0x22,0x00,0x00,0x34,(byte)0xEC};
    byte[][] mCmds =
    {
        {(byte) 0xFD ,0x00 ,0x00 ,0x0E ,0x00 ,0x15 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte) 0xC5 ,0x22 ,0x55 ,(byte) 0xAA ,0x4F},
        {(byte) 0xFD ,0x00 ,0x00 ,0x0E ,0x00 ,0x15 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte) 0xC5 ,0x22 ,(byte) 0x99 ,(byte) 0xAA ,0x1A},
        {(byte) 0xFD ,0x00 ,0x00 ,0x0D ,0x00 ,0x19 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte) 0xF2 ,0x17 ,0x52 ,(byte) 0x5E },
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.gatestatus_control_layout,null);
        initview();
        initdata();
        return mView;
    }

    private void initdata() {
    }

    private void initview() {
        mResultinfotextview = mView.findViewById(R.id.gate_status_control_result_info);
        mButton = mView.findViewById(R.id.gate_status_control_button);
        mSelectitem = mView.findViewById(R.id.gate_status_control_select);

        mSelectitem.setOnCheckedChangeListener(new OnRadioGroupSelectedimpl());
        mButton.setOnClickListener(new OnButtonClickedlisternerimpl());
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
//        Log.d("zl","Gate OndataCometoParse: "+ CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());
        MainActivity.getInstance().mDialog.dismiss();
        if(!mIsatart)
        {
            return;
        }
        switch (mCheckedindex)
        {
            case 0:
            case 1:
                if(Comparebyte(readOutBuf1,responecontrol)!=0)
                {
                    mResultinfotextview.setText("指令相应失败");
                }
                else
                {
                    mResultinfotextview.setText("发送成功，等待1分钟后查询状态");
                }
                break;
            case 2:
                byte tempstatus=readOutBuf1[readOutBuf1.length-6];
                tempstatus&=0x03;
                switch (tempstatus)
                {
                    case 0:
                        mResultinfotextview.setText("阀门开");
                        break;
                    case 1:
                        mResultinfotextview.setText("阀门关");
                        break;
                    case 3:
                        mResultinfotextview.setText("阀门异常");
                        break;
                    default:
                        mResultinfotextview.setText("未知错误");
                        break;
                }
                break;
        }
    }

    class OnRadioGroupSelectedimpl implements RadioGroup.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
           switch(checkedId)
           {
               case R.id.gate_status_control_open:
                   mCheckedindex=0;
                   break;
               case R.id.gate_status_control_close:
                   mCheckedindex=1;
                   break;
               case R.id.gate_status_control_query:
                   mCheckedindex=2;
                   break;
           }
        }
    }
    class OnButtonClickedlisternerimpl implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            String readOutMsg;
            mIsatart=true;
            readOutMsg = DigitalTrans.byte2hex(mCmds[mCheckedindex]);
//            Log.d("zl","Gatestatus "+readOutMsg);
            verycutstatus(readOutMsg);
            mResultinfotextview.setText("");
        }
    }
    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF",8000);
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }
    private void verycutstatus(String readOutMsg,int timeout)
    {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF",timeout);
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }
    private int Comparebyte(byte[] buf1,byte[] buf2)
    {
        int  result=-1;
        if(buf1==null||buf2==null)
        {
            result=-3;
        }
        else if(buf1.length!=buf2.length)
        {
            result=-2;
        }
        else
        {
            result=-1;
            int i=0;
            for(i=0;i<buf1.length;i++)
            {
                if(buf1[i]!=buf2[i])
                {
                    result=i+1;
                    break;
                }
                else
                {
                    result=0;
                }
            }
        }
        return  result;
    }
}
