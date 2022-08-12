package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.databean.Ask;
import gc.dtu.weeg.dtugc.databean.Take;
import gc.dtu.weeg.dtugc.databean.Take2;
import gc.dtu.weeg.dtugc.databean.Take3;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.NbServiceAddrInputActivity;
import gc.dtu.weeg.dtugc.utils.SoftKeyBoardListener;
//import okhttp3.ResponseBody;
import gc.dtu.weeg.dtugc.utils.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class ICCardSettingFragment extends BaseFragment {
    View mView;

    ListView MeterDataInfoShowList;
    LinearLayout mConnter;
    TextView MeterDataInfoShowText;
    Spinner  MeterTypeSelectView;
    Spinner  MeterOwnerSelectView;
    EditText MeterSnInputView;
    Button SettingICCardBtn;
    Button ReadICCardBtn;
    LinearLayout ICsettingCantainer;
    LinearLayout ButtonContainer;
    LinearLayout mVCMDView;
    //cdm_view_layout

    TextView mCmdRequest;
    TextView mCmdRespone;
    int Metertype;
    ArrayList<Map<String,String>> MeterListContentData;
    MeterListViewAdapter MeterListContentDataAdapter;
    public ImageView mImageView;
//    private SharedPreferences sp ;
    private String addrurl;
    public TextView maddrview;
    String[] MeterTypeSelectContentData = {"莱德","普瑞米特","莱德物联网表"};
    String[] MeterOwnerSelectContentData = {"秦华燃气","长安燃气"};
    int[]  MeterTypeSelectContentDataSnLength = {12,14,14};

    byte[] setbufhead = {(byte)0xFD ,0x00 ,0x00 ,0x6A ,0x00 ,0x15 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x7A,0x03,0x01 ,0x30 ,0x35,0x30
                              ,0x31 ,0x30 ,0x37 ,0x34 ,0x32 ,0x34 ,0x35 ,0x33 ,0x38 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00};
    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {

        Log.d("zl","读表指令返回:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());
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
//        MainActivity.getInstance().mDialog.setDlgMsg("正在读取");
//        MainActivity.getInstance().mDialog.dismiss();
        mIsatart = false;
        Ask ask = new Ask();
        ask.icType= String.format("%d",MeterTypeSelectView.getSelectedItemPosition()+1);
        ask.icSerial = MeterSnInputView.getText().toString();
        ask.cmdType = "RM";
//        ByteBuffer buf2 ;
        int len = readOutBuf1.length-16-2;
        String  test = "";
        if(len < 9 )
        {
            test = "FFFF";
            ToastUtils.showToast(MainActivity.getInstance(),"接收数据长度出错 长度="+len);
            MainActivity.getInstance().mDialog.dismiss();
            MeterDataInfoShowText.setText("接收数据长度出错 长度="+len);
            return;
        }
        else
        {
            if(Metertype == 1) //莱德
            {
                for(int i=0;i<len;i++)
                {
                    test += (char)(readOutBuf1[16+i]);
                }
            }
            else if(Metertype == 2) //普瑞米特
            {
                byte[] buftest =new byte[len];
                for(int i=0;i<len;i++)
                {
                    buftest[i] = readOutBuf1[16+i];
                }
                test =  DigitalTrans.byte2hex(buftest);
            }
            else if(Metertype == 3)
            {
                byte[] buftest =new byte[len];
                for(int i=0;i<len;i++)
                {
                    buftest[i] = readOutBuf1[16+i];
                }
                test =  DigitalTrans.byte2hex(buftest);
            }
        }
        mCmdRespone.setText(test);
        ask.data = new Ask.DataDTO();
//        ask.data.icResponse = "";
        ask.data.icResponse = test;
//        Ask.DataDTO.icResponse = test;
//        ask.data.icResponse = test;
//        ask.data.icResponse="6823B9B6B896ADBBADBBADBBADBBADBBADBBADBBA69FA996A97063FF66FF66FE5516";
        Log.d("zl","cmdrespone: "+ask.data.icResponse );

        if(ask.icSerial.length()!= MeterTypeSelectContentDataSnLength[MeterTypeSelectView.getSelectedItemPosition()])
        {
            ToastUtils.showToast(MainActivity.getInstance(),"输入序列号长度异常");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(addrurl) //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Api1  request = retrofit.create(Api1.class);
        final Api2  request1 = retrofit.create(Api2.class);


        if(MainActivity.getInstance().mDialog.isShowing()==false)
        {
            MainActivity.getInstance().mDialog.show();
        }
        MainActivity.getInstance().mDialog.setDlgMsg("请求服务器解析返回");

        if(Metertype ==-1)  // 3
        {
            Call<Take3> call = request1.request(ask);
            call.enqueue(new Callback<Take3>() {
                @Override
                public void onResponse(Call<Take3> call, Response<Take3> response) {
                    if(response.isSuccessful())
                    {
                        MainActivity.getInstance().mDialog.dismiss();

                        Take3 t = response.body();
                        String res = "csq="+ response.body().data.analysisResult.csq+" curprice="+response.body().data.analysisResult.curprice
                                 +" gleft="+response.body().data.analysisResult.gleft + " switchstate="+ response.body().data.analysisResult.switchstate
                                +" gsum="+response.body().data.analysisResult.gsum;
                        Log.d("zl","服务器解析返回："+res);
                        MeterDataInfoShowText.setText(""+res);

                    }
                    else
                    {
                        ToastUtils.showToast(MainActivity.getInstance(),"服务器解析失败");
                        MainActivity.getInstance().mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Take3> call, Throwable t) {
                    ToastUtils.showToast(MainActivity.getInstance(),"连接服务器失败");
                    MainActivity.getInstance().mDialog.dismiss();
                }
            });
        }
        else
        {
            Call<Take2> call = request.request(ask);

            call.enqueue(new Callback<Take2>() {
                @Override
                public void onResponse(Call<Take2> call, Response<Take2> response) {
                    if(response.isSuccessful())
                    {
                        MainActivity.getInstance().mDialog.dismiss();

                        Take2 t = response.body();
                        String res =  response.body().data.analysisResult;
                        Log.d("zl","服务器解析返回："+res);
                        MeterDataInfoShowText.setText(""+res);

                    }
                    else
                    {
                        ToastUtils.showToast(MainActivity.getInstance(),"服务器解析失败");
                        MainActivity.getInstance().mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Take2> call, Throwable t) {
                    ToastUtils.showToast(MainActivity.getInstance(),"连接服务器失败");
                    MainActivity.getInstance().mDialog.dismiss();
                }
            });
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.icard_seting_fragment_layout, container, false);
        initdata();
        initview();
        return mView;
    }

    private void initdata() {
//        sp=MainActivity.getInstance().getSharedPreferences("User", Context.MODE_PRIVATE);
        MeterListContentData = new ArrayList<>();
//        Map<String,String> map = new HashMap<>();
//        map.put("Meterinfo","仪表信息");
//        map.put("Metercontent","数据内容");
//        MeterListContentData.add(map);
        MeterListContentDataAdapter = new MeterListViewAdapter();
        Metertype = 0;
    }

    private void initview() {

        mConnter = mView.findViewById(R.id.iccard_meter_data_respone_conter);
        MeterDataInfoShowText = mView.findViewById(R.id.iccard_meter_data_show_text);
        MeterDataInfoShowList = mView.findViewById(R.id.iccard_meter_data_show_list);
        MeterTypeSelectView  = mView.findViewById(R.id.iccard_type_setting);
        MeterOwnerSelectView = mView.findViewById(R.id.iccard_spiner_owner);
        MeterSnInputView = mView.findViewById(R.id.iccard_sn_input);
        SettingICCardBtn = mView.findViewById(R.id.iccard_btn_setting);
        ReadICCardBtn = mView.findViewById(R.id.iccard_btn_reading);
        MeterDataInfoShowList.setAdapter(MeterListContentDataAdapter);
        mImageView = mView.findViewById(R.id.nb_img_set_addr);
        maddrview=mView.findViewById(R.id.nb_add_info);
        ICsettingCantainer = mView.findViewById(R.id.iccard_setting_container);
        ButtonContainer =mView.findViewById(R.id.button_container);
        mVCMDView = mView.findViewById(R.id.cdm_view_layout);

        mCmdRequest = mView.findViewById(R.id.iccard_meter_cmd_request);
        mCmdRespone = mView.findViewById(R.id.iccard_meter_cmd_respone);

//        addrurl=sp.getString(Constants.ICCARD_SERVICE_KEY,"http://58.216.223.222:7988/");
        addrurl = "http://58.216.223.222:7988/";
        maddrview.setText(addrurl);

        setSpinneradpater(MeterTypeSelectView,MeterTypeSelectContentData);
        setSpinneradpater(MeterOwnerSelectView,MeterOwnerSelectContentData);

        SettingICCardBtn.setOnClickListener(new OnButtonClickedListerner());
        ReadICCardBtn.setOnClickListener(new OnButtonClickedListerner());
        mImageView.setVisibility(View.INVISIBLE);
//        mImageView.setOnClickListener(new OnButtonClickedListerner());
//        MeterSnInputView.setOnClickListener(new OnButtonClickedListerner());

        View headview = View.inflate(MainActivity.getInstance(),R.layout.iccard_meter_info_item_show,null);
        TextView keyheadview = headview.findViewById(R.id.iccard_meter_data_item_key);
        TextView valueheadview = headview.findViewById(R.id.iccard_meter_data_item_value);
        keyheadview.setText("仪表信息");
        valueheadview.setText("数据内容");
        MeterDataInfoShowList.addHeaderView(headview);


        SoftKeyBoardListener.setListener(MainActivity.getInstance(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height, int visiblehight) {
//                mConnter.setVisibility(View.INVISIBLE);
//                ButtonCantainer.setVisibility(View.GONE);
//                MeterSnInputView.setFocusable(true);
//                MeterSnInputView.setVisibility(View.VISIBLE);
//
//                MeterSnInputView.invalidate();
//                ICsettingCantainer.setWeightSum(10);
                ButtonContainer.setVisibility(View.INVISIBLE);
                mVCMDView.setVisibility(View.GONE);
            }

            @Override
            public void keyBoardHide(int height, int visiblehight) {
//                mConnter.setVisibility(View.VISIBLE);
//                ButtonCantainer.setVisibility(View.VISIBLE);
//                MeterSnInputView.setFocusable(false);
//                ICsettingCantainer.setWeightSum(3);
                ButtonContainer.setVisibility(View.VISIBLE);
                mVCMDView.setVisibility(View.VISIBLE);
            }
        });

        MeterOwnerSelectView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("zl","iccard select position " +position);
                switch (position)
                {
                    case 0:
                        addrurl = "http://58.216.223.222:7988/";
                        maddrview.setText("http://58.216.223.222:7988/");
                        break;
                    case 1:
                        addrurl = "http://58.216.223.222:7080/";
                        maddrview.setText("http://58.216.223.222:7080/");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    public class MeterTypeSettingAdapter extends

    public class MeterListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return MeterListContentData.size();
        }

        @Override
        public Object getItem(int position) {
            return MeterListContentData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = View.inflate(MainActivity.getInstance(),R.layout.iccard_meter_info_item_show,null);
            TextView MeterKeyView = convertView.findViewById(R.id.iccard_meter_data_item_key);
            TextView MeterValueView = convertView.findViewById(R.id.iccard_meter_data_item_value);

            MeterKeyView.setText(MeterListContentData.get(position).get("Meterinfo"));
            MeterValueView.setText(MeterListContentData.get(position).get("Metercontent"));
            return convertView;
        }
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
    public class OnButtonClickedListerner implements View.OnClickListener{
        int id;
        @Override
        public void onClick(View v) {
            id = v.getId();
            mIsatart = true;
            switch (id)
            {
                case R.id.iccard_btn_setting:
                    Log.d("zl","ICCard RequestURL:"+addrurl);
                    if(addrurl.indexOf("http://")!=0)
                    {
                        ToastUtils.showToast(MainActivity.getInstance(),"服务器未正确设置");
                        return;
                    }
                    Metertype = MeterTypeSelectView.getSelectedItemPosition()+1;
                    MeterDataInfoShowText.setText("");
                    mCmdRequest.setText("");
                    mCmdRespone.setText("");
                    onCmdRequest();

                    break;
                case R.id.iccard_btn_reading:
//                    String  temp= "00000000000000000000000000000000683000012421999000810050C3968CA58FAB50C801D6999E2B2D280229463CD4D4E2D2A1B51DED2941C40065A8665F75C0291807FF004C5E4872701EDD8B6B605F61B8AB590634459F44A488F90FDC526EDBD3DE1098DF624F1186378C160000";

//                      String temp = "00000000000000000000000000000000683000012421999000810050C3968CA58FAB50C801D6999E2B2D280229463CD4D4E2D2A1B51DED2941C40065A8665F75C0291807FF004C5E4872701EDD8B6B605F61B8AB590634459F44A488F90FDC526EDBD3DE1098DF624F1186378C160000";
//                    byte[] buff = DigitalTrans.hex2byte(temp);
//                    buff[3] = (byte) (buff.length-5);
//                    Metertype = 3;
//                    OndataCometoParse(temp,buff);

                    break;
                case R.id.nb_img_set_addr:
                    Intent intent;
                    intent=new Intent(MainActivity.getInstance(), NbServiceAddrInputActivity.class);
                    intent.putExtra("requestpage","ICCARD");
                    startActivityForResult(intent, Constants.NBINPUTSETTINGFLAG);
                    break;

                default:
                    break;
            }
        }
    }

    private void onCmdRequest() {
        Ask ask = new Ask();
        ask.icType= String.format("%d",MeterTypeSelectView.getSelectedItemPosition()+1);
        ask.icSerial = MeterSnInputView.getText().toString();
        Log.d("zl","请求序列号:"+ ask.icSerial);
//        ask.data.icResponse ="";
        if(ask.icSerial.length()!= MeterTypeSelectContentDataSnLength[MeterTypeSelectView.getSelectedItemPosition()])
        {
            ToastUtils.showToast(MainActivity.getInstance(),"输入序列号长度异常");
            return;
        }
        MainActivity.getInstance().mDialog.show();
        MainActivity.getInstance().mDialog.setDlgMsg("正在请求服务器");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(addrurl) //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Api request = retrofit.create(Api.class);

        ask.cmdType = "RM";
      //  String test = ask.data.toString();
        Call<Take> call = request.request(ask);

        call.enqueue(new Callback<Take>() {
            @Override
            public void onResponse(Call<Take> call, Response<Take> response) {
                if (response.isSuccessful())
                {

                    String test =  response.body().data.icCmd;
                    if(test!=null)
                    {

                        Log.d("zl","获取到服务器指令："+test);
                        mCmdRequest.setText(test);
                        byte[] buff = new byte[0];
                        if(Metertype == 1)  //莱德
                        {
                            buff = test.getBytes();
                        }
                        else if(Metertype == 2) // 普瑞米特
                        {
                            buff = DigitalTrans.hex2byte(test);
                        }
                        else if(Metertype == 3)
                        {
                            buff = DigitalTrans.hex2byte(test);
                        }
                        Log.d("zl","转换byte:"+CodeFormat.byteToHex(buff,buff.length).toUpperCase());
//                        ByteBuffer buf1;
//                        buf1=ByteBuffer.allocateDirect(18);
//                        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
//                        buf1.put(temp);
//                        buf1.rewind();
//                        buf1.get(bufofreadcmd[i]);
//                        mCmdRequest.setText(CodeFormat.byteToHex(buff,buff.length).toUpperCase());


                        ByteBuffer buf1;
                        byte[] sendbuf = new byte[setbufhead.length+buff.length+2];


                        buf1 = ByteBuffer.allocateDirect(setbufhead.length);
                        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                        buf1.put(setbufhead);
                        buf1.rewind();
                        buf1.get(sendbuf,0,setbufhead.length);

                        buf1 = ByteBuffer.allocateDirect(buff.length);
                        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                        buf1.put(buff);
                        buf1.rewind();
                        buf1.get(sendbuf,setbufhead.length,buff.length);


                        buf1 = ByteBuffer.allocateDirect(2);
                        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                        buf1.putShort((short)(buff.length+25+13));
                        buf1.rewind();
                        buf1.get(sendbuf,3,2);

                        sendbuf[16] = (byte) ((MeterTypeSelectView.getSelectedItemPosition()+1)&0xff);

                        buf1 = ByteBuffer.allocateDirect(MeterSnInputView.getText().toString().length());
                        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                        buf1.put(MeterSnInputView.getText().toString().getBytes());
                        buf1.rewind();
                        buf1.get(sendbuf,17,MeterSnInputView.getText().toString().getBytes().length);
                        CodeFormat.crcencode(sendbuf);
                        String readOutMsg = DigitalTrans.byte2hex(sendbuf);
                        Log.d("zl","打包设置指令:"+CodeFormat.byteToHex(sendbuf,sendbuf.length).toUpperCase());


                        verycutstatus(readOutMsg,90000);

                    }
                    else
                    {
                        MainActivity.getInstance().mDialog.dismiss();
                        MainActivity.getInstance().mDialog.setDlgMsg("正在读取");
                        ToastUtils.showToast(MainActivity.getInstance(),"指令没有获取");
                    }
                }
            }

            @Override
            public void onFailure(Call<Take> call, Throwable t) {
                ToastUtils.showToast(MainActivity.getInstance(),"访问服务器失败，请联系管理员");
                MainActivity.getInstance().mDialog.dismiss();
            }
        });

    }

    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("正在通过蓝牙设置命令...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
            parentActivity1.mDialog.dismiss();
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
            parentActivity1.mDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        addrurl=sp.getString(Constants.ICCARD_SERVICE_KEY,"");
        Log.d("zl","onActivityResult: "+addrurl);
        maddrview.setText(addrurl);
    }

    public interface Api {
        @POST("icverifycmdresponse?")
        Call<Take> request(@Body Ask ask);
    }

    public interface Api1 {
        @POST("icverifycmdresponse")
        Call<Take2> request(@Body Ask ask);
    }

    public interface Api2 {
        @POST("icverifycmdresponse")
        Call<Take3> request(@Body Ask ask);
    }
}
