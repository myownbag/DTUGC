package gc.dtu.weeg.dtugc.fregment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Semaphore;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.bluetooth.BluetoothService;
import gc.dtu.weeg.dtugc.bluetooth.BluetoothState;
import gc.dtu.weeg.dtugc.hexfile2bin.FileBrowserActivity;
import gc.dtu.weeg.dtugc.hexfile2bin.Hex2Bin;
import gc.dtu.weeg.dtugc.myview.CustomDialog;
import gc.dtu.weeg.dtugc.myview.Procseedlg;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ItemSetingActivity;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class Hex2BinConvertFragment extends BaseFragment {
    private View mView=null;
    private static final int ByteSize = 200 * 1024; //读取的字节数
    private static final String TAG = "zl";
    public static final int FILE_RESULT_CODE = 1;
    private ImageView btn_open;
    private TextView changePath;
    private TextView textshow;

    private Button btn_Convert;
    private String url;
    public CustomDialog mDialog1;
    private Handler mHander;
    private byte[] byte_firmware;
    private Semaphore semaphore = new Semaphore(1);

    private String buftextshow;

    private int updatestep=0;
    private int checksum=0;
    private int databytelen=0;

    private Procseedlg mprodlg;
    private Thread cv=null;

    //    CountDownTimer mcountDownTimer;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mView=inflater.inflate(R.layout.hexfile2binfile_fragment,null,false);
        initView();
        initListener();
        return mView;
    }



    private void initView() {
        btn_open =  mView.findViewById(R.id.btn_openfile);
        btn_Convert= mView.findViewById(R.id.btn_firmupdate);
        changePath =  mView.findViewById(R.id.hex2binfilepath);
        textshow = mView.findViewById(R.id.firmware_show);
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
    }

    private void initListener() {
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser();
            }
        });
        btn_Convert.setOnClickListener(new firmwareupdatbutlisterner());
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
                case 0:
                    if(MainActivity.getInstance().mDialog.isShowing())
                    {
                        MainActivity.getInstance().mDialog.dismiss();
                    }
                    if(readOutBuf1[0]!=0x06)
                    {
                        Dialog dialog = new android.app.AlertDialog.Builder(MainActivity.getInstance()) // 实例化对象
                                .setIcon(R.drawable.warning_icon) 						// 设置显示图片
                                .setTitle("操作提示") 							// 设置显示标题
                                .setMessage("设备禁止写入!!\n请确认写入版本号是否高于设备版本号。") 				// 设置显示内容
                                .setPositiveButton("确定", 						// 增加一个确定按钮
                                        new DialogInterface.OnClickListener() {	// 设置操作监听
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) { 			// 单击事件
                                            }
                                        }).create(); 							// 创建Dialog
                        dialog.show();
                    }
                    else
                    {
                        updatestep=1;
                        int[] tempbuf=new int[2];
                        tempbuf[0]=byte_firmware.length;
                        tempbuf[1]=checksum;
                        Log.d("zl","lenth/checksum: "+tempbuf[0]+" / "+tempbuf[1]);
                        byte [] sendbuf=new byte[10];
                        ByteBuffer buf;
                        for(int i=0;i<2;i++)
                        {
                            buf=ByteBuffer.allocateDirect(4);
                            buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                            buf.putInt(tempbuf[i]);
                            buf.rewind();
                            buf.get(sendbuf,i*4,4);
                        }
                        CodeFormat.crcencode(sendbuf);
                        databytelen=0;
                        verycutstatus(sendbuf,2000);
                    }
                    break;
                case 1:

                    if(readOutBuf1[0]!=0x06)
                    {
                        mprodlg.showresult("文件属性失败",R.drawable.update_fail,true);
                    }
                    else
                    {
                        updatestep=2;
                        byte sendbuf[]=new byte[Constants.FIRM_WRITE_FRAMELEN+2];
                        memcry(sendbuf,byte_firmware,0,Constants.FIRM_WRITE_FRAMELEN);
                        CodeFormat.crcencode(sendbuf);
                        verycutstatus(sendbuf,2000);
//                        mprodlg.show();
                        mprodlg.show("正在写入...");
                        mprodlg.setCurProcess(0);
                    }
                    break;
                case 2:
                    if(readOutBuf1[0]!=0x06)
                    {
                        mprodlg.showresult("文件写入失败",R.drawable.update_fail,true);
                    }
                    else
                    {
                        byte[] sendbuf;
                        databytelen+=Constants.FIRM_WRITE_FRAMELEN;
                        if((byte_firmware.length-databytelen)>Constants.FIRM_WRITE_FRAMELEN)
                        {
                            sendbuf=new byte[Constants.FIRM_WRITE_FRAMELEN+2];
                            memcry(sendbuf,byte_firmware,databytelen,Constants.FIRM_WRITE_FRAMELEN);
                        }
                        else
                        {
                            int lenleft= byte_firmware.length-databytelen;
                            sendbuf=new byte[lenleft+2];
                            memcry(sendbuf,byte_firmware,databytelen,lenleft);
                            updatestep=3;
                        }
                        CodeFormat.crcencode(sendbuf);
                        verycutstatus(sendbuf,2000);
                        int process=databytelen*100/byte_firmware.length;
                        mprodlg.setCurProcess(process);
                    }
                    break;
                case 3:
                    if(readOutBuf1[0]!=0x06)
                    {
//                        mprodlg.dismiss("文件写入失败",R.drawable.update_fail);
                        mprodlg.showresult("文件写入失败",R.drawable.update_fail,true);
                    }
                    else
                    {
//                        mprodlg.dismiss("文件写入成功",R.drawable.update_success);
                        mprodlg.showresult("文件写入成功",R.drawable.update_success,true);
                        mprodlg.setCurProcess(100);
                    }
                    break;
                    default:
                        break;
            }
    }

    private void memcry(byte[] des, byte[] src, int offset, int len) {
        for(int i=0;i<len;i++)
        {
            des[i]=src[i+offset];
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
        if(code==Constants.FIRMWARE_CONVERT_SUCCESS)
        {
            if(mDialog1.isShowing())
            {
                if(buftextshow!=null)
                    textshow.setText(buftextshow);
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
            checksum+=byte_firmware[i];
        }
        mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO, Constants.FIRMWARE_CONVERT_SUCCESS,0) //FIRMWARE_CONVERT_SUCCESS
                .sendToTarget();
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
            if(byte_firmware==null)
            {
                ToastUtils.showToast(getActivity(),"请先载入文件");
                return;
            }
            else if(byte_firmware.length==0)
            {
                ToastUtils.showToast(getActivity(),"文件数据长度异常");
                return;
            }
            else
            {
                //让设备处于等待状态
                updatestep=-1;
                verycutstatus("18",0);
                //延时2秒
                CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        updatestep=0;
                        byte [] sendbuf;
                        int datalen=4;
                        sendbuf=new byte[datalen+18];
                        sendbuf[0]= (byte) 0xFD;
                        sendbuf[3]= (byte) ((datalen+13)%0x100);
                        sendbuf[5]=0x15;
                        sendbuf[14]= (byte) (0xff&4);
                        String ver = url.substring(url.length()-8,url.length()-4);
                        Log.d("zl",ver);
                        ByteBuffer buf1;
                        buf1=ByteBuffer.allocateDirect(4);
                        buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                        buf1.put(ver.getBytes());
                        buf1.rewind();
                        buf1.get(sendbuf,16,4);
                        CodeFormat.crcencode(sendbuf);
                        String readOutMsg = DigitalTrans.byte2hex(sendbuf);
                        Log.d("zl",CodeFormat.byteToHex(sendbuf,sendbuf.length).toLowerCase());
                        verycutstatus(readOutMsg,2000);
                    }
                };
                countDownTimer.start();
                mIsatart=true;
            }
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
   private void data_write_timeout()
    {
        if(mprodlg.isShowing())
        {
            mprodlg.showresult("写入超时",R.drawable.update_fail,true);
            updatestep=-1;
        }
    }
}
