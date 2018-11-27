package gc.dtu.weeg.dtugc.fregment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class PressSensoraddSetframent extends BaseFragment implements View.OnClickListener {
   public View mView;
   public TextView mTextResultView;
   public RadioGroup mSelectGroup;
   public RadioGroup mMainSelectGroup;
   public Button mbutsend;
   public int mSelectfun=0;
   public String cmd1="+++++7";
   public String cmdgas="+++++4";
   String[] cmds={"R_1","R_2","W12","W21"};
   Dialog minfodlg;
   String cmdgasitems[]=
           {
                   "DATAE\r",
                   "ZERO2\r",
                   "CALB 0250\r"
           };

   byte[][] factorysetcmd= {
           {(byte) 0xFD, 0x00, 0x00, 0x0E, 0x00,
                   0x15, 0x00, 0x00, 0x00, 0x00,
                   0x00, 0x00, 0x00, 0x00, 0x62,
                   0x00, (byte) 0x5A, 0x52, (byte) 0x88},   //进入工厂模式
           {(byte)0xFD ,0x00 ,0x00 ,0x0E ,0x00 ,
                   0x15 ,0x00 ,0x00 ,0x00 ,0x00 ,
                   0x00 ,0x00 ,0x00 ,0x00 ,0x62 ,
                   0x00 ,(byte)0xA5 ,0x52 ,(byte)0x88},     //保存数据并且复位
   };
   int mMainselectmode=0;

   public int mfunstep=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView=inflater.inflate(R.layout.press_sensor_set_layout,null);
        initview();

        return mView;

    }

    private void initview() {
        mTextResultView =mView.findViewById(R.id.press_sensor_set_result);
        mSelectGroup =mView.findViewById(R.id.press_sensor_set_but_group);
        mbutsend =mView.findViewById(R.id.press_sensor_but1);
        mbutsend.setOnClickListener(this);
        mSelectGroup.setOnCheckedChangeListener(new OnGroupCkeckedListernerimpl());

        mMainSelectGroup = mView.findViewById(R.id.sensor_main_selected);
        mMainSelectGroup.setOnCheckedChangeListener(new OnMainGroupCheckedListenerimpl());
        mSelectGroup.setVisibility(View.GONE);


        minfodlg =new  AlertDialog.Builder(MainActivity.getInstance())
                .setTitle("模式切换提示")
                .setMessage("进入工厂模式或设备复位都需要重新连接蓝牙")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        byte cmd[];
                        if(mMainselectmode==0)
                        {
                            cmd=factorysetcmd[0];
                        }
                        else if(mMainselectmode==4)
                        {
                            cmd=factorysetcmd[1];

                        }
                        else
                        {
                            cmd=new byte[3];
                        }
                        CodeFormat.crcencode(cmd);
                        String readOutMsg = DigitalTrans.byte2hex(cmd);
                        verycutstatus(readOutMsg,2000);
                        Log.d("zl","in dialog:"+CodeFormat.byteToHex(cmd,cmd.length));
                    }
                })
                .create();
       // minfodlg.show();
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl", "OndataCometoParse: "+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
        StringBuilder tempstr= new StringBuilder();
        for (byte aReadOutBuf1 : readOutBuf1) {
            tempstr.append((char) aReadOutBuf1);
        }
        Log.d("zl","OndataCometoParse: "+tempstr);
        if(!mIsatart)
        {
            return;
        }
      //  String str="";
        switch (mfunstep)
        {
            case 0:
                checkstep1(readOutBuf1);
                break;
            case 1:
                checkstep2(readOutBuf1);
                break;
            case 2:
                checkstep3(readOutBuf1);
                break;
            case 3:
                checkstep4(readOutBuf1);
                break;
            case 4:
                checkstep5(readOutBuf1);
                break;
        }

    }

    private void checkstep5(byte[] readOutBuf1) {
        int i;
        StringBuilder str= new StringBuilder();
        for(i=0;i<readOutBuf1.length;i++)
        {
            str.append((char) readOutBuf1[i]);
        }
        switch (mMainselectmode)
        {
            case 2:  //gas1
                mTextResultView.setText(str.substring(0,str.length()-1));
                MainActivity.getInstance().mDialog.dismiss();
                break;
            case 3: //gas2
                break;
        }
    }

    private void checkstep4(byte[] readOutBuf1) {
        int i;
        StringBuilder str= new StringBuilder();
        for(i=0;i<readOutBuf1.length-2;i++)
        {
            str.append((char) readOutBuf1[i]);
        }
        switch (mMainselectmode)
        {
            case 2:  //gas1
                if(str.indexOf("OEM")>=0)
                {
                    mfunstep++;
                    sendstep4();
                }
                else if(str.indexOf("USER")>=0)
                {
                    mfunstep++;
                   str = new StringBuilder("进入OEM失败");
                    mTextResultView.setText(str.toString());
                    MainActivity.getInstance().mDialog.dismiss();
                }
                else
                {
                    mTextResultView.setText("未知错误");
                    MainActivity.getInstance().mDialog.dismiss();
                }
                break;
            case 3: //gas2
                break;
        }
    }

    private void checkstep3(byte[] readOutBuf1) {
        int i;
        StringBuilder str= new StringBuilder();
        String readOutMsg;
        for(i=0;i<readOutBuf1.length;i++)
        {
            str.append((char) readOutBuf1[i]);
        }
        switch (mMainselectmode)
        {
            case 2:  //gas1
                if(str.indexOf("OEM")>=0)
                {
                    mfunstep+=2;
                    sendstep4();
                }
                else if(str.indexOf("USER")>=0)
                {
                    mfunstep++;
                    readOutMsg = DigitalTrans.byte2hex("OEM 0000\r".getBytes());
                    Log.d("zl","in PRESS:"+"OEM 0000");

                    verycutstatus(readOutMsg);
                    Log.d("zl",readOutMsg);
                }
                else
                {
                    mTextResultView.setText("未知错误");
                    MainActivity.getInstance().mDialog.dismiss();
                }
                break;
            case 3: //gas2
                break;
        }
    }

    private void sendstep4() {
        String readOutMsg="";
        switch (mSelectfun)
        {
            case 5://校零
                 readOutMsg = DigitalTrans.byte2hex(cmdgasitems[1].getBytes());
                Log.d("zl","IN PRESS"+cmdgasitems[1]);
                break;
            case 6://校量程
                readOutMsg = DigitalTrans.byte2hex(cmdgasitems[2].getBytes());
                Log.d("zl","IN PRESS"+cmdgasitems[2]);
                break;
        }

        verycutstatus(readOutMsg);
        Log.d("zl",readOutMsg);

    }

    private void checkstep2(byte[] readOutBuf1) {
        int i;
        StringBuilder str= new StringBuilder();
        String readOutMsg;
        for(i=0;i<readOutBuf1.length;i++)
        {
            str.append((char) readOutBuf1[i]);
        }
        switch (mMainselectmode)
        {
            case 1:

                mTextResultView.setText(str.substring(0,str.length()-2));
                MainActivity.getInstance().mDialog.dismiss();
                break;
            case 2:
                if(readOutBuf1[2]==0)
                {
                    mfunstep++;
                    switch (mSelectfun)
                    {
                        case 4:
                            ByteBuffer buf;
                            buf=ByteBuffer.allocate(2);
                            buf.order(ByteOrder.BIG_ENDIAN);
                            buf.put(readOutBuf1,0,2);
                            buf.rewind();
                            short temp=buf.getShort();
                            mTextResultView.setText("红外探头读数:"+temp);
                            MainActivity.getInstance().mDialog.dismiss();
                            break;
                        case 5:
                        case 6:
                            readOutMsg = DigitalTrans.byte2hex("UART?\r".getBytes());
                            verycutstatus(readOutMsg);
                            Log.d("zl","IN press: UART?");
                            Log.d("zl",readOutMsg);
                            break;
                    }
                }
                else
                {
                    mTextResultView.setText("探头异常，异常码:"+readOutBuf1[2]);
                    MainActivity.getInstance().mDialog.dismiss();
                }

                break;
            case 3:
                break;

        }
    }

    private void checkstep1(byte[] readOutBuf1) {
        int i;
        StringBuilder str= new StringBuilder();
        String readOutMsg;
        for(i=0; i<readOutBuf1.length; i++)
        {
            str.append((char) readOutBuf1[i]);
        }
        int temp=-1;
        temp=str.indexOf("OK");
        Log.d("zl","temp:"+temp);
        switch (mMainselectmode)
        {
            case 0:
            case 4:
                break;
            case 1:
                if(temp>=0)
                {
                    mfunstep++;
                }
                else
                {
                    mTextResultView.setText("进入压力传感器调试模式失败");
                    MainActivity.getInstance().mDialog.dismiss();
                }
                readOutMsg = DigitalTrans.byte2hex(cmds[mSelectfun].getBytes());
                verycutstatus(readOutMsg);
                Log.d("zl", "press: " +CodeFormat.byteToHex(cmds[mSelectfun].getBytes(),cmds[mSelectfun].getBytes().length));
                Log.d("zl",cmds[mSelectfun]);
                break;
            case 2:
                if(temp>=0)
                {
                    mfunstep++;
                }
                else
                {
                    mTextResultView.setText("进入气体传感器调试模式失败");
                    MainActivity.getInstance().mDialog.dismiss();
                }
                readOutMsg = DigitalTrans.byte2hex(cmdgasitems[0].getBytes());
                verycutstatus(readOutMsg);
                Log.d("zl", "press: " +CodeFormat.byteToHex(cmdgasitems[0].getBytes(),cmdgasitems[0].getBytes().length));
                Log.d("zl",cmdgasitems[0]);
                break;
            case 3:
                if(temp>=0)
                {
                   mfunstep++;
                }
                else
                {
                    mTextResultView.setText("进入气体传感器调试模式失败");
                    MainActivity.getInstance().mDialog.dismiss();
                }

                break;

        }
    }

    @Override
    public void onClick(View v) {
        mIsatart=true;
        mfunstep=0;
        mTextResultView.setText("");
        String readOutMsg;
        switch (mMainselectmode)
        {
            case 0:
            case 4:
                minfodlg.show();
                break;
            case 1:
                readOutMsg= DigitalTrans.byte2hex(cmd1.getBytes());
                verycutstatus(readOutMsg);
                break;
            case 2:
            case 3:
                if(mMainselectmode==3)
                {
                    ToastUtils.showToast(MainActivity.getInstance(),"暂时不支持");
                    return;
                }
                readOutMsg = DigitalTrans.byte2hex(cmdgas.getBytes());
                verycutstatus(readOutMsg);
                break;
                default:

                    break;
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
    public class OnGroupCkeckedListernerimpl implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId)
            {
                case R.id.press_sensor_set_but_press1rd:
                    mSelectfun=0;
                    break;
                case R.id.press_sensor_set_but_press2rd:
                    mSelectfun=1;
                    break;
                case R.id.press_sensor_set_but_press1wd:
                    mSelectfun=2;
                    break;
                case R.id.press_sensor_set_but_press2wd:
                    mSelectfun=3;
                    break;
                case R.id.press_sensor_gassensor_getdata:
                    mSelectfun=4;
                    break;
                case R.id.press_sensor_gassensor_zero:
                    mSelectfun=5;
                    break;
                case R.id.press_sensor_gassensor_calb:
                    mSelectfun=6;
                    break;
            }
        }
    }
    public class OnMainGroupCheckedListenerimpl implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId)
            {
                case R.id.sensor_enternfactory:
                    mMainselectmode=0;
                    mSelectGroup.setVisibility(View.GONE);
                    break;
                case R.id.sensor_press_debug:
                    mMainselectmode=1;
                    setPresssetingsVisibility(true);
                    setGassettingsVisivility(false);
                    mSelectGroup.setVisibility(View.VISIBLE);
                    break;
                case R.id.sensor_gas_1:
                    mMainselectmode=2;
                    setGassettingsVisivility(true);
                    setPresssetingsVisibility(false);
                    mSelectGroup.setVisibility(View.VISIBLE);
                    break;
                case R.id.sensor_gas_2:
                    mMainselectmode=3;
                    mSelectGroup.setVisibility(View.VISIBLE);
                    setGassettingsVisivility(true);
                    setPresssetingsVisibility(false);
                    break;
                case R.id.sensor_reset:
                    mMainselectmode=4;
                    mSelectGroup.setVisibility(View.GONE);
                    break;
            }
        }
    }
    private void setPresssetingsVisibility(boolean state)
    {
        RadioButton bt1,bt2,bt3,bt4;
        bt1=mView.findViewById(R.id.press_sensor_set_but_press1rd);
        bt2=mView.findViewById(R.id.press_sensor_set_but_press2rd);
        bt3=mView.findViewById(R.id.press_sensor_set_but_press1wd);
        bt4=mView.findViewById(R.id.press_sensor_set_but_press2wd);
        if(state)
        {
            bt1.setVisibility(View.VISIBLE);
            bt2.setVisibility(View.VISIBLE);
            bt3.setVisibility(View.VISIBLE);
            bt4.setVisibility(View.VISIBLE);
            bt1.setChecked(true);
        }
        else
        {
            bt1.setVisibility(View.GONE);
            bt2.setVisibility(View.GONE);
            bt3.setVisibility(View.GONE);
            bt4.setVisibility(View.GONE);
        }
    }
    private void setGassettingsVisivility(boolean state )
    {
        RadioButton bt1,bt2,bt3;
        bt1=mView.findViewById(R.id.press_sensor_gassensor_calb);
        bt2=mView.findViewById(R.id.press_sensor_gassensor_getdata);
        bt3=mView.findViewById(R.id.press_sensor_gassensor_zero);
        if(state)
        {
            bt1.setVisibility(View.VISIBLE);
            bt2.setVisibility(View.VISIBLE);
            bt3.setVisibility(View.VISIBLE);
            bt2.setChecked(true);
        }
        else
        {
            bt1.setVisibility(View.GONE);
            bt2.setVisibility(View.GONE);
            bt3.setVisibility(View.GONE);
        }
    }
}
