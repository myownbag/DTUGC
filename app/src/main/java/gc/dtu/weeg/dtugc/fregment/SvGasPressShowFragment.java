package gc.dtu.weeg.dtugc.fregment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class SvGasPressShowFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    TextView textView;
    Button button;

    TextView mOnlineStatus;
    TextView mPCSetting;
    TextView mSleepStatus;
    TextView mOpenAccept;
    TextView mMovingStatus;
    TextView mGateStatus;
    TextView mBatteryStatus;
    TextView mGasAlarm;
    TextView mAlarmSetting;
    TextView mExPowerStatus;
    int mStep;
    byte sendbufread[];
    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        if(mIsatart == false)
        {
            return;
        }

        MainActivity.getInstance().mDialog.dismiss();
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

        if(mStep == 0)
        {
            ByteBuffer buf = ByteBuffer.allocate(4);
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(readOutBuf1,18,4);
            buf.rewind();
            float f1 = buf.getFloat();
            textView.setText(""+f1);

            mStep++;

            buf = ByteBuffer.allocate(2);
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putShort((short) 0xFFFE);
            buf.rewind();
            buf.get(sendbufread,14,2);
            CodeFormat.crcencode(sendbufread);
            String readOutMsg = DigitalTrans.byte2hex(sendbufread);
            verycutstatus(readOutMsg, 0);
        }
        else if(mStep == 1)
        {
            int alarmstatus;
            ByteBuffer buf = ByteBuffer.allocate(4);
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(readOutBuf1,16,4);
            buf.rewind();
            alarmstatus = buf.getInt();

            byte al = readOutBuf1[20];
            byte gate = readOutBuf1[21];

            if((alarmstatus &1)!=0)
            {
                mOnlineStatus.setText("设备在线或在拨号");
            }
            else
            {
                mOnlineStatus.setText("设备离线");
            }
            if((alarmstatus&(1<<1))!=0)
            {
                mPCSetting.setText("PC不可设置");
            }
            else
            {
                mPCSetting.setText("可以设置");
            }

            if((alarmstatus&(1<<2))!=0)
            {
                mSleepStatus.setText("休眠");
            }
            else
            {
                mSleepStatus.setText("未休眠");
            }

            if((alarmstatus&(1<<7))!=0)
            {
                mOpenAccept.setText("允许开阀");
            }
            else
            {
                mOpenAccept.setText("无");
            }

            if((alarmstatus&(1<<8))!=0)
            {
                mMovingStatus.setText("阀门动作中...");
            }
            else
            {
                mMovingStatus.setText("阀门无动作");
            }

            if((alarmstatus&(1<<15))!=0)
            {
                mGateStatus.setText("阀门异常");
            }
            else
            {
                if(gate == 0){
                    mGateStatus.setText("阀门开");
                }
                else{
                    mGateStatus.setText("阀门关");
                }
            }

            if((alarmstatus&(1<<16))!=0)
            {
                mBatteryStatus.setText("电压过低");
            }
            else{
                mBatteryStatus.setText("电池电压正常");
            }
            if((alarmstatus&(1<<17))!=0)
            {
                mExPowerStatus.setText("有");
            }
            else{
                mExPowerStatus.setText("无");
            }
            if((alarmstatus&(3<<23))!=0)
            {
                switch (al)
                {
                    case -3:
                        mGasAlarm.setText("L3A");
                        break;
                    case -2:
                        mGasAlarm.setText("L2A");
                        break;
                    case -1:
                        mGasAlarm.setText("L1A");
                        break;
                    case 1:
                        mGasAlarm.setText("H1A");
                        break;
                    case 2:
                        mGasAlarm.setText("H2A");
                        break;
                    case 3:
                        mGasAlarm.setText("H3A");
                        break;
                }
            }
            else{
                mGasAlarm.setText("无压力报警");
            }

            if((alarmstatus&(1<<27))!=0)
            {
                mAlarmSetting.setText("未设置");
            }
            else{
                mAlarmSetting.setText("已经设置");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView == null)
        {
            mView=inflater.inflate(R.layout.fragment_sv_gate_press_show,null,false);
        }
        InitView();
        InitData();
        return mView;
    }

    private void InitView() {
        textView = mView.findViewById(R.id.sv_gate_press_data);
        button = mView.findViewById(R.id.btn_event_show);
        button.setOnClickListener(this);
        // 报警状态
        mOnlineStatus = mView.findViewById(R.id.sv_gate_on_line_data);
        mPCSetting = mView.findViewById(R.id.sv_gate_pc_set_data);
        mSleepStatus = mView.findViewById(R.id.sv_gate_sleep_data);
        mOpenAccept = mView.findViewById(R.id.sv_gate_open_accept_data);
        mMovingStatus = mView.findViewById(R.id.sv_gate_moving_data);
        mGateStatus = mView.findViewById(R.id.sv_gate_data_opn);
        mBatteryStatus = mView.findViewById(R.id.sv_gate_battery_vol);
        mGasAlarm = mView.findViewById(R.id.sv_gate_gas_alarm);
        mAlarmSetting = mView.findViewById(R.id.sv_gate_alarm_set);
        mExPowerStatus = mView.findViewById(R.id.sv_gate_ex_pow);
    }
    private void InitData() {
        mStep = 0;

        sendbufread= new byte[]{(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
                ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0};
    }

    @Override
    public void onClick(View v) {
        mIsatart = true;

        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort((short) 3005);
        buf.rewind();
        buf.get(sendbufread,14,2);
        CodeFormat.crcencode(sendbufread);
        mIsatart = true;

        mStep = 0;
        String readOutMsg = DigitalTrans.byte2hex(sendbufread);
        verycutstatus(readOutMsg, 0);
    }

    private void verycutstatus(String readOutMsg,int timeout) {
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
}
