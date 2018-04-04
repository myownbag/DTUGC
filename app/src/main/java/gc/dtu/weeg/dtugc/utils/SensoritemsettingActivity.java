package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;


public class SensoritemsettingActivity extends Activity {
    Intent intent;
    MainActivity mainActivity;
    RelativeLayout  selectlayout;
    RelativeLayout  anologinputlayout;
    TextView  mtitle;
    TextView  text1;
    TextView  text2;
    Spinner msettings;
    ImageView Imageback;
    Button   butcommit;
    ArrayList<String> listcontent;
    ArrayList<String> listvalue;
    int m_currentselect=-1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensoritemsettinglayout);
        intent=getIntent();
        selectlayout=findViewById(R.id.sensor_select_layout);
        anologinputlayout=findViewById(R.id.sensor_anolog_layout);
        text1=findViewById(R.id.sensor_set_item_hight_lable);
        text2=findViewById(R.id.sensor_set_item_low_lable);
        mtitle=findViewById(R.id.Sensor_item_txt_titles);
        msettings= findViewById(R.id.sensor_set_item);
        Imageback=findViewById(R.id.Sensor_imgBackItemset);
        butcommit=findViewById(R.id.buttsensorcommite);
        mainActivity=MainActivity.getInstance();


        initview();

    }

    private void initview() {

            int position=intent.getIntExtra("position",-1);
            String temptitle=intent.getStringExtra("name");
            mtitle.setText(temptitle);
            listcontent=new ArrayList<String>();
            listvalue=new ArrayList<String>();
            String tempcontent=intent.getStringExtra("item1");;
            if(position==1||position==2)
            {
                for(int i=0;i<mainActivity.fregment5.sensorinfo.length;i++)
                {
                    if(mainActivity.fregment5.sensorinfo[i][0]=="1")
                    {
                        listcontent.add(mainActivity.fregment5.sensorinfo[i][1]);
                        listvalue.add(mainActivity.fregment5.sensorinfo[i][2]);
                        if(tempcontent==mainActivity.fregment5.sensorinfo[i][1])
                        {
                            m_currentselect=i;
                        }
                    }
                }
            }
            else if(position==3)
            {
                for(int i=0;i<mainActivity.fregment5.sensorinfo.length;i++)
                {
                    if(mainActivity.fregment5.sensorinfo[i][0]=="2")
                    {
                        listcontent.add(mainActivity.fregment5.sensorinfo[i][1]);
                        listvalue.add(mainActivity.fregment5.sensorinfo[i][2]);
                        if(tempcontent==mainActivity.fregment5.sensorinfo[i][1])
                        {
                            m_currentselect=i;
                        }
                    }
                }
            }
        switch (position)
            {
                case 1:
                case 2:
                case 3:
                    selectlayout.setVisibility(View.VISIBLE);
                    anologinputlayout.setVisibility(View.VISIBLE);
                    text1.setText("高报警");
                    text2.setText("低报警");
                    break;
                case 4:
                    selectlayout.setVisibility(View.GONE);
                    anologinputlayout.setVisibility(View.GONE);
                    text1.setText("扫描时间");
                    text2.setText("记录时间");
                    break;

            }

        //适配器
        ArrayAdapter<String> arr_adapter;
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listcontent);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        msettings.setAdapter(arr_adapter);
        //        serverIntent.putExtra("name","第二路压力");
//        serverIntent.putExtra("position",2);
//        serverIntent.putExtra("item1",mpressmode2.getText().toString());
//        serverIntent.putExtra("item2",mPress2H.getText().toString());
//        serverIntent.putExtra("item3",mPress2L.getText().toString());
        msettings.setSelection(m_currentselect,true);
        msettings.setOnItemSelectedListener(new SpinerOnitemselectimp());
        if(tempcontent=="模拟量量程")
        {
            anologinputlayout.setVisibility(View.VISIBLE);
        }
        else
        {
            anologinputlayout.setVisibility(View.GONE);
        }
        Imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensoritemsettingActivity.this.finish();
            }
        });
        butcommit.setOnClickListener(new ButtonOnclicklistenerimp());
    }
    private class SpinerOnitemselectimp implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listvalue.get(position).equals("65535"))
                {
                    anologinputlayout.setVisibility(View.VISIBLE);
//                    anologinputlayout.setFocusable(true);
//                    anologinputlayout.setFocusableInTouchMode(true);
                    anologinputlayout.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(anologinputlayout,0);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                else
                {
                    anologinputlayout.setVisibility(View.GONE);
                }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    private class ButtonOnclicklistenerimp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            ArrayList<Map<String,String>> itemdata=new ArrayList<Map<String,String>>();
            int m_currentselect=  msettings.getSelectedItemPosition();
            Map<String,String> temp =new HashMap<String,String>();
            temp.put("text",listcontent.get(m_currentselect));
            temp.put("settings",listvalue.get(m_currentselect));
            itemdata.add(temp);

        }
    }
}
