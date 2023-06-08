package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class SvEventShowFragment extends BaseFragment implements View.OnClickListener {
    View mView;
    ListView mylist;
    Button mybut;
    byte[] recbuf;
    int buflenth;
    Alarmtype[] mEnabletype;
    thislistviewadpater adpater;

    ArrayList<String []> listinfo;

    CountDownTimer countDownTimer = new CountDownTimer(5000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
//            Log.d("zl","SvEventShowFragment Onfinish");
            if(buflenth<2)
            {
                return;
            }

            ByteBuffer buf = ByteBuffer.allocate(2);
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(recbuf,0,2);
            buf.rewind();
            short items = buf.getShort();
            Log.d("zl","current items="+items);
            if(items<0)
            {
                ToastUtils.showToast(getContext(),"没有事件信息");
                return;
            }
            int curitem = 0;
            byte[] eventbuf = new byte[66];
            for(curitem = 0;curitem<items;curitem++ )
            {
                if(((curitem+1)*64+2)>buflenth)
                {
                    break;
                }

                for(int i=0;i<64;i++)
                {
                    eventbuf[i] = recbuf[curitem*64+i+2];
                }
                int testint = CodeFormat.crcencode(eventbuf);
                Log.d("zl","CRC ="+testint);
                Log.d("zl",CodeFormat.byteToHex(eventbuf,eventbuf.length));
                if(CodeFormat.crcencode(eventbuf) == 0)
                {
                    String[] s = new String[4];

                    s[0] = String.format("%x-%x-%x %x:%x:%x",eventbuf[0],eventbuf[1],eventbuf[2],eventbuf[4],eventbuf[5],eventbuf[6]);

                    int t1,t2;

                    t1 = Integer.valueOf(String.format("%x",eventbuf[7])).intValue();
                    t2 = Integer.valueOf(String.format("%x",eventbuf[8])).intValue();

                     switch (t1){
                         case 0:
                             s[1] = "阀门";

                             switch (t2)
                             {
                                 case 1:
                                     s[2] = "开阀指令";
                                     break;
                                 case 2:
                                     s[2] = "关阀指令";
                                     break;
                                 case 3:
                                     s[2] = "执行开阀";
                                     break;
                                 case 4:
                                     s[2] = "执行关阀";
                                     break;
                                 case 5:
                                     s[2] = "超压关阀";
                                     break;
                                 case 6:
                                     s[2] = "阀门异常";
                                     break;
                                 default:
                                     s[2] = "未知";
                                     break;
                             }
                             break;
                         case 1:
                             s[1] = "报警";
                             switch (t2)
                             {
                                 case 1:
                                     s[2] = "H1A";
                                     break;
                                 case 2:
                                     s[2] = "H2A";
                                     break;
                                 case 3:
                                     s[2] = "H3A";
                                     break;
                                 case 4:
                                     s[2] = "L1A";
                                     break;
                                 case 5:
                                     s[2] = "L2A";
                                     break;
                                 case 6:
                                     s[2] = "L3A";
                                     break;
                                 default:
                                     s[2] = "未知";
                                     break;
                             }
                             break;
                         case 2:
                             s[1] = "本机";
                             switch (t2)
                             {
                                 case 1:
                                     s[2] = "三级报警设置";
                                     break;
                                 case 2:
                                     s[2] = "外供电状态";
                                     break;
                                 case 3:
                                     s[2] = "压力变送器故障";
                                     break;
                                 case 4:
                                     s[2] = "压力异常";
                                     break;
                                 case 5:
                                     s[2] = "压力异常超20次";
                                     break;
                                 default:
                                     s[2] = "未知";
                                     break;
                             }
                             break;

                         default:
                             s[1] = "未知";
                             break;
                     }

                     s[3] = "";
                if(t1!=2)
                {
                    String CurrentGasPress = "当前压力：";
                    float gaspress;
                    buf = ByteBuffer.allocate(4);
                    buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.put(eventbuf,9,4);
                    buf.rewind();
                    gaspress = buf.getFloat();
                    CurrentGasPress+=gaspress;
                    CurrentGasPress+=" ";

                    String EnableStatus = "";
                    for(Alarmtype testbit:mEnabletype)
                    {
                        if((testbit.ByteEnableValue&eventbuf[13])!=0)
                        {
                            EnableStatus+=testbit.Info;
                            EnableStatus+=" ";
                        }
                    }
                    String AlarmSetting="";
                    String [] lable = {"低三级：","低二级：","低一级：","高一级：","高二级：","高三级："};
                    for(int k=0;k<6;k++)
                    {
                        float f;
                        buf = ByteBuffer.allocate(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.put(eventbuf,14+k*4,4);
                        buf.rewind();
                        f = buf.getFloat();
                        AlarmSetting+=lable[k]+f;
                        AlarmSetting+=" ";
                    }
                    s[3] = CurrentGasPress+EnableStatus+AlarmSetting;
                    Log.d("zl","items info"+s[0]+s[1]+s[2]+s[3]);
                }
                else
                {
                    switch (t2)
                    {
                        case 1:
                            String EnableStatus = "";
                            for(Alarmtype testbit:mEnabletype)
                            {
                                if((testbit.ByteEnableValue&eventbuf[9])!=0)
                                {
                                    EnableStatus+=testbit.Info;
                                    EnableStatus+=" ";
                                }
                            }
                            String AlarmSetting="";
                            String [] lable = {"低三级：","低二级：","低一级：","高一级：","高二级：","高三级："};
                            for(int k=0;k<6;k++)
                            {
                                float f;
                                buf = ByteBuffer.allocate(4);
                                buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                                buf.put(eventbuf,10+k*4,4);
                                buf.rewind();
                                f = buf.getFloat();
                                AlarmSetting+=lable[k]+f;
                                AlarmSetting+=" ";
                            }
                            s[3] = EnableStatus+AlarmSetting;
                            break;
                        case 2:
                        case 3:
                            if(eventbuf[9] == 0)
                            {
                                s[3] = "正常";
                            }
                            else
                            {
                                s[3] = "异常";
                            }
                            break;
                        case 4:
                            String  st = ""+(0x00ff&eventbuf[9]);
                            s[3] = "异常次数:"+st;
                            break;
                        default:
                            s[3] = "";
                            break;
                    }
                }

                    listinfo.add(s);
                }
            }
            adpater.notifyDataSetChanged();

            if(MainActivity.getInstance().mDialog.isShowing())
            {
                MainActivity.getInstance().mDialog.dismiss();
            }
        }
    };
    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        if(mIsatart == false)
        {
            return;
        }
//        Log.d("zl",CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());
        countDownTimer.cancel();
        for(byte rec:readOutBuf1)
        {
            recbuf[buflenth++] = rec;
        }
        countDownTimer.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        if(mView == null)
        {
            mView=inflater.inflate(R.layout.fragment_event_info_show,null,false);
        }
        listinfo = new ArrayList<>();
        listinfo.clear();
        InitView();
        InitData();
        return mView;
    }

    private void InitView() {
        mylist = mView.findViewById(R.id.event_show_list);
        mybut = mView.findViewById(R.id.btn_event_show);
        mybut.setOnClickListener(this);


        adpater = new thislistviewadpater();

        mylist.setAdapter(adpater);
    }

    private void InitData() {
        recbuf = new byte[1024*100];
        buflenth = 0;

        Alarmtype test;
        test = new Alarmtype();
        test.ByteEnableValue = 1;
        test.Info = "123";
        mEnabletype = new Alarmtype[6];

        int index = 0;
        mEnabletype[index]= new Alarmtype();
        mEnabletype[index].ByteEnableValue = 0x01;
        mEnabletype[index++].Info = "低一级使能";

        mEnabletype[index]= new Alarmtype();
        mEnabletype[index].ByteEnableValue = 0x02;
        mEnabletype[index++].Info = "低二级使能";

        mEnabletype[index]= new Alarmtype();
        mEnabletype[index].ByteEnableValue = 0x04;
        mEnabletype[index++].Info = "低三级使能";

        mEnabletype[index]= new Alarmtype();
        mEnabletype[index].ByteEnableValue = 0x10;
        mEnabletype[index++].Info = "高一级使能";

        mEnabletype[index]= new Alarmtype();
        mEnabletype[index].ByteEnableValue = 0x20;
        mEnabletype[index++].Info = "高二级使能";

        mEnabletype[index]= new Alarmtype();
        mEnabletype[index].ByteEnableValue = 0x40;
        mEnabletype[index++].Info = "高三级使能";

    }

    @Override
    public void onClick(View v) {
        listinfo.clear();
        buflenth = 0;

        byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
                ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0};

        ByteBuffer buf = ByteBuffer.allocate(2);
        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort((short) 3006);
        buf.rewind();
        buf.get(sendbufread,14,2);
        CodeFormat.crcencode(sendbufread);
        mIsatart = true;

        String readOutMsg = DigitalTrans.byte2hex(sendbufread);
        verycutstatus(readOutMsg, 0);
        countDownTimer.start();
    }

    public class thislistviewadpater extends BaseAdapter
    {

        @Override
        public int getCount() {
            return listinfo.size();
        }

        @Override
        public Object getItem(int position) {
            return listinfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=View.inflate(MainActivity.getInstance(),R.layout.event_show_item_contain,null);
            //event_item_show_time   event_item_show_event_type   event_item_show_event_info  event_item_show_detail
            TextView EventTime=convertView.findViewById(R.id.event_item_show_time) ;
            TextView EventType=convertView.findViewById(R.id.event_item_show_event_type) ;
            TextView EventInfo=convertView.findViewById(R.id.event_item_show_event_info) ;
            TextView EventDetail=convertView.findViewById(R.id.event_item_show_detail) ;
            String [] infoshow = listinfo.get(position);

            EventTime.setText(infoshow[0]);
            EventType.setText(infoshow[1]);
            EventInfo.setText(infoshow[2]);
            EventDetail.setText(infoshow[3]);

            return convertView;
        }
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

    private class Alarmtype{
        public byte ByteEnableValue;
        public String Info;
    }
}
