package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.CustomDialog;
import gc.dtu.weeg.dtugc.myview.LocalSetaddr201ExtrainfoView;
import gc.dtu.weeg.dtugc.myview.LocalSetaddr219ExtraInfoView;
import gc.dtu.weeg.dtugc.myview.LocalSetaddr221ExtrainfoView;


/**
 * Created by Administrator on 2018-03-27.
 */

public class ItemSetingActivity extends Activity {
    EditText currentshow;
    KeyListener keyListener;
    Button   mybutton;
    Intent intent;
    TextView mtextaddr;
    TextView mtextaddrname;
    Spinner spinner;
    ImageView backbut;
    LinearLayout ExtraSetView;
    MainActivity mainActivity;
    private List<String> data_list;
    private int [] currsetvaluesettings;
    private int  currsetvalue;
    boolean isSpinnerFirst = true ;
    RelativeLayout spinerconter;
    int mposition=0;
    int spinerposition=0;
    int datalen=0;
    public CustomDialog mDialog;
    String setcontent;
    String mMdoulset="";//为联网参数保存输入数据
    byte[] addr220setcmd = new byte[40+18];//保存220的设置指令与201相互关联
    String str220setting;
    String str201setting;
    boolean str220enable = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curentsettinglayout);
        String temp;
        currentshow=findViewById(R.id.currentset_item_addrsettings);
        keyListener=currentshow.getKeyListener();
        mybutton =findViewById(R.id.tv_itemsettings_btn_write);
        mtextaddr=findViewById(R.id.currentset_item_addr);
        mtextaddrname=findViewById(R.id.currentset_item_addrname);
        mybutton.setOnClickListener(new buttonclickimp());
        ExtraSetView = findViewById(R.id.local_extraitem_set);
        intent=getIntent();
        temp=intent.getStringExtra("addrs");
        mtextaddr.setText(temp);
        temp=intent.getStringExtra("name");
        mtextaddrname.setText(temp);
        setcontent=intent.getStringExtra("settings");

        mMdoulset = intent.getStringExtra("addr198setting");
        str220setting = intent.getStringExtra("220addrset");
        if(temp!=null)
        {
            currentshow.setText(setcontent);
        }

        //serverIntent.putExtra("datalen",registerlen);
        temp=intent.getStringExtra("datalen");
        datalen=Integer.valueOf(temp);
        spinner=findViewById(R.id.currentset_item_addrspiner);
        spinerconter=findViewById(R.id.selectitemspiner);
        mainActivity=MainActivity.getInstance();
        backbut=findViewById(R.id.imgBackItemset);
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemSetingActivity.this.finish();
            }
        });
        initview();
    }


    private void initview() {
        int i;
        int j;
        mDialog = CustomDialog.createProgressDialog(this, Constants.TimeOutSecond, new CustomDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(CustomDialog dialog) {
                dialog.dismiss();
                ToastUtils.showToast(getBaseContext(), "超时啦!");
            }
        });
        String temp=mtextaddr.getText().toString();
//        Log.d("zl","mainActivity.setOndataparse(new datacometoparse()) has been executed");
        mainActivity.setOndataparse(new datacometoparse());
        for( i=0;i<mainActivity.fregment4.baseinfo.length;i++)
        {
            if(temp.equals(mainActivity.fregment4.baseinfo[i][0]))
            {
                mposition=i;
                if("L".equals(mainActivity.fregment4.baseinfo[i][3]))
                {
                    spinerconter.setVisibility(View.VISIBLE);
                    currentshow.setFocusable(false);
                    currentshow.setFocusableInTouchMode(false);
                    data_list= new ArrayList<>();
                    currsetvaluesettings=new int[30];
                    for(j=0;j<mainActivity.fregment4.registerinfosel.length;j++)
                    {
                        //int L_index=0;
                        if(temp.equals(mainActivity.fregment4.registerinfosel[j][0]))
                        {
                            data_list.add(mainActivity.fregment4.registerinfosel[j][1]);
                            currsetvaluesettings[data_list.size()-1]= Integer.valueOf(mainActivity.fregment4.registerinfosel[j][2]);
                        }
                    }
                }
                else if("T".equals(mainActivity.fregment4.baseinfo[i][3]))
                {
                    spinerconter.setVisibility(View.GONE);
                }
                else if("E".equals(mainActivity.fregment4.baseinfo[i][3]))
                {
                    String tempaddr= mainActivity.fregment4.baseinfo[i][0];
                    spinerconter.setVisibility(View.GONE);
                    currentshow.setFocusable(false);
                    currentshow.setFocusableInTouchMode(false);
                    if(tempaddr.equals("201"))
                    {
                        LocalSetaddr201ExtrainfoView view;
                        if(setcontent==null)
                        {
                            setcontent="";
                        }
                        if(str220setting==null)
                        {
                            str201setting="";
                        }
                        view = new LocalSetaddr201ExtrainfoView(this,setcontent,str220setting,mMdoulset);
                        ExtraSetView.addView(view);
                        view.setOncursettingChanged(new OnExtrasettingchange());
                        view.updatecurrentshow();
                    }
                    else if (tempaddr.equals("219"))
                    {
                        LocalSetaddr219ExtraInfoView view;
                        view =new LocalSetaddr219ExtraInfoView(this,setcontent);
                        ExtraSetView.addView(view);
                        view.setOncursettingChanged(new OnExteasettingchange219());
                    }
                    else if(tempaddr.equals("221"))
                    {
                        LocalSetaddr221ExtrainfoView view;
                        byte[] setbytes = intent.getByteArrayExtra("221receivebytes");
                        view =new LocalSetaddr221ExtrainfoView(this,setbytes);
                        RelativeLayout relativeLayout = findViewById(R.id.item_activity_setting_content);
                        relativeLayout.setVisibility(View.GONE);
                        ExtraSetView.addView( view );
//                        ExtraSetView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
//                                ,CodeFormat.dip2px(this,400)));
                    }
                }
                else
                {
                    Toast.makeText(this,"未知类型",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        if(spinerconter.getVisibility()==View.VISIBLE)
        {
            //适配器
            ArrayAdapter<String> arr_adapter;
            arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(arr_adapter);
//            spinner.setSelection(-1,true);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentshow.setText(data_list.get(position));
                    spinerposition=position;
                    currsetvalue=currsetvaluesettings[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            if(setcontent.length()!=0)
            {
                for(int dataindex=0;dataindex<data_list.size();dataindex++)
                {
                    if(setcontent.equals(data_list.get(dataindex)))
                    {
                        spinner.setSelection(dataindex,true);
                        currentshow.setText(data_list.get(dataindex));
                        break;
                    }
                }
            }
            else
            {
                currentshow.setText(data_list.get(0));
            }
        }
    }

    private class buttonclickimp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
           String temp=currentshow.getText().toString();
           String addrtemp=mtextaddr.getText().toString();
            int i;
            byte [] sendbuf={(byte)0xFD,0x00,0x00,0x0E,0x00,0x15,0x00,0x00,0x00,0x00,
                    0x00,0x00,0x00,0x00,0x64,0x00,0x02,(byte)0xA2,(byte) 0xF3};
           int transmit=0;
           if(addrtemp.equals("221"))
           {
               //LocalSetaddr221ExtrainfoView.
               //221解析有子View完成，这里给temp赋值，目的是为了能跳过 if(temp.length()==0)
               if(temp.length()==0)
                    temp="数据需要解析";
           }
            if(temp.length()==0)
            {
                Toast.makeText(ItemSetingActivity.this,"请输入填入的内容",Toast.LENGTH_LONG).show();
                return;
            }
            if(Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][2])==1
                    ||Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][2])==10)
             {

                 sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);
                 sendbuf[16]= (byte) (currsetvalue%0x100);

                 if(sendbuf[14]==(byte)0xD0) //0xD0=208
                 {
                     transmit=currsetvalue;
                 }
                 if(sendbuf[14]==(byte)0x6E) //6E 110
                 {
                     sendbuf=new byte[datalen+18];
                     sendbuf[0]= (byte) 0xFD;
                     sendbuf[3]= (byte) ((datalen+13)%0x100);
                     sendbuf[5]=0x15;
                     sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);
                     switch(currsetvalue)
                     {
                         case 1:
                             sendbuf[16]=0x01;
                             sendbuf[17]=0x02; //湖州金辰截止阀
                             sendbuf[18]=0x30;
                             sendbuf[19]=0x75;
                             sendbuf[20]= (byte) 0xe8;
                             sendbuf[21]=0x03;
                             sendbuf[22]=0x00;
                             sendbuf[23]=0x00;
                             sendbuf[24]=0x00;
                             sendbuf[25]=0x00;
                             break;
                         case 2:
                             sendbuf[16]=0x01;
                             sendbuf[17]=0x00; //GC
                             sendbuf[18]=(byte)(Constants.GCOPENTIME%0x100);
                             sendbuf[19]=(byte)(Constants.GCOPENTIME/0x100);
                             sendbuf[20]= (byte)(Constants.GCCLOSETIME%0x100);
                             sendbuf[21]=(byte)(Constants.GCCLOSETIME/0x100);
                             sendbuf[22]=0x00;
                             sendbuf[23]=0x00;
                             sendbuf[24]=0x00;
                             sendbuf[25]=0x00;
                             break;
                         case 3:
                             sendbuf[16]=0x01;
                             sendbuf[17]=0x00; //G6
                             sendbuf[18]=(byte)(Constants.G6OPENTIME%0x100);
                             sendbuf[19]=(byte)(Constants.G6OPENTIME/0x100);
                             sendbuf[20]= (byte)(Constants.G6CLOSETIME%0x100);
                             sendbuf[21]=(byte)(Constants.G6CLOSETIME/0x100);
                             sendbuf[22]=0x00;
                             sendbuf[23]=0x00;
                             sendbuf[24]=0x00;
                             sendbuf[25]=0x00;
                             break;
                         case 4:
                             sendbuf[16]=0x02;
                             sendbuf[17]=0x01; //球阀
                             sendbuf[18]= (byte) 0xFF;
                             sendbuf[19]= (byte) 0xFF;
                             sendbuf[20]= (byte) 0xFF;
                             sendbuf[21]= (byte) 0xFF;
                             sendbuf[22]= (byte) 0x96;
                             sendbuf[23]=0x00;
                             sendbuf[24]= (byte) 0x96;
                             sendbuf[25]=0x00;
                             break;
                         case 5:
                             sendbuf[16]=0x01;
                             sendbuf[17]=0x01; //IC 卡控制阀
                             sendbuf[18]=0x30;
                             sendbuf[19]=0x75;
                             sendbuf[20]= (byte) 0xe8;
                             sendbuf[21]=0x03;
                             sendbuf[22]=0x00;
                             sendbuf[23]=0x00;
                             sendbuf[24]=0x00;
                             sendbuf[25]=0x00;
                             break;
                     }

                 }
                 CodeFormat.crcencode(sendbuf);
             }
             else if(addrtemp.equals("202") ||addrtemp.equals("205"))
            {
                int jsearch=0;
                int kjudge=0;
                byte mytestbyte[]=temp.getBytes();
                sendbuf=new byte[datalen+18];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((datalen+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);
                String [] ipanport=new String[5];
                ipanport[0]="";
                for(int ichar=0;ichar<mytestbyte.length;ichar++)
                {
                    if(mytestbyte[ichar]>=0x30&&mytestbyte[ichar]<=0x39)
                    {
                        if(kjudge==0)
                        {
                            ipanport[jsearch]="";
                            kjudge++;
                        }
                        ipanport[jsearch]+=(char)mytestbyte[ichar];

                    }
                    else
                    {
                        if(kjudge!=0)
                        {
                            jsearch++;
                        }
                        kjudge=0;
                        if(jsearch>=5)
                        {
                            break;
                        }
                    }
                }
                for(jsearch=0;jsearch<4;jsearch++)
                {
                    if(ipanport[jsearch]==null)
                    {
                        Toast.makeText(ItemSetingActivity.this,"输入IP或端口格式有错误",Toast.LENGTH_LONG).show();
                        return;
                    }
                    sendbuf[16+jsearch]=(byte) (Integer.valueOf(ipanport[jsearch])%0x100);
                }
                if(ipanport[4]==null)
                {
                    Toast.makeText(ItemSetingActivity.this,"输入IP或端口格式有错误",Toast.LENGTH_LONG).show();
                    return;
                }
                int ipport=Integer.valueOf(ipanport[4]);
                sendbuf[20]=(byte) (ipport%0x100);
                sendbuf[21]= (byte)(ipport/0x100);
                CodeFormat.crcencode(sendbuf);
            }
            else if(addrtemp.equals("209"))
            {
                sendbuf=new byte[datalen+18];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((datalen+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);
                 int spacetime = Integer.valueOf(temp);
                if (spacetime<1||spacetime>10000)
                {
                    //AfxMessageBox("209 数据传输频率设置错误！",MB_OK|MB_ICONERROR);
                    Toast.makeText(ItemSetingActivity.this,"209 数据传输频率设置错误！",Toast.LENGTH_SHORT).show();
                    return ;
                }
//                              memcpy(valuetmp,&spacetime,2);
//                              memcpy(writecmd.cmdbuf+sizeof(PROTOCOL_PACKAGE_HEADINFO),valuetmp,reglen);
                sendbuf[16]= (byte) (spacetime%0x100);
                sendbuf[17]= (byte) (spacetime/0x100);
                CodeFormat.crcencode(sendbuf);
            }
            else if(addrtemp.equals("210"))
            {
                sendbuf=new byte[datalen+18];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((datalen+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);
                //int spacetime = Integer.valueOf(temp);
                for( i=0;i<datalen;i++)
                {
                    sendbuf[16+i]=(byte)0xFF;
                }
                for(int d=0;d<4;d++)
                {
                    byte [] daytime={(byte) 0xff,(byte)0xff,(byte)0xff};
                    int l=temp.indexOf(";");
                    if(l==-1)
                          break;
                    CStringFormatArray(temp.substring(0,l),daytime,transmit);
//                    Log.d("zl","daytime:"+daytime[0]+"-"+daytime[1]+"-"+daytime[2]);
                    if (transmit==0x01&&daytime[0]==0xff)
                    {
                        //AfxMessageBox("210—数据传输频率与设置不匹配<星期一,12:30>！");
                        //return FALSE;
                        Toast.makeText(ItemSetingActivity.this,"210—数据传输频率与设置不匹配<星期一,12:30>！",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    if(daytime[1]>24||daytime[2]>60)
                    {
//                                      AfxMessageBox("210—时间格式错误！");
//                                      return FALSE;
                        Toast.makeText(ItemSetingActivity.this,"210—时间格式错误！",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    for( i=0;i<3;i++)
                    {
                        sendbuf[16+d*3+i]=daytime[i];
                    }
                    temp=temp.substring(l+1,temp.length());
                }
                CodeFormat.crcencode(sendbuf);
//                Log.d("zl","addrtemp.equals 210:"+CodeFormat.byteToHex(sendbuf,sendbuf.length));
            }
            else if(addrtemp.equals("219"))
            {
                boolean result;
                byte crusetbyte[] = new byte[14]; // =temp.getBytes();
                result=LocalSetaddr219ExtraInfoView.parsetimestr(temp,crusetbyte);
                if(result==false)
                {
                    ToastUtils.showToast(ItemSetingActivity.this,"参数设置错误");
                    return;
                }
                sendbuf=new byte[datalen+18];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((datalen+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);

                if(crusetbyte.length>datalen)
                {
                    Toast.makeText(ItemSetingActivity.this,"输入字节超出长度",Toast.LENGTH_SHORT).show();
                    return;
                }
                for( i=0;i<datalen;i++)
                {
                    if(i<crusetbyte.length)
                    {
                        sendbuf[16+i]=crusetbyte[i];
                    }
                    else
                        sendbuf[16+i]=(byte)0x00;
                }
                CodeFormat.crcencode(sendbuf);
            }
            else if(addrtemp.equals("221"))
            {
                byte[] settings;
                LocalSetaddr221ExtrainfoView View221= (LocalSetaddr221ExtrainfoView) ExtraSetView.getChildAt(0);
                settings=View221.dacodeshowinfo();
                if(settings == null)
                {
                    return;
                }
                sendbuf=new byte[datalen+18];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((datalen+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);

                if(settings.length>datalen)
                {
                    Toast.makeText(ItemSetingActivity.this,"输入字节超出长度",Toast.LENGTH_SHORT).show();
                    return;
                }
                for( i=0;i<datalen;i++)
                {
                    if(i<settings.length)
                    {
                        sendbuf[16+i]=settings[i];
                    }
                    else
                        sendbuf[16+i]=(byte)0x00;
                }
                CodeFormat.crcencode(sendbuf);
//                Log.d("zl","ddatalen:"+datalen);
//                Log.d("zl","cmd:"+CodeFormat.byteToHex(sendbuf,sendbuf.length));
                if(sendbuf!=null)
                    currentshow.setText(LocalSetaddr221ExtrainfoView.dacodetoStr(sendbuf));
            }
            else
            {
                int index201=0;
      //          int strlen=0;
                String Setstr201="";
                byte crusetbyte[]=temp.getBytes();
//                strlen=findstrlen(crusetbyte);
//                Log.d("zl","len:"+strlen);
                if(addrtemp.equals("201"))
                {
//                    Log.d("zl","IiemSetting: str220setting"+str220setting);
                    str220enable=true;
                    addr220setcmd[0]= (byte) 0xFD;
                    addr220setcmd[3]= (byte) ((datalen+13)%0x100);
                    addr220setcmd[5]=0x15;
                    addr220setcmd[14]= (byte) (220);
                    if(crusetbyte.length<datalen)
                    {
                        for( i=0;i<datalen;i++)
                        {
                            addr220setcmd[16+i]=(byte)0x00;
                        }
                        str220setting="";
                        str201setting=temp;
                    }
                    else
                    {
                        index201=temp.indexOf(",");
                        Setstr201=temp.substring(0,index201);
                        str220setting= new String(Setstr201);
                        crusetbyte=Setstr201.getBytes();
                        for( i=0;i<datalen;i++)
                        {
                            if(i<crusetbyte.length)
                            {
                                addr220setcmd[16+i]=crusetbyte[i];
                            }
                            else
                                addr220setcmd[16+i]=(byte)0x00;
                        }
                        Setstr201=temp.substring(index201,temp.length());
                        crusetbyte=Setstr201.getBytes();
                        str201setting=Setstr201;
                    }
//                    Log.d("zl","IiemSetting: str220setting"+str220setting);
//                    Log.d("zl","IiemSetting: str201setting"+str201setting);
                    CodeFormat.crcencode(addr220setcmd);
                }
                sendbuf=new byte[datalen+18];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((datalen+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(mainActivity.fregment4.baseinfo[mposition][0])%0x100);

                if(crusetbyte.length>datalen)
                {
                    Toast.makeText(ItemSetingActivity.this,"输入字节超出长度",Toast.LENGTH_SHORT).show();
                    return;
                }
                for( i=0;i<datalen;i++)
                {
                    if(i<crusetbyte.length)
                    {
                        sendbuf[16+i]=crusetbyte[i];
                    }
                        else
                          sendbuf[16+i]=(byte)0x00;
                }
                CodeFormat.crcencode(sendbuf);
            }
            String readOutMsg = DigitalTrans.byte2hex(sendbuf);
            verycutstatus(readOutMsg);
            //                        Log.d("zl","temp:"+temp+" "+"position:"+mposition);
//                        intent.putExtra("name",temp);
//                        intent.putExtra("addrs",mposition);
//                        ItemSetingActivity.this.setResult(spinerposition,intent);
        }
    }

    private int findstrlen(byte[] p0) {
        int len=0;
        for(len=0;len<p0.length;len++)
        {
            if(p0[len]==0)
            {
                break;
            }
        }
        return len;
    }

    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = ItemSetingActivity.this.mainActivity;
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            ItemSetingActivity.this.mDialog.show();
            ItemSetingActivity.this.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(ItemSetingActivity.this, "请先建立蓝牙连接!");
        }
    }

    private void CStringFormatArray(String regstrbuf, byte [] daytime, int transmit) {
//        Log.d("zl","CStringFormatArray regstrbuf:"+regstrbuf);
        int l=regstrbuf.indexOf(",");
        if (l!=-1&&transmit==1)
        {
            String strweek=regstrbuf.substring(0,l);
            if (strweek.equals("星期一"))
            {
                daytime[0]=0x01;
            }
            if (strweek.equals("星期二"))
            {
                daytime[0]=0x02;
            }
            if (strweek.equals("星期三"))
            {
                daytime[0]=0x03;
            }
            if (strweek.equals("星期四"))
            {
                daytime[0]=0x04;
            }
            if (strweek.equals("星期五"))
            {
                daytime[0]=0x05;
            }
            if (strweek.equals("星期六"))
            {
                daytime[0]=0x06;
            }
            if (strweek.equals("星期日"))
            {
                daytime[0]=0x07;
            }
            regstrbuf=regstrbuf.substring(l+1,regstrbuf.length());
        }
        l=regstrbuf.indexOf(":");
        byte [] byteteturn=new byte[2];
        String temp1=regstrbuf.substring(0,l);

        daytime[1]= (byte) (Integer.valueOf(temp1)%0x100);
       // Log.d("zl","CStringFormatArray daytime[1]:"+daytime[1]);
        temp1=regstrbuf.substring(l+1,regstrbuf.length());
        daytime[2]= (byte) (Integer.valueOf(temp1)%0x100);
       // Log.d("zl","CStringFormatArray daytime[2]:"+daytime[2]);
    }

    private class MyEditTextChangeListener implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d("zl","before:"+currentshow.getText());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("zl","after:"+currentshow.getText());
        }
    }
   private class datacometoparse  implements MainActivity.Ondataparse
   {

       @Override
       public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {
           ItemSetingActivity.this.mDialog.dismiss();
           String temp=currentshow.getText().toString();
           //currentset_item_addr
           String addrname = mtextaddr.getText().toString() ;
           if(addrname.equals("201"))
           {
               if(str220enable)
               {
                   str220enable=false;
                   String readOutMsg = DigitalTrans.byte2hex(addr220setcmd);
                   verycutstatus(readOutMsg);
                   return;
               }
               else
               {
                   intent.putExtra("name",str201setting);
                   intent.putExtra("name1",str220setting);
                   intent.putExtra("addrs",mposition);
               }
           }
           else
           {
               intent.putExtra("name",temp);
               intent.putExtra("addrs",mposition);
           }

           ItemSetingActivity.this.setResult(1,intent);
           ItemSetingActivity.this.finish();
       }
   }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivity.setOndataparse(null);
    }

    class OnExtrasettingchange implements LocalSetaddr201ExtrainfoView.SettingInterface
    {

        @Override
        public void OncurSetting(String set) {
            currentshow.setText(set);
        }
    }
    class OnExteasettingchange219 implements LocalSetaddr219ExtraInfoView.SettingInterface
    {
        @Override
        public void OncurSetting(String set, byte[] setbyte) {
            currentshow.setText(set);
        }
    }
}
