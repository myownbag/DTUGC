package gc.dtu.weeg.dtugc.fregment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class PTZSettingsFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    Button mButRead;
    //按键类型，0,read 1.write
    int mFlagBuType;
    TextView mWorkModeView;
    TextView mPulseModeView;
    TextView mPulseSensorView;
    TextView mPulseDataView;
    TextView mPressureSensorView;
    TextView mPressureLowLimitView;
    TextView mPressureUpLimitView;
    TextView mTemperatureView;
    TextView mTemperatureLowLimitView;
    TextView mTemperatureUpLimitView;
    TextView mCompressFactorModeView;
    TextView mCompressFactorZView;
    TextView mScanTimeView;
    TextView mInitiateMeterDataView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView=inflater.inflate(R.layout.ptz_setttings_fragment,null);
        initview();

        return mView;
    }

    private void initview() {
        mButRead = mView.findViewById(R.id.ptz_settings_read_button);
        mButRead.setOnClickListener(this);

         mWorkModeView = mView.findViewById(R.id.ptz_work_mode);
         mPulseModeView = mView.findViewById(R.id.ptz_pulse_mode);
         mPulseSensorView = mView.findViewById(R.id.ptz_pulse_sensor);
         mPulseDataView= mView.findViewById(R.id.ptz_data_pulse_mode);
         mPressureSensorView= mView.findViewById(R.id.ptz_pressure_mode);
         mPressureLowLimitView= mView.findViewById(R.id.ptz_pressure_limit_l);
         mPressureUpLimitView= mView.findViewById(R.id.ptz_pressure_limit_h);
         mTemperatureView= mView.findViewById(R.id.ptz_temperature_mode);
         mTemperatureLowLimitView= mView.findViewById(R.id.ptz_temperature_limit_l);
         mTemperatureUpLimitView= mView.findViewById(R.id.ptz_temperature_limit_h);
         mCompressFactorModeView= mView.findViewById(R.id.ptz_compress_mode);
         mCompressFactorZView= mView.findViewById(R.id.ptz_factor_z);
         mScanTimeView= mView.findViewById(R.id.ptz_scan_time);
         mInitiateMeterDataView= mView.findViewById(R.id.ptz_initiate_meter_data);
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        if(mIsatart==false)
        {
            return;
        }
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
       if(mFlagBuType == 0) { //读数据 响应
            decodereadrespone(readOutBuf1);
       }else{ //写数据 响应

       }
    }



    public void decodereadrespone(byte[] ResponeReadByte)
    {
        int index=16;
        int datapulse = 0;
        //工作模式
        for(String[] temp: Constants.workmodetype)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mWorkModeView.setText(temp[0]);
                break;
            }
        }
        index++;
        //脉冲模式
        for(String[] temp: Constants.plusemode)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mPulseModeView.setText(temp[0]);
                break;
            }
        }
        index++;
        //脉冲传感模式
        for(String[] temp: Constants.plusedevice)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mPulseSensorView.setText(temp[0]);
                break;
            }
        }
        index++;

        //数据脉冲
        for(String[] temp: Constants.plusedata)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mPulseDataView.setText(temp[0]);
                datapulse = ResponeReadByte[index];
                break;
            }
        }
        index++;

        //压力传感模式
        for(String[] temp: Constants.pressmode)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mPressureSensorView.setText(temp[0]);
                break;
            }
        }
        index++;
        //压力上下限
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(ResponeReadByte,index,4);
        byteBuffer.rewind();
        float limitpressure = byteBuffer.getFloat();
        mPressureLowLimitView.setText(""+limitpressure);

        index+=4;
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(ResponeReadByte,index,4);
        byteBuffer.rewind();
        limitpressure = byteBuffer.getFloat();
        mPressureUpLimitView.setText(""+limitpressure);

        index+=4;
        //压力传感模式
        for(String[] temp: Constants.temperaturemode)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mTemperatureView.setText(temp[0]);
                break;
            }
        }
        index++;
        //温度上下限
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(ResponeReadByte,index,4);
        byteBuffer.rewind();
        limitpressure = byteBuffer.getFloat();
        mTemperatureLowLimitView.setText(""+limitpressure);
        index+=4;

        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(ResponeReadByte,index,4);
        byteBuffer.rewind();
        limitpressure = byteBuffer.getFloat();
        mTemperatureUpLimitView.setText(""+limitpressure);
        index+=4;

        //压缩因子
        for(String[] temp: Constants.compressibilityfactormode)
        {
            if(Short.valueOf(temp[1])==ResponeReadByte[index])
            {
                mCompressFactorModeView.setText(temp[0]);
                break;
            }
        }
        index++;

        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(ResponeReadByte,index,4);
        byteBuffer.rewind();
        limitpressure = byteBuffer.getFloat();
        mCompressFactorZView.setText(""+limitpressure);
        index+=4;

        mScanTimeView.setText(""+(short)(0x00ff&ResponeReadByte[index]));
        index++;

        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(ResponeReadByte,index,4);
        byteBuffer.rewind();
        int  initdata = byteBuffer.getInt();
        //转换为float型
        String datashow = SetInitMeterData(datapulse,initdata);
        mInitiateMeterDataView.setText(datashow);
    }
    public String SetInitMeterData(int a, int b)
    {
        String temp;
        int lenth=0,index=0,i=0;
        temp = ""+b;
        lenth=temp.length();
        index=4-a; //小数点的个数
        for(i=lenth-1;i<index;i++)
        {
            temp="0"+temp;
        }
        StringBuffer stringBuffer = new StringBuffer(temp);
        if(index>0)
            stringBuffer.insert(temp.length()-index,'.');
        return stringBuffer.toString();
//        if(index>0)
//            temp.Insert(temp.GetLength()-index,'.');
    }
    private byte[] encoderwritesettings() {
        String settingstr;
        String findstr = null;
        int index = 16;
        byte[] tempinfo = {(byte) 0xFD ,0x00 ,0x00 ,0x2D ,0x00 ,0x15 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte)0xDF
                           ,0x00 ,0x03 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00
                           ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00
                           ,0x00 ,0x00 ,0x00 ,0x53 ,0x00 };
        settingstr = mWorkModeView.getText().toString();
        for(String temp[]:Constants.workmodetype)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                findstr = temp[0];
                break;
            }
        }
        index++;
        if(findstr == null)
        {
            return null;
        }
        settingstr = mPulseModeView.getText().toString();
        for(String temp[]:Constants.plusemode)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                break;
            }
        }
        index++;

        settingstr = mPulseSensorView.getText().toString();
        for(String temp[]:Constants.plusedevice)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                break;
            }
        }
        index++;

        settingstr = mPulseDataView.getText().toString();
        for(String temp[]:Constants.plusedata)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                break;
            }
        }
        index++;

        settingstr = mPressureSensorView.getText().toString();
        for(String temp[]:Constants.pressmode)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                break;
            }
        }
        index++;

        float settingfloatvalue = Float.parseFloat(mPressureLowLimitView.getText().toString());
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingfloatvalue = Float.parseFloat(mPressureUpLimitView.getText().toString());
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingstr = mTemperatureView.getText().toString();
        for(String temp[]:Constants.temperaturemode)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                break;
            }
        }
        index++;

        settingfloatvalue = Float.parseFloat(mTemperatureLowLimitView.getText().toString());
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingfloatvalue = Float.parseFloat(mTemperatureUpLimitView.getText().toString());
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingstr = mCompressFactorModeView.getText().toString();
        for(String temp[]:Constants.compressibilityfactormode)
        {
            if(settingstr.equals(temp[0]))
            {
                tempinfo[index] = Byte.parseByte(temp[1]);
                break;
            }
        }
        index++;

        settingfloatvalue = Float.parseFloat(mCompressFactorZView.getText().toString());
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingstr = mScanTimeView.getText().toString();
        tempinfo[index] = Byte.parseByte(settingstr);
        index++;

        StringBuffer initdata = new StringBuffer(mInitiateMeterDataView.getText().toString());
        int positionofpoint = -1;
        positionofpoint = initdata.indexOf(".");
        if(positionofpoint>0)
        {
            initdata.deleteCharAt(positionofpoint);
        }
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(Integer.parseInt(initdata.toString()));
        byteBuffer.get(tempinfo,index,4);

        return tempinfo;
    }
    @Override
    public void onClick(View v) {
        mIsatart = true;
        int id = v.getId();
        switch (id)
        {
            case R.id.ptz_settings_read_button:
                mFlagBuType = 0;
                ReadPTZInformation();
                break;
            case R.id.ptz_settings_write_button:
                mFlagBuType = 1;
                WritePTZSettings();
                break;
            default:
                break;
        }
    }
    private void ReadPTZInformation() {
        byte[] temp={(byte)0xFD,0x00 ,0x00 ,0x0D ,0x00 ,0x19 ,0x00 ,0x00 ,0x00 ,0x00
                ,0x00 ,0x00 ,0x00 ,0x00 ,(byte)0xCE ,0x07 ,0x42 ,(byte)0x92};

        ByteBuffer buf;
        buf=ByteBuffer.allocateDirect(2); //无额外内存的直接缓存
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
        buf.putShort((short) 223);
        buf.rewind();
        buf.get(temp,14,2);

        CodeFormat.crcencode(temp);
        Log.d("zl",CodeFormat.byteToHex(temp,temp.length).toUpperCase());

        String readOutMsg = DigitalTrans.byte2hex(temp);
        verycutstatus(readOutMsg);
    }
    private void WritePTZSettings() {

        byte[] writedata;
        writedata =encoderwritesettings();
        if(writedata!=null)
        {
            CodeFormat.crcencode(writedata);
            Log.d("zl",CodeFormat.byteToHex(writedata,writedata.length).toUpperCase());
            String readOutMsg = DigitalTrans.byte2hex(writedata);
            verycutstatus(readOutMsg);
        }
    }

    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("读取中...");
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }
}
