package gc.dtu.weeg.dtugc.fregment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.NbServiceAddrInputActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class NBRegisiterfragment  extends BaseFragment {
    public  View mView;
    public ImageView mImageView;
    public TextView maddrview;
    private SharedPreferences sp ;
    private String url;
    private Button mbutsend;
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
        mView=inflater.inflate(R.layout.nb_registger_fragment,null);
        initview();

        return mView;
    }

    private void initview() {

        mImageView=mView.findViewById(R.id.nb_img_set_addr);
        maddrview=mView.findViewById(R.id.nb_add_info);
        mbutsend=mView.findViewById(R.id.nb_but1);
        sp=MainActivity.getInstance().getSharedPreferences("User", Context.MODE_PRIVATE);

        initdata();

    }

    private void initdata() {
        String addr;
        addr=sp.getString(Constants.NB_SERVICE_KEY,"");
        maddrview.setText(addr);
        mImageView.setOnClickListener(new OnClicklisternerIMPL());
        mbutsend.setOnClickListener(new OnClicklisternerIMPL());
        url=addr+Constants.NB_Service_END;
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        MainActivity.getInstance().mDialog.dismiss();
        int i=0;
        if(!mIsatart)
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
        byte[] buf=new byte[15];
        ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(15);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(readOutBuf1,30,15);
        buf1.rewind();
        buf1.get( buf);
        String str="";
       for(i=0;i<15;i++)
       {
           if(buf[i]>=0x30&&buf[i]<=0x39)
           {
               str+=(char)buf[i];
           }
           else
           {
               String readOutMsg = DigitalTrans.byte2hex(sendbufread);
               verycutstatus(readOutMsg);
               return;
           }
       }
       
        Log.d("zl", "OndataCometoParse: "+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
    }
    public class OnClicklisternerIMPL implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch(id)
            {
                case R.id.nb_img_set_addr:
                    Intent intent;
                    // intent=new Intent(mainActivity, InstrumemtItemseetingActivity.class);
                    intent=new Intent(MainActivity.getInstance(), NbServiceAddrInputActivity.class);
                    startActivityForResult(intent, Constants.NBINPUTSETTINGFLAG);
                    break;
                case R.id.nb_but1:
                    mIsatart=true;
                    ByteBuffer buf1;
                    buf1=ByteBuffer.allocateDirect(4);
                    buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                    buf1.putInt(5);
                    buf1.rewind();
                    buf1.get(sendbufread,14,2);
                    CodeFormat.crcencode(sendbufread);

                    String readOutMsg = DigitalTrans.byte2hex(sendbufread);
                    verycutstatus(readOutMsg);
                    Log.d("zl", "onClick: "+CodeFormat.byteToHex(sendbufread,sendbufread.length));
                    break;
                    default:
                        break;
            }

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String addr;

        addr=sp.getString(Constants.NB_SERVICE_KEY,"未获取");
        Log.d("zl","onActivityResult: "+addr);
        maddrview.setText(addr);
        url=addr+Constants.NB_Service_END;
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

    @Override
    public void Oncurrentpageselect(int index) {
        if(index!=2)
        {
            mIsatart=false;
        }
    }
}
