package gc.dtu.weeg.dtugc.myview;

import android.app.Activity;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.slidingbutton.BaseSlidingToggleButton;
import gc.dtu.weeg.dtugc.myview.slidingbutton.SlidingToggleButton;
import gc.dtu.weeg.dtugc.utils.CodeFormat;
import gc.dtu.weeg.dtugc.utils.SoftKeyBoardListener;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class LocalSetaddr221ExtrainfoView extends LinearLayout {
    View myview;
    Context mContext;
    SlidingToggleButton mFunenbt;
    //SlidingToggleButton TimeEnablebts = new SlidingToggleButton[4];
    ArrayList<SlidingToggleButton> TimeEnablebts= new ArrayList<>();
    SlidingToggleButton m1stenablebt;
    SlidingToggleButton m2ndenablebt;
    SlidingToggleButton m3rdenablebt;
    SlidingToggleButton m4thenablebt;
    ScrollView mSetingsView;
    TextView mStartTimeTx;
    TextView mStopTimeTx;
    TextView[] TimePointTxs=new TextView[4];

    TextView mCurrentClickView;
//    TextView _1stTimePointTx;
//    TextView _2ndTimePointTx;
//    TextView _3rdTimePointTx;
//    TextView _4thTimePointTx;
    TextView mIsenableinfoTx;
    EditText mInstrumentTx;
    EditText mAirPressTx;
    EditText mGasSensorTx;
    EditText mReportFreqTx;
    EditText mTempTx;
    RadioGroup mReportModeslt;
    RadioButton testbut1;
    RadioButton testbut2;

    LinearLayout mFirmTimeSetContentView;
    View testlayout;
    SoftKeyBoardListener softKeyBoardListener ;
    byte[] mSetbytes;
    TimePickerView pvTime;
    TimePickerView pvMonth;

    boolean mcursate;

    public LocalSetaddr221ExtrainfoView(Context context)
    {
        this(context,null);
    }
    public LocalSetaddr221ExtrainfoView(Context context,byte[] setbytes) {
        super(context);
        mContext = context;
        mSetbytes = setbytes;
        softKeyBoardListener =new SoftKeyBoardListener((Activity) context);
        softKeyBoardListener.setListener((Activity) context,onSoftKeyBoardChangeListener);
        myview = View.inflate(mContext,R.layout.localsetting_addr221_layout,null);
        addView(myview);
        initview();
    }

    private void initview() {
        mFunenbt = findViewById(R.id.reg221funenable);
        mSetingsView = findViewById(R.id.reg221itemsetingslayout);
        mStartTimeTx = findViewById(R.id.reg221starttime);
        mStopTimeTx  = findViewById(R.id.reg221stoptime);
        mInstrumentTx = findViewById(R.id.instrumentset);
        mAirPressTx  = findViewById(R.id.airpressset);
        mGasSensorTx = findViewById(R.id.gassensorset);
        mReportModeslt = findViewById(R.id.reg221reportemodeset);
        mReportFreqTx = findViewById(R.id.reportmodesetvalue);
        TimePointTxs[0] = findViewById(R.id.firsttimepointvalue);
        TimePointTxs[1] = findViewById(R.id.secondtimepointvalue);
        TimePointTxs[2] = findViewById(R.id.thirdtimepointvalue);
        TimePointTxs[3] =findViewById(R.id.fourthtimepointvalue);
        m1stenablebt = findViewById(R.id.firsttimepointenable);
        m2ndenablebt = findViewById(R.id.secondtimepointenable);
        m3rdenablebt = findViewById(R.id.thirdtimepointenable);
        m4thenablebt =findViewById(R.id.fourthtimepointenable);
        testbut1 =findViewById(R.id.reportmodefreqset);
        testbut2 =findViewById(R.id.reportmodefirmtimeset);

        TimeEnablebts.add(m1stenablebt);
        TimeEnablebts.add(m2ndenablebt);
        TimeEnablebts.add(m3rdenablebt);
        TimeEnablebts.add(m4thenablebt);

        mFirmTimeSetContentView = findViewById(R.id.reportmodefirmtimecontent);
        mIsenableinfoTx = findViewById(R.id.reg221isenableinfo);

        testlayout = findViewById(R.id.testheight);
//        initlisterners();
        setAllViews2Show();
        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.MONTH,1);
        date1.set(Calendar.HOUR_OF_DAY,1);
        date1.set(Calendar.MINUTE,55);
        pvTime = new TimePickerBuilder(mContext,new Ontimeselectpickimpl())
                .setDate(date1)
                .setType(new boolean[]{false,false,false,true,true,false})
                .setLabel(null,null,null,"时","分",null)
                .build();
        pvMonth = new TimePickerBuilder(mContext,new Ontimeselectpickmonthimpl())
                .setDate(date1)
                .setType(new boolean[]{false,true,false,false,false,false})
                .setLabel(null,"月",null,null,null,null)
                .build();
        initlisterners();
    }


    private void initlisterners() {

        mReportModeslt.setOnCheckedChangeListener(new OnRadiobuttoncheckedlistenerimpl());
        mInstrumentTx.setOnFocusChangeListener(new onEdittextfocusimp());
        mAirPressTx.setOnFocusChangeListener(new onEdittextfocusimp());
        mGasSensorTx.setOnFocusChangeListener(new onEdittextfocusimp());
        mReportFreqTx.setOnFocusChangeListener(new onEdittextfocusimp());

        testbut1.setOnClickListener(new onClicklinternerbutimpl());
        testbut2.setOnClickListener(new onClicklinternerbutimpl());

        for(SlidingToggleButton but:TimeEnablebts)
        {
            but.setOnCheckedChanageListener(new OnSildebuttonCheckedimpl());
        }
        for(TextView tx:TimePointTxs)
        {
            tx.setOnClickListener(new onClicklinternerbutimpl());
        }
        mStartTimeTx.setOnClickListener(new onClicklinternerbutimpl());
        mStopTimeTx.setOnClickListener(new onClicklinternerbutimpl());
//        Log.d("zl"," mFunenbt:"+mFunenbt.isChecked());
        mFunenbt.setOnCheckedChanageListener(new funenabellisterberimpl());
    }

    private void setAllViews2Show() {
        int i=0;
        if(mSetbytes==null)
        {
            mSetbytes = new byte[23];
            mSetbytes[0]=0;
        }
//        Log.d("zl","setAllViews2Show:" + CodeFormat.byteToHex(mSetbytes,mSetbytes.length));
        Log.d("zl","setAllViews2Show");
        if(mSetbytes[0]==0x00||mSetbytes[0]==(byte) 0xff)
        {
            SetContentViewShow(false);

        }
        else
        {
            SetContentViewShow(true);
            mStartTimeTx.setText(""+(short)(0x00ff&mSetbytes[0]));
            mStopTimeTx.setText(""+(short)(0x00ff&mSetbytes[1]));
            //更新仪表频率
            ByteBuffer buf;
            buf=ByteBuffer.allocateDirect(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(mSetbytes,2,2);
            buf.rewind();
            mInstrumentTx.setText(""+buf.getShort());
            //更新压力传感器
            buf=ByteBuffer.allocateDirect(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(mSetbytes,4,2);
            buf.rewind();
            mAirPressTx.setText(""+buf.getShort());
            //更新报警器
            buf=ByteBuffer.allocateDirect(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(mSetbytes,6,2);
            buf.rewind();
            mGasSensorTx.setText(""+buf.getShort());

            //更新上传模式选择
            mReportModeslt.clearCheck();
            if(mSetbytes[8]==0x00)
            {
                mReportModeslt.check(R.id.reportmodefreqset);
                mReportFreqTx.setVisibility(View.VISIBLE);
                mFirmTimeSetContentView.setVisibility(View.GONE);
                buf=ByteBuffer.allocateDirect(2);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(mSetbytes,9,2);
                buf.rewind();
                mReportFreqTx.setText(""+buf.getShort());
            }
            else if(mSetbytes[8]==0x02)
            {
//                Log.d("zl","mReportModeslt.check(R.id.reportmodefirmtimeset);");
                mReportModeslt.check(R.id.reportmodefirmtimeset);
                mReportFreqTx.setVisibility(View.GONE);
                mFirmTimeSetContentView.setVisibility(View.VISIBLE);
                //第一段时间
                for(i=0;i<4;i++)
                {
                    if(mSetbytes[12+i*3]==(byte) 0xff)
                    {
                        TimePointTxs[i].setEnabled(false);
                        if(TimeEnablebts.get(i).isChecked())
                        {
                            TimeEnablebts.get(i).setChecked(false);
                        }
                    }
                    else
                    {
                        TimePointTxs[i].setText(""+(short)(0x00ff&mSetbytes[12+i*3])+":"+(short)(0x00ff&mSetbytes[13+i*3]));
                        if(!TimeEnablebts.get(i).isChecked())
                        {
                            TimeEnablebts.get(i).setChecked(true);
                        }
                    }

                }
            }
        }

    }

    public void SetContentViewShow(boolean isshow) {
        if (mFunenbt.isChecked() != isshow) {
//            Log.d("zl"," mFunenbt.setChecked(isshow) in SetContentViewShow ");
            mFunenbt.setChecked(isshow,0);
        }
        if(isshow)
        {
            mReportModeslt.check(R.id.reportmodefreqset);
            mReportFreqTx.setVisibility(View.VISIBLE);
            mFirmTimeSetContentView.setVisibility(View.GONE);
            mSetingsView.setVisibility(View.VISIBLE);
            mIsenableinfoTx.setText("功能使能");
        }
        else
        {
            mSetingsView.setVisibility(View.GONE);
            mIsenableinfoTx.setText("功能禁止");
        }
        mcursate = isshow;
    }

     private SoftKeyBoardListener.OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener =
             new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                 @Override
                 public void keyBoardShow(int height, int visiblehight) {
                     int[] local=new int[2];
                     int[] local1 = new int[2];
                     LocalSetaddr221ExtrainfoView.this.mReportFreqTx.getLocationOnScreen(local);
                     LocalSetaddr221ExtrainfoView.this.mIsenableinfoTx.getLocationOnScreen(local1);
                     if(local[1]>visiblehight)
                     {
                         mSetingsView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,visiblehight-local1[1]
                         +LocalSetaddr221ExtrainfoView.this.testlayout.getHeight()
                         ));
                     }
                 }

                 @Override
                 public void keyBoardHide(int height, int visiblehight) {
                     mSetingsView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                 }

     };

    private class onEdittextfocusimp implements EditText.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(hasFocus)
            {
                manager.showSoftInput(v,0);
                LocalSetaddr221ExtrainfoView.this.mTempTx = (EditText) v;
            }
            else
            {

                manager.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private class onClicklinternerbutimpl implements OnClickListener{

        @Override
        public void onClick(View v) {
            if(LocalSetaddr221ExtrainfoView.this.mTempTx !=null)
                LocalSetaddr221ExtrainfoView.this.mTempTx.clearFocus();
            v.requestFocus();
            for(TextView tx:TimePointTxs)
            {
                if(tx.getId()==v.getId())
                {
                    pvTime.show();
                    mCurrentClickView = (TextView) v;
                    break;
                }
            }
            if(v.getId()==mStartTimeTx.getId()||v.getId()==mStopTimeTx.getId())
            {
                mCurrentClickView = (TextView) v;
                pvMonth.show();
            }
        }
    }

    private class OnRadiobuttoncheckedlistenerimpl implements  RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId)
            {
                case R.id.reportmodefreqset:
                    mReportFreqTx.setVisibility(View.VISIBLE);
                    mFirmTimeSetContentView.setVisibility(View.GONE);
                    break;
                case R.id.reportmodefirmtimeset:
                    mReportFreqTx.setVisibility(View.GONE);
                    mFirmTimeSetContentView.setVisibility(View.VISIBLE);
                    break;
                    default:
                        break;
            }
        }
    }

    private class OnSildebuttonCheckedimpl implements BaseSlidingToggleButton.OnCheckedChanageListener{

        @Override
        public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
            int i=0;
            for(i=0;i<TimeEnablebts.size();i++)
            {
                if(TimeEnablebts.get(i).getId()==slidingToggleButton.getId())
                {
                    TimePointTxs[i].setEnabled(isChecked);
                    break;
                }
            }
        }
    }
    private class Ontimeselectpickimpl implements OnTimeSelectListener {
        @Override
        public void onTimeSelect(Date date, View v) {
            int hours = date.getHours();
            int minute = date.getMinutes();
          //  ((TextView)v).setText(""+hours+":"+minute);
            if(mCurrentClickView!=null)
               mCurrentClickView.setText(""+hours+":"+minute);
        }
    }
    private class Ontimeselectpickmonthimpl implements OnTimeSelectListener {
        @Override
        public void onTimeSelect(Date date, View v) {
            int mon ;
            //  ((TextView)v).setText(""+hours+":"+minute);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date );
            mon = cal.get(Calendar.MONTH) + 1;
            if(mCurrentClickView!=null)
                mCurrentClickView.setText(""+mon);
        }
    }
    private class funenabellisterberimpl implements BaseSlidingToggleButton.OnCheckedChanageListener {

        private boolean prestate = false;
        funenabellisterberimpl()
        {
            prestate = !mcursate;
        }
        @Override
        public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
//            Log.d("zl","funenabellisterberimpl:"+isChecked+prestate);
//            LocalSetaddr221ExtrainfoView.this.SetContentViewShow(isChecked);
            if(prestate==true)
            {
                LocalSetaddr221ExtrainfoView.this.SetContentViewShow(isChecked);
//                Log.d("zl","run funenabellisterberimpl:"+isChecked+prestate);
            }
            else
            {
                prestate = true;
            }
        }
    }
//    @Override
//    public String toString() {
//        String sinfo="";
//        int i=0;
//        if(mSetbytes==null)
//        {
//            return  sinfo;
//        }
//        if(mSetbytes[0]==0x00||mSetbytes[0]==(byte) 0xff)
//        {
//            sinfo="功能禁止";
//        }
//        else
//        {
//            sinfo+=(short)(0x00ff&mSetbytes[0]);
//            sinfo+=",";
//            sinfo+=(short)(0x00ff&mSetbytes[1]);
//            sinfo+=",";
//            //更新仪表频率
//            ByteBuffer buf;
//            buf=ByteBuffer.allocateDirect(2);
//            buf.order(ByteOrder.LITTLE_ENDIAN);
//            buf.put(mSetbytes,2,2);
//            buf.rewind();
//            sinfo+=""+buf.getShort();
//            sinfo+=",";
//            //更新压力传感器
//            buf=ByteBuffer.allocateDirect(2);
//            buf.order(ByteOrder.LITTLE_ENDIAN);
//            buf.put(mSetbytes,4,2);
//            buf.rewind();
//            sinfo+=""+buf.getShort();
//            sinfo+=",";
//            //更新报警器
//            buf=ByteBuffer.allocateDirect(2);
//            buf.order(ByteOrder.LITTLE_ENDIAN);
//            buf.put(mSetbytes,6,2);
//            buf.rewind();
//            sinfo+=""+buf.getShort();
//            sinfo+=",";
//            //更新上传模式选择
//            if(mSetbytes[8]==0x00)
//            {
//                sinfo+="频率";
//                sinfo+=",";
//                buf=ByteBuffer.allocateDirect(2);
//                buf.order(ByteOrder.LITTLE_ENDIAN);
//                buf.put(mSetbytes,9,2);
//                buf.rewind();
//                sinfo+=""+buf.getShort();
//            }
//            else if(mSetbytes[8]==0x02)
//            {
//                sinfo+="时刻";
//                sinfo+=",";
//                //第一段时间
//                for(i=0;i<4;i++)
//                {
//                    if(mSetbytes[12+i*3]==(byte) 0xff)
//                    {
//                        sinfo+="无";
//                        sinfo+=",";
//                    }
//                    else
//                    {
//                        sinfo+=""+(short)(0x00ff&mSetbytes[12+i*3])+":"+(short)(0x00ff&mSetbytes[13+i*3]);
//                        sinfo+=",";
//                    }
//
//                }
//            }
//        }
//        return sinfo;
//    }

    public byte[] dacodeshowinfo()
    {
        byte[] setbyes = null;
        short temp;
        int i=0;
        int index;
        String settimes,infotime;
        ByteBuffer buf;
        if(mFunenbt.isChecked())
        {
            if(mStartTimeTx.getText().length()==0 || mStopTimeTx.getText().length()==0)
            {
                ToastUtils.showToast(mContext,"月份不能为空");
                setbyes=null;
            }
            else
            {
                setbyes = new byte[23];
                temp=Integer.valueOf(mStartTimeTx.getText().toString()).shortValue();
                setbyes[0] =(byte)(temp%0x100);

                temp=Integer.valueOf(mStopTimeTx.getText().toString()).shortValue();
                setbyes[1] =(byte)(temp%0x100);

                if(mInstrumentTx.getText().length()==0)
                {
                    setbyes[2]=0x00;
                    setbyes[3]=0x00;
                }
                else
                {
                    buf = ByteBuffer.allocate(2);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    temp = Integer.valueOf(mInstrumentTx.getText().toString()).shortValue();
                    buf.putShort(temp);
                    buf.rewind();
                    buf.get(setbyes,2,2);
                }
                if(mAirPressTx.getText().length()==0)
                {
                    setbyes[4]=0x00;
                    setbyes[5]=0x00;
                }
                else
                {
                    buf = ByteBuffer.allocate(2);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    temp = Integer.valueOf(mAirPressTx.getText().toString()).shortValue();
                    buf.putShort(temp);
                    buf.rewind();
                    buf.get(setbyes,4,2);
                }
                if(mGasSensorTx.getText().length()==0)
                {
                    setbyes[6]=0x00;
                    setbyes[7]=0x00;
                }
                else
                {
                    buf = ByteBuffer.allocate(2);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    temp = Integer.valueOf(mGasSensorTx.getText().toString()).shortValue();
                    buf.putShort(temp);
                    buf.rewind();
                    buf.get(setbyes,6,2);
                }

                if(mReportModeslt.getCheckedRadioButtonId()==R.id.reportmodefreqset)
                {
                    setbyes[8]=0x00;
                    if(mReportFreqTx.getText().toString().length()==0)
                    {
                        temp=0;
                    }
                    else
                        temp = Integer.valueOf(mReportFreqTx.getText().toString()).shortValue() ;

                    buf = ByteBuffer.allocate(2);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.putShort(temp);
                    buf.rewind();
                    buf.get(setbyes,9,2);

                }
                else if(mReportModeslt.getCheckedRadioButtonId()==R.id.reportmodefirmtimeset)
                {
                    setbyes[8]=0x02;
                    for(i=0;i<12;i++)
                    {
                        setbyes[i+11]=(byte)0xff;
                    }
                    for(i=0;i<TimeEnablebts.size();i++)
                    {
                        if(TimeEnablebts.get(i).isChecked())
                        {
                            settimes=TimePointTxs[i].getText().toString();
                            index = settimes.indexOf(':');
                            if(index>0)
                            {
                                infotime = settimes.substring(0,index);
                                byte tempbyte = Integer.valueOf(infotime).byteValue();
                                setbyes[i*3+12]=tempbyte;

                                index++;
                                infotime = settimes.substring(index);
                                tempbyte = Integer.valueOf(infotime).byteValue();
                                setbyes[i*3+13]=tempbyte;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            setbyes = new byte[23];
            for(i=0;i<23;i++)
                setbyes[i]=0x00;
        }
        return setbyes;
    }
    static public String dacodetoStr(byte[] bytedecode)
    {
        String sinfo="";
        int i=0;
        if(bytedecode==null)
        {
            return  sinfo;
        }
        if(bytedecode.length!=23)
        {
            return  sinfo;
        }
        if(bytedecode[0]==0x00||bytedecode[0]==(byte) 0xff)
        {
            sinfo="功能禁止";
        }
        else
        {
            sinfo+=(short)(0x00ff&bytedecode[0]);
            sinfo+=",";
            sinfo+=(short)(0x00ff&bytedecode[1]);
            sinfo+=",";
            //更新仪表频率
            ByteBuffer buf;
            buf=ByteBuffer.allocateDirect(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(bytedecode,2,2);
            buf.rewind();
            sinfo+=""+buf.getShort();
            sinfo+=",";
            //更新压力传感器
            buf=ByteBuffer.allocateDirect(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(bytedecode,4,2);
            buf.rewind();
            sinfo+=""+buf.getShort();
            sinfo+=",";
            //更新报警器
            buf=ByteBuffer.allocateDirect(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(bytedecode,6,2);
            buf.rewind();
            sinfo+=""+buf.getShort();
            sinfo+=",";
            //更新上传模式选择
            if(bytedecode[8]==0x00)
            {
                sinfo+="F";
                sinfo+=",";
                buf=ByteBuffer.allocateDirect(2);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(bytedecode,9,2);
                buf.rewind();
                sinfo+=""+buf.getShort();
                sinfo+=",";
            }
            else if(bytedecode[8]==0x02)
            {
                sinfo+="T";
                sinfo+=",";
                //第一段时间
                for(i=0;i<4;i++)
                {
                    if(bytedecode[12+i*3]==(byte) 0xff)
                    {
                        sinfo+="N";
                        sinfo+=",";
                    }
                    else
                    {
                        sinfo+=""+(short)(0x00ff&bytedecode[12+i*3])+":"+(short)(0x00ff&bytedecode[13+i*3]);
                        sinfo+=",";
                    }

                }
            }
        }
        return sinfo;
    }

    static public byte[] strinfo2bytes(String setinfo)
    {
        byte[] setbytes = null;
        int index=-1;
        String temp;
        short setings=-1;
        ByteBuffer buf;
        int i=0;
        if(setinfo!=null)
        {
            setbytes = new byte[23];
            setbytes[0]=0x00;
            if(setinfo.equals("功能禁止")==false && setinfo.length()!=0)
            {
                index = setinfo.indexOf(',');
                temp = setinfo.substring(0,index);
                setbytes[0] = Integer.valueOf(temp).byteValue();

                setinfo = setinfo.substring(index+1);
                index = setinfo.indexOf(',');
                temp = setinfo.substring(0,index);
                setbytes[1] = Integer.valueOf(temp).byteValue();
                for(i=0;i<3;i++)
                {
                    setinfo = setinfo.substring(index+1);
                    index = setinfo.indexOf(',');
                    temp = setinfo.substring(0,index);
                    setings = Integer.valueOf(temp).shortValue();

                    buf = ByteBuffer.allocate(2);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.putShort(setings);
                    buf.rewind();
                    buf.get (setbytes,2+i*2,2);
                }
                setinfo = setinfo.substring(index+1);
                index = setinfo.indexOf(',');
                temp = setinfo.substring(0,index);
                for(i=0;i<12;i++)
                {
                    setbytes[11+i]=(byte) 0xff;
                }
                if(temp.equals("F"))
                {
                    setbytes[8]=0x00;
                    setinfo = setinfo.substring(index+1);
                    index = setinfo.indexOf(',');
                    temp = setinfo.substring(0,index);
                    setings = Integer.valueOf(temp).shortValue();

                    buf = ByteBuffer.allocate(2);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.putShort(setings);
                    buf.rewind();
                    buf.get (setbytes,9,2);
                }
                else if(temp.equals("T"))
                {
                    setbytes[8]=0x02;

                    for(i=0;i<4;i++)
                    {
                        setinfo = setinfo.substring(index+1);
                        index = setinfo.indexOf(',');
                        temp = setinfo.substring(0,index);
                        if(temp.equals("N"))
                        {
                            continue;
                        }
                        index = setinfo.indexOf(':');
                        temp = setinfo.substring(0,index);
                        setbytes[12+i*3] = Integer.valueOf(temp).byteValue();

                        setinfo = setinfo.substring(index+1);
                        index = setinfo.indexOf(',');
                        temp = setinfo.substring(0,index);
                        setbytes[13+i*3] = Integer.valueOf(temp).byteValue();
                    }
                }
//                Log.d("zl","strinfo2bytes:"+CodeFormat.byteToHex(setbytes,setbytes.length));
            }
        }
        return setbytes;
    }
}
