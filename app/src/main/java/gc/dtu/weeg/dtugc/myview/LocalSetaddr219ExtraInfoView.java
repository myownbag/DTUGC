package gc.dtu.weeg.dtugc.myview;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.myview.slidingbutton.BaseSlidingToggleButton;
import gc.dtu.weeg.dtugc.myview.slidingbutton.SlidingToggleButton;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class LocalSetaddr219ExtraInfoView extends LinearLayout {

    String mCursetstr;
    Context mActivity;
    View myview;
    TimePickerView pvTime;
    SlidingToggleButton slidingToggleButton1;
    SlidingToggleButton mTimeset1;
    SlidingToggleButton mTimeset2;
    SlidingToggleButton mTimeset3;
    public EditText mTimegap;
    LinearLayout mdetaillayout;
    TextView mstart1;
    TextView mend1;
    TextView mstart2;
    TextView mend2;
    TextView mstart3;
    TextView mend3;
    int mClickViewid;
    byte[] settings=new byte[14];
    SettingInterface settingInterface;

    public LocalSetaddr219ExtraInfoView(Context context,String setingstr) {
        super(context);
        mActivity = context;
        mCursetstr=setingstr;
        myview = View.inflate(mActivity,R.layout.localsetting_addr219_layout,null);
        addView(myview);

        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.HOUR_OF_DAY,1);
        date1.set(Calendar.MINUTE,55);
        initview();
        pvTime = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
              //  Toast.makeText(mActivity, getTime(date), Toast.LENGTH_SHORT).show();
                String temp=""+date.getHours()+":"+date.getMinutes();
                TextView selectview;
                selectview=myview.findViewById(mClickViewid);
                selectview.setText(temp);
                switch(mClickViewid)
                {
                    case R.id.local_219_starttime1:
                        settings[2]=(byte)(date.getHours()%0x100);
                        settings[3]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime1:
                        settings[4]=(byte)(date.getHours()%0x100);
                        settings[5]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_starttime2:
                        settings[6]=(byte)(date.getHours()%0x100);
                        settings[7]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime2:
                        settings[8]=(byte)(date.getHours()%0x100);
                        settings[9]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_starttime3:
                        settings[10]=(byte)(date.getHours()%0x100);
                        settings[11]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime3:
                        settings[12]=(byte)(date.getHours()%0x100);
                        settings[13]=(byte)(date.getMinutes()%0x100);
                        break;
                        default:
                            break;
                }
                updatesetting();
            }
        })
                .setDate(date1)
                .setType(new boolean[]{false,false,false,true,true,false})
                .setLabel(null,null,null,"时","分",null)
                .build();


        slidingToggleButton1.setOnCheckedChanageListener(new BaseSlidingToggleButton.OnCheckedChanageListener() {
            @Override
            public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
              //  pvTime.show();
                if(isChecked)
                {
                    //mTimegap.setFocusable(true);
                    mTimegap.setEnabled(true);
                    mdetaillayout.setVisibility(View.VISIBLE);
                    mTimegap.setText("1");
                    mTimegap.setSelection(mTimegap.getText().length());
                }
                else
                {
                    //mTimegap.setFocusable(false);
                    mTimegap.setEnabled(false);
                    mdetaillayout.setVisibility(View.GONE);
                    settings[0]=0x00;
                    settings[1]=0x00;
                    mTimegap.setText("功能禁止");
                }
                updatesetting();
            }
        });
    }

    private void initview() {
        slidingToggleButton1= myview.findViewById(R.id.local_extra_219_first);
        mTimegap=myview.findViewById(R.id.local_edit_219_timegap);
        mdetaillayout=myview.findViewById(R.id.local_219_detail_view);
        mdetaillayout.setVisibility(View.GONE);

        mstart1=myview.findViewById(R.id.local_219_starttime1);
        mstart1.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend1 = myview.findViewById(R.id.local_219_endtime1);
        mend1.setOnClickListener(new OnTextViewClicklinenterimpl());

        mstart2=myview.findViewById(R.id.local_219_starttime2);
        mstart2.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend2 = myview.findViewById(R.id.local_219_endtime2);
        mend2.setOnClickListener(new OnTextViewClicklinenterimpl());

        mstart3=myview.findViewById(R.id.local_219_starttime3);
        mstart3.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend3 = myview.findViewById(R.id.local_219_endtime3);
        mend3.setOnClickListener(new OnTextViewClicklinenterimpl());

        mTimeset1 = myview.findViewById(R.id.local_219_checkbut_1);
        mTimeset2 = myview.findViewById(R.id.local_219_checkbut_2);
        mTimeset3 = myview.findViewById(R.id.local_219_checkbut_3);

//        mstart1.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
//        mend1.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
//        mstart2.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
//        mend2.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
//        mstart3.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
//        mend3.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));

        mTimeset1.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());
        mTimeset2.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());
        mTimeset3.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());

        mTimegap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int gap;
                if(editable.toString().length()==0)
                {
                    gap=-1;
                }
                else if(editable.toString().equals("功能禁止"))
                {
                    gap=0;
                }
                else
                {
                    gap=Integer.valueOf(editable.toString());

                }
                ByteBuffer buf1;
                buf1=ByteBuffer.allocateDirect(4);
                buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                buf1.putInt(gap);
                buf1.rewind();
                buf1.get(settings,0,2);
                updatesetting();
            }
        });

        mTimegap.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(b)
                {
                    imm.hideSoftInputFromWindow(((Activity)mActivity).getWindow().getDecorView().getWindowToken(), HIDE_NOT_ALWAYS);
                }
                else
                    imm.hideSoftInputFromWindow(((Activity)mActivity).getWindow().getDecorView().getWindowToken(), 0);
            }
        });
        addr219showinit();

    }

    private void addr219showinit() {
        if(mCursetstr.length()==0)
        {
            for(int i=2;i<settings.length;i++)
            {
                settings[i]=(byte) 0xff;
            }
        }
        else
        {
            byte[] byteset = new byte[14];
            parsetimestr(mCursetstr,byteset);

            short funactivity;
            ByteBuffer buf = ByteBuffer.allocate(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(byteset,0,2);
            buf.rewind();
            funactivity=buf.getShort();
            if(funactivity==0)
            {
                slidingToggleButton1.setChecked(false);
                mTimegap.setText("功能禁止");
                mTimegap.setEnabled(false);
                mdetaillayout.setVisibility(View.GONE);
            }
            else
            {
                slidingToggleButton1.setChecked(true);
                mTimegap.setText(""+funactivity);
                mTimegap.setEnabled(true);
                mdetaillayout.setVisibility(View.VISIBLE);
            }
            settimesetpartshow(byteset,2,mstart1,mend1,mTimeset1);
            settimesetpartshow(byteset,6,mstart2,mend2,mTimeset2);
            settimesetpartshow(byteset,10,mstart3,mend3,mTimeset3);
        }
    }

    private void settimesetpartshow(byte[] byteset,int offset,TextView s,TextView e,SlidingToggleButton sbutton) {
        String timeformate="";
        String temp;
        if(byteset[offset]==0xff)
        {
            sbutton.setChecked(false);
            s.setText("");
            e.setText("");
        }
        else
        {
            sbutton.setChecked(true);
            temp=""+byteset[offset];
            if(temp.length()==1)
            {
                temp="0"+temp;
            }
            timeformate+=temp;
            temp=""+byteset[offset+1];
            if(temp.length()==1)
            {
                temp="0"+temp;
            }
            timeformate+=":";
            timeformate+=temp;
            s.setText(timeformate);

            timeformate="";

            temp=""+byteset[offset+2];
            if(temp.length()==1)
            {
                temp="0"+temp;
            }
            timeformate+=temp;
            temp=""+byteset[offset+3];
            if(temp.length()==1)
            {
                temp="0"+temp;
            }
            timeformate+=":";
            timeformate+=temp;
            e.setText(timeformate);
        }
    }

    class OnTextViewClicklinenterimpl implements OnClickListener
    {

        @Override
        public void onClick(View view) {
            mClickViewid = view.getId();
            pvTime.show();
        }
    }

    public interface SettingInterface
    {
        void OncurSetting(String set,byte[] setbyte);
    }

    public void setOncursettingChanged(SettingInterface si)
    {
         settingInterface = si;
    }
    public void updatesetting()
    {
        if(settingInterface!=null)
        {
            int i=0;
            short temp=0;
            String setstr="";
            String temptime="";
            ByteBuffer buf1;
            buf1=ByteBuffer.allocateDirect(2);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(settings,0,2) ;
            buf1.rewind();
            temp = buf1.getShort();
            if(temp==0)
            {
                setstr="功能禁止";
            }
            else
            {
                if(temp==-1)
                {
                    setstr="";
                }
                else
                {
                    setstr+=temp;
                }
                setstr+=",";

                for(i=2;i<settings.length;i++)
                {
                    if(settings[i]>=0)
                    {
                        temptime=""+settings[i];
                        if(temptime.length()==1)
                        {
                            temptime="0"+temptime;
                        }
                        setstr+=temptime;
                        if((i-1)%4==0&&(i-2)>0)
                        {
                            setstr+=",";
                        }
                        else
                        {
                            if(i%2==0)
                                setstr+=":";
                            else
                                setstr+="-";
                        }
                    }
                    else if((settings[i]==(byte) 0xff)&&(i-1)%4==0)
                    {
                        setstr+="时段禁止,";
                    }
                }
            }

            settingInterface.OncurSetting(setstr,settings);
        }
    }
    class OnSlidebuttonCkeckedchangedlistenerImpl implements SlidingToggleButton.OnCheckedChanageListener
    {

        @Override
        public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
            int id = slidingToggleButton.getId();
            int i=0;
            switch (id)
            {
                case R.id.local_219_checkbut_1:

                    mstart1.setEnabled(isChecked);
                    mend1.setEnabled(isChecked);
                    setslidebuttonpremeter(isChecked,mstart1,mend1,2);
                    break;
                case R.id.local_219_checkbut_2:

                    mstart2.setEnabled(isChecked);
                    mend2.setEnabled(isChecked);
                    setslidebuttonpremeter(isChecked,mstart2,mend2,6);
                    break;
                case R.id.local_219_checkbut_3:
                    mstart3.setEnabled(isChecked);
                    mend3.setEnabled(isChecked);
                    setslidebuttonpremeter(isChecked,mstart3,mend3,10);
                    break;
            }
            updatesetting();
        }
    }

    private void setslidebuttonpremeter(boolean isChecked,TextView s, TextView e,int index) {
        int i;
        if(isChecked)
        {
//            s.setBackgroundResource(R.drawable.framebackground);
//            e.setBackgroundResource(R.drawable.framebackground);
            s.setText("00:00");
            e.setText("00:00");
            for(i=0;i<4;i++)
            {
                settings[index+i]=0;
            }
        }
        else
        {
//            s.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
//            e.setBackgroundColor(mActivity.getResources().getColor(R.color.color_grey));
            for(i=0;i<4;i++)
            {
                settings[index+i]=(byte) 0xff;
                s.setText("");
                e.setText("");
            }
        }
    }

   public static boolean parsetimestr( String timesetstr ,byte[] set)
   {
       int index=0;
       int i=0;
       String temp;
       ByteBuffer buffer;
       if(set==null)
       {
           return false;
       }
       if(set.length!=14)
       {
           return  false;
       }
       index=timesetstr.indexOf(",");
       if(index==0)
       {
           return  false;
       }

       if(timesetstr.contains("功能禁止"))
       {
           for( i=0;i<set.length;i++)
           {
               set[i]=0;
           }
       }
       else
       {
           temp=timesetstr.substring(0,index);
           buffer =ByteBuffer.allocateDirect(2);
           buffer=buffer.order(ByteOrder.LITTLE_ENDIAN);
           buffer.putShort(Short.valueOf(temp));
           buffer.rewind();
           buffer.get(set,0,2);
           for(i=0;i<3;i++)
           {
               timesetstr = timesetstr.substring(index+1,timesetstr.length());
               index = timesetstr.indexOf(",");
               temp=timesetstr.substring(0,index);
               parsetimes2e(set, temp,2+i*4);
           }
       }
       return true;
   }

    private static void parsetimes2e(byte[] set, String temp,int indexset) {
        int i;
        if(temp.equals("时段禁止"))
        {
            for(i=0;i<4;i++)
            {
                set[indexset+i]=(byte)0xff;
            }
        }
        else
        {
            set[indexset]=Byte.valueOf(temp.substring(0,2)) ;
            indexset++;
            set[indexset]=Byte.valueOf(temp.substring(3,3+2)) ;
            indexset++;
            set[indexset]=Byte.valueOf(temp.substring(6,6+2)) ;
            indexset++;
            set[indexset]=Byte.valueOf(temp.substring(9,9+2)) ;
        }
    }
}
