package gc.dtu.weeg.dtugc.myview.slidingbutton;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;

public class AlarmSettingDlg extends Dialog {
    Context mActivity;
    byte[] mSet;
    long setvalue = 0;
    Button btsetOk;
    LinearLayout[] containerallbuttons;
    CheckBox[] buttons;
    private Onbutclicked myonbutlisterner;
    public AlarmSettingDlg(@NonNull Context context,byte[] set) {
        super(context);
        mActivity = context;
        mSet = set;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.button_container_layout);
       btsetOk = findViewById(R.id.selectok);
        containerallbuttons = new LinearLayout[16];
       containerallbuttons[0] = findViewById(R.id.btoongroup1);
        containerallbuttons[1] = findViewById(R.id.btoongroup2);
        containerallbuttons[2] = findViewById(R.id.btoongroup3);
        containerallbuttons[3] = findViewById(R.id.btoongroup4);
        containerallbuttons[4] = findViewById(R.id.btoongroup5);
        containerallbuttons[5] = findViewById(R.id.btoongroup6);
        containerallbuttons[6] = findViewById(R.id.btoongroup7);
        containerallbuttons[7] = findViewById(R.id.btoongroup8);
        containerallbuttons[8] = findViewById(R.id.btoongroup9);
        containerallbuttons[9] = findViewById(R.id.btoongroup10);
        containerallbuttons[10] = findViewById(R.id.btoongroup11);
        containerallbuttons[11] = findViewById(R.id.btoongroup12);
        containerallbuttons[12] = findViewById(R.id.btoongroup13);
        containerallbuttons[13] = findViewById(R.id.btoongroup14);
        containerallbuttons[14] = findViewById(R.id.btoongroup15);
        containerallbuttons[15] = findViewById(R.id.btoongroup16);
       buttons = new CheckBox[64];
       for(int i=0;i<16;i++)
       {
           for (int j=0;j<4;j++)
           {
               buttons[i*4+j] = new CheckBox(mActivity);
               LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
               layoutParams.weight = 1;
               buttons[i*4+j].setLayoutParams(layoutParams);
               buttons[i*4+j].setText(""+(i*4+j+1));
               containerallbuttons[i].addView(buttons[i*4+j]);
           }
       }
        btsetOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tr="";
                byte[] temp = new byte[8];
                for(int i=0;i<8;i++)
                {
                    for(int j=0;j<8;j++)
                    {
                        temp[i]>>=1;
                        if(buttons[i*8+j].isChecked())
                        {
                            temp[i]|=(byte)0x80;
                        }
                        else
                        {
                            temp[i]&=(byte)0x7f;
                        }
                    }
                }

                if(myonbutlisterner !=null)
                {
                    myonbutlisterner.Onbutclicked(temp);
                }
                dismiss();
            }
        });

       if(mSet !=null)
       {
           for(int i=0;i<8;i++)
           {
               byte temp =mSet[7 - i];
               for(int j=0;j<8;j++)
               {
                   byte t =0x01;
                   t&=temp;
                   if(t == 1)
                   {
                       buttons[i*8+j].setChecked(true);
                   }
                   t = 1;
                   temp>>=1;
               }
           }
       }
    }

    public  interface Onbutclicked
    {
        public void Onbutclicked(byte select[]);
    }
    public void SetOnbutclickListernerdlg(Onbutclicked onbutclicked)
    {
        myonbutlisterner=onbutclicked;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
