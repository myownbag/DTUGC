package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.InstrumemtItemseetingActivity;
import gc.dtu.weeg.dtugc.utils.NbServiceAddrInputActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class ExternedGasAlarmSettingFragment extends BaseFragment {
    View mView;
    TextView mAlarmCountsView;
    RelativeLayout mRelativeLayout;
    thislistviewadpater myadpater;
    Button mbut;
    int mItemCounts;
//    String [][] baseinfo;

    ArrayList<String[]> baseinfo1;
    ListView mAlarmList;
//    private byte [] bufofreadcmd=new byte[18];
    private SharedPreferences sp ;
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

        int devicestatus=0x000000ff&readOutBuf1[16];
        int devicetype;
        ByteBuffer buf1;
        byte[] tempbyte;
        tempbyte=new byte[4];
        tempbyte[0]=readOutBuf1[17];
        tempbyte[1]=readOutBuf1[18];
        buf1=ByteBuffer.allocateDirect(4);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(tempbyte);
        buf1.rewind();
        devicetype=buf1.getInt();
        Log.d("zl","devicetype:"+devicetype);
        String info[][]= InstrumemtItemseetingActivity.baseinfo;
        if(baseinfo1 == null)
        {
            baseinfo1 = new ArrayList<>();
        }
//        registerser.setText(baseinfo1.get(position)[0]);
//        registetype.setText(baseinfo1.get(position)[1]);
//        registeradress.setText(baseinfo1.get(position)[2]);
//        registervalue.setText(baseinfo1.get(position)[3]);
//        registeronoff.setText(baseinfo1.get(position)[4]);
        String [] temp = new String[5];
        for(int i=0;i<temp.length;i++)
        {
            temp[0] = "";
        }
        temp[0] = ""+(3000+baseinfo1.size());
        for(int i=0;i<info.length;i++)
        {
            if(info[i][0].equals("2000")&&info[i][1].equals("1"))
            {
                if(Integer.valueOf(info[i][3]).intValue()== devicestatus)
                {
                    temp[4] = info[i][2];
                }
            }
            if(info[i][0].equals("3000")&&info[i][1].equals("2"))
            {

                if(Integer.valueOf(info[i][3]).intValue()== devicetype)
                {
                    temp[1] = info[i][2];
//                    reg2000list.get(1).put("value",info[i][2]);
                }
            }
        }
        //仪表地址
        int addr=0x000000ff&(readOutBuf1[19]);
        temp[2] = ""+addr;
//        reg2000list.get(2).put("value",""+addr);
        //供电时长
//        buf1=ByteBuffer.allocateDirect(4);
//        buf1=buf1.order(ByteOrder.BIG_ENDIAN);
//        buf1.put(readOutBuf1,24,4);
//        buf1.rewind();
//        int timegap=buf1.getInt();
//        reg2000list.get(3).put("value",""+timegap);


        buf1=ByteBuffer.allocateDirect(8);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(readOutBuf1,28,8);
        buf1.rewind();
        byte[] by1=new byte[8];
        buf1.get(by1);
        byte[] t = new byte[8];
        for(int i =0;i<8;i++)
        {
            t[i] = by1[7-i];
        }

        temp[3] = DigitalTrans.byte2hex(t);
//        reg2000list.get(4).put("value",DigitalTrans.byte2hex(by1));
        String s = "";
        for(int i=0;i<temp.length;i++)
        {
            s += temp[i]+",";
        }
        Log.d("zl","OnBlueParse "+ s);
        baseinfo1.add(temp);
        if(baseinfo1.size()>=mItemCounts)
        {
            myadpater.notifyDataSetChanged();
            MainActivity parentActivity1 = (MainActivity) getActivity();
            if(parentActivity1.mDialog.isShowing())
            {
                parentActivity1.mDialog.dismiss();
            }
        }
        else
        {
            String readOutMsg = DigitalTrans.byte2hex(initsendbuf(3000 +baseinfo1.size()));
            verycutstatus(readOutMsg);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.extern_gas_alarm_setting_fragment_layout, container, false);
        sp=MainActivity.getInstance().getSharedPreferences("User", Context.MODE_PRIVATE);
        initdata();
        initview();
        return mView;
    }

    private void initdata() {
        mItemCounts = 1;
    }
    private void initview() {
        mRelativeLayout = mView.findViewById(R.id.extern_gas_head_container);
        mAlarmList = mView.findViewById(R.id.extern_gas_alarm_list);
        mAlarmCountsView = mView.findViewById(R.id.extern_gas_alarm_count);
        myadpater = new thislistviewadpater();
        mAlarmList.setAdapter(myadpater);
        mAlarmList.setOnItemClickListener(new OnListItemClicked());
        mRelativeLayout.setOnClickListener(new OnViewClicked());
        String addrurl=sp.getString(Constants.EXALARM_SERVICE_KEY,"1");
        mAlarmCountsView.setText(addrurl);
        mbut = mView.findViewById(R.id.extern_gas_alarm_but);
        mbut.setOnClickListener(new OnViewClicked());
    }
    public class thislistviewadpater extends BaseAdapter
    {

        @Override
        public int getCount() {
            int count =0;
            if(baseinfo1 == null)
            {
                count = 0;
            }
            else
            {
                count = baseinfo1.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
           String[] b;
            if(baseinfo1 != null)
            {
               b= null;
            }
            else
            {
                b = baseinfo1.get(position);
            }
            return b;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=View.inflate(MainActivity.getInstance(),R.layout.extern_gas_alarm_list_row,null);
            TextView registerser=convertView.findViewById(R.id.regadress_alarm) ;
            TextView registetype=convertView.findViewById(R.id.type_alarm) ;
            TextView registeradress=convertView.findViewById(R.id.alarm_address) ;
            TextView registervalue=convertView.findViewById(R.id.alarm_value) ; //regadress_onoff
            TextView registeronoff=convertView.findViewById(R.id.regadress_onoff) ; //regadress_onoff
            if(baseinfo1 !=null)
            {
                registerser.setText(baseinfo1.get(position)[0]);
                registetype.setText(baseinfo1.get(position)[1]);
                registeradress.setText(baseinfo1.get(position)[2]);
                registervalue.setText(baseinfo1.get(position)[3]);
                registeronoff.setText(baseinfo1.get(position)[4]);
            }
            return convertView;
        }
    }

    public class OnListItemClicked implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity mainActivity=MainActivity.getInstance();
            Intent intent;
            intent=new Intent(mainActivity, InstrumemtItemseetingActivity.class);
            String str;
            TextView textView= view.findViewById(R.id.regadress_alarm);
            str = textView.getText().toString();
            intent.putExtra("title","Reg "+(Integer.valueOf(str)));
            String[] temp =  baseinfo1.get(position);
            putdata2000(intent,temp,Integer.valueOf(str));
            startActivityForResult(intent, Constants.InstrumemtsetingFlag);
        }


    }
    private void putdata2000(Intent intent, String[] valueOf,int regadd) {
        intent.putExtra("listdata",valueOf);
        intent.putExtra("regaddr",regadd);
    }
    public class OnViewClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id)
            {
                case R.id.extern_gas_head_container:
                    Intent intent;
                    intent=new Intent(MainActivity.getInstance(), NbServiceAddrInputActivity.class);
                    intent.putExtra("requestpage","EXTERNED_ALARM");
                    startActivityForResult(intent, Constants.EXTERNEDALARMINPUTSETTINGFLAG);
                    break;
                case R.id.extern_gas_alarm_but:
                    mIsatart = true;
                    if(baseinfo1 != null)
                        baseinfo1.clear();
                    baseinfo1 = null;
                    mItemCounts = Integer.valueOf(mAlarmCountsView.getText().toString()).intValue();
                    String readOutMsg = DigitalTrans.byte2hex(initsendbuf(3000));
                    verycutstatus(readOutMsg);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constants.EXTERNEDALARMINPUTSETTINGFLAG == requestCode)
        {
            String addrurl;
            addrurl=sp.getString(Constants.EXALARM_SERVICE_KEY,"");
            Log.d("zl","onActivityResult: "+addrurl);
            mAlarmCountsView.setText(addrurl);
        }
       else if(Constants.InstrumemtsetingFlag == requestCode)
        {
//            intent.putExtra("returnsettings",temp);
//            intent.putExtra("regaddr",reg);
            if(resultCode ==0)
            {
                return;
            }
             String temp[] = data.getStringArrayExtra("returnsettings");
             int Reg = data.getIntExtra("regaddr",3000);
             String t="";
             for(int i=0;i<temp.length;i++)
             {
                 t+= temp[i]+",";
             }
             Log.d("zl","onActivityResult " + t + " Reg "+Reg);

//            registerser.setText(baseinfo1.get(position)[0]);
//            registetype.setText(baseinfo1.get(position)[1]);
//            registeradress.setText(baseinfo1.get(position)[2]);
//            registervalue.setText(baseinfo1.get(position)[3]);
//            registeronoff.setText(baseinfo1.get(position)[4]);

            String[] str = new String[5];
            str[0] = ""+Reg;
            str[1] = temp[1];
            str[2] = temp[2];
            str[3] = temp[4];
            str[4] = temp[0];

            baseinfo1.set(Reg-3000,str);
            myadpater.notifyDataSetChanged();
        }
    }

    private byte[] initsendbuf( int addressreg) {
        byte [] bufofreadcmd=new byte[18];
        byte[] temp={(byte)0xFD,0x00 ,0x00 ,0x0D ,0x00 ,0x19 ,0x00 ,0x00 ,0x00 ,0x00
                ,0x00 ,0x00 ,0x00 ,0x00 ,(byte)0xCE ,0x07 ,0x42 ,(byte)0x92};
        ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(18);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(temp);
        buf1.rewind();
        buf1.get(bufofreadcmd);
        ByteBuffer buf;
        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
        buf.putInt(addressreg);
        buf.rewind();
        buf.get(bufofreadcmd,14,2);
        CodeFormat.crcencode(bufofreadcmd);
        return  bufofreadcmd;
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
}
