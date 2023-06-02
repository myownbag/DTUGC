package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;

import gc.dtu.weeg.dtugc.myview.LocalSetaddr219ExtraInfoView;
import gc.dtu.weeg.dtugc.myview.LocalSetaddr221ExtrainfoView;
import gc.dtu.weeg.dtugc.myview.LocalSetaddr222ExtraInfoView;
import gc.dtu.weeg.dtugc.myview.LocalSetaddr229ExtraInfoView;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.FileWriterUtils;
import gc.dtu.weeg.dtugc.utils.FileWriterUtilsV2;
import gc.dtu.weeg.dtugc.utils.ItemSetingActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;



/**
 * Created by Administrator on 2018-03-22.
 */

public class LocalsettngsFregment extends BaseFragment {
    View mView;
    LayoutInflater thisinflater;
    ViewGroup thiscontainer;
    ListView mylist;
    Button mybut;
    thislistviewadpater myadpater;
    int mIndexcmd=0;

    //把当前的模块选择项进行保存
    String mModuleType="";
    //byte[] reg221datacontent = new byte[23];
    public String[][] baseinfo=
    {
            {"100","连接设备属性","1","L",},
            {"101","设备供电方式","1","L"},
            {"103","二级地址","8","T"},
            {"110","阀门选择","10","L"},
            {"197","上传解析协议","1","L"},
            {"198","无线模块","1","L"},
            {"201","联网参数","40","E"},
            {"220","电信定制APN","40","T"},
            {"202","主站IP及端口","6","T"},
            {"205","校时IP及端口","6","T"},
            {"206","数据传输协议","1","L"},
            {"207","数据传输方式","1","L"},
            {"208","频率方式","1","L"},
            {"209","传输频率(分)","2","T"},
            {"210","数据传输固定时刻","12","T"},
            {"219","时间段传输设置","14","E"},
            {"221","月高峰采集传输","23","E"},
            {"222","拍照功能","3","E"},
            {"229","报警延时","2","E"},
    };
    public String[][] registerinfosel=
            {
//                    {"100","","-1"},
                    {"100","热量表采集","1"},
                    {"100","修正仪表采集","2"},
                    {"100","可燃气体报警器","4"},
                    {"100","压力报警器","8"},
                    {"100","燃气仪表采集","16"},

//                    {"101","","-1"},
                    {"101","外供电","0"},
                    {"101","锂电池","1"},
                    {"101","外供电+备电","16"},
                    {"101","干电池+备电","17"},
                    //阀门解析和打包需要特别注意
//                    {"110","","-1"},
                    {"110","未挂接","0"},
                    {"110","EMV DJF","1"},
                    {"110","EMV CV GC","2"},
                    {"110","EMV CV G6+","3"},
                    {"110","EMV BV","4"},
                    {"110","IC卡 控制阀","5"},
                    {"110","MYDJF","6"},
                    //无线模块
//                    {"198","","-1"},
                    {"197","维格TCP","0"},
                    {"197","维格IOT通用","1"},
                    {"197","杭州IOT通用","2"},
                    {"197","无锡华润IOT","3"},
                    {"197","常州港华IOT","4"},
                    {"197","新奥燃气IOT","5"},


                    {"198","模块关闭","0"},
                   // {"198","模块自适应","1"},
                    {"198","M72","2"},
                    {"198","MC323","3"},
                    {"198","EC20 4G","4"},
                    {"198","NB-IOT","5"},
                    {"198","5G-RM500UCN","6"},

//                    {"206","","-1"},
                    {"206","TCP","0"},
                    {"206","UDP","1"},
                    {"206","CoAP","2"},
                    {"206","Onenet","3"},

//                    {"207","","-1"},
                    {"207","主动上传","0"},
                    {"207","远程抄读","1"},
                    {"207","透明传输","2"},

//                    {"208","","-1"},
                    {"208","频率","0"},
                    {"208","每天固定时间","2"},
            };
    public String[] settingscontent=new String[baseinfo.length];
    byte [][] senddatabuf=new byte[baseinfo.length][18];

    CountDownTimer mytimer= new CountDownTimer(1000, 500) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {

            if(MainActivity.getInstance()!=null)
            {
                updatecmdlist();
                Log.d("zl","不支持的指令");
            }
        }
    };

    private void updatecmdlist() {
        mIndexcmd++;
        if (mIndexcmd == senddatabuf.length) {
            myadpater.notifyDataSetChanged();
            MainActivity.getInstance().mDialog.dismiss();

//            WriteLogFile();
        }
        else
        {
            mytimer.start();
        }
        if (mIndexcmd < senddatabuf.length) {
            String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd]);
            verycutstatus(readOutMsg, 0);
        }
    }

    private void WriteLogFile() {

        boolean  test;
        String mfileName = "/LOG.txt";
        String rootPath  = Environment.getExternalStorageDirectory()
                .toString();
        Log.d("zl","LocalsettngsFregment+URL: "+rootPath);
        if (rootPath == null) {
            Toast.makeText(MainActivity.getInstance(), "无法获取存储路径！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            rootPath+="/GC2018";
            final String fileroot = rootPath;
            File file = new File(rootPath);
            if(!file.exists())
            {
//                Boolean test ;
                test  = file.mkdirs();
//                mfileName = "/LOG.txt";
                Log.d("zl","file dir mk result ："+test);
                if(test == true)
                {
                    Log.d("zl","文件处理:"+fileroot+"/"+mfileName);
//                    mFileutile = new FileWriterUtils((Context) MainActivity.getInstance(),fileroot+"/"+mfileName,response.body());
                }
                else
                {
                    ToastUtils.showToast(MainActivity.getInstance(),"无法创建本地缓存");

                }
            }
            else
            {
//                DownloadFileProcess(fileroot);
            }
        }
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
//            BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
            FileWriterUtilsV2 mFileutile = new FileWriterUtilsV2((Context) MainActivity.getInstance(),rootPath+mfileName,process.getInputStream());
            mFileutile.SetOnFilewriteResult(new FileWriterUtilsV2.writefileResult() {
                @Override
                public void OnFilewritesuccess() {
                        Log.d("zl","LOG已经形成请导出");
                       ToastUtils.showToast(getActivity(),"LOG已经形成请导出");
                }
            });
            mFileutile.startthread();
//            StringBuilder log=new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                log.append(line);
////                FileWriterUtils mFileutile =  mFileutile = new FileWriterUtils((Context) MainActivity.getInstance(),fileroot+"/"+mfileName,response.body());
//            }
//            TextView tv = (TextView)findViewById(R.id.textView1);
//            tv.setText(log.toString());
        } catch (IOException e) {
            Log.d("zl",e.toString());
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        thisinflater =inflater;
        thiscontainer=container;
        mView = inflater.inflate(R.layout.localsetlayout, container, false);
        initView();
//        initdata();
        return  mView;
    }

    private void initView() {
        mylist= mView.findViewById(R.id.local_info_list);
        myadpater=new thislistviewadpater();
        mylist.setAdapter(myadpater);
        View view=View.inflate(MainActivity.getInstance(),R.layout.lcalseitemthead,null);
        Log.d("zl","Version"+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT>21)
            mylist.addHeaderView(view);
        mybut= mView.findViewById(R.id.btn_realtime_data);
        mybut.setOnClickListener(new butonclicklistener());
        mylist.setOnItemClickListener(new Onlistviewitemclicked());
//        reg221datacontent[0]=0;
    }
//    private void initdata() {
//        MainActivity.getInstance().setOndataparse(new DataParse());
//    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
//        Log.d("zl","in LocalsettngsFregment");
        mytimer.cancel();
        if(mIsatart==false)
        {
            return;
        }
        String temp;
        int tempint;
        int tempint2;
        int  i=0;
        int transtrit=-1;
        if(readOutBuf1.length<5)
        {
            ToastUtils.showToast(getActivity(), "数据长度短");
//                if(mIndexcmd<senddatabuf.length)
//                {
//                    String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd]);
//                    verycutstatus(readOutMsg);
//                }
            return;
        }
        else
        {
            if(readOutBuf1[3]!=(readOutBuf1.length-5))
            {
                ToastUtils.showToast(getActivity(), "数据长度异常");
//                    if(mIndexcmd<senddatabuf.length)
//                    {
//                        String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd]);
//                        verycutstatus(readOutMsg);
//                    }
                return;
            }
        }
        byte addr= readOutBuf1[14];//(byte) (Integer.valueOf(baseinfo[mIndexcmd][0])%0x100);
        if(mIndexcmd>=baseinfo.length)
        {
            Log.d("zl","接收任务完成" );
            return;
        }
        if(Integer.valueOf(baseinfo[mIndexcmd][2])==1)
        {
            for(i=0;i<registerinfosel.length;i++)
            {
                byte bytetemp= (byte) (Integer.valueOf(registerinfosel[i][0])%0x100);
                if(addr==bytetemp)
                {
                    tempint=(0x000000ff&readOutBuf1[15])*0x100+(0x000000ff&readOutBuf1[16]);
                    if(tempint==Integer.valueOf(registerinfosel[i][2]))
                    {
                        settingscontent[mIndexcmd]=registerinfosel[i][1];
                        //   myadpater.notifyDataSetChanged();
                        if(Integer.valueOf(baseinfo[mIndexcmd][0])==208)
                        {
                            transtrit=tempint;
                        }
                        else if(Integer.valueOf(baseinfo[mIndexcmd][0])==198)
                        {
                            mModuleType = settingscontent[mIndexcmd];

                            Log.d("zl"," mModuleType = settingscontent[mIndexcmd] :" + mModuleType);
                        }
                        break;
                    }
                }
            }
        }
        else if(Integer.valueOf(baseinfo[mIndexcmd][2])==10)
        {
            tempint=0x000000ff&readOutBuf1[16];
            int gatetype=0;
            if(tempint==1)
            {
                tempint2=0x000000ff&readOutBuf1[17];
                if(tempint2==2)
                {
                    gatetype=1;
                }
                else if(tempint2==1)
                {
                    gatetype=5;
                }
                else if(tempint2==0)
                {
                    tempint2=(0x000000ff&readOutBuf1[19])*0x100+(0x000000ff&readOutBuf1[18]);
                    if(tempint2==Constants.GCOPENTIME)
                    {
                        gatetype=2;
                    }
                    if(tempint2==Constants.G6OPENTIME)
                    {
                        gatetype=3;
                    }
                }
            }
            else if(tempint==0)
            {
                gatetype=0;
            }
            else if(tempint==2)
            {
                gatetype=4;
            }
            else if(tempint == 3)
            {
                gatetype=6;
            }
            else
            {
                gatetype=-1;
            }
            for(i=0;i<registerinfosel.length;i++)
            {

                if(0x6E==Integer.valueOf(registerinfosel[i][0])%0x100) //0x64=110
                {
                    if(gatetype==Integer.valueOf(registerinfosel[i][2]))
                    {
                        settingscontent[mIndexcmd]=registerinfosel[i][1];
                        // myadpater.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
        else
        {
            tempint2=0x000000ff&readOutBuf1[14];
            if(tempint2==202||tempint2==205) //解析IP
            {
                temp=String.format("%d.%d.%d.%d,",0x000000ff&readOutBuf1[16],0x000000ff&readOutBuf1[17]
                        ,0x000000ff&readOutBuf1[18],0x000000ff&readOutBuf1[19]);
                tempint=(0x000000ff&readOutBuf1[20])+(0x000000ff&readOutBuf1[21])*0x100;
                temp=temp+tempint;
                settingscontent[mIndexcmd]=temp;
                //myadpater.notifyDataSetChanged();
            }
            else if(tempint2==209)
            {
                tempint=(0x000000ff&readOutBuf1[16])+(0x000000ff&readOutBuf1[17])*0x100;
                temp=""+tempint;
                settingscontent[mIndexcmd]=temp;
            }
            else if(tempint2==210)
            {
                String daytime="";
                String lockdaytime1=ArrayFormatCString(0x000000ff&readOutBuf1[16],0x000000ff&readOutBuf1[17]
                        ,0x000000ff&readOutBuf1[18],transtrit);
                if(lockdaytime1.equals("")==false)
                {
                    daytime+=lockdaytime1+";";
                }
                String lockdaytime2=ArrayFormatCString(0x000000ff&readOutBuf1[19],0x000000ff&readOutBuf1[20],
                        0x000000ff&readOutBuf1[21],transtrit);
                if (lockdaytime2.equals("")==false)
                {
                    daytime+=lockdaytime2+";";
                }
                String lockdaytime3=ArrayFormatCString(0x000000ff&readOutBuf1[22],0x000000ff&readOutBuf1[23],
                        0x000000ff&readOutBuf1[24],transtrit);
                if (lockdaytime3.equals("")==false)
                {
                    daytime+=lockdaytime3+";";
                }
                String lockdaytime4=ArrayFormatCString(0x000000ff&readOutBuf1[25],0x000000ff&readOutBuf1[26],
                        0x000000ff&readOutBuf1[27],transtrit);
                if (lockdaytime4.equals("")==false)
                {
                    daytime+=lockdaytime4+";";
                }
                settingscontent[mIndexcmd]=daytime;
            }
            else if(tempint2==219)
            {
                //解析219
                byte[] data2convert = new byte[14];
                ByteBuffer buf = ByteBuffer.allocate(14);
                buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(readOutBuf1,16,14)  ;
                buf.rewind();
                buf.get(data2convert);
                settingscontent[mIndexcmd]=LocalSetaddr219ExtraInfoView.Hexinfo2Str(data2convert);
            }
            else if(tempint2==221)
            {
                //解析221
                byte[] reg221datacontent = new byte[23];
                ByteBuffer buf = ByteBuffer.allocate(23);
                buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(readOutBuf1,16,23)  ;
                buf.rewind();
                buf.get(reg221datacontent);
                settingscontent[mIndexcmd]= LocalSetaddr221ExtrainfoView.dacodetoStr(reg221datacontent);
            }
            else if(tempint2 == 222)
            {
                byte[] reg222datacontent = new byte[3];
                ByteBuffer buf = ByteBuffer.allocate(3);
                buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(readOutBuf1,16,3)  ;
                buf.rewind();
                buf.get(reg222datacontent);
                settingscontent[mIndexcmd]= LocalSetaddr222ExtraInfoView.dacodetoStr(reg222datacontent);
            }
            else if(tempint2 == 229)
            {
                byte[] reg229datacontent = new byte[2];
                ByteBuffer buf = ByteBuffer.allocate(2);
                buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(readOutBuf1,16,2)  ;
                buf.rewind();
                buf.get(reg229datacontent);
                settingscontent[mIndexcmd]= LocalSetaddr229ExtraInfoView.dacodetoStr(reg229datacontent);
            }
            else
            {
//                if(tempint2 == 220)
//                {
//                    Log.d("zl","12345678");
//                }

                temp="";
                for(i=0;i<readOutBuf1.length-18;i++)
                {
                    if(readOutBuf1[i+16]==0)
                    {
                        break;
                    }
                    temp+=(char)readOutBuf1[16+i];
                }
                settingscontent[mIndexcmd]=temp;
            }
        }
        updatecmdlist();
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
            mIsatart=true;
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
                String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd]);
                verycutstatus(readOutMsg);
                mytimer.start();
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
    private class Onlistviewitemclicked implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String registername;
            String registerconnet;
            String registersetting;
            String registerlen;
            registername=((TextView)view.findViewById(R.id.register_item)).getText().toString();
            registersetting=((TextView)view.findViewById(R.id.registerinfo_item)).getText().toString();
            registerconnet= ((TextView)view.findViewById(R.id.registerset_item)).getText().toString();   //registerlen_item
            registerlen=((TextView)view.findViewById(R.id.registerlen_item)).getText().toString();
            Intent serverIntent = new Intent(MainActivity.getInstance(), ItemSetingActivity.class);
            serverIntent.putExtra("addrs",registername);
            serverIntent.putExtra("name",registersetting);
            serverIntent.putExtra("settings",registerconnet);
            serverIntent.putExtra("datalen",registerlen);
            mModuleType=settingscontent[5];
            Log.d("zl"," mModuleType=settingscontent[4] :" +mModuleType);
            serverIntent.putExtra("addr198setting",mModuleType);
            serverIntent.putExtra("220addrset",settingscontent[7]);
//            if(registername.equals("221"))
//            {
//                serverIntent.putExtra("221receivebytes",reg221datacontent);
//            }
            if(registername.equals("220"))
            {
                ToastUtils.showToast(MainActivity.getInstance(),"寄存器220必须和201关联设置");
                return;
            }
            else if(registername.equals("201"))
            {
                if(mModuleType==null)
                {
                    ToastUtils.showToast(MainActivity.getInstance(),"请先设置好寄存器198:通信模块");
                    return;
                }
                else if(mModuleType.equals(""))
                {
                    ToastUtils.showToast(MainActivity.getInstance(),"请先设置好寄存器198:通信模块");
                    return;
                }
            }
            startActivityForResult(serverIntent, Constants.LocalsetingFlag);
           // Log.d("zl","position:"+position+"id:"+id);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        MainActivity.getInstance().setOndataparse(new DataParse());
        Log.d("zl","requestCode:"+requestCode+" "+"resultCode:"+resultCode);
        if(resultCode==1)
        {
            Toast.makeText(MainActivity.getInstance(),"参数设置成功",Toast.LENGTH_LONG).show();
        }
        else
        {
            return;
        }
        if(data!=null)
        {

            String temp=data.getStringExtra("name");
            int index=  data.getIntExtra("addrs",-1);
            if(index==6)
            {
                if(temp!=null)
                    settingscontent[index]=temp;
                String temp1= data.getStringExtra("name1");
                if(temp1!=null)
                    settingscontent[index+1]=temp1;
//                Log.d("zl","localreagment : 5-6"+settingscontent[5]+"  "+settingscontent[6]);
            }
            if(index>=0&&temp!=null)
            {
                settingscontent[index]=temp;
            }
            myadpater.notifyDataSetChanged();
        }
    }
//    private class DataParse implements MainActivity.Ondataparse
//    {
//
//        @Override
//        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1)
//        {
//
//        }
//    }

    private String ArrayFormatCString(int week, int hour, int minute, int transtrit1) {

        String temp="";
        if(hour==0xff||minute==0xff)
            return "";
        String strweek = "";
        String strhourminute;
        switch (week)
        {
            case 1:
                strweek="星期一";
                break;
            case 2:
                strweek="星期二";
                break;
            case 3:
                strweek="星期三";
                break;
            case 4:
                strweek="星期四";
                break;
            case 5:
                strweek="星期五";
                break;
            case 6:
                strweek="星期六";
                break;
            case 7:
                strweek="星期日";
                break;
        }
        //strhourminute.Format("%.2d:%.2d",hour,minute);
        strhourminute=String.format("%02d:%02d",hour,minute);
        if (transtrit1==0x01)
            strweek=strweek+","+strhourminute;
        else
            strweek=strhourminute;
        return strweek;
    }
}
