package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;

import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ItemSetingActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;



/**
 * Created by Administrator on 2018-03-22.
 */

public class LocalsettngsFregment extends Fragment {
    View mView;
    LayoutInflater thisinflater;
    ViewGroup thiscontainer;
    ListView mylist;
    Button mybut;
    thislistviewadpater myadpater;
    int mIndexcmd=0;
    public String[][] baseinfo=
    {
            {"100","连接设备属性","1","L"},
            {"101","设备供电方式","1","L"},
            {"103","二级地址","8","T"},
            {"110","阀门选择","10","L"},
            {"198","无线模块","1","L"},
            {"201","联网参数","40","T"},
            {"202","主站IP及端口","6","T"},
            {"205","校时IP及端口","6","T"},
            {"206","数据传输协议","1","L"},
            {"207","数据传输方式","1","L"},
            {"208","频率方式","1","L"},
            {"209","传输频率","2","T"},
            {"210","数据传输固定时刻","12","T"},
    };
    public String[][] registerinfosel=
            {
                    {"100","热量表采集","1"},
                    {"100","修正仪表采集","2"},
                    {"100","可燃气体报警器","4"},
                    {"100","压力报警器","8"},
                    {"100","燃气仪表采集","16"},

                    {"101","外供电","0"},
                    {"101","锂电池","1"},
                    {"101","外供电+备电","16"},
                    {"101","干电池+备电","17"},
                    //阀门解析和打包需要特别注意
                    {"110","未挂接","0"},
                    {"110","EMV DJF","1"},
                    {"110","EMV CV GC","2"},
                    {"110","EMV CV G6+","3"},
                    {"110","EMV BV","4"},
                    //无线模块
                    {"198","模块关闭","0"},
                    {"198","模块自适应","1"},
                    {"198","M72","2"},
                    {"198","MC323","3"},

                    {"206","TCP","0"},
                    {"206","UDP","1"},

                    {"207","主动上传","0"},
                    {"207","远程抄读","1"},
                    {"207","透明传输","2"},

                    {"208","频率","0"},
                    {"208","每天固定时间","2"},
            };
    public String[] settingscontent=new String[baseinfo.length];
    byte [][] senddatabuf=new byte[baseinfo.length][18];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        thisinflater =inflater;
        thiscontainer=container;
        mView = inflater.inflate(R.layout.localsetlayout, container, false);
        initView();
        initdata();
        return  mView;
    }

    private void initView() {
        mylist= mView.findViewById(R.id.local_info_list);
        myadpater=new thislistviewadpater();
        mylist.setAdapter(myadpater);
        View view=View.inflate(MainActivity.getInstance(),R.layout.lcalseitemthead,null);
        mylist.addHeaderView(view);
        mybut= mView.findViewById(R.id.btn_realtime_data);
        mybut.setOnClickListener(new butonclicklistener());
        mylist.setOnItemClickListener(new Onlistviewitemclicked());
    }
    private void initdata() {
        MainActivity.getInstance().setOndataparse(new DataParse());
    }
    public class thislistviewadpater extends BaseAdapter
    {

        @Override
        public int getCount() {
            return baseinfo.length;
        }

        @Override
        public Object getItem(int position) {
            return baseinfo[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=View.inflate(MainActivity.getInstance(),R.layout.localsetitem,null);
            TextView registerser=convertView.findViewById(R.id.sernum_item) ;
            TextView registeraddr=convertView.findViewById(R.id.register_item) ;
            TextView registerinfo=convertView.findViewById(R.id.registerinfo_item) ;
            TextView registerlenth=convertView.findViewById(R.id.registerlen_item) ;
            TextView regisiteritem=convertView.findViewById(R.id.registerset_item);
            registerser.setText(""+(position+1));
            registeraddr.setText(baseinfo[position][0]);
            registerinfo.setText(baseinfo[position][1]);
            registerlenth.setText(baseinfo[position][2]);
            if(settingscontent[position]!=null)
                regisiteritem.setText(settingscontent[position]);
            return convertView;
        }
    }
    private class butonclicklistener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int index=0;
            int i;
            int tempint;
            byte[] adsinf0;//={1,3,105, (byte) 0xC7};
            adsinf0=new byte[baseinfo.length];
            for(i=0;i<adsinf0.length;i++)
            {
                tempint=Integer.valueOf(baseinfo[i][0]);
                adsinf0[i]= (byte) (tempint%0x100);
            }
            mIndexcmd=0;
            for(int j=0;j<adsinf0.length;j++)
            {
                senddatabuf[j][index++]= (byte) 0xfd;
                senddatabuf[j][index++]= (byte) 0x00;
                senddatabuf[j][index++]= (byte) 0x00;
                senddatabuf[j][index++]= 13;
                senddatabuf[j][index++]= (byte) 0x00;
                senddatabuf[j][index++]= (byte) 0x19;
                for(i=0;i<8;i++)
                {
                    senddatabuf[j][index++]= (byte) 0x00;
                }
                senddatabuf[j][index++]= adsinf0[j];
                senddatabuf[j][index++]= (byte) 0x00;
                CodeFormat.crcencode(senddatabuf[j]);
                index=0;
            }
            if(mIndexcmd<adsinf0.length)
            {
                String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd++]);
                verycutstatus(readOutMsg);
            }

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
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }

    private class Onlistviewitemclicked implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String registername;
            String registerconnet;
            String registersetting;
            registername=((TextView)view.findViewById(R.id.register_item)).getText().toString();
            registersetting=((TextView)view.findViewById(R.id.registerinfo_item)).getText().toString();
            registerconnet= ((TextView)view.findViewById(R.id.registerset_item)).getText().toString();
            Intent serverIntent = new Intent(MainActivity.getInstance(), ItemSetingActivity.class);
            serverIntent.putExtra("addrs",registername);
            serverIntent.putExtra("name",registersetting);
            serverIntent.putExtra("settings",registerconnet);
            startActivityForResult(serverIntent, Constants.LocalsetingFlag);
           // Log.d("zl","position:"+position+"id:"+id);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("zl","requestCode:"+requestCode+" "+"resultCode:"+resultCode);
        MainActivity.getInstance().setOndataparse(new DataParse());
        if(data!=null)
        {
            String temp=data.getStringExtra("name");
            int index=  data.getIntExtra("addrs",-1);
            if(index>=0&&temp!=null)
            {
                settingscontent[index]=temp;
            }
            myadpater.notifyDataSetChanged();
        }
    }
    private class DataParse implements MainActivity.Ondataparse
    {

        @Override
        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {
            String temp;
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
                    ToastUtils.showToast(getActivity(), "数据长度异常");
                    return;
                }
            }
//            switch()
//            {
//                case :
//                    break;
//            }
        }
    }
}
