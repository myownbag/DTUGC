package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class FrozendataFregment extends BaseFragment implements View.OnClickListener {
    View mView;
    public Button mBut;
    public Spinner mSpiner;
    public boolean mIsTotleRDing=false;
    public ListView mlistview;
    public listviewadpater myadpater;
    public ArrayList<Map<String,String>> mlistdata;
    String [] mylist={"最新第一条","最新第二条","最新第三条","最新第四条","最新第五条","全部历史数据"};
    byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x0E ,        0x00 ,0x24 ,0x00 ,        0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0,0x00};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.freeze_data_layout, container, false);
        initView();
        mlistdata=new ArrayList<>();
        return  mView;

    }

    private void initView() {
        mBut=mView.findViewById(R.id.freeze_but);
        mBut.setOnClickListener(this);
        mSpiner=mView.findViewById(R.id.freeze_selc);
        mlistview=mView.findViewById(R.id.freeze_data_list_view);
        myadpater=new listviewadpater();
        mlistview.setAdapter(myadpater);
        setSpinneradpater(mSpiner,mylist);
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        int i=0;
        if(mIsatart==false)
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
        if(mIsTotleRDing==false)
        {
            byte [] buf=new byte[31];
            ByteBuffer buf1;
            buf1=ByteBuffer.allocateDirect(29);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(readOutBuf1,16,29);
            buf1.rewind();
            buf1.get(buf);

            short crc= CodeFormat.crcencode(buf);
            String[] timeinfo=new String[7];
            if(crc==0)
            {

                for(i=0;i<buf.length;i++)
                {
                    String hex = Integer.toHexString(buf[i+2] & 0xFF);
                    if (hex.length() == 1) {
                        hex = '0' + hex;
                    }
                    timeinfo[i]=hex;
                }
            }
            Map<String,String> map=new HashMap();
            String time1="20"+timeinfo[0]+"-"+timeinfo[1]+"-"+timeinfo[2]+" "+timeinfo[4]+":"+timeinfo[5]+":"+timeinfo[6];
            MainActivity.getInstance().mDialog.dismiss();
        }
        else
        {

        }

        Log.d("zl","data:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
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

    @Override
    public void onClick(View v) {
        int index=mSpiner.getSelectedItemPosition();
        if(index==(mylist.length-1))
        {
            Dialog dialog=new AlertDialog.Builder(getActivity())
                    .setTitle("警告！！！")
                    .setIcon(R.drawable.warning_icon)
                    .setMessage("全部读出历史数据需耗时约30分钟！！\r\n是否继续？")
                    .setPositiveButton("确定", 						// 增加一个确定按钮
                            new DialogInterface.OnClickListener() {	// 设置操作监听
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) { 			// 单击事件
                                 FrozendataFregment.this.dofrozendataread(0);
                                }
                            }).setNegativeButton("取消", 			// 增加取消按钮
                            new DialogInterface.OnClickListener() {	// 设置操作监听
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) { 			// 单击事件

                                }
                            }).create(); 							// 创建Dialog
            dialog.show();
        }
        else
        {
            dofrozendataread(index+1);
        }

    }

    private void dofrozendataread(int i) {
        int index=0;
        byte[] adsinf0=new byte[2];//={1,3,105, (byte) 0xC7};
        mIsatart=true;
        if(i==0)
        {
            mIsTotleRDing=true;
        }
        else
        {
            mIsTotleRDing=false;
        }
        ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.putInt(6020);
        buf1.rewind();
        buf1.get(sendbufread,14,2);

        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.putInt(i);
        buf1.rewind();
        buf1.get(sendbufread,16,1);

        CodeFormat.crcencode(sendbufread);
        String readOutMsg = DigitalTrans.byte2hex(sendbufread);
        verycutstatus(readOutMsg);
        Log.d("zl", "dofrozendataread: "+CodeFormat.byteToHex(sendbufread,sendbufread.length));
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

    private class listviewadpater extends BaseAdapter
    {

        @Override
        public int getCount() {
            return mlistdata.size();
        }

        @Override
        public Object getItem(int position) {
            return mlistdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView=View.inflate(getActivity(),R.layout.freeze_item_show_layout,null);
            }
            TextView tem=convertView.findViewById(R.id.freeze_item_tem);
            TextView press1=convertView.findViewById(R.id.freeze_item_press1);
            TextView press2=convertView.findViewById(R.id.freeze_item_press2);
            TextView timeinfo=convertView.findViewById(R.id.freeze_item_timeinfo);
            String temp=mlistdata.get(position).get("temp");
            if(temp!=null)
                tem.setText(temp);
            temp=mlistdata.get(position).get("press1");
            if(temp!=null)
              press1.setText(temp);
            temp=mlistdata.get(position).get("press2");
            if(temp!=null)
                press2.setText(temp);
            temp=mlistdata.get(position).get("time");
            if(temp!=null)
                timeinfo.setText(temp);
            return convertView;
        }
    }
}
