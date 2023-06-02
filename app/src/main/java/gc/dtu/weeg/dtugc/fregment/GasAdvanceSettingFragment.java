package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.slidingbutton.BaseSlidingToggleButton;
import gc.dtu.weeg.dtugc.myview.slidingbutton.SlidingToggleButton;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class GasAdvanceSettingFragment extends BaseFragment {
    View mView;
    Spinner mPressIndexSelect;
    Spinner mPressHighGradeSetting;
    Spinner mPressLowGradeSetting;

    EditText mPressH1;
    EditText mPressH2;
    EditText mPressH3;

    EditText mPressL1;
    EditText mPressL2;
    EditText mPressL3;

    EditText[] mTextctrl = new EditText[6];//{mPressL1,mPressL2,mPressL3,mPressH3,mPressH2,mPressH1};

    Button mBtnRead;
    Button mBtnWrite;

    SlidingToggleButton mEnableH1bt;
    SlidingToggleButton mEnableH2bt;
    SlidingToggleButton mEnableH3bt;
    SlidingToggleButton mEnableL1bt;
    SlidingToggleButton mEnableL2bt;
    SlidingToggleButton mEnableL3bt;

    int[][] butfunmap;

    byte mEnableAlarmSet;

    int mCurrentStep;

    String[] setinfo1 = {
            "第一路",
            "第二路"
    };

    String[] setinfo2 = {
            "禁止",
            "使能"
    };

    byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0};

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl","OndataCometoParse:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));

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
        if(mCurrentStep == 1)
        {
//            if((readOutBuf1[16]&0x00ff)==0)
//            {
//                mPressHighGradeSetting.setSelection(0);
//            }
//            else
//            {
//                mPressHighGradeSetting.setSelection(1);
//            }
            mEnableAlarmSet = readOutBuf1[16];
            for(int[] item:butfunmap)
            {
                SlidingToggleButton bt = mView.findViewById(item[0]);
                if((mEnableAlarmSet & ((byte)item[1]))!=0)
                {
                    if(bt.isChecked()!=true)
                                 bt.setChecked(true);
                }
                else
                {
                    if(bt.isChecked() == true)
                            bt.setChecked(false);
                }
            }

            int index = 0;
            for(EditText set:mTextctrl)
            {
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
                byteBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                byteBuffer.put(readOutBuf1,17+index*4,4);
                byteBuffer.rewind();
                float f = byteBuffer.getFloat();
                set.setText(""+f);
                index++;
            }
        }
        else
        {
            ToastUtils.showToast(getContext(),"写入成功");
        }
       MainActivity.getInstance().mDialog.dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     //   return super.onCreateView(inflater, container, savedInstanceState);
        mIsatart=false;
        if(mView!=null)
        {
            return mView;
        }
        mView= inflater.inflate(R.layout.gas_press_setting_layout,null);
        InitView();
        InitData();
        return mView;
    }
    private void InitView(){
        mPressIndexSelect = mView.findViewById(R.id.gas_press_index_select);
        mPressHighGradeSetting = mView.findViewById(R.id.instrument_device_status_value);
        mPressLowGradeSetting = mView.findViewById(R.id.instrument_device_type_value);

        mPressH1 = mView.findViewById(R.id.gas_press_h1_device_addr_value);
        mPressH2 = mView.findViewById(R.id.gas_press_h2_device_addr_value);
        mPressH3 = mView.findViewById(R.id.gas_press_h3_device_addr_value);

        mPressL1 = mView.findViewById(R.id.gas_press_l1_device_addr_value);
        mPressL2 = mView.findViewById(R.id.gas_press_l2_device_addr_value);
        mPressL3 = mView.findViewById(R.id.gas_press_l3_device_addr_value);

        mBtnRead = mView.findViewById(R.id.buttsensorcommite);
        mBtnWrite = mView.findViewById(R.id.buttsensorcommite1);

        mBtnRead.setOnClickListener(new OnViewClickedImpl());
        mBtnWrite.setOnClickListener(new OnViewClickedImpl());

        setSpinneradpater(mPressIndexSelect,setinfo1);
        setSpinneradpater(mPressHighGradeSetting,setinfo2);
        setSpinneradpater(mPressLowGradeSetting,setinfo2);

        mPressH1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
        mPressH2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
        mPressH3.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);

        mPressL1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
        mPressL2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
        mPressL3.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);

        mEnableH1bt=mView.findViewById(R.id.alarm_h1_enable_set);
        mEnableH2bt=mView.findViewById(R.id.alarm_h2_enable_set);
        mEnableH3bt=mView.findViewById(R.id.alarm_h3_enable_set);

        mEnableL1bt=mView.findViewById(R.id.alarm_l1_enable_set);
        mEnableL2bt=mView.findViewById(R.id.alarm_l2_enable_set);
        mEnableL3bt=mView.findViewById(R.id.alarm_l3_enable_set);

        mEnableH1bt.setOnCheckedChanageListener(new OnToggleButChangedIMPL());
        mEnableH2bt.setOnCheckedChanageListener(new OnToggleButChangedIMPL());
        mEnableH3bt.setOnCheckedChanageListener(new OnToggleButChangedIMPL());

        mEnableL1bt.setOnCheckedChanageListener(new OnToggleButChangedIMPL());
        mEnableL2bt.setOnCheckedChanageListener(new OnToggleButChangedIMPL());
        mEnableL3bt.setOnCheckedChanageListener(new OnToggleButChangedIMPL());
        //{mPressL1,mPressL2,mPressL3,mPressH3,mPressH2,mPressH1};
        int indexitem = 0;
        mTextctrl[indexitem++] = mPressL1;
        mTextctrl[indexitem++] = mPressL2;
        mTextctrl[indexitem++] = mPressL3;
        mTextctrl[indexitem++] = mPressH3;
        mTextctrl[indexitem++] = mPressH2;
        mTextctrl[indexitem++] = mPressH1;
    }
    private void InitData() {
        butfunmap = new int[][]{
                {R.id.alarm_h1_enable_set,0x40},
                {R.id.alarm_h2_enable_set,0x20},
                {R.id.alarm_h3_enable_set,0x10},
                {R.id.alarm_l1_enable_set,0x04},
                {R.id.alarm_l2_enable_set,0x02},
                {R.id.alarm_l3_enable_set,0x01},
        };

        mEnableAlarmSet = 0;
    }

    private class OnViewClickedImpl implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int id = v.getId();
            mIsatart = true;
            switch(id)
            {
                case R.id.buttsensorcommite:
                    mCurrentStep = 1;
                    CmdReadDevice();
                    break;
                case R.id.buttsensorcommite1:
                    mCurrentStep = 2;
                    CmdWriteDeice();
                    break;
            }
        }
    }

    private void CmdReadDevice() {

        short sensorindex = (short) mPressIndexSelect.getSelectedItemPosition();

        //第一路地址 227
        //第二路地址 228

        sensorindex+=227;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2);
        byteBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(sensorindex);
        byteBuffer.rewind();
        byteBuffer.get(sendbufread,14,2);

        CodeFormat.crcencode(sendbufread);

        String readOutMsg = DigitalTrans.byte2hex(sendbufread);
        Log.d("zl",CodeFormat.byteToHex(sendbufread,sendbufread.length));
        verycutstatus(readOutMsg);
    }

    private void CmdWriteDeice() {
        byte[] sendpressmodify = new byte[18+25];
        for(int i=0;i<(sendbufread.length-2);i++)
        {
            sendpressmodify[i]=sendbufread[i];
        }
        sendpressmodify[3]= (byte) (25+13);
      //  sendpressmodify[14]= (byte) 0xe6;
        sendpressmodify[5]=0x15;

//        int selectitem = mPressHighGradeSetting.getSelectedItemPosition();

        short sensorindex = (short) mPressIndexSelect.getSelectedItemPosition();

        //第一路地址 227
        //第二路地址 228

        sensorindex+=227;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2);
        byteBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(sensorindex);
        byteBuffer.rewind();

        byteBuffer.get(sendpressmodify,14,2);

//        if(selectitem == 0)
//        {
//            sendpressmodify[16]=0;
//        }
//        else
//        {
//            sendpressmodify[16]=1;
//        }
        sendpressmodify[16] = mEnableAlarmSet;
        int indexitem = 0;
       for(EditText set:mTextctrl)
       {
           try {
               float f1 = Float.valueOf(set.getText().toString());
               byteBuffer = ByteBuffer.allocateDirect(4);
               byteBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
               byteBuffer.putFloat(f1);
               byteBuffer.rewind();

               byteBuffer.get(sendpressmodify,17+indexitem*4,4);
               indexitem++;
           }
           catch (NumberFormatException hexx)
           {
               ToastUtils.showToast(getContext(),"参数设置错误");
               return;
           }
       }


        CodeFormat.crcencode(sendpressmodify);

        String readOutMsg = DigitalTrans.byte2hex(sendpressmodify);
        Log.d("zl",CodeFormat.byteToHex(sendpressmodify,sendpressmodify.length));
        verycutstatus(readOutMsg);
    }

    private void setSpinneradpater(Spinner spinner, String[] list )
    {
        int i=0;
        ArrayList<String> arrayList;
        arrayList=new ArrayList<>();
        for(i=0;i<list.length;i++)
        {
            arrayList.add(list[i]);
        }
        //适配器
        ArrayAdapter<String> arr_adapter;
        Activity activity=getActivity();
        if(activity!=null)
        {
            arr_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(arr_adapter);
        }
    }

    public void verycutstatus(String readOutMsg) {
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

    private  class  OnToggleButChangedIMPL implements BaseSlidingToggleButton.OnCheckedChanageListener{

        @Override
        public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
                int ides = slidingToggleButton.getId();

            for(int[] item:butfunmap)
            {
                if(ides == item[0])
                {
                    if(isChecked == true)
                    {
                        mEnableAlarmSet |= (byte)item[1];
                    }
                    else
                    {
                        mEnableAlarmSet &=(byte)(~item[1]);
                    }
                }
            }
        }
    }
}
