package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.fregment.InstrumentInputFregment;
import gc.dtu.weeg.dtugc.fregment.instrumentComSetFragment;
import gc.dtu.weeg.dtugc.fregment.instrumentWorkModeSetFragment;
import gc.dtu.weeg.dtugc.fregment.instrumentbaseFragment;
import gc.dtu.weeg.dtugc.fregment.instrumenttimegapFragment;

public class InstrumemtItemseetingActivity extends FragmentActivity {

    private TextView mtltie;
    private ImageView mbutback;
    private MainActivity mainActivity;
    //private instrumentComSetFragment fragmentcom;  //instrumentbaseFragment
    private ArrayList<instrumentbaseFragment> fragments;
    private instrumentComSetFragment fragment1;
    private instrumenttimegapFragment fragment2;
    private instrumentWorkModeSetFragment fragment3;
    private static Activity activity;
    public static String baseinfo[][]={
            {"1998","1","1","300"}, // reg,item,seletc,value
            {"1998","1","2","600"},
            {"1998","1","4","1200"},
            {"1998","1","8","2400"},
            {"1998","1","16","4800"},
            {"1998","1","32","9600"},
            {"1998","1","64","19200"},

            {"1998","2","0","无"},
            {"1998","2","1","偶"},
            {"1998","2","2","奇"},

            {"1998","3","0","5"},
            {"1998","3","4","6"},
            {"1998","3","8","7"},
            {"1998","3","12","8"},

            {"1998","4","16","0.5"},
            {"1998","4","0","1"},
            {"1998","4","48","1.5"},
            {"1998","4","32","2"},

            {"2000","1","关闭","0"},
            {"2000","1","打开","1"},


            {"2000","2","Empty","0"},
            {"2000","2","ActarisMeter","1"},
            {"2000","2","HytroMeter","2"},
            {"2000","2","SiemensMeter","3"},
            {"2000","2","Kamstrup","4"},
            {"2000","2","LUG_2wr6","5"},
            {"2000","2","WEEG_Gas","6"},
            {"2000","2","FC6000H","7"},
            {"2000","2","WEEG_Gas_ISM","8"},

            {"2000","2","MFGD_Modbus","1000"},
            {"2000","2","Trancy 1.2","1001"},
            {"2000","2","Trancy 1.3","1002"},
            {"2000","2","Trancy_Modbus","1003"},
            {"2000","2","C.N.","1004"},
            {"2000","2","C.N._Modbus","1005"},
            {"2000","2","PTZ_BOX with Kp","1006"},
            {"2000","2","PTZ_BOX without Kp","1007"},
            {"2000","2","PTZ_BOX V3","1008"},
            {"2000","2","PTZ_BOX V3-2","1009"},
            {"2000","2","Corus","1010"},
            {"2000","2","Corus 2003","1011"},
            {"2000","2","Corus_Modbus","1012"},
            {"2000","2","SEVC-D 3.0","1013"},
            {"2000","2","Elster","1014"},
            {"2000","2","Elster_Modbus","1015"},
            {"2000","2","MFFD_Modbus","1016"},
            {"2000","2","EVC300","1017"},
            {"2000","2","AS Ultrasonic","1018"},
            {"2000","2","PTZ_BOX CV","1019"},
            {"2000","2","AS Ultrasonic_80","1020"},
            {"2000","2","Trancy cpuCard","1021"},
    };


    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instrumemt_itemset_layout);
        activity=this;
        mtltie=findViewById(R.id.txt_titles_insitem);
        mbutback=findViewById(R.id.imgBack_insitem);
        intent=getIntent();
        mainActivity=MainActivity.getInstance();
        initview();
        initdata();

    }

    private void initview() {
        mbutback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstrumemtItemseetingActivity.this.finish();
            }
        });
        fragments=new ArrayList<instrumentbaseFragment>();
        int reg=intent.getIntExtra("regaddr",-1);
        initfragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        switch (reg)
        {
            case 1998:
//                Bundle bundle_3 = new Bundle();
//                bundle_3.putString("some_data", parmData);
//                upParamConfig_F.setArguments(bundle_3);
//
//                transaction.replace(R.id.content, upParamConfig_F);  content_insitem
//                intent.putExtra("buad",mBuardTx.getText().toString());
//                intent.putExtra("parity",mParityTx.getText().toString());
//                intent.putExtra("databit",mDataTx.getText().toString());
//                intent.putExtra("stopbit",mStopTx.getText().toString());
                Bundle bundle_1 = new Bundle();
                String[] settings=new String[4];
                settings[0]=intent.getStringExtra("buad");
                settings[1]=intent.getStringExtra("parity");
                settings[2]=intent.getStringExtra("databit");
                settings[3]=intent.getStringExtra("stopbit");
                bundle_1.putStringArray("settings",settings);
                fragment1.setArguments(bundle_1);
                transaction.replace(R.id.content_insitem,fragment1);
                //fragments.get(0)
                break;
            case 1999:
                String setgap=intent.getStringExtra("recordgap");
                Bundle bundle_2 = new Bundle();
                bundle_2.putString("settings",setgap);
                fragment2.setArguments(bundle_2);
                transaction.replace(R.id.content_insitem, fragment2);
                break;
            case 2000:
                Bundle bundle_3 = new Bundle();
                String[] tempset=intent.getStringArrayExtra("listdata");
                bundle_3.putStringArray("listdata",tempset);
                fragment3.setArguments(bundle_3);
                transaction.replace(R.id.content_insitem, fragment3);
                break;
        }
        transaction.commit();
        mainActivity.setOndataparse(new Onbluetoothdataparse());
    }

    private void initfragment() {
        fragment1=new instrumentComSetFragment();
        fragments.add(fragment1);
        fragment2=new instrumenttimegapFragment();
        fragments.add(fragment2);
        fragment3=new instrumentWorkModeSetFragment();
        fragments.add(fragment3);
       // fragments.add(fragment1);
    }

    private void initdata()
    {
        intent=getIntent();
        String titlehere=intent.getStringExtra("title");
        mtltie.setText(titlehere);

//        for(int i=0;i<3;i++)
//        {
//            String temp1=CodeFormat.byteToHex(bufofreadcmd[i],bufofreadcmd[i].length);
//            Log.d("zl",temp1);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivity.setOndataparse(null);
    }

    public static Activity getcurinstance()
    {
       return activity;
    }
    private  class Onbluetoothdataparse implements MainActivity.Ondataparse
    {
        @Override
        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {

        }
    }
}
