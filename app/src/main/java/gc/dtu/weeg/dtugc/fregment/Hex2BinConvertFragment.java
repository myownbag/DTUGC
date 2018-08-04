package gc.dtu.weeg.dtugc.fregment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.Constants;
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
    }

    private void initListener() {
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser();
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
        Log.d("zl","OnFileConvertResult: "+code);
        String temp="";
        if(code==Constants.FIRMWARE_CONVERT_SUCCESS)
        {
            if(mDialog1.isShowing())
            {
                if(byte_firmware!=null)
                  temp =  CodeFormat.byteToHex(byte_firmware,byte_firmware.length).toUpperCase();
                textshow.setText(temp);
                mDialog1.dismiss();
                Log.d("zl","OnFileConvertResult: "+byte_firmware.length);
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
                byte_firmware=new byte[len];
                ByteBuffer buf1;
                buf1=ByteBuffer.allocateDirect(filedata.length);
                buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                buf1.put(filedata);
                buf1.rewind();
                buf1.get(byte_firmware,0,len);
                mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,Constants.FIRMWARE_CONVERT_SUCCESS,0) //FIRMWARE_CONVERT_SUCCESS
                        .sendToTarget();

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
    class ConvertStatusImpl implements Hex2Bin.OnConvertStatusListerner
    {
        // 因为在线程中调用的所以不能操控界面


        @Override
        public void OnConvertSuccess(byte[] buf, int len) {
            byte_firmware=new byte[len];
            ByteBuffer buf1;
            buf1=ByteBuffer.allocateDirect(buf.length);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(buf);
            buf1.rewind();
            buf1.get(byte_firmware,0,len);

            mHander.obtainMessage(BluetoothState.MESSAGE_CONVERT_INFO,Constants.FIRMWARE_CONVERT_SUCCESS,0) //FIRMWARE_CONVERT_SUCCESS
                    .sendToTarget();



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
}
