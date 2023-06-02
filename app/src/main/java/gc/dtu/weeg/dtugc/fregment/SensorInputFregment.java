package gc.dtu.weeg.dtugc.fregment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.MyDlg;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ItemSetingActivity;
import gc.dtu.weeg.dtugc.utils.SensoritemsettingActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class SensorInputFregment extends BaseFragment {
    View mView;
    LinearLayout mlayoutpress1;
    LinearLayout mlayoutpress2;
    LinearLayout mlayouttemperature;
    LinearLayout mlayouttime;
    TextView mpressmode1;
    TextView mPress1H;
    TextView mPress1L;
    TextView mpressmode2;
    TextView mPress2H;
    TextView mPress2L;
    TextView mtempmode;
    TextView mtempIn1;
    TextView mtempIn2;
    TextView mtimemode;
    TextView mtime1;
    TextView mtime2;

    TextView mPress1unit;
    TextView mPress2unit;

    Button mButcommand;
    ArrayList<Map<String,String>> mdataitem;
    byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
                              ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0};

    byte [] sendbufwrite=new byte[51];

    int m_position;
    private SharedPreferences sp ;

    //基础数据
    public  String sensorinfo[][]=
            {
                    {"1","无","0"},
                    {"1","I2C","65534"},
                    {"1","RS485_ExSAF","65533"},
                    {"1","RS485_ANCN","65531"},
                    {"1","转换模块","65532"},
                    {"1","PCM9890B","65530"},
                    {"1","模拟量量程","65535"},


                    {"2","无","0"},
                    {"2","PT100","1"},
            };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if(mView!=null)
        {
            return mView;
        }
        mView= inflater.inflate(R.layout.sensorinputsettingslayout,null);
        mlayoutpress1=mView.findViewById(R.id.sensor_press1set);
        mlayoutpress2=mView.findViewById(R.id.sensor_press2set);
        mlayouttemperature=mView.findViewById(R.id.sensor_temperatureset);
        mlayouttime=mView.findViewById(R.id.sensor_timeset);

        mpressmode1=mView.findViewById(R.id.tv_sensor_type1);
        mpressmode2=mView.findViewById(R.id.tv_sensor_type2);
        mtempmode=mView.findViewById(R.id.tv_sensor_type3);
        mtimemode=mView.findViewById(R.id.tv_sensor_type4);

        mPress1H=mView.findViewById(R.id.tv_sensor_pressvalue1h);
        mPress1L=mView.findViewById(R.id.tv_sensor_pressvalue1l);
        mPress2H=mView.findViewById(R.id.tv_sensor_pressvalue2h);
        mPress2L=mView.findViewById(R.id.tv_sensor_pressvalue2l);
        mtempIn1=mView.findViewById(R.id.tv_sensor_temperatureh);
        mtempIn2=mView.findViewById(R.id.tv_sensor_temperaturel);
        mtime1=mView.findViewById(R.id.tv_sensor_time1);
        mtime2=mView.findViewById(R.id.tv_sensor_time2);
        mButcommand=mView.findViewById(R.id.tv_sensor_btn_write);
        mPress1unit=mView.findViewById(R.id.tv_sensor_type1_unit);
        mPress2unit=mView.findViewById(R.id.tv_sensor_type2_unit);

        initview();
        initdata();
        return mView;
    }

    private void initdata() {
       for(int i=0;i<(sendbufread.length-2);i++)
       {
           sendbufwrite[i]=sendbufread[i];
       }
        sendbufwrite[3]=0x2e;
        sendbufwrite[5]=0x15;
    }

    private void initview() {
        mlayoutpress1.setOnClickListener(new OnclicklistenerImp());
        mlayoutpress2.setOnClickListener(new OnclicklistenerImp());
        mlayouttemperature.setOnClickListener(new OnclicklistenerImp());
        mlayouttime.setOnClickListener(new OnclicklistenerImp());
//        MainActivity.getInstance().SetonPageSelectedinviewpager(new Oncurrentpageselect());
//        MainActivity.getInstance().setOndataparse(new ondataParseimp());
        mButcommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsatart=true;
                MyDlg dlg=new MyDlg(MainActivity.getInstance());
                dlg.SetOnbutclickListernerdlg(new MyDlg.Onbutclicked() {
                    @Override
                    public void Onbutclicked(int select) {
                        if(select==1) //读数据
                        {
                           Toast.makeText(MainActivity.getInstance(),"read",Toast.LENGTH_SHORT).show();
                            CodeFormat.crcencode(sendbufread);
                            String readOutMsg = DigitalTrans.byte2hex(sendbufread);
                            Log.d("zl",readOutMsg);
                            verycutstatus(readOutMsg);
                        }
                        else if(select==0)//写数据
                        {
                           Toast.makeText(MainActivity.getInstance(),"write",Toast.LENGTH_SHORT).show();
                            CodeFormat.crcencode(sendbufwrite);
                            String readOutMsg = DigitalTrans.byte2hex(sendbufwrite);
                            Log.d("zl","写:"+ CodeFormat.byteToHex(sendbufwrite,sendbufwrite.length));
                            if(checkinput())
                            {
                                verycutstatus(readOutMsg);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.getInstance(),"数据未填充完整",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
//                Log.d("zl","dlg.show()");
                dlg.show();
            }

            private Boolean checkinput() {
                Boolean temp=false;
                    if(mpressmode1.getText().length()==0)
                    {
                        return false;
                    }
                    else if(mpressmode2.getText().length()==0)
                    {
                       return false;
                    }
                    else if(mtempmode.getText().length()==0)
                    {
                        return false;
                    }
                    else if(mtime1.getText().length()==0)
                    {
                        return false;
                    }
                    else if (mtime2.getText().length()==0)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
            }
        });
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
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        if(mIsatart==false)
        {
            return;
        }
        MainActivity.getInstance().mDialog.dismiss();
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
        if(readOutBuf1.length>20)
        {
            sendbufwrite=readOutBuf1;
            sendbufwrite[5]=0x15;
            CodeFormat.crcencode(sendbufwrite);

//            ByteBuffer buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
//            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
//            buf.put(b);
//            buf.rewind();
//            float f2=buf.getFloat();
//            alldatafloat.add(new String(""+f2));
            int temp;
            String textshow;
            String tem;

            temp=(sendbufwrite[17]&0x000000FF)*0x100+(sendbufwrite[16]&0x000000FF);
            textshow=""+temp;
            tem=findwhichvalueString("1",textshow);
            if(tem.equals(textshow))
            {
                mPress1unit.setText("Kp");
            }
            else
            {
                mPress1unit.setText("");
            }
            mpressmode1.setText(tem);

            ByteBuffer buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,18,4);
            buf.rewind();
            float f2=buf.getFloat();
            mPress1H.setText(""+f2);



            buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,22,4);
            buf.rewind();
            f2=buf.getFloat();
            mPress1L.setText(""+f2);

            temp=(sendbufwrite[27]&0x000000FF)*0x100+(sendbufwrite[26]&0x000000FF);
            textshow=""+temp;
            tem=findwhichvalueString("1",textshow);
            if(tem.equals(textshow))
            {
                mPress2unit.setText("Kp");
            }
            else
            {
                mPress2unit.setText("");
            }
            mpressmode2.setText(tem);

            buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,28,4);
            buf.rewind();
            f2=buf.getFloat();
            mPress2H.setText(""+f2);

            buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,32,4);
            buf.rewind();
            f2=buf.getFloat();
            mPress2L.setText(""+f2);

            temp=(sendbufwrite[36]&0x000000FF);
            textshow=""+temp;
            tem=findwhichvalueString("2",textshow);
            mtempmode.setText(tem);

            buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,37,4);
            buf.rewind();
            f2=buf.getFloat();
            mtempIn1.setText(""+f2);

            buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,41,4);
            buf.rewind();
            f2=buf.getFloat();
            mtempIn2.setText(""+f2);

            temp=(sendbufwrite[46]&0x000000FF)*0x100+(sendbufwrite[45]&0x000000FF);
            mtime1.setText(""+temp);

            temp=(sendbufwrite[48]&0x000000FF)*0x100+(sendbufwrite[47]&0x000000FF);
            mtime2.setText(""+temp);
        }
        else
        {
            Toast.makeText(MainActivity.getInstance(),"数据设置成功",Toast.LENGTH_SHORT).show();
        }
    }

    private String findwhichvalueString(String type, String value) {
        String show=value;
        for(int i=0;i<sensorinfo.length;i++)
        {
            if(type.equals(sensorinfo[i][0])&&value.equals(sensorinfo[i][2]))
            {
                show=sensorinfo[i][1];
                break;
            }
        }
        return show;
    }

    private class OnclicklistenerImp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Intent serverIntent = new Intent(MainActivity.getInstance(), SensoritemsettingActivity.class);
           int vid= v.getId();
           switch (vid)
           {
               case R.id.sensor_press1set:
                   serverIntent.putExtra("name","第一路压力");
                   serverIntent.putExtra("position",1);
                   serverIntent.putExtra("item1",mpressmode1.getText().toString());
                   serverIntent.putExtra("item2",mPress1H.getText().toString());
                   serverIntent.putExtra("item3",mPress1L.getText().toString());
                   m_position=0;
//                   Log.d("zl","R.id.sensor_press1set:");
                   break;
               case R.id.sensor_press2set:
                   serverIntent.putExtra("name","第二路压力");
                   serverIntent.putExtra("position",2);
                   serverIntent.putExtra("item1",mpressmode2.getText().toString());
                   serverIntent.putExtra("item2",mPress2H.getText().toString());
                   serverIntent.putExtra("item3",mPress2L.getText().toString());
                   m_position=1;
//                   Log.d("zl","R.id.sensor_press2set:");
                   break;
               case R.id.sensor_temperatureset:
                   serverIntent.putExtra("name","温度");
                   serverIntent.putExtra("position",3);
                   serverIntent.putExtra("item1",mtempmode.getText().toString());
                   serverIntent.putExtra("item2",mtempIn1.getText().toString());
                   serverIntent.putExtra("item3",mtempIn2.getText().toString());
                   m_position=2;
//                   Log.d("zl","R.id.sensor_temperatureset:");
                   break;
               case R.id.sensor_timeset:
                   serverIntent.putExtra("name","时间");
                   serverIntent.putExtra("position",4);
                   serverIntent.putExtra("item1","");
                   serverIntent.putExtra("item2",mtime1.getText().toString());
                   serverIntent.putExtra("item3",mtime2.getText().toString());
                   m_position=3;
//                   Log.d("zl","R.id.sensor_timeset:");
                   break;
                   default:
                       break;
           }
            startActivityForResult(serverIntent, Constants.SensorlsetingFlag);
        }
    }

    @Override
    public void Oncurrentpageselect(int index) {
        if(index!=position)
        {
            mIsatart=false;
        }
        if(index==position)
        {
            sp = MainActivity.getInstance().getSharedPreferences("User", Context.MODE_PRIVATE);
            int inftshow=sp.getInt("info",-1);
            if(inftshow!=1)
            {
                Dialog dialog = new AlertDialog.Builder(MainActivity.getInstance()) // 实例化对象
                        .setIcon(R.drawable.i_ve_got_it) 						// 设置显示图片
                        .setTitle("操作提示") 							// 设置显示标题
                        .setMessage("单击条目可以进行设置") 				// 设置显示内容
                        .setPositiveButton("确定", 						// 增加一个确定按钮
                                new DialogInterface.OnClickListener() {	// 设置操作监听
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) { 			// 单击事件
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putInt("info",1);
                                        edit.commit();
                                    }
                                }).create(); 							// 创建Dialog
                dialog.show();
            }
//                 Toast.makeText(MainActivity.getInstance(),"单击各个条目进行设置",Toast.LENGTH_SHORT).show();

        }
    }

//    private  class Oncurrentpageselect implements MainActivity.OnPageSelectedinviewpager
//    {
//
//        @Override
//        public void currentviewpager(int position) {
//
//        }
//    }
    public void updateallsettingitems(ArrayList<Map<String,String>> arrayList)
    {
            this.mdataitem=arrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ByteBuffer buf;
        String tem;
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)
        {
            if(mdataitem!=null)
            {
//                Log.d("zl","text 0:"+mdataitem.get(0).get("text"));
//                Log.d("zl","settings 0:"+mdataitem.get(0).get("settings"));
//
//                Log.d("zl","text 1:"+mdataitem.get(1).get("text"));
//                Log.d("zl","settings 1:"+mdataitem.get(1).get("settings"));
                switch (m_position)
                {
                    case 0:
                        mpressmode1.setText(mdataitem.get(0).get("text"));
                        mPress1H.setText(mdataitem.get(1).get("text"));
                        mPress1L.setText(mdataitem.get(2).get("text"));
                        mPress1unit.setText(mdataitem.get(0).get("unit"));

                        tem=mdataitem.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem));
                        buf.rewind();
                        buf.get(sendbufwrite,16,2);

                        tem=mdataitem.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,18,4);

                        tem=mdataitem.get(2).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,22,4);

                        String readOutMsg =  CodeFormat.byteToHex (sendbufwrite,sendbufwrite.length);
                        Log.d("zl", "the value "+tem+"is \n"+readOutMsg);
                        break;
                    case 1:
                        mpressmode2.setText(mdataitem.get(0).get("text"));
                        mPress2H.setText(mdataitem.get(1).get("text"));
                        mPress2L.setText(mdataitem.get(2).get("text"));
                        mPress2unit.setText(mdataitem.get(0).get("unit"));

                        tem=mdataitem.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(sendbufwrite,26,2);

                        tem=mdataitem.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,28,4);

                        tem=mdataitem.get(2).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,32,4);
                        readOutMsg =  CodeFormat.byteToHex (sendbufwrite,sendbufwrite.length);
                        Log.d("zl", "the value "+tem+"is \n"+readOutMsg);
                        break;
                    case 2:
                        mtempmode.setText(mdataitem.get(0).get("text"));
                        mtempIn1.setText(mdataitem.get(1).get("text"));
                        mtempIn2.setText(mdataitem.get(2).get("text"));

                        tem=mdataitem.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem));
                        buf.rewind();
                        buf.get(sendbufwrite,36,1);

                        tem=mdataitem.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,37,4);

                        tem=mdataitem.get(2).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,41,4);
                        readOutMsg =  CodeFormat.byteToHex (sendbufwrite,sendbufwrite.length);
                        Log.d("zl", "the value "+tem+"is \n"+readOutMsg);
                        break;
                    case 3:
                        mtime1.setText(mdataitem.get(0).get("text"));
                        mtime2.setText(mdataitem.get(1).get("text"));

                        tem=mdataitem.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(sendbufwrite,45,2);

                        tem=mdataitem.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(sendbufwrite,47,2);
                        readOutMsg =  CodeFormat.byteToHex (sendbufwrite,sendbufwrite.length);
                        Log.d("zl", "the value "+tem+"is \n"+readOutMsg);
                        break;
                        default:
                            break;
                }
            }

        }
    }
//    private class ondataParseimp implements MainActivity.Ondataparse
//    {
//
//        @Override
//        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {
//
//        }
//    }
}
