package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;


public class SensoritemsettingActivity extends Activity {
    Intent intent;
    public MainActivity mainActivity;
    RelativeLayout  selectlayout;
    RelativeLayout  anologinputlayout;
    TextView  mtitle;
    TextView  text1;
    TextView  text2;
    Spinner msettings;
    EditText m_range;
    EditText editText1;
    EditText editText2;

    ImageView Imageback;
    Button   butcommit;
    ArrayList<String> listcontent;
    ArrayList<String> listvalue;
    int m_currentselect=0;
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
        m_range=findViewById(R.id.sensor_set_item_manual);
        editText1=findViewById(R.id.sensor_set_item_hight_input);
        editText2=findViewById(R.id.sensor_set_item_low_input);


        initview();

    }

    private void initview() {

            int position=intent.getIntExtra("position",-1);
            String temptitle=intent.getStringExtra("name");
            boolean isfind=false;
            mtitle.setText(temptitle);
            listcontent=new ArrayList<String>();
            listvalue=new ArrayList<String>();
            String tempcontent=intent.getStringExtra("item1");
            if(position==1||position==2)
            {
                for(int i=0;i<mainActivity.fregment5.sensorinfo.length;i++)
                {
                    if(mainActivity.fregment5.sensorinfo[i][0]=="1")
                    {
                        listcontent.add(mainActivity.fregment5.sensorinfo[i][1]);
                        listvalue.add(mainActivity.fregment5.sensorinfo[i][2]);

                        if(tempcontent.equals(mainActivity.fregment5.sensorinfo[i][1]))
                        {
                            m_currentselect=listvalue.size()-1;
                            isfind=true;
                        }
                    }
                }
                if(isfind==false)
                {
                    if(tempcontent.length()!=0)
                    {
                        m_currentselect=listvalue.size()-1;
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
                        if(tempcontent.equals(mainActivity.fregment5.sensorinfo[i][1]))
                        {
                            m_currentselect=listvalue.size()-1;
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
                    m_range.setText(tempcontent);
                    editText1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                    editText2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                    break;
                case 4:
                    selectlayout.setVisibility(View.GONE);
                    anologinputlayout.setVisibility(View.GONE);
                    text1.setText("扫描时间");
                    text2.setText("记录时间");
                    editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

            }
            editText1.setText(intent.getStringExtra("item2"));
            editText2.setText(intent.getStringExtra("item3"));
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
        int sizemax=listvalue.size()-1;
        if(position==1||position==2)
        {
            if(m_currentselect==sizemax)
                anologinputlayout.setVisibility(View.VISIBLE);
            else
                anologinputlayout.setVisibility(View.GONE);
        }
        else if(position==3)
        {
            anologinputlayout.setVisibility(View.GONE);
        }
        else
        {
            selectlayout.setVisibility(View.GONE);
        }
        Imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensoritemsettingActivity.this.finish();
            }
        });
        butcommit.setOnClickListener(new ButtonOnclicklistenerimp());
    }
    public void test()
    {

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
                    m_range.setText("");
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
            Map<String,String> temp;
            if(listvalue.size()!=0)
            {
                temp=new HashMap<String,String>();

                if(listvalue.get(m_currentselect).equals("65535")==false)
                {
                    temp.put("text",listcontent.get(m_currentselect));
                    temp.put("settings",listvalue.get(m_currentselect));
                    temp.put("unit","");
                    itemdata.add(temp);
                }
                else
                {
                    if(m_range.length()==0)
                    {
                        Toast.makeText(SensoritemsettingActivity.this,"请完善信息",Toast.LENGTH_SHORT).show();
                        SensoritemsettingActivity.this.setResult(-1,intent);
                        return;
                    }
                    else
                    {
                        temp.put("text",m_range.getText().toString());
                        temp.put("settings",m_range.getText().toString());
                        temp.put("unit","Kp");
                        itemdata.add(temp);
                    }
                }
            }

             if(m_currentselect!=0&&m_currentselect!=-1)
             {
                 if(editText1.length()==0||editText2.length()==0)
                 {
                     Toast.makeText(SensoritemsettingActivity.this,"请完善信息",Toast.LENGTH_SHORT).show();
                     SensoritemsettingActivity.this.setResult(-1,intent);
                     return;
                 }
                 if(Float.valueOf(editText1.getText().toString())<Float.valueOf(editText2.getText().toString()))
                 {
                     Toast.makeText(SensoritemsettingActivity.this,"高报警必须大于低报警",Toast.LENGTH_SHORT).show();
                     SensoritemsettingActivity.this.setResult(-1,intent);
                     return;
                 }
             }
            temp=new HashMap<String,String>();
            if(editText1.getText().length()==0)
            {
                temp.put("text","0");
                temp.put("settings","0");
            }
            else
            {
                temp.put("text",editText1.getText().toString());
                temp.put("settings",editText1.getText().toString());
            }
            itemdata.add(temp);

            temp=new HashMap<String,String>();
            if(editText2.getText().toString().length()==0)
            {
                temp.put("text","0");
                temp.put("settings","0");
            }
            else
            {
                temp.put("text",editText2.getText().toString());
                temp.put("settings",editText2.getText().toString());
            }
            itemdata.add(temp);

            mainActivity.fregment5.updateallsettingitems(itemdata);
            SensoritemsettingActivity.this.setResult(1,intent);
            SensoritemsettingActivity.this.finish();
        }
    }
}
