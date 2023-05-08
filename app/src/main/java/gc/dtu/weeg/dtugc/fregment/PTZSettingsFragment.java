package gc.dtu.weeg.dtugc.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.ptz.PTZSettingActicity;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class PTZSettingsFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    Button mButRead;
    Button mButWrite;
    //按键类型，0,read 1.write
    int mFlagBuType;
    TextView mWorkModeView;
    TextView mPulseModeView;
    TextView mPulseSensorView;
    TextView mPulseDataView;
    TextView mPressureSensorView;
    TextView mPressureLowLimitView;
    TextView mPressureLowLimitlabView;
    TextView mPressureUpLimitView;
    TextView mTemperatureView;
    TextView mTemperatureLowLimitView;
    TextView mTemperatureUpLimitView;
    TextView mCompressFactorModeView;
    TextView mCompressFactorZView;
    TextView mScanTimeView;
    TextView mInitiateMeterDataView;
    LinearLayout mPressSettingContainerView;
    LinearLayout mPressureUpLimiteContainerView;
    LinearLayout mTemperatureContainer;
    LinearLayout mPressFactorContainer;
    LinearLayout mScanTimeContainer;
    LinearLayout mPlusContainer;
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
        mButWrite = mView.findViewById(R.id.ptz_settings_write_button);
        mButWrite.setOnClickListener(this);
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
         mPressSettingContainerView = mView.findViewById(R.id.ptz_container_press);

         mPressureLowLimitlabView = mView.findViewById(R.id.ptz_pressure_limit_l_lab);
        mPressureUpLimiteContainerView = mView.findViewById(R.id.ptz_pressure_limit_h_container);

        mTemperatureContainer = mView.findViewById(R.id.ptz_temperature_container);
        mPressFactorContainer = mView.findViewById(R.id.ptz_press_factor_container);
        mScanTimeContainer = mView.findViewById(R.id.ptz_container_scan_time);
        mPlusContainer = mView.findViewById(R.id.ptz_pluse_container);

         mWorkModeView.setOnClickListener(new  OnMyViewClickedViewImpl());
        mPulseModeView.setOnClickListener(new  OnMyViewClickedViewImpl());
        mPulseSensorView.setOnClickListener(new  OnMyViewClickedViewImpl());
        mPulseDataView.setOnClickListener(new  OnMyViewClickedViewImpl());
        mPressSettingContainerView.setOnClickListener(new OnMyViewClickedViewImpl());
        mTemperatureContainer.setOnClickListener( new OnMyViewClickedViewImpl());
        mPressFactorContainer.setOnClickListener(new OnMyViewClickedViewImpl());
        mScanTimeContainer.setOnClickListener(new OnMyViewClickedViewImpl());
        mPlusContainer.setOnClickListener(new OnMyViewClickedViewImpl());
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
//            MainActivity.getInstance().mDialog.dismiss();
       }else{ //写数据 响应

       }
        MainActivity.getInstance().mDialog.dismiss();
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
        Log.d("zl","init data settings:"+initdata);
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
        if(settingstr.length() == 0)
        {
            ToastUtils.showToast(getContext(),"工作模式未设置");
            return null;
        }
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
              try{  tempinfo[index] = Byte.parseByte(temp[1]);}
              catch (NumberFormatException e)
              {
                  tempinfo[index] = 0;
              }
                break;
            }
        }
        index++;

        settingstr = mPulseSensorView.getText().toString();
        for(String temp[]:Constants.plusedevice)
        {
            if(settingstr.equals(temp[0]))
            {
             try{   tempinfo[index] = Byte.parseByte(temp[1]);}
             catch (NumberFormatException e)
             {
                 tempinfo[index] = 0;
             }
                break;
            }
        }
        index++;
        int dataPluseValue = 0;
        settingstr = mPulseDataView.getText().toString();
        for(String temp[]:Constants.plusedata)
        {
            if(settingstr.equals(temp[0]))
            {
              try{  tempinfo[index] = Byte.parseByte(temp[1]);
                  dataPluseValue = tempinfo[index];
              }
              catch (NumberFormatException e)
              {
                  tempinfo[index] = 0;
              }
                break;
            }
        }
        index++;

        settingstr = mPressureSensorView.getText().toString();
        for(String temp[]:Constants.pressmode)
        {
            if(settingstr.equals(temp[0]))
            {
              try{  tempinfo[index] = Byte.parseByte(temp[1]);}
              catch (NumberFormatException e){
                  tempinfo[index] = 0;
              }
                break;
            }
        }
        index++;

        float settingfloatvalue ;
        if(mPressureLowLimitView.getText().toString().length()==0)
        {
            settingfloatvalue = 0;
        }
         else
        {
           try{ settingfloatvalue = Float.parseFloat(mPressureLowLimitView.getText().toString());}
           catch (NumberFormatException e){
               settingfloatvalue = 0;
           }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.rewind();
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        if(mPressureUpLimitView.getText().toString().length() == 0)
        {
            settingfloatvalue = 0;
        }
        else
        {
           try{ settingfloatvalue = Float.parseFloat(mPressureUpLimitView.getText().toString());}
           catch (NumberFormatException e){
               settingfloatvalue = 0;
           }
        }

        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.rewind();
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingstr = mTemperatureView.getText().toString();
        for(String temp[]:Constants.temperaturemode)
        {
            if(settingstr.equals(temp[0]))
            {
          try{
              tempinfo[index] = Byte.parseByte(temp[1]);
          }
          catch (NumberFormatException e){
              tempinfo[index] = 0;
          }
                break;
            }
        }
        index++;

        if(mTemperatureLowLimitView.getText().toString().length() == 0)
        {
            settingfloatvalue = 0;
        }
        else
        {
          try {
              settingfloatvalue = Float.parseFloat(mTemperatureLowLimitView.getText().toString());
          }
          catch (NumberFormatException e)
          {
              settingfloatvalue = 0;
          }
        }
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.rewind();
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        if(mTemperatureUpLimitView.getText().toString().length() == 0)
        {
            settingfloatvalue = 0;
        }
        else
        {
            try {
                settingfloatvalue = Float.parseFloat(mTemperatureUpLimitView.getText().toString());
              //  settingfloatvalue = Float.parseFloat(mTemperatureLowLimitView.getText().toString());
            }
            catch (NumberFormatException e)
            {
                settingfloatvalue = 0;
            }
        }

        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.rewind();
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingstr = mCompressFactorModeView.getText().toString();
        for(String temp[]:Constants.compressibilityfactormode)
        {
            if(settingstr.equals(temp[0]))
            {
               try {
                      tempinfo[index] = Byte.parseByte(temp[1]);
                   }
               catch (NumberFormatException e){

                   tempinfo[index] = 0;
               }
                break;
            }
        }
        index++;

        if(mCompressFactorZView.getText().toString().length() == 0)
        {
            settingfloatvalue = 0;
        }
        else
        {
          try{  settingfloatvalue = Float.parseFloat(mCompressFactorZView.getText().toString());}
          catch(NumberFormatException e) {
            settingfloatvalue = 0;
        }
        }

        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putFloat(settingfloatvalue);
        byteBuffer.rewind();
        byteBuffer.get(tempinfo,index,4);
        index+=4;

        settingstr = mScanTimeView.getText().toString();
      try {
          tempinfo[index] = Byte.parseByte(settingstr);
      }
      catch (NumberFormatException e)
      {
          tempinfo[index] =0;
      }
        index++;

        if(mInitiateMeterDataView.getText().toString().length()==0)
        {
            ToastUtils.showToast(getContext(),"未设置初始读数");
            return null;
        }

        int tempsetpluse = 4-dataPluseValue;
        tempsetpluse+=1;
        StringBuffer initdata = new StringBuffer(mInitiateMeterDataView.getText().toString());
        int positionofpoint = -1;
        positionofpoint = initdata.indexOf(".");
        if(positionofpoint>0)
        {
            String test = initdata.toString();
//            Log.d("zl","data pluse set is " +test);
            test = test.substring(positionofpoint+1);
//            Log.d("zl","data pluse after . set is " +test);

            for(int i=test.length();i<tempsetpluse;i++)
            {
                initdata.append('0');
            }

            initdata.deleteCharAt(positionofpoint);

//            Log.d("zl","data pluse modify set is " +initdata.toString());
        }
        byteBuffer = ByteBuffer.allocateDirect(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(Integer.parseInt(initdata.toString()));
        byteBuffer.rewind();
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

    private class OnMyViewClickedViewImpl implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int ids = v.getId();
            int ActivityRequestcode = 0;
            String selected ;
            Intent serverIntent = new Intent(MainActivity.getInstance(), PTZSettingActicity.class);
            switch (ids)
            {
                case R.id.ptz_work_mode:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG1;
                    selected = mWorkModeView.getText().toString();
                    int item =-1;
                    for(int i=0;i<Constants.workmodetype.length;i++)
                    {
                        if(selected.equals(Constants.workmodetype[i][0]))
                        {
                            item = Integer.parseInt(Constants.workmodetype[i][1]);
                            break;
                        }
                    }
                    serverIntent.putExtra("index",1);
                    serverIntent.putExtra("set1",item);
                    break;
                case R.id.ptz_pulse_mode:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG2;
                    selected = mPulseModeView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.plusemode.length;i++)
                    {
                        if(selected.equals(Constants.plusemode[i][0]))
                        {
                            item = Integer.parseInt(Constants.plusemode[i][1]);
                            break;
                        }
                    }
                    serverIntent.putExtra("index",2);
                    serverIntent.putExtra("set1",item);
                    break;
                case R.id.ptz_pulse_sensor:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG3;
                    selected = mPulseSensorView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.plusedevice.length;i++)
                    {
                        if(selected.equals(Constants.plusedevice[i][0]))
                        {
                            item = Integer.parseInt(Constants.plusedevice[i][1]);
                            break;
                        }
                    }
                    serverIntent.putExtra("index",3);
                    serverIntent.putExtra("set1",item);
                    break;
                case R.id.ptz_data_pulse_mode:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG4;
                    selected = mPulseDataView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.plusedata.length;i++)
                    {
                        if(selected.equals(Constants.plusedata[i][0]))
                        {
                            item = Integer.parseInt(Constants.plusedata[i][1]);
                            break;
                        }
                    }
                    serverIntent.putExtra("index",4);
                    serverIntent.putExtra("set1",item);
                    break;
                case R.id.ptz_container_press:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG5;
//                    mPressureSensorView= mView.findViewById(R.id.ptz_pressure_mode);
//                    mPressureLowLimitView= mView.findViewById(R.id.ptz_pressure_limit_l);
//                    mPressureUpLimitView= mView.findViewById(R.id.ptz_pressure_limit_h);
                    String stwokmode = mWorkModeView.getText().toString();
                    if(stwokmode.equals(Constants.workmodetype[0][0])||stwokmode.length() == 0)
                    {
                        ToastUtils.showToast(getContext(),"当前工作模式无法设置压力传感器");
                        return;
                    }

                    selected = mPressureSensorView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.pressmode.length;i++)
                    {
                        if(selected.equals(Constants.pressmode[i][0]))
                        {
                            item = Integer.parseInt(Constants.pressmode[i][1]);
                            break;
                        }
                    }
                    serverIntent.putExtra("index",5);
                    serverIntent.putExtra("set1",item);
                    serverIntent.putExtra("set2",mPressureLowLimitView.getText().toString());
                    serverIntent.putExtra("set3",mPressureUpLimitView.getText().toString());
                    serverIntent.putExtra("curb",mWorkModeView.getText().toString());

                    break;
                case R.id.ptz_temperature_container:

                    stwokmode = mWorkModeView.getText().toString();
                    if(stwokmode.equals(Constants.workmodetype[0][0])||stwokmode.length() == 0)
                    {
                        ToastUtils.showToast(getContext(),"当前工作模式无法设置温度传感器");
                        return;
                    }
                    ActivityRequestcode = Constants.PTZSETTINGFLAG6;
                    selected = mTemperatureView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.temperaturemode.length;i++)
                    {
                        if(selected.equals(Constants.temperaturemode[i][0]))
                        {
                            item = Integer.parseInt(Constants.temperaturemode[i][1]);
                            break;
                        }
                    }
                    serverIntent.putExtra("index",6);
                    serverIntent.putExtra("set1",item);
                    serverIntent.putExtra("set2",mTemperatureLowLimitView.getText().toString());
                    serverIntent.putExtra("set3",mTemperatureUpLimitView.getText().toString());
                    serverIntent.putExtra("curb",mWorkModeView.getText().toString());
                    break;
                case R.id.ptz_press_factor_container:
                    stwokmode = mWorkModeView.getText().toString();
                    if(stwokmode.equals(Constants.workmodetype[0][0])||stwokmode.length() == 0)
                    {
                        ToastUtils.showToast(getContext(),"当前工作模式无法设置温度传感器");
                        return;
                    }
                    ActivityRequestcode = Constants.PTZSETTINGFLAG7;
                    selected = mCompressFactorModeView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.compressibilityfactormode.length;i++)
                    {
                        if(selected.equals(Constants.compressibilityfactormode[i][0]))
                        {
                            item = Integer.parseInt(Constants.compressibilityfactormode[i][1]);
                            break;
                        }
                    }

                    serverIntent.putExtra("index",7);
                    serverIntent.putExtra("set1",item);
                    serverIntent.putExtra("set2",mCompressFactorZView.getText().toString());
                    serverIntent.putExtra("set3","");
                    serverIntent.putExtra("curb",mWorkModeView.getText().toString());

                    break;
                case R.id.ptz_container_scan_time:
                    stwokmode = mWorkModeView.getText().toString();
                    if(stwokmode.equals(Constants.workmodetype[0][0])||stwokmode.length() == 0)
                    {
                        ToastUtils.showToast(getContext(),"当前工作模式无法设置温度传感器");
                        return;
                    }
                    serverIntent.putExtra("index",8);
                    serverIntent.putExtra("set1","");
                    serverIntent.putExtra("set2",mScanTimeView.getText().toString());
                    serverIntent.putExtra("set3","");
                    serverIntent.putExtra("curb",mWorkModeView.getText().toString());
                    break;
                case R.id.ptz_pluse_container:
                    selected = mPulseDataView.getText().toString();
                    item =-1;
                    for(int i=0;i<Constants.plusedata.length;i++)
                    {
                        if(selected.equals(Constants.plusedata[i][0]))
                        {
                            item = Integer.parseInt(Constants.plusedata[i][1]);
                            break;
                        }
                    }
                    if(item == -1){
                        ToastUtils.showToast(getContext(),"请先设置数据脉冲类型");
                        return;
                    }
                    serverIntent.putExtra("index",9);
                    serverIntent.putExtra("curb",mPulseDataView.getText().toString());
                    serverIntent.putExtra("set2",mInitiateMeterDataView.getText().toString());
                    break;
                case  R.id.ptz_pressure_limit_h:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG6;
                    break;
                case R.id.ptz_compress_mode:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG7;
                    break;
                case R.id.ptz_factor_z:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG8;
                    break;
                case R.id.ptz_scan_time:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG9;
                    break;
                case R.id.ptz_initiate_meter_data:
                    ActivityRequestcode = Constants.PTZSETTINGFLAG10;
                    break;
            }
            startActivityForResult(serverIntent, ActivityRequestcode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     //   switch (requestCode)
        Log.d("zl","onActivityResult requestCode = "+requestCode+" resultCode="+resultCode);
        if(resultCode == 100)
        {
            return;
        }
    String settings=""+data.getStringExtra("res1");
        switch (resultCode)
        {
            case 1:

                mWorkModeView.setText(settings);
//                for(int i=0;i<Constants.workmodetype.length;i++)
//                {
//                    if(settings.equals(Constants.workmodetype[i][1]))
//                    {
//                        mWorkModeView.setText(Constants.workmodetype[i][0]);
//                        break;
//                    }
//                }

                int item =-1;
                for(int i=0;i<Constants.workmodetype.length;i++)
                {
                    if(settings.equals(Constants.workmodetype[i][0]))
                    {
                        item = Integer.parseInt(Constants.workmodetype[i][1]);
                        break;
                    }
                }
                if(item>=0&&item<=1)
                {
                    mPressureSensorView.setText(Constants.pressmode[item][0]);
                }
                if(item == 0 ){
                    mPressureLowLimitlabView.setText("无");
                    mPressureUpLimiteContainerView.setVisibility(View.GONE);
                    mPressureLowLimitView.setText("");
                    mTemperatureLowLimitView.setText("无");
                }
                else if(item == 1){
                    mPressureLowLimitlabView.setText("固定值");
                    mPressureUpLimiteContainerView.setVisibility(View.GONE);
                    mTemperatureLowLimitView.setText("");
                }
                else {
                    mPressureLowLimitlabView.setText("下限(KP)");
                    mPressureUpLimiteContainerView.setVisibility(View.VISIBLE);
                    mTemperatureLowLimitView.setText("");
                }
                if(item == 0){
                    mCompressFactorModeView.setText("");
                }else if(item == 1 ||item == 2){
                    mCompressFactorModeView.setText(Constants.compressibilityfactormode[0][0]);
                //    mCompressFactorZView.setText(Constants.compressibilityfactormode[0]);
                }else {
                    mCompressFactorModeView.setText(Constants.compressibilityfactormode[1][0]);
                }

                break;
            case 2:
                mPulseModeView.setText(settings);
//                for(int i=0;i<Constants.plusemode.length;i++)
//                {
//                    if(settings.equals(Constants.plusemode[i][1]))
//                    {
//                        mPulseModeView.setText(Constants.plusemode[i][0]);
//                        break;
//                    }
//                }
                break;
            case 3:
                mPulseSensorView.setText(settings);
//                for(int i=0;i<Constants.plusedevice.length;i++)
//                {
//                    if(settings.equals(Constants.plusedevice[i][1]))
//                    {
//                        mPulseSensorView.setText(Constants.plusedevice[i][0]);
//                        break;
//                    }
//                }
                break;
            case 4:
                mPulseDataView.setText(settings);
                break;
            case 5:
//                intent.putExtra("res1",str);
//                intent.putExtra("res2",edit1.getText().toString());
//                intent.putExtra("res3",edit2.getText().toString());
                mPressureSensorView.setText(settings);
                String settings2=""+data.getStringExtra("res2");
                String settings3=""+data.getStringExtra("res3");
                mPressureLowLimitView.setText(settings2);
                mPressureUpLimitView.setText(settings3);
                break;
            case 6:
                settings2=""+data.getStringExtra("res2");
                settings3=""+data.getStringExtra("res3");
                mTemperatureView.setText(settings);
                mTemperatureLowLimitView.setText(settings2);
                mTemperatureUpLimitView.setText(settings3);
                break;
            case 7:
                settings2=""+data.getStringExtra("res2");
                settings3=""+data.getStringExtra("res3");
                mCompressFactorModeView.setText(settings);
                mCompressFactorZView.setText(settings2);
                break;
            case 8:
                settings2=""+data.getStringExtra("res2");
                mScanTimeView.setText(settings2);
                break;
            case 9:
                settings2=""+data.getStringExtra("res2");
                mInitiateMeterDataView.setText(settings2);
                break;
        }
    }
}
