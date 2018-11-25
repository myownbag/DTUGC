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

public class PressSensoraddSetframent extends BaseFragment implements View.OnClickListener {
   public View mView;
   public TextView mTextResultView;
   public RadioGroup mSelectGroup;
   public Button mbutsend;
   public int mSelectfun=0;
   public String cmd1="+++++7";
   String[] cmds={"R_1","R_2","W12","W21"};
   public boolean mIsitem=false;
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
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl", CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
        if(!mIsatart)
        {
            return;
        }
        String str="";
        int i=0;
        if(mIsitem==false)
        {
            for(i=0;i<readOutBuf1.length;i++)
            {
                str+=(char)readOutBuf1[i];
            }
            int temp=str.indexOf("OK");
            Log.d("zl","temp:"+temp);
            if(temp>=0)
            {
                mIsitem=true;
                String readOutMsg = DigitalTrans.byte2hex(cmds[mSelectfun].getBytes());
                verycutstatus(readOutMsg);
                Log.d("zl", "OndataCometoParse: " +CodeFormat.byteToHex(cmds[mSelectfun].getBytes(),cmds[mSelectfun].getBytes().length));
            }
            else
            {
                mTextResultView.setText("进入传感器调试模式失败");
                MainActivity.getInstance().mDialog.dismiss();
            }
        }
        else
        {
            for(i=0;i<readOutBuf1.length-2;i++)
            {
                str+=(char)readOutBuf1[i];
            }
            mTextResultView.setText(str);
            MainActivity.getInstance().mDialog.dismiss();
        }

    }

    @Override
    public void onClick(View v) {
        mIsatart=true;
        mIsitem=false;
        mTextResultView.setText("");
        String readOutMsg = DigitalTrans.byte2hex(cmd1.getBytes());
        verycutstatus(readOutMsg);
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
            }
        }
    }
}
