package gc.dtu.weeg.dtugc.fregment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.Semaphore;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.bluetooth.BluetoothState;
import gc.dtu.weeg.dtugc.hexfile2bin.FileBrowserActivity;
import gc.dtu.weeg.dtugc.hexfile2bin.Hex2Bin;
import gc.dtu.weeg.dtugc.myview.CustomDialog;
import gc.dtu.weeg.dtugc.myview.DeviceSelectedDlg;
import gc.dtu.weeg.dtugc.myview.Procseedlg;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.FileWriterUtils;
import gc.dtu.weeg.dtugc.utils.ToastUtils;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.HTTP;
import retrofit2.http.Path;

public class CameraTestFragment extends BaseFragment implements  EasyPermissions.PermissionCallbacks {
    private View mView=null;
    private static final int ByteSize = 512 * 1024; //读取的字节数
    private static final String TAG = "zl";
    public static final int FILE_RESULT_CODE = 1;
    private ImageView btn_open;
    private TextView changePath;
    private TextView textshow;
    private TextView filename;
    private CardView textcontainerView;
    private ImageView CameraView;

    private int curdevicetype ; //-1通信失败，1，老的ST设备，2，新的复旦微设备，-2通信正常,型号未定
//    private ViewFlipper viewFlipper;

    private Button btn_Convert;
    private String url;
    public CustomDialog mDialog1;
    private Handler mHander;
    private byte[] byte_firmware;
    private Semaphore semaphore = new Semaphore(1);
    private Semaphore semaphore2 = new Semaphore(1);

    InputStream mInStream;
    ByteArrayOutputStream mOutStream;

    private String buftextshow;

    private int updatestep=0;

    private  long checksum=0;

    private int mpackageIndex=0;
//    private int checksum=0;
    private int databytelen=0;

    private int mpackagelen=0;

    private Procseedlg mprodlg;
    private Thread cv=null;
    private Thread ErrorTimesTh=null;

    private int ErrorTimesCounter=0;

    private FileWriterUtils.writefileResult mlisterner;
    private FileWriterUtils mFileutile;

    private byte[] RepeateSendbuf;
    //Http 请求
    Callback.Cancelable httpget;
    String mfileName ;
    String mHttpupdatefile;
    int mCurDevices = -1;
    //    CountDownTimer mcountDownTimer;
    DeviceSelectedDlg  deviceSelectedDlg;
    CountDownTimer  mcountDownTimer;

    CustomDialog mCameraInfoShowDlg;

    public int FMMCU_DEVICE; // 0,未知设备 1，DTUGC 2,MSUGC  3 SGULCA  4,压力密集采集
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mView=inflater.inflate(R.layout.camera_test_fragment,null,false);

        FMMCU_DEVICE = 0;
        initView();
        initListener();
        return mView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        Intent intent = new Intent();  intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivity(intent);
        //把申请权限的回调交由EasyPermissions处理
        Log.d("zl","onRequestPermissionsResult");
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void initView() {
        mCameraInfoShowDlg = CustomDialog.createProgressDialog(getContext(), 3*60*1000, new CustomDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(CustomDialog dialog) {
                dialog.dismiss();
                ToastUtils.showToast(getContext(), "超时啦!");
            }
        });

//        mInStream = new ByteArrayInputStream(mOutStream.toByteArray());

        deviceSelectedDlg = new DeviceSelectedDlg(MainActivity.getInstance());
        deviceSelectedDlg.SetOnDevicesSelectedListerner(new OndeviceshaveSelectedp());
        btn_open =  mView.findViewById(R.id.btn_openfile);
        btn_Convert= mView.findViewById(R.id.btn_firmupdate);
        changePath =  mView.findViewById(R.id.hex2binfilepath);
        textcontainerView = mView.findViewById(R.id.textcontainer);
        filename = mView.findViewById(R.id.hex2binfilename);
//        textcontainerView.setScrollContainer(true);
//        textcontainerView.setVerticalScrollBarEnabled(true);
        int currentapiVersion=android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion>=26)
        {
            ViewGroup.LayoutParams layoutParams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
//            textshow = new TextView(MainActivity.getInstance());
            CameraView = new ImageView(MainActivity.getInstance());
            CameraView.setLayoutParams(new WindowManager
                    .LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT));

            textcontainerView.addView(CameraView);
//            textshow.setMovementMethod(ScrollingMovementMethod.getInstance());
//

//            WebView scrollView  = new WebView(MainActivity.getInstance());
//            ViewGroup.LayoutParams sclayoutParams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                    , ViewGroup.LayoutParams.MATCH_PARENT);
//            scrollView.setLayoutParams(new ViewGroup.LayoutParams(sclayoutParams));
//            scrollView.addView(textshow);
//            textcontainerView.addView(scrollView);
//            scrollView.setScrollContainer(true);
//            scrollView.setVerticalScrollBarEnabled(true);
//            viewFlipper.addView(textshow);
//            textshow.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        else
        {
            ScrollView scrollView  = new ScrollView(MainActivity.getInstance());
            ViewGroup.LayoutParams layoutParams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            scrollView.setLayoutParams(new ViewGroup.LayoutParams(layoutParams));
//            textshow = new TextView(MainActivity.getInstance());
            CameraView = new ImageView(MainActivity.getInstance());
            CameraView.setLayoutParams(new WindowManager
                    .LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT));
            scrollView.addView(CameraView);
            textcontainerView.addView(scrollView);
        }


//        textshow = mView.findViewById(R.id.firmware_show);
//        textshow.setMovementMethod(ScrollingMovementMethod.getInstance());
        mDialog1 = CustomDialog.createProgressDialog(MainActivity.getInstance(), Constants.TimeOutSecond, new CustomDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(CustomDialog dialog) {
                dialog.dismiss();
                ToastUtils.showToast(MainActivity.getInstance(), "超时啦!");
            }
        });
        mDialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("zl","Hex2BinConvertFragment dialog has been cancelde");
            }
        });
        mHander=MainActivity.getInstance().mHandler;
        mprodlg=new Procseedlg(MainActivity.getInstance());
        mprodlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("zl","setOnDismissListener");
                updatestep=-1;
            }
        });

        mprodlg.setCanceledOnTouchOutside(false);
    }

    private void initListener() {


        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openBrowser();
 //               deviceSelectedDlg.show();
//                if(checkInternet())
//                        HttpGetfile();

                DetectDeviveType();

            }
        });
        btn_Convert.setOnClickListener(new firmwareupdatbutlisterner());
    }

    private void DetectDeviveType() {
        updatestep=-1;
        byte [] sendbuf;
        int datalen=4;
        int index = 0;
        int i =0;
        sendbuf=new byte[18];
        sendbuf[index++]= (byte) 0xFD;
        sendbuf[index++]= (byte) 0x00;
        sendbuf[index++]= (byte) 0x00;
        sendbuf[index++]= 13;
        sendbuf[index++]= (byte) 0x00;
        sendbuf[index++]= (byte) 0x19;
        for(i=0;i<8;i++)
        {
            sendbuf[index++]= (byte) 0x00;
        }
        sendbuf[index++]= (byte) 103;
        sendbuf[index++]= (byte) 0x00;
        CodeFormat.crcencode(sendbuf);
        String readOutMsg = DigitalTrans.byte2hex(sendbuf);
        Log.d("zl",CodeFormat.byteToHex(sendbuf,sendbuf.length).toLowerCase());
        verycutstatus(readOutMsg,0);

        mcountDownTimer = new CountDownTimer(300,100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //  curdevicetype = 1; //复旦微
                if(curdevicetype == -1)
                {
                    if(((MainActivity)(MainActivity.getInstance())).mDialog.isShowing())
                    {
                        ((MainActivity)(MainActivity.getInstance())).mDialog.dismiss();
                    }
                    ToastUtils.showToast(MainActivity.getInstance(),"无法通信");
                }
            }
        };

        mcountDownTimer.start();
        mIsatart=true;
    }

    public Boolean checkInternet()
    {
        Boolean result=false;
        String[] perms = {Manifest.permission.INTERNET };
        if (EasyPermissions.hasPermissions(MainActivity.getInstance(), perms)) {//检查是否获取该权限
            Log.i(TAG, "已获取网络权限");
            // httpgetfile2stored();
            result = true;
            ToastUtils.showToast(MainActivity.getInstance(),"已获取网络权限");
        } else {
            //第二个参数是被拒绝后再次申请该权限的解释
            //第三个参数是请求码
            //第四个参数是要申请的权限
            ToastUtils.showToast(MainActivity.getInstance(),"无网络权限");
            EasyPermissions.requestPermissions(CameraTestFragment.this,"必要的权限", 1, perms);
            Log.i(TAG, "网络申请权限");
            result = false;
        }
        return  result;
    }
    public void HttpGetfile(int ids) {
        MainActivity.getInstance().mDialog.show();
        MainActivity.getInstance().mDialog.setDlgMsg("正在下载");
//        RequestParams params = new RequestParams(Constants.FIRM_BASEUPDATESERVICER+Constants.FIRM_UPDATESERVER_INFO);
    //变更方法
        String FIRMBASEUPDATESERVICER="";
        String FIRMUPDATESERVERINFO="";
        if(ids == 0)
        {
            FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER;
            FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_INFO;
        }
        else if(ids == 1)
        {
            FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER_MSU;
            FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_INFO_MSU;
        }
        else if(ids == 2)
        {
            FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER_SGU;
            FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_INFO_SGU;
        }
        else if(ids == 4)
        {
            FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER_P;
            FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_INFO;
        }
        else
        {

        }
        Log.d("zl","URL IN DOWNLOAD :"+FIRMBASEUPDATESERVICER+"/");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FIRMBASEUPDATESERVICER+"/") //设置网络请求的Url地址
                .build();
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        Call<ResponseBody> call = request.getCall(FIRMUPDATESERVERINFO);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    try {
                        String s = response.body().string();
                        Log.d("zl",s);
//                        MainActivity.getInstance().mDialog.setDlgMsg(s);

                        //                Log.d("zl","Http GET:"+result);
                mfileName=s;
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE };  //, Manifest.permission.CALL_PHONE
                if (EasyPermissions.hasPermissions(MainActivity.getInstance(), perms)) {//检查是否获取该权限
                    Log.i(TAG, "已获取权限");
                    httpgetfile2stored();
                } else {
                    //第二个参数是被拒绝后再次申请该权限的解释
                    //第三个参数是请求码
                    //第四个参数是要申请的权限
                    EasyPermissions.requestPermissions(CameraTestFragment.this,"必要的权限", 0, perms);
                    Log.i(TAG, "申请权限");
                }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                   if(MainActivity.getInstance().mDialog.isShowing())
                   {
                       MainActivity.getInstance().mDialog.dismiss();
                   }
                   ToastUtils.showToast(MainActivity.getInstance(),"网络访问失败");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(MainActivity.getInstance().mDialog.isShowing())
                    MainActivity.getInstance().mDialog.dismiss();
                ToastUtils.showToast(MainActivity.getInstance(),"网络访问失败");

            }
        });
//        httpget =  x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Log.d("zl","Http GET:"+result);
//                mfileName=result;
//                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE };  //, Manifest.permission.CALL_PHONE
//                if (EasyPermissions.hasPermissions(MainActivity.getInstance(), perms)) {//检查是否获取该权限
//                    Log.i(TAG, "已获取权限");
//                    httpgetfile2stored();
//                } else {
//                    //第二个参数是被拒绝后再次申请该权限的解释
//                    //第三个参数是请求码
//                    //第四个参数是要申请的权限
//                    EasyPermissions.requestPermissions(Hex2BinConvertFragment.this,"必要的权限", 0, perms);
//                    Log.i(TAG, "申请权限");
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                Log.d("zl","HTTP onError"+ex.toString());
//                HttpException httpEx = (HttpException) ex;
//                int responseCode = httpEx.getCode();
//                String responseMsg = httpEx.getMessage();
//                String errorResult = httpEx.getResult();
//
//                String result1 = ""+responseCode+","+responseMsg+","+errorResult;
//              //  MainActivity.getInstance().mDialog.setDlgMsg(result1);
//                Log.d("zl",result1);
//                ToastUtils.showToast(MainActivity.getInstance(),result1);
////                MainActivity.getInstance().mDialog.dismiss();
////                String[] perms = {Manifest.permission.INTERNET };
//               // EasyPermissions.requestPermissions(Hex2BinConvertFragment.this,"必要的权限", 1, perms);
////                ToastUtils.showToast(MainActivity.getInstance(),ex.toString());
//               // Log.i(TAG, "申请网络权限");
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

    public void HttpGetFMBinFile()
    {
        MainActivity.getInstance().mDialog.show();
        MainActivity.getInstance().mDialog.setDlgMsg("正在下载");

        String FIRMBASEUPDATESERVICER="";
        String FIRMUPDATESERVERINFO="";
        if(FMMCU_DEVICE == 0)
        {
            ToastUtils.showToast(MainActivity.getInstance(),"未知设备型号，请返厂拆机确认");
            return;
        }
        switch (FMMCU_DEVICE)
        {
            case 1:
                FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER_FMMCU_DTUGC;
                FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_FMMCU_DTUGC;
                break;
            case 2:
                FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER_FMMCU_MSUGC;
                FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_FMMCU_MSUGC;
                break;
            case 3:
                FIRMBASEUPDATESERVICER = Constants.FIRM_BASEUPDATESERVICER_FMMCU_SGULCA;
                FIRMUPDATESERVERINFO = Constants.FIRM_UPDATESERVER_FMMCU_SGULCA;
                break;
//            case 4:
//                //预留新设备
//                break;
        }


        Log.d("zl","URL IN DOWNLOAD :"+FIRMBASEUPDATESERVICER+"/");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FIRMBASEUPDATESERVICER+"/") //设置网络请求的Url地址
                .build();
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        Call<ResponseBody> call = request.getCall(FIRMUPDATESERVERINFO);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    try {
                        String s = response.body().string();
                        Log.d("zl",s);
//                        MainActivity.getInstance().mDialog.setDlgMsg(s);

                        //                Log.d("zl","Http GET:"+result);
                        mfileName=s;
                        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE };  //, Manifest.permission.CALL_PHONE
                        if (EasyPermissions.hasPermissions(MainActivity.getInstance(), perms)) {//检查是否获取该权限
                            Log.i(TAG, "已获取权限");
                            httpgetfile2stored();
                        } else {
                            //第二个参数是被拒绝后再次申请该权限的解释
                            //第三个参数是请求码
                            //第四个参数是要申请的权限
                            EasyPermissions.requestPermissions(CameraTestFragment.this,"必要的权限", 0, perms);
                            Log.i(TAG, "申请权限");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    if(MainActivity.getInstance().mDialog.isShowing())
                    {
                        MainActivity.getInstance().mDialog.dismiss();
                    }
                    ToastUtils.showToast(MainActivity.getInstance(),"网络访问失败");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(MainActivity.getInstance().mDialog.isShowing())
                    MainActivity.getInstance().mDialog.dismiss();
                ToastUtils.showToast(MainActivity.getInstance(),"网络访问失败");

            }
        });
    }
    private void openBrowser() {
        new AlertDialog.Builder(MainActivity.getInstance()).setTitle("选择存储区域").setIcon(
                R.drawable.icon_opnefile_browser).setSingleChoiceItems(
                new String[]{"内置sd卡", "外部sd卡","内部数据"}, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.getInstance(), FileBrowserActivity.class);
                        if (which == 0)
                            intent.putExtra("area", 0);
                        else if(which==1)
                            intent.putExtra("area", 1);
                        else
                            intent.putExtra("area", 2);
                        startActivityForResult(intent, FILE_RESULT_CODE);
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl","OndataCometoParse: "+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());

        if(!mIsatart)
        {
            return;
        }
        if(cv!=null)
        {
            cv.interrupt();
            cv=null;
        }
            switch (updatestep)
            {
                case -2:
                    break;
                case -1:
                    int  tempint2=0x000000ff&readOutBuf1[14];
                    if(tempint2 == 103)
                    {
                        Log.d("zl","通信正常"+readOutMsg1);
                        curdevicetype = -2;
                        mcountDownTimer.cancel();
                        mcountDownTimer = null;
                        byte [] sendbuf;
                        int datalen=4;
                        int index = 0;
                        int i =0;
                        sendbuf=new byte[18];
                        sendbuf[index++]= (byte) 0xFD;
                        sendbuf[index++]= (byte) 0x00;
                        sendbuf[index++]= (byte) 0x00;
                        sendbuf[index++]= 13;
                        sendbuf[index++]= (byte) 0x00;
                        sendbuf[index++]= (byte) 0x19;
                        for(i=0;i<8;i++)
                        {
                            sendbuf[index++]= (byte) 0x00;
                        }
                        sendbuf[index++]= (byte) 6;
                        sendbuf[index++]= (byte) 0x00;
                        CodeFormat.crcencode(sendbuf);
                        String readOutMsg = DigitalTrans.byte2hex(sendbuf);
                        Log.d("zl",CodeFormat.byteToHex(sendbuf,sendbuf.length).toLowerCase());
                        verycutstatus(readOutMsg,0);
                        mcountDownTimer = new CountDownTimer(1000,500) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                //  curdevicetype = 1; //复旦微
                                deviceSelectedDlg.show();
                                if(curdevicetype == -1)
                                {
                                    ToastUtils.showToast(MainActivity.getInstance(),"无法通信");
                                }
                                else if(curdevicetype == -2)
                                {
                                    curdevicetype = 1;
                                    if(((MainActivity)(MainActivity.getInstance())).mDialog.isShowing())
                                    {
                                        ((MainActivity)(MainActivity.getInstance())).mDialog.dismiss();
                                    }
//                                    deviceSelectedDlg.show();
//                                    String hexbintype = url;
//                                    if(curdevicetype == 1)
//                                    {
//                                        if(hexbintype.toUpperCase().indexOf("HEX")<0)
//                                        {
//                                            ToastUtils.showToast(MainActivity.getInstance(),"检测到ST芯片，必须用HEX文件升级");
//                                            return;
//                                        }
//                                    }
//                                    else if(curdevicetype == 2){
//                                        if(hexbintype.toUpperCase().indexOf("BIN")<0)
//                                        {
//                                            ToastUtils.showToast(MainActivity.getInstance(),"检测到复旦微芯片，必须用BIN文件升级");
//                                            return;
//                                        }
//                                    }else{
//                                        Log.d("zl","没有匹配到合适的型号");
//                                        return;
//                                    }
                                  //  StartUpdateFirm2Device();
                                }
                            }
                        };

                        mcountDownTimer.start();
                    }
                    else if(tempint2 == 6){
//                        String temp;
//                        //  string temp;
//                        temp="";
//                        int i=0;
//                        for(i=0;i<readOutBuf1.length-18;i++)
//                        {
//                            if(readOutBuf1[i+16]==0)
//                            {
//                                break;
//                            }
//                            temp+=(char)readOutBuf1[16+i];
//                        }
//                        Log.d("zl","当前型号:"+temp);
//                        if(temp.indexOf("FM33A0610EV")>=0)
//                        {
//                            curdevicetype = 2;
//                        }
//                        else {
//                            curdevicetype = -2;
//                        }
                        if(readOutBuf1[17] == 0x01)
                        {
                            curdevicetype = 2;
                            Log.d("zl","当前型号:"+"FM33A0610EV");

                            FMMCU_DEVICE = 0x000000ff&readOutBuf1[16];
//                            FMMCU_DEVICE = 2;
                            Log.d("zl","当前设备类型:"+FMMCU_DEVICE);
                            HttpGetFMBinFile();
                        }
                        if(readOutBuf1[17] == 0x00)
                        {
                            curdevicetype = 1;
                            FMMCU_DEVICE = 4;

                            filename.setText("DTUGC-2018 密集采集");
                            HttpGetfile(FMMCU_DEVICE);
                           // HttpGetFMBinFile();
                        }
                        else
                        {
                            curdevicetype = -2;
                        }

                        mcountDownTimer.cancel();
                        mcountDownTimer = null;
//                        String hexbintype = url;
//                        if(curdevicetype == 1)
//                        {
//                            if(hexbintype.toUpperCase().indexOf("HEX")<0)
//                            {
//                                ToastUtils.showToast(MainActivity.getInstance(),"检测到ST芯片，必须用HEX文件升级");
//                                return;
//                            }
//                        }
//                        else if(curdevicetype == 2){
//                            if(hexbintype.toUpperCase().indexOf("BIN")<0)
//                            {
//                                ToastUtils.showToast(MainActivity.getInstance(),"检测到复旦微芯片，必须用BIN文件升级");
//                                return;
//                            }
//                        }else{
//                            Log.d("zl","没有匹配到合适的型号");
//                            return;
//                        }
           //             StartUpdateFirm2Device();
                    }
                    else
                    {
                        Log.d("zl","非法指令"+readOutMsg1);
                    }
                    break;
                case 1:
                    if(MainActivity.getInstance().mDialog.isShowing() == true)
                    {
                        Log.d("zl","mDialog is cancled");
                        MainActivity.getInstance().mDialog.dismiss();
                    }
                    CountDownTimer countDownTimer = new CountDownTimer(1*60*1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        //    long timeleft = 2*60*1000-millisUntilFinished;
                            if(mCameraInfoShowDlg.isShowing() == false)
                            {
                                mCameraInfoShowDlg.show();
                            }
                            mCameraInfoShowDlg.setDlgMsg("请等待:"+millisUntilFinished/1000+"秒");
//                            Log.d("zl","wait second:"+millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            mCameraInfoShowDlg.dismiss();
                            updatestep = 2;
                            byte buf[];
                            buf = new byte[]{
                                    (byte) 0xFD,0x00 ,0x00 ,0x0D ,0x00 ,0x19 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x74 ,0x00 ,0x70 ,0x30
                            };

                            String readOutMsg = DigitalTrans.byte2hex(buf);
                            verycutstatus(readOutMsg,0);
                        }
                    };

                    countDownTimer.start();
                    break;
                case 2:
                    try {
                        mOutStream.write(readOutBuf1);
                    } catch (IOException e) {
                        ToastUtils.showToast(getContext(),e.toString());
                        e.printStackTrace();
                    }
                    int pos = readOutBuf1.length;
                    if((readOutBuf1[pos-1]&0x00ff) == 0x00D9&&(readOutBuf1[pos-2]&0x00ff)== 0x00ff)
                    {
                        MainActivity.getInstance().mDialog.dismiss();
                        Log.d("zl","detect the rail of the jpg file");
                        try {
                            mInStream = new ByteArrayInputStream(mOutStream.toByteArray());
                            mOutStream.flush();
                            Bitmap bitmap;
                            Log.d("zl","input stream lenthe is "+mInStream.available());
                            bitmap = BitmapFactory.decodeStream(mInStream);
                            CameraView.setImageBitmap(bitmap);
//                            mInStream.reset();
                            mInStream.close();
                        } catch (IOException e) {
                            ToastUtils.showToast(getContext(),e.toString());
                            e.printStackTrace();
                        }

                    }
                    break;
                case 3:
                    if(ErrorTimesTh!=null)
                    {
                        ErrorTimesTh.interrupt();
                        ErrorTimesTh=null;

                    }
                    if(readOutBuf1[0]!=0x06)
                    {
//                        mprodlg.dismiss("文件写入失败",R.drawable.update_fail);
                        ErrorTimesTh = new Thread(new ErrortiemsSupercisor() );
                        ErrorTimesTh.start();
//                        mprodlg.showresult("文件写入失败",R.drawable.update_fail,true);
                    }
                    else
                    {
//                        mprodlg.dismiss("文件写入成功",R.drawable.update_success);
                        if(mprodlg.isShowing())
                             mprodlg.show("文件写入完成");
                        ErrorTimesCounter=0;
                        updatestep=4;
                        if(mprodlg.isShowing())
                            mprodlg.setCurProcess(100);
                        ErrorTimesTh = new Thread(new ErrortiemsSupercisor(Constants.FIRMWARE_DATAFINISH_TIMEOUT) );
                        ErrorTimesTh.start();
                    }
                    break;
                case 4:
                    if(ErrorTimesTh!=null)
                    {
                        ErrorTimesTh.interrupt();
                        ErrorTimesTh=null;

                    }
                    //String readOutMsg = DigitalTrans.byte2hex(sendbuf);
                    Log.d("zl","OndataCometoParse: 结束");
                    Log.d("zl","OndataCometoParse step: "+4+"  "+readOutMsg1);
                    if(readOutMsg1.equals("0406"))
                    {
                        if(mprodlg.isShowing())
                            mprodlg.showresult("文件写入成功，\r\n请保持设备不要断电，\r\n并等待设备液晶PPPPPPP显示消失",R.drawable.update_success,true);
                    }
                    else
                    {
                        if(mprodlg.isShowing())
                            mprodlg.showresult("校验值出错",R.drawable.update_fail,true);
                    }
                    break;
                    default:
                        break;
            }
    }

    private void memcry(byte[] des, byte[] src,int offset_des, int offset, int len) {
        for(int i=0;i<len;i++)
        {
            des[i+offset_des]=src[i+offset];
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (FILE_RESULT_CODE == requestCode) {
            Bundle bundle = null;
            if (data != null && (bundle = data.getExtras()) != null) {
                String path = bundle.getString("file");
                url=path;
                Log.d(TAG, "onActivityResult: " + path);
                changePath.setText("选择路径为 : " + path);

                //判断文件类型，HXE文件需要转BIN
                String type=url.substring(url.length()-3);
                String type1 =  type.toUpperCase();
                Log.d("zl","TYPE: "+type1);
                mDialog1.show();
                if(type1.equals("HEX"))
                {
                    mDialog1.setDlgMsg("文件格式转换中...");
                    Hex2Bin hex2Bin;
                    hex2Bin=new Hex2Bin(url);
                    hex2Bin.SetOnConverterListerner(new ConvertStatusImpl());
                    hex2Bin.converhex();
                }
                else if(type1.equals("BIN"))
                {
                    mDialog1.setDlgMsg("文件加载中...");
                    readdatafromfile(url);
                }
                else
                {
                    mDialog1.dismiss();
                    ToastUtils.showToast(getActivity(), "文件类型无法识别");
                }
            }
        }
    }
    private void readdatafromfile(String arg)
    {

        Thread thread;
        thread=new Thread( new readfilesthread(arg));
        thread.start();
    }
    public void OnFileConvertResult(int code)
    {
//        Log.d("zl","OnFileConvertResult: "+code);
        if(code==Constants.FIRMWARE_CONVERT_SUCCESS)
        {
            if(buftextshow!=null)
                textshow.setText(buftextshow);
            if(mDialog1.isShowing())
            {
                mDialog1.dismiss();
            }

        }
        else if(code==Constants.FIRMWARE_CONVERT_FAIL)
        {
            mDialog1.dismiss();
            ToastUtils.showToast(getActivity(),"文件转换失败");
        }
        else if(code==Constants.FIRMWARE_CONVERT_BUSING)
        {

        }
        else if(code==Constants.FIRMWARE_DATAWRITE_TIMEOUT)
        {
            data_write_timeout();
        }
        else if(code==Constants.FIRMWARE_DATAERROR_TIMEOUT)
        {
            int temp=0;
            ErrorTimesTh=null;
            try {
                semaphore2.acquire();
                temp=ErrorTimesCounter;
                semaphore2.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(temp<3)
            {
                /*
                byte sendbuf[]=new byte[mpackagelen+4];
                ByteBuffer buf;
                buf = ByteBuffer.allocateDirect(4);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.putInt(mpackageIndex);
                buf.rewind();
                buf.get(sendbuf,0,2);
                memcry(sendbuf,byte_firmware,2,databytelen,Constants.FIRM_WRITE_FRAMELEN);
                //根据新协议添加步骤 头部
                byte sendbuf1[]=new byte[mpackagelen+5];
                sendbuf1[0]=0x02;

                memcry(sendbuf1,sendbuf,1,0,sendbuf.length);
                CodeFormat.crcencode(sendbuf1);
                verycutstatus(sendbuf1,5000);
                CodeFormat.crcencode(sendbuf1);
                Log.d("zl","新增序号 超时"+CodeFormat.byteToHex(sendbuf1,sendbuf1.length).toLowerCase());
                mprodlg.show("正在写入...");
                int process=databytelen*100/byte_firmware.length;
                mprodlg.setCurProcess(process);
                 */
                // 超时重传
                verycutstatus(RepeateSendbuf,5000);
                Log.d("zl","超时重传"+CodeFormat.byteToHex(RepeateSendbuf,RepeateSendbuf.length));
            }
            else
            {
                if(mprodlg.isShowing())
                    mprodlg.showresult("写入失败",R.drawable.update_fail,true);
                try {
                    semaphore2.acquire();
                    ErrorTimesCounter=0;
                    semaphore2.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(code==Constants.FIRMWARE_DATAFINISH_TIMEOUT)
        {
            if(mprodlg.isShowing())
                mprodlg.showresult("获取校验值超时",R.drawable.update_fail,true);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("zl","onPermissionsGranted:" + requestCode);
        Boolean ishaveperms=false;
        Boolean isInternet = false;
        if(requestCode==0)
        {
            Log.d("zl","存储权限获取");
            for(int i=0;i<perms.size();i++)
            {
                if(perms.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    ishaveperms=true;
                    break;
                }
            }
            if(!ishaveperms)
            {
                ToastUtils.showToast(MainActivity.getInstance(),"未获取权限");
                return;
            }
            httpgetfile2stored();
        }
        else if(requestCode==1)
        {
            Log.d("zl","网络权限获取");
            for(int i=0;i<perms.size();i++)
            {
                if(perms.get(i).equals(Manifest.permission.INTERNET))
                {
                    isInternet=true;
                    break;
                }
            }
            if(!isInternet)
            {
                ToastUtils.showToast(MainActivity.getInstance(),"未获取网络权限");
                return;
            }
            HttpGetfile(mCurDevices);
        }


    }


    private void httpgetfile2stored() {
        Boolean test ;
        String rootPath  = Environment.getExternalStorageDirectory()
                .toString();
        Log.d("zl","onPermissionsGranted+URL: "+rootPath);

        if (rootPath == null) {
            Toast.makeText(MainActivity.getInstance(), "无法获取存储路径！", Toast.LENGTH_SHORT).show();
        } else {
            rootPath+="/GC2018";
            final String fileroot = rootPath;
            File file = new File(rootPath);
            if(!file.exists())
            {
//                Boolean test ;
                test = file.mkdirs();
                Log.d("zl","file dir mk result ："+test);
                if(test == true)
                {
                    DownloadFileProcess(fileroot);
                }
                else
                {
                    ToastUtils.showToast(MainActivity.getInstance(),"无法创建本地缓存");
                    if(MainActivity.getInstance().mDialog.isShowing())
                    {
                        MainActivity.getInstance().mDialog.dismiss();
                    }
                }
            }
            else
            {
                DownloadFileProcess(fileroot);
            }
        }
    }

    private void DownloadFileProcess(final String fileroot) {
        if(mfileName!=null)
        {
            String Httpurl = "";
            if(mfileName.indexOf("bin")>=0 || mfileName.indexOf("BIN")>=0)
            {
                switch (FMMCU_DEVICE)
                {
                    case 1:
                        Httpurl= Constants.FIRM_BASEUPDATESERVICER_FMMCU_DTUGC;
                        break;
                    case 2:
                        Httpurl= Constants.FIRM_BASEUPDATESERVICER_FMMCU_MSUGC;
                        break;
                    case 3:
                        Httpurl= Constants.FIRM_BASEUPDATESERVICER_FMMCU_SGULCA;
                        break;
                }

            }
            else
            {
                if(mCurDevices == 0)
                {
                    Httpurl = Constants.FIRM_BASEUPDATESERVICER;
                }
                else if(mCurDevices == 1)
                {
                    Httpurl = Constants.FIRM_BASEUPDATESERVICER_MSU;
                }
                else if(mCurDevices == 2)
                {
                    Httpurl = Constants.FIRM_BASEUPDATESERVICER_SGU;
                }
                else
                {
                    //   Httpurl= Constants.FIRM_BASEUPDATESERVICER_FMMCU_DTUGC
                    if(FMMCU_DEVICE == 4)
                    {
                        Httpurl = Constants.FIRM_BASEUPDATESERVICER_P;
                    }
                }
            }

            //变更方法
            Log.d("zl","file is downloading ...");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Httpurl) //设置网络请求的Url地址
                    .build();
            GetRequest_GetFile_Interface request = retrofit.create(GetRequest_GetFile_Interface.class);

            Call<ResponseBody> call = request.getCall(mfileName);
            call.enqueue(new retrofit2.Callback<ResponseBody>(){

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {
                        Log.d("zl","文件处理:"+fileroot+"/"+mfileName);
                        mFileutile = new FileWriterUtils((Context) MainActivity.getInstance(),fileroot+"/"+mfileName,response.body());
                        mlisterner = new getfilewriteresultimpl(fileroot+"/"+mfileName);
                        mFileutile.SetOnFilewriteResult(mlisterner);
                        mFileutile.startthread();

                    }
                    else
                    {
                        if(MainActivity.getInstance().mDialog.isShowing())
                            MainActivity.getInstance().mDialog.dismiss();
                        ToastUtils.showToast(MainActivity.getInstance(), "文件下载失败");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(MainActivity.getInstance().mDialog.isShowing())
                        MainActivity.getInstance().mDialog.dismiss();
                    ToastUtils.showToast(MainActivity.getInstance(), "文件下载失败");
                }

            });

            Log.d("zl","wait for the file downloading  to finish");
                    //设置请求参数
//                    RequestParams params = new RequestParams(Httpurl);
//                    params.setAutoResume(true);//设置是否在下载是自动断点续传
//                    params.setAutoRename(false);//设置是否根据头信息自动命名文件
//                    url=rootPath+"/"+mfileName;
//                    params.setSaveFilePath(url);
//                    Log.d("zl","缓存文件路径: "+url);
//
//                    params.setExecutor(new PriorityExecutor(2, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
//                    params.setCancelFast(true);//是否可以被立即停止
//
//                    Log.d("zl","recallHttpgetrequest: 开始下载");
//
//                    Log.d("zl","recallHttpgetrequest URL:"+Constants.FIRM_BASEUPDATESERVICER+mfileName);
//                    httpget = x.http().get(params,new recallHttpgetrequest(url) );
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        for(int i=0;i<perms.size();i++)
        {
            Log.d("zl","onPermissionsDenied: "+perms.get(i));
            if(perms.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                if(MainActivity.getInstance().mDialog.isShowing())
                    MainActivity.getInstance().mDialog.dismiss();
                ToastUtils.showToast(MainActivity.getInstance(),"未获取存储权限");
            }
        }
    }

    class readfilesthread implements Runnable
    {
        String arg=null;
        byte [] filedata=new byte[ByteSize];
        int len=0;
        public readfilesthread(String url)
        {
            arg=url;
        }

        private void readfiledata()
        {
            Log.d("zl","开始读取BIN数据");
            File file = new File(arg);
            byte [] temp=new byte[512];
            InputStream in = null;
            ByteBuffer buf;
            int flag=1;
            try {
                in = new FileInputStream(file);
                while (flag!=-1)
                {
                    flag= in.read(temp);
                    if(flag>0)
                    {
                        buf=ByteBuffer.allocateDirect(flag);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.put(temp,0,flag);
                        buf.rewind();
                        buf.get(filedata,len,flag);
                        len+=flag;
                    }
                }
                FirmwareDataProsess(filedata,len);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        @Override
        public void run() {
            if(semaphore.tryAcquire()==false)
            {
                Log.d("zl","获取信号量失败 readfiledata()");
                return;
            }
            readfiledata();
            semaphore.release();
        }
    }

    private void FirmwareDataProsess(byte [] buf,int lenth) {
        //线程中在执行可以处理耗时工作，但不可以操控界面
        byte_firmware=new byte[lenth];
        ByteBuffer buf1;
        buf1=ByteBuffer.allocateDirect(buf.length);
        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
        buf1.put(buf);
        buf1.rewind();
        buf1.get(byte_firmware,0,lenth);
        buftextshow= CodeFormat.byteToHex(byte_firmware,byte_firmware.length).toUpperCase();
        //计算checksum
        checksum=0;
        for(int i=0;i<lenth;i++)
        {
            checksum+=byte_firmware[i]&0xff;
        }
        mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO, Constants.FIRMWARE_CONVERT_SUCCESS,0) //FIRMWARE_CONVERT_SUCCESS
                .sendToTarget();
        Log.d("zl","发送消息");
    }
    class ConvertStatusImpl implements Hex2Bin.OnConvertStatusListerner
    {
        // 因为在线程中调用的所以不能操控界面


        @Override
        public void OnConvertSuccess(byte[] buf, int len) {
//            byte_firmware=new byte[len];
//            ByteBuffer buf1;
//            buf1=ByteBuffer.allocateDirect(buf.length);
//            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
//            buf1.put(buf);
//            buf1.rewind();
//            buf1.get(byte_firmware,0,len);
//            buftextshow= CodeFormat.byteToHex(byte_firmware,byte_firmware.length).toUpperCase();
//            mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,Constants.FIRMWARE_CONVERT_SUCCESS,0) //FIRMWARE_CONVERT_SUCCESS
//                    .sendToTarget();

            FirmwareDataProsess(buf,len);

        }

        @Override
        public void OnConvertFailed(int code) {
//            mDialog1.dismiss();
//            ToastUtils.showToast(getActivity(), "文件转换失败");
            mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,Constants.FIRMWARE_CONVERT_FAIL,code)
                    .sendToTarget();
        }

        @Override
        public void OnBusing() {
//            mDialog1.dismiss();
//            ToastUtils.showToast(getActivity(), "文件正在转换");
            mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,Constants.FIRMWARE_CONVERT_BUSING,0)
                    .sendToTarget();
        }
    }

    private  class firmwareupdatbutlisterner implements View.OnClickListener{

        @Override
        public void onClick(View v) {
//            Toast.makeText(getActivity(),"开始转换",Toast.LENGTH_SHORT)
//                    .show();
                updatestep = 1;
            mIsatart = true;
            if(mInStream !=null)
            {

                try {
                    mInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if(mOutStream !=null){
                    mOutStream.close();
                }
                mOutStream = new ByteArrayOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte buf[] ;
                //拍照指令
                buf = new byte[]{(byte) 0xFD, 0x00, 0x00, 0x19, 0x00, 0x15, 0x00, 0x00, 0x00,
                                        0x00, 0x00, 0x00, 0x00, 0x00, 0x74, 0x00, 0x01, 0x01,
                                        0x00, 0x00, 0x00, 0x00, 0x3A, (byte) 0xD8, (byte) 0xDF,
                                        (byte) 0xDE, 0x71, 0x42, (byte) 0xC9, 0x63};
            String readOutMsg = DigitalTrans.byte2hex(buf);
        //    Log.d("zl",CodeFormat.byteToHex(sendbuf,sendbuf.length).toLowerCase());
            verycutstatus(readOutMsg,0);
        }
    }

    private void verycutstatus(String readOutMsg,int timeout) {
        MainActivity parentActivity1 = MainActivity.getInstance();
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
            ToastUtils.showToast(parentActivity1, "请先建立蓝牙连接!");
        }
    }

    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = MainActivity.getInstance();
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
            ToastUtils.showToast(parentActivity1, "请先建立蓝牙连接!");
        }
    }
    private void verycutstatus(byte [] buf,int timeout)
    {
        MainActivity parentActivity1 = MainActivity.getInstance();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
//            parentActivity1.mDialog.show();
//            parentActivity1.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(buf,0);
        }
        else
        {
            ToastUtils.showToast(parentActivity1, "请先建立蓝牙连接!");
        }
        if(timeout>0)
        {

            cv = new Thread(new timeoutSupervisor(timeout));
            cv.start();
        }
    }

   private  class timeoutSupervisor implements Runnable
   {
       int mtimeout=0;
       public timeoutSupervisor(int timeout)
       {
           mtimeout=timeout;
       }

       @Override
       public void run() {
           try {
               Thread.sleep(mtimeout);
               mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,Constants.FIRMWARE_DATAWRITE_TIMEOUT,1)
                       .sendToTarget();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   }

   private class ErrortiemsSupercisor implements Runnable
   {
       int mtype=0;
       public ErrortiemsSupercisor(int requesttype)
       {
           mtype=requesttype;
       }
       public ErrortiemsSupercisor()
       {
           mtype=Constants.FIRMWARE_DATAERROR_TIMEOUT;
       }
       @Override
       public void run() {
           try {
               Thread.sleep(6000); //增加等待时间
               if(mtype==Constants.FIRMWARE_DATAERROR_TIMEOUT)
               {
                   semaphore2.acquire();
                   ErrorTimesCounter++;
                   semaphore2.release();
               }
               mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,mtype,1)
                       .sendToTarget();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   }
   private void data_write_timeout()
    {
        if(mprodlg.isShowing())
        {
            mprodlg.showresult("写入超时"+checksum,R.drawable.update_fail,true);
            updatestep=-1;
        }
    }

    @Override
    public void Ondlgcancled() {
        super.Ondlgcancled();
        if(httpget!=null)
        {
            httpget.cancel();
            Log.d("zl","httpget cancle");
        }
    }
    class recallHttpgetrequest implements Callback.CommonCallback<File>
    {
        String murl;
        Boolean Httpresult=false;
        public  recallHttpgetrequest(String url)
        {
            murl = url;
        }

        @Override
        public void onSuccess(File result) {
            Log.d("zl","onSuccess");
            Httpresult=true;
            if(MainActivity.getInstance().mDialog.isShowing())
                MainActivity.getInstance().mDialog.dismiss();
            mDialog1.show();
            mDialog1.setDlgMsg("文件格式转换...");
            Hex2Bin hex2Bin;
            hex2Bin=new Hex2Bin(murl);
            hex2Bin.SetOnConverterListerner(new ConvertStatusImpl());
            hex2Bin.converhex();
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            Log.d("zl","onError:\n"+ex.toString());
            Httpresult=false;
            if(MainActivity.getInstance().mDialog.isShowing())
                MainActivity.getInstance().mDialog.dismiss();
            ToastUtils.showToast(MainActivity.getInstance(),"文件下载失败");
        }

        @Override
        public void onCancelled(CancelledException cex) {
            Log.d("zl","onCancelled");
            Httpresult=false;
            if(MainActivity.getInstance().mDialog.isShowing())
                MainActivity.getInstance().mDialog.dismiss();
            ToastUtils.showToast(MainActivity.getInstance(),"文件下载任务被取消");
        }

        @Override
        public void onFinished() {
            Log.d("zl","onFinished");
            if(Httpresult==false)
            {
                if(MainActivity.getInstance().mDialog.isShowing())
                    MainActivity.getInstance().mDialog.dismiss();
            }
        }
    }

    public interface GetRequest_Interface {
        /**
         * method：网络请求的方法（区分大小写）
         * path：网络请求地址路径
         * hasBody：是否有请求体
         * @param id
         */
        @HTTP(method = "GET", path = "{id}", hasBody = false)
        Call<ResponseBody> getCall(@Path("id") String id);
        // {id} 表示是一个变量
        // method 的值 retrofit 不会做处理，所以要自行保证准确
    }

    public  interface GetRequest_GetFile_Interface
    {
        @HTTP(method = "GET", path = "{id}", hasBody = false)
        Call<ResponseBody> getCall(@Path("id") String id);
    }

    private class getfilewriteresultimpl implements FileWriterUtils.writefileResult{

        String mUrl;
        getfilewriteresultimpl(String url)
        {
            mUrl = url;
            CameraTestFragment.this.url = url;
        }
        @Override
        public void OnFilewritesuccess() {
//                Log.d("zl","write success");
            if(MainActivity.getInstance().mDialog.isShowing())
                MainActivity.getInstance().mDialog.dismiss();
            if(mUrl.indexOf("bin")>=0 || mUrl.indexOf("BIN")>=0 )
            {
                readdatafromfile(mUrl);
            }else
            {
                mDialog1.show();
                mDialog1.setDlgMsg("文件格式转换...");
                Hex2Bin hex2Bin;
                hex2Bin=new Hex2Bin(mUrl);
                hex2Bin.SetOnConverterListerner(new ConvertStatusImpl());
                hex2Bin.converhex();
            }
        }
    }

    private class OndeviceshaveSelectedp implements DeviceSelectedDlg.OnSelectedOKEvent{

        @Override
        public void OnDevicesSelected(int devicesID) {
            mCurDevices = devicesID;
            if(mCurDevices == 0)
            {
                filename.setText("DTUGC-2018");
            }
            else if(mCurDevices == 1)
            {
                filename.setText("MSUGC");
            }
            else if(mCurDevices == 2)
            {
                filename.setText("SGU");
            }
            else
            {

            }
            Log.d("zl","current deviceID is :"+mCurDevices);
                            if(checkInternet())
                                        HttpGetfile(devicesID);
        }
    }
}