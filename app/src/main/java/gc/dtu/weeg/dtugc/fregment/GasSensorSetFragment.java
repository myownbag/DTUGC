package gc.dtu.weeg.dtugc.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import gc.dtu.weeg.dtugc.utils.SensoritemsettingActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class GasSensorSetFragment extends BaseFragment {
    View mView;

    LinearLayout mlayoutgas;
    LinearLayout mlayouttime;
    TextView mgastype;
    TextView mgasH;
    TextView mgasL;
    TextView mtime1;
    TextView mtime2;
    Button mButcommand;
    ArrayList<Map<String,String>> mdataitem;
    byte sendbufread[]={(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xDA ,0x00 ,0x0C , (byte) 0xA0};

    byte [] sendbufwrite=new byte[51];
    int m_position;

    //基础数据
    public  String gassensorinfo[][]=
            {
                    {"3","无","0"},
                    {"3","WS2100-TTL","65534"},
                    {"3","WS2100-485","65533"},
                    {"3","MIPEX-TTL","65532"},
                    {"3","MIPEX-485","65531"},
                    {"3","4-20 mA","65530"},
                    {"3","手动输入","65535"},
            };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        mIsatart=false;
        if(mView!=null)
        {
            return mView;
        }
        mView= inflater.inflate(R.layout.gas_sensor_debug_fragment_layout,null);
        mlayoutgas=mView.findViewById(R.id.sensor_press1set);
        mgastype=mView.findViewById(R.id.tv_sensor_type1);
        mgasH=mView.findViewById(R.id.tv_sensor_pressvalue1h);
        mgasL=mView.findViewById(R.id.tv_sensor_pressvalue1l);
        mtime1=mView.findViewById(R.id.tv_sensor_time1);
        mtime2=mView.findViewById(R.id.tv_sensor_time2);
        mButcommand=mView.findViewById(R.id.tv_sensor_btn_write);
        mlayouttime=mView.findViewById(R.id.sensor_timeset);

        initview();
        initdata();
        return mView;
    }

    private void initdata() {
        for(int i=0;i<(sendbufread.length-2);i++)
        {
            sendbufwrite[i]=sendbufread[i];
        }
        sendbufwrite[3]=0x1B;
        sendbufwrite[5]=0x15;
    }

    private void initview() {
        mlayoutgas.setOnClickListener(new OnclicklistenerImp());
        mlayouttime.setOnClickListener(new OnclicklistenerImp());
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
                if(mgastype.getText().length()==0)
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

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl",CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());
        if(mIsatart==false)
        {
            return;
        }
        MainActivity.getInstance().mDialog.dismiss();
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
        if(readOutBuf1.length>20)
        {
            sendbufwrite=readOutBuf1;
            sendbufwrite[5]=0x15;
            CodeFormat.crcencode(sendbufwrite);

            int temp;
            String textshow;
            String tem;

            temp=(sendbufwrite[17]&0x000000FF)*0x100+(sendbufwrite[16]&0x000000FF);
            textshow=""+temp;
            tem=findwhichvalueString("3",textshow);
//            if(tem.equals(textshow))
//            {
//                mPress1unit.setText("Kp");
//            }
//            else
//            {
//                mPress1unit.setText("");
//            }
            mgastype.setText(tem);

            ByteBuffer buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,18,4);
            buf.rewind();
            float f2=buf.getFloat();
            mgasL.setText(""+f2);



            buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
            buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
            buf.put(sendbufwrite,22,4);
            buf.rewind();
            f2=buf.getFloat();
            mgasH.setText(""+f2);

            temp=(sendbufwrite[27]&0x000000FF)*0x100+(sendbufwrite[26]&0x000000FF);
            mtime1.setText(""+temp);

            temp=(sendbufwrite[29]&0x000000FF)*0x100+(sendbufwrite[28]&0x000000FF);
            mtime2.setText(""+temp);
        }
        else
        {
            Toast.makeText(MainActivity.getInstance(),"数据设置成功",Toast.LENGTH_SHORT).show();
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
    private class OnclicklistenerImp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Intent serverIntent = new Intent(MainActivity.getInstance(), SensoritemsettingActivity.class);
            int vid= v.getId();
            switch (vid)
            {
                case R.id.sensor_press1set:
                    serverIntent.putExtra("name","报警器类型");
                    serverIntent.putExtra("position",5);
                    serverIntent.putExtra("item1",mgastype.getText().toString());
                    serverIntent.putExtra("item2",mgasH.getText().toString());
                    serverIntent.putExtra("item3",mgasL.getText().toString());
                    m_position=4;
//                   Log.d("zl","R.id.sensor_press1set:");
                    break;
                case R.id.sensor_timeset:
                    serverIntent.putExtra("name","时间");
                    serverIntent.putExtra("position",6);
                    serverIntent.putExtra("item1","");
                    serverIntent.putExtra("item2",mtime1.getText().toString());
                    serverIntent.putExtra("item3",mtime2.getText().toString());
                    m_position=5;
//                   Log.d("zl","R.id.sensor_timeset:");
                    break;
                default:
                    break;
            }
            startActivityForResult(serverIntent, Constants.SensorlsetingFlag);
        }
    }

    private String findwhichvalueString(String type, String value) {
        String show=value;
        for(int i=0;i<gassensorinfo.length;i++)
        {
            if(type.equals(gassensorinfo[i][0])&&value.equals(gassensorinfo[i][2]))
            {
                show=gassensorinfo[i][1];
                break;
            }
        }
        return show;
    }

    public void updateallsettingitems(ArrayList<Map<String,String>> arrayList)
    {
        this.mdataitem=arrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ByteBuffer buf;
        String tem;
        if(resultCode==1)
        {
            if(mdataitem!=null)
            {
                switch (m_position)
                {
                    case 4:
                        mgastype.setText(mdataitem.get(0).get("text"));
                        mgasH.setText(mdataitem.get(1).get("text"));
                        mgasL.setText(mdataitem.get(2).get("text"));
//                        mPress1unit.setText(mdataitem.get(0).get("unit"));

                        tem=mdataitem.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem));
                        buf.rewind();
                        buf.get(sendbufwrite,16,2);

                        tem=mdataitem.get(2).get("settings");   //低报警
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,18,4);

                        tem=mdataitem.get(1).get("settings");  //高报警
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(sendbufwrite,22,4);

                        String readOutMsg =  CodeFormat.byteToHex (sendbufwrite,sendbufwrite.length);
                        Log.d("zl", "the value "+tem+"is \n"+readOutMsg);
                        break;
                    case 5:
                        mtime1.setText(mdataitem.get(0).get("text"));
                        mtime2.setText(mdataitem.get(1).get("text"));

                        tem=mdataitem.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(sendbufwrite,26,2);

                        tem=mdataitem.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(sendbufwrite,28,2);
                        readOutMsg =  CodeFormat.byteToHex (sendbufwrite,sendbufwrite.length);
                        Log.d("zl", "the value "+tem+"is \n"+readOutMsg);
                        break;
                    default:
                        break;
                }
            }

        }
    }
}
