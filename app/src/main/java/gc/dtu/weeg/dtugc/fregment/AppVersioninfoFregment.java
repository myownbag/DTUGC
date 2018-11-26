package gc.dtu.weeg.dtugc.fregment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import gc.dtu.weeg.dtugc.BuildConfig;
import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class AppVersioninfoFregment extends BaseFragment implements View.OnClickListener {
    public View mView;
    public TextView mVresionCode;
    public TextView mVersionName;
    public Button mbut;
    public byte[] cmdrest={(byte)0xFD ,0x00 ,0x00 ,0x0E ,0x00 ,
            0x15 ,0x00 ,0x00 ,0x00 ,0x00 ,
            0x00 ,0x00 ,0x00 ,0x00 ,0x63 ,
            0x00 ,(byte)0xA5 ,0x52 ,(byte)0x88};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.app_version_fregment_layout, container, false);
        initView();
        return  mView;
    }

    private void initView() {
        mVresionCode= mView.findViewById(R.id.app_version_code_text);
        mVersionName= mView.findViewById(R.id.app_version_name_text);
        mbut = mView.findViewById(R.id.app_version_btn_reset);
        mbut.setOnClickListener(this);
        mVresionCode.setText(""+ BuildConfig.VERSION_CODE);
        mVersionName.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl", "APPVersion OndataCometoParse: "
                + CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
    }

    @Override
    public void onClick(View v) {


        Dialog dialog = new AlertDialog.Builder(MainActivity.getInstance()) // 实例化对象
                .setIcon(R.drawable.i_ve_got_it) 						// 设置显示图片
                .setTitle("蓝牙断开提示:") 							// 设置显示标题
                .setMessage("设备复位会导致蓝牙断开！\r\n复位后请重新进行蓝牙连接。") 				// 设置显示内容
                .setPositiveButton("确定", 						// 增加一个确定按钮
                        new DialogInterface.OnClickListener() {	// 设置操作监听
                            public void onClick(DialogInterface dialog,
                                                int whichButton) { 			// 单击事件
                                String readOutMsg = DigitalTrans.byte2hex(cmdrest);
                                verycutstatus(readOutMsg,1000);
                            }
                        }).create(); 							// 创建Dialog
        dialog.show();
    }

    private void verycutstatus(String readOutMsg,int timeout) {
        MainActivity parentActivity1 = MainActivity.getInstance();
        String strState1="无连接";
        strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.sendData(readOutMsg, "FFFF",timeout);
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }
}
