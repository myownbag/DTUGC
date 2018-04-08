package gc.dtu.weeg.dtugc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gc.dtu.weeg.dtugc.bluetooth.BluetoothService;
import gc.dtu.weeg.dtugc.bluetooth.BluetoothState;
import gc.dtu.weeg.dtugc.bluetooth.DeviceListActivity;
import gc.dtu.weeg.dtugc.fregment.BaseFragment;
import gc.dtu.weeg.dtugc.fregment.BasicinfoFregment;
import gc.dtu.weeg.dtugc.fregment.CNKFixedPagerAdapter;
import gc.dtu.weeg.dtugc.fregment.FrozendataFregment;
import gc.dtu.weeg.dtugc.fregment.InstrumentInputFregment;
import gc.dtu.weeg.dtugc.fregment.LocalsettngsFregment;
import gc.dtu.weeg.dtugc.fregment.RealtimedataFregment;
import gc.dtu.weeg.dtugc.fregment.SensorInputFregment;
import gc.dtu.weeg.dtugc.myview.CustomDialog;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.DigitalTrans;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

import static gc.dtu.weeg.dtugc.bluetooth.BluetoothState.REQUEST_CONNECT_DEVICE;
import static gc.dtu.weeg.dtugc.bluetooth.BluetoothState.REQUEST_ENABLE_BT;

public class MainActivity extends FragmentActivity {
    //蓝牙扫描
    RelativeLayout rllBtScan;
    private TextView mTxtStatus;

    LayoutInflater mLayoutInflater;
    BaseFragment mCurrentpage;
    ViewPager info_viewpager;
    private CNKFixedPagerAdapter mPagerAdater;
    /**
     * 当前选择的分类
     */
    private int mCurClassIndex=0;
    /**
     * 选择的分类字体颜色
     */
    private int mColorSelected;
    /**
     * 非选择的分类字体颜色
     */
    private int mColorUnSelected;
    /**

     /**
     * 水平滚动的Tab容器
     */
    private HorizontalScrollView mScrollBar;
    /**
     * 分类导航的容器
     */
    private ViewGroup mClassContainer;
    int mScrollX = 0;
    private List<BaseFragment> fragments;
    private String[] titles=new String[]{"基本信息","实时数据", "历史数据","本机设置", "传感器设置", "仪表接入"};

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mBTService = null;

    String gOwner = "";
    static MainActivity instanceMainActivity = null;

    private long exitTime = 0;
    public CustomDialog mDialog;

    //各个子页面
    public BasicinfoFregment fregment1;
    public RealtimedataFregment fregment2;
    public FrozendataFregment   fregment3;
    public LocalsettngsFregment fregment4;
    public SensorInputFregment  fregment5;
    public InstrumentInputFregment fregment6;
    //接口
    Ondataparse mydataparse=null;
//    OnPageSelectedinviewpager myOnPageSelectedinviewpager=null;
    public static MainActivity getInstance() {
        return instanceMainActivity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instanceMainActivity = this;
        mydataparse=null;
        InitView();
        InitFrgment();
        InitBlueTooth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            //打开蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            if (mBTService == null) {
                // Initialize the BluetoothService to perform bluetooth
                // connections
                mBTService = new BluetoothService(this, mHandler);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mBTService.getState() == BluetoothState.STATE_NONE) {
                // Start the Bluetooth services
                mBTService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mBTService != null)
            mBTService.stop();
    }
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case BluetoothState.MESSAGE_STATE_CHANGE:
                    // if (D)
                    //    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1)
                    {
                        case BluetoothState.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to,
                            //		mConnectedDeviceName));
                            mTxtStatus.setText("已连接到:" + mConnectedDeviceName);

                            // mConversationArrayAdapter.clear();

                            break;
                        case BluetoothState.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            mTxtStatus.setText(R.string.title_connecting);
                            break;
                        case BluetoothState.STATE_LISTEN:
                        case BluetoothState.STATE_NONE:
                         //   Log.d("zl","BluetoothState_state:"+"STATE_NONE/STATE_LISTEN");
                            //setStatus(R.string.title_not_connected);
                            mTxtStatus.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case BluetoothState.MESSAGE_WRITE:
                    // byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    // String writeMessage = new String(writeBuf);
                    // mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case BluetoothState.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = "";
                    for(int i = 0; i < msg.arg1; i++)
                    {
                        //readMessage += readBuf[i];

                        String hex = Integer.toHexString(readBuf[i] & 0xFF);
                        if (hex.length() == 1) {
                            hex = '0' + hex;
                        }

                        readMessage += hex;
                    }

                    byte[] readOutBuf = DigitalTrans.hex2byte(readMessage);
                    String readOutMsg = DigitalTrans.byte2hex(readOutBuf);

                    //获取接收的返回数据
                    Log.v("ttt", "recv:" + readOutMsg);

                    if(mydataparse!=null)
                    {
                        mydataparse.datacometoparse(readOutMsg,readOutBuf);
                    }
                    else
                    {
                        mCurrentpage.OndataCometoParse(readOutMsg,readOutBuf);
                    }
                    mDialog.dismiss();
                    break;
                case BluetoothState.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BluetoothState.DEVICE_NAME);
                    ToastUtils.showToast(getApplicationContext(), "连接到" + mConnectedDeviceName);

                    break;
                case BluetoothState.MESSAGE_TOAST:

                    ToastUtils.showToast(getApplicationContext(),
                            msg.getData().getString(BluetoothState.TOAST));
                    break;
            }
        }
    };
    private void InitFrgment() {

        //添加Tab标签
        int index=0;
        addScrollView(titles);
        mScrollBar.post(new Runnable() {
            @Override
            public void run() {
                mScrollBar.scrollTo(mScrollX,0);
            }
        });

        fragments=new ArrayList<BaseFragment>();

        fregment1 = new BasicinfoFregment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("extra",titles[index++]);
        //BasicinfoFregment.setArguments(bundle1);
        fragments.add(fregment1);

        fregment2 =new RealtimedataFregment();
        Bundle bundle11 = new Bundle();
        bundle1.putString("extra",titles[index++]);
        //oneFragment.setArguments(bundle11);
        fragments.add(fregment2);

        fregment3 = new FrozendataFregment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("extra",titles[index++]);
        //secondFragment.setArguments(bundle2);
        fragments.add(fregment3);

        fregment4 = new LocalsettngsFregment();
        Bundle bundle3 = new Bundle();
        bundle3.putString("extra",titles[index++]);
        //thirdFragment.setArguments(bundle3);
        fragments.add(fregment4);

        fregment5 = new SensorInputFregment();
        Bundle bundle4 = new Bundle();
        bundle4.putString("extra",titles[index++]);
        //fourthFragment.setArguments(bundle4);
        fragments.add(fregment5);

        fregment6 = new InstrumentInputFregment();
        Bundle bundle5 = new Bundle();
        bundle5.putString("extra",titles[index++]);
        //fifthFragment.setArguments(bundle5);
        fragments.add(fregment6);

//        sixthFragment = new Pressure2Fragment();
//        Bundle bundle6 = new Bundle();
//        bundle6.putString("extra",titles[index++]);
//        sixthFragment.setArguments(bundle6);
//        fragments.add(sixthFragment);

        mPagerAdater=new CNKFixedPagerAdapter(getSupportFragmentManager());
        mPagerAdater.setTitles(titles);
        mPagerAdater.setFragments(fragments);
        info_viewpager.setAdapter(mPagerAdater);
        info_viewpager.addOnPageChangeListener(new OnpagechangedListernerImp());
    }

    private void addScrollView(String[] titles) {
        final int count = titles.length;
        for (int i = 0; i < count; i++) {
            // Log.e("tchl","onclick: i:"+i);
            final String title = titles[i];
            final View view = mLayoutInflater.inflate(R.layout.horizontal_item_layout, null);
            final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.horizontal_linearlayout_type);
            final ImageView img_type = (ImageView) view.findViewById(R.id.horizontal_img_type);
            final TextView type_name = (TextView) view.findViewById(R.id.horizontal_tv_type);
            type_name.setText(title);
            if (i == mCurClassIndex) {
                //已经选中
                type_name.setTextColor(ContextCompat.getColor(this, R.color.color_selected));
                img_type.setImageResource(R.drawable.bottom_line_blue);
            } else {
                //未选中
                type_name.setTextColor(ContextCompat.getColor(this, R.color.color_unselected));
                img_type.setImageResource(R.drawable.bottom_line_gray);
            }
            final int index=i;
            //点击顶部Tab标签，动态设置下面的ViewPager页面
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //首先设置当前的Item为正常状态
                    // Log.e("tchl","onclick: first mCurClassIndex:"+mCurClassIndex);
                    View currentItem=mClassContainer.getChildAt(mCurClassIndex);
                    ((TextView)(currentItem.findViewById(R.id.horizontal_tv_type))).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_unselected));
                    ((ImageView)(currentItem.findViewById(R.id.horizontal_img_type))).setImageResource(R.drawable.bottom_line_gray);
                    mCurClassIndex=index;
                    // Log.e("tchl","onclick: first index:"+index);
                    //设置点击状态
                    img_type.setImageResource(R.drawable.bottom_line_blue);
                    type_name.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_selected));
                    //跳转到指定的ViewPager
                    info_viewpager.setCurrentItem(mCurClassIndex);
                }
            });

            mClassContainer.addView(view);
        }

    }

    private void InitView() {
        mDialog = CustomDialog.createProgressDialog(this, Constants.TimeOutSecond, new CustomDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(CustomDialog dialog) {
                dialog.dismiss();
                ToastUtils.showToast(getBaseContext(), "超时啦!");
            }
        });

        //蓝牙扫描
        rllBtScan = (RelativeLayout)findViewById(R.id.rll_bt_scan);
        rllBtScan.setOnClickListener(new OnclickListererImp());
        //蓝牙监听状态
        mTxtStatus = (TextView) findViewById(R.id.txt_status);

        mLayoutInflater = LayoutInflater.from(this);
        info_viewpager = (ViewPager)findViewById(R.id.info_viewpager);
        mScrollBar = (HorizontalScrollView)findViewById(R.id.horizontal_info);
        mClassContainer = (ViewGroup)findViewById(R.id.ll_container);
    }

    private void InitBlueTooth()
    {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null)
        {
            ToastUtils.showToast(this, "该设备不支持蓝牙，强制退出");
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothState.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBTService.connect(device);
                }
                break;
            case BluetoothState.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                    // Initialize the BluetoothService to perform bluetooth
                    // connections

                    mBTService = new BluetoothService(this, mHandler);

                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);


                } else {
                    // User did not enable Bluetooth or an error occured
                    // Log.d(TAG, "BT not enabled");
                    ToastUtils.showToast(this, "蓝牙没有打开，程序退出");

                    finish();
                }

                break;

        }
    }

    public void sendData(String data, String strOwner) {


        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothState.STATE_CONNECTED) {

            ToastUtils.showToast(this,  R.string.not_connected);
            return;
        }

        // Check that there's actually something to send
        if (data.length() > 0) {
            gOwner = strOwner;
            Log.v("ttt", "Send: " + data);
            String hexString = data;
            byte[] buff = DigitalTrans.hex2byte(hexString);

            mBTService.write(buff);
        }
    }
    // 双击退出-----------------------------------------------
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            return false;
        }

        return super.dispatchKeyEvent(event);

    }
    private class OnclickListererImp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.rll_bt_scan:// 蓝牙扫描
                    Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    break;
                default:
                    break;
            }

        }
    }

    private class OnpagechangedListernerImp implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentpage=fragments.get(position);
            mCurrentpage.Oncurrentpageselect(position);
//            if(myOnPageSelectedinviewpager!=null)
//            {
//                myOnPageSelectedinviewpager.currentviewpager(position);
//            }
            View preView=mClassContainer.getChildAt(mCurClassIndex);
            ((TextView)(preView.findViewById(R.id.horizontal_tv_type))).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_unselected));
            ((ImageView)(preView.findViewById(R.id.horizontal_img_type))).setImageResource(R.drawable.bottom_line_gray);
            mCurClassIndex=position;
            //设置当前为选中状态
            View currentItem=mClassContainer.getChildAt(mCurClassIndex);
            ((ImageView)(currentItem.findViewById(R.id.horizontal_img_type))).setImageResource(R.drawable.bottom_line_blue);
            ((TextView)(currentItem.findViewById(R.id.horizontal_tv_type))).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_selected));
            //这边移动的距离 是经过计算粗略得出来的
            mScrollX=currentItem.getLeft()-300;
            //Log.d("zttjiangqq", "mScrollX:" + mScrollX);
            mScrollBar.post(new Runnable() {
                @Override
                public void run() {
                    mScrollBar.scrollTo(mScrollX,0);
                }
            });

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    public String GetStateConnect()
    {
        return mTxtStatus.getText().toString();
    }
    public interface Ondataparse
    {
        void datacometoparse(String readOutMsg1,byte[] readOutBuf1);
    }

//    public interface OnPageSelectedinviewpager
//    {
//        void currentviewpager(int position);
//    }
//    public void SetonPageSelectedinviewpager(OnPageSelectedinviewpager onPageSelectedinviewpager )
//    {
//        myOnPageSelectedinviewpager=onPageSelectedinviewpager;
//    }
    public void setOndataparse(Ondataparse ondataparse)
    {
        mydataparse=ondataparse;
    }



}
