package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.sqltools.FreezedataSqlHelper;
import gc.dtu.weeg.dtugc.sqltools.MytabCursor;
import gc.dtu.weeg.dtugc.sqltools.MytabOperate;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class FrozendataFregment extends BaseFragment implements View.OnClickListener {
    View mView;
    public Button mBut;
    private Button Btest;
    private Button Brd;
    private Button Btotle;
    public Spinner mSpiner;
    public boolean mIsTotleRDing=false;
    public ListView mlistview;
    public listviewadpater myadpater;
    public ArrayList<Map<String,String>> mlistdata;
    public SimpleDateFormat myFmt;

    String [] mylist={"最新第一条","最新第二条","最新第三条","最新第四条","最新第五条"};
    byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x11 ,        0x00 ,0x24 ,0x00 ,        0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C ,0x00,0x00,0x00, (byte) 0xA0,0x00};

    public FreezedataSqlHelper helper = null ;		 //mysqlhelper				// 数据库操作
    private MytabOperate mtab = null ;
    //private MytabCursor mycur=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.freeze_data_layout, container, false);
        mlistdata=new ArrayList<>();
        helper = new FreezedataSqlHelper(getContext(), Constants.TABLENAME1
                ,null,1);  //this.helper = new MyDatabaseHelper(this) ;
        initView();
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
        Btest=mView.findViewById(R.id.testdb);
        Btest.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                Date now=new Date();
                myFmt = new SimpleDateFormat(Constants.DATE_FORMAT);
                FrozendataFregment.this.mtab = new MytabOperate(
                        FrozendataFregment.this.helper.getWritableDatabase());
                FrozendataFregment.this.mtab.insert1("14010001","20.5"
                        ,"23","55",myFmt.format(now));
            }
        });
        Brd=mView.findViewById(R.id.testrd);
        Brd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MytabCursor cur = new MytabCursor(	// 实例化查询
                        // 取得SQLiteDatabase对象
                        FrozendataFregment.this.helper.getReadableDatabase()) ;
                ArrayList<Map<String,String>> all  =      cur.find1("14010001",
                        "DESC"
                        ,-1,3);
                if(all==null)
                {
                    Log.d("zl","all=null");
                    return;
                }
                int count=all.size();
                int i;
                for(i=0;i<count;i++)
                {
                    Log.d("zl",""+i+":"
                            +all.get(i).get("mac")+"  "
                            +all.get(i).get("temp")+"  "
                            +all.get(i).get("press1")+"  "
                            +all.get(i).get("press2")+"  "
                            +all.get(i).get("time")+"\r\n"
                    );
                }

            }
        });

        Btotle=mView.findViewById(R.id.testtotle);
        Btotle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MytabCursor cur = new MytabCursor(	// 实例化查询
                        // 取得SQLiteDatabase对象
                        FrozendataFregment.this.helper.getReadableDatabase()) ;
             int temp=   cur.getcount("14010001");
             Log.d("zl","总数是:"+temp);
            }
        });
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1)
    {
        //MainActivity.getInstance().mDialog.dismiss();
        Log.d("zl","OndataCometoParse:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
        //Log.d("zl","name:"+MainActivity.getInstance().getmConnectedDeviceName());

        boolean need2stroe=false;
        int i;
        if(!mIsatart)
        {
            return;
        }
        if(mIsTotleRDing)
        {

//            boolean flag= MainActivity.getInstance().getcurblueservice().getcurSemaphore().tryAcquire();
//            if(flag==false)
//            {
//                Log.d("zl", "OndataCometoParse: 获取信号量失败");
//            }
            alldatacomtoparse(readOutBuf1);
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
        MainActivity.getInstance().mDialog.dismiss();
        if(!mIsTotleRDing)
        {
            byte [] buf=new byte[31];
            int tempint;
            float tempfloat;
            ByteBuffer buf1;
            buf1=ByteBuffer.allocateDirect(29);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(readOutBuf1,16,29);
            buf1.rewind();
            buf1.get(buf,0,29);

            short crc= CodeFormat.crcencode(buf);
            String[] timeinfo=new String[7];
            if(crc!=0)
            {
                MainActivity.getInstance().mDialog.dismiss();
                Toast.makeText(getActivity(),"数据区CRC错误",Toast.LENGTH_SHORT).show();
                return;
            }
            for(i=0;i<timeinfo.length;i++)
            {
                String hex = Integer.toHexString(buf[i+2] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                timeinfo[i]=hex;
            }
            // Map<String,String> map=new HashMap();
            //解析时间
            String time1="20"+timeinfo[0]+timeinfo[1]+timeinfo[2]+" "
                    +timeinfo[4]+timeinfo[5]+timeinfo[6];
            Date date1=null,date2 = null;
            //   date1=datecompare(time1);


            MytabCursor cursor=new MytabCursor(	// 实例化查询
                    // 取得SQLiteDatabase对象
                    FrozendataFregment.this.helper.getReadableDatabase()) ;
            ArrayList<Map<String,String>> all=   cursor.find1(MainActivity.getInstance().mConnectedDeviceName
                    ,"DESC",1,0);
            String dbtime= all.get(0).get("time");
            //   date2=datecompare(dbtime);
            if(date2!=null&&date1!=null)
            {
                if(date2.compareTo(date1)>=0)
                {
                    // Toast.makeText(getActivity(),"该条记录已在数据库中",Toast.LENGTH_SHORT).show();
                    need2stroe=false;
                }
                else
                {
                    need2stroe=true;
                }
            }

            //解析温度
            /*
            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(buf,11,4);
            buf1.rewind();
            */
            //解析压力1
            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(buf,15,4);
            buf1.rewind();
            tempint=buf1.getInt();
            String press1;
            if(tempint==0)
            {
                press1=Constants.SENSOR_DISCONNECT;
            }
            else if(tempint==0xffffffff)
            {
                press1=Constants.SENSOR_ERROR;
            }
            else
            {
                buf1=ByteBuffer.allocateDirect(4);
                buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                buf1.put(buf,15,4);
                buf1.rewind();
                tempfloat=buf1.getFloat();
                press1=""+tempfloat;
            }
            //解析压力2
            buf1=ByteBuffer.allocateDirect(4);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(buf,19,4);
            buf1.rewind();
            tempint=buf1.getInt();
            String press2;
            if(tempint==0)
            {
                press2=Constants.SENSOR_DISCONNECT;
            }
            else if(tempint==0xffffffff)
            {
                press2=Constants.SENSOR_ERROR;
            }
            else
            {
                buf1=ByteBuffer.allocateDirect(4);
                buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                buf1.put(buf,15,4);
                buf1.rewind();
                tempfloat=buf1.getFloat();
                press2=""+tempfloat;
            }
            if(need2stroe==true)
            {
                FrozendataFregment.this.mtab = new MytabOperate(
                        FrozendataFregment.this.helper.getWritableDatabase());
                FrozendataFregment.this.mtab.insert1(MainActivity.getInstance().getmConnectedDeviceName()
                        ," ",press1,press2,time1);
            }
            //显示 mlistdata
            Map<String,String> map=new HashMap();
            String ser=MainActivity.getInstance().getmConnectedDeviceName();
            Log.d("zl","DEVICE NO:"+ser);
            //  map.put("mac",ser);
            map.put("temp","");
            map.put("press1",press1);
            map.put("press2",press2);
            map.put("time",time1);
            myadpater.notifyDataSetChanged();
        }


        //Log.d("zl","data:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
    }

    private void alldatacomtoparse(byte[] readOutBuf1) {

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
//        if(index==(mylist.length-1))
//        {
//            Dialog dialog=new AlertDialog.Builder(getActivity())
//                    .setTitle("警告！！！")
//                    .setIcon(R.drawable.warning_icon)
//                    .setMessage("全部读出历史数据需耗时约30分钟！！\r\n是否继续？")
//                    .setPositiveButton("确定", 						// 增加一个确定按钮
//                            new DialogInterface.OnClickListener() {	// 设置操作监听
//                                public void onClick(DialogInterface dialog,
//                                                    int whichButton) { 			// 单击事件
//                                 FrozendataFregment.this.dofrozendataread(0);
//                                }
//                            }).setNegativeButton("取消", 			// 增加取消按钮
//                            new DialogInterface.OnClickListener() {	// 设置操作监听
//                                public void onClick(DialogInterface dialog,
//                                                    int whichButton) { 			// 单击事件
//
//                                }
//                            }).create(); 							// 创建Dialog
//            dialog.show();
//            MainActivity.getInstance().getcurblueservice().SetBlockmode(true);
//        }
//        else
//        {
//            dofrozendataread(index+1);
//            MainActivity.getInstance().bluetoothblockdisable();
//        }
        dofrozendataread(index+1);
      //  dofrozendataread(0);
    }

    private void dofrozendataread(int i) {
        int index=0;
        byte[] adsinf0=new byte[2];//={1,3,105, (byte) 0xC7};
        mIsatart=true;
        if(i==0)
        {
            mIsTotleRDing=true;
            //MainActivity.getInstance().getcurblueservice().SetBlockmode(true);
        }
        else
        {
            mIsTotleRDing=false;
            //MainActivity.getInstance().getcurblueservice().SetBlockmode(false);
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
        buf1.get(sendbufread,16,4);

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

    @Override
    public void Ondlgcancled() {
        super.Ondlgcancled();

        String temp="cancel";
        Log.d("zl","cancel");
        String readOutMsg = DigitalTrans.byte2hex(temp.getBytes());
        verycutstatus(readOutMsg);
    }
    public Date datecompare(String d)
    {
        Date d1=null;
        SimpleDateFormat timefm= new SimpleDateFormat(Constants.DATE_FORMAT);
        try {
            d1=timefm.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1;
    }
}
