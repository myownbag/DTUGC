package gc.dtu.weeg.dtugc.ptz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.ToastUtils;

public class PTZSettingActicity extends Activity {
    Intent intent;
    ImageView imageView;
    int index;
    int set1;
    Spinner spinner;
    Button mButton;
    TextView mPTZLab1;
    TextView mPTZLab2;
    EditText edit1;
    EditText edit2;
    LinearLayout SeletctContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptz_write_activity_layout);
        intent = getIntent();
        index = intent.getIntExtra("index",-1);
        set1 = intent.getIntExtra("set1",-1);


        imageView  = findViewById(R.id.ptz_settings_imgBackItemset);
        spinner = findViewById(R.id.ptz_settings_spiner_ctrl);
        mButton = findViewById(R.id.ptz_settings_ok_button);

       // Log.d("zl,","PTZSettingActicity: index = "+index+" set1 = "+set1);
        initview();
    }

    public void initview()
    {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PTZSettingActicity.this.setResult(100);
                finish();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      int items = spinner.getSelectedItemPosition();
                int items =0;
                String curb;
                String str;
                Object object = spinner.getSelectedItem();
                if(object !=null)
                {
                    str = spinner.getSelectedItem().toString();
                }
                else
                {
                    str = "";
                }

                intent.putExtra("res1",str);
                intent.putExtra("res2",edit1.getText().toString());
                intent.putExtra("res3",edit2.getText().toString());

                if(index == 9)
                {
                    curb = intent.getStringExtra("curb");

                    String set = edit1.getText().toString();
                    int num=0;
                    int pos = curb.indexOf('.');
                    if(pos<0)
                    {

                    }
                    else
                    {
                        curb = curb.substring(pos+1);
                    }
                    num = curb.length();
                    if(pos<0)
                    {
                        if(num == 1)
                        {
                            if(set.indexOf('.')<0)
                            {
                                ToastUtils.showToast(PTZSettingActicity.this,"请预估一位小数");
                                return;
                            }
                            else
                            {
                                set = set.substring(set.indexOf('.')+1);
                                if(set.length()>1)
                                {
                                    ToastUtils.showToast(PTZSettingActicity.this,"只能预估一位小数");
                                    return;
                                }
                            }
                        }
                        else if(num == 2)
                        {
                            if(set.indexOf('.')>=0)
                            {
                                ToastUtils.showToast(PTZSettingActicity.this,"不能有小数");
                                return;
                            }
                            else
                            {
                                set = set.substring(set.indexOf('.')+1);
                                if(set.length()!=2)
                                {
                                    ToastUtils.showToast(PTZSettingActicity.this,"请输入一个两位数");
                                    return;
                                }
                            }
                        }
                    }
                    else
                    {
                        if(set.indexOf('.')<0)
                        {
                            ToastUtils.showToast(PTZSettingActicity.this,"输入不正确");
                            return;
                        }
                        else {
                            set = set.substring(set.indexOf('.')+1);
                            if(set.length()!=(num+1))
                            {
                                ToastUtils.showToast(PTZSettingActicity.this,"输入不正确");
                                return;
                            }
                        }
                    }

                }

                PTZSettingActicity.this.setResult(index,intent);
                PTZSettingActicity.this.finish();

            }
        });
        TextView title;
        SeletctContainer = findViewById(R.id.addr201_APN_Container);
        title = findViewById(R.id.ptz_settings_txt_titles);
        LinearLayout container1 = findViewById(R.id.ptz_settings_edit1_container);
        LinearLayout container2 =findViewById(R.id.ptz_settings_edit2_containers);
        TextView lab = findViewById(R.id.ptz_settings_spiner_lab);
        edit1 = findViewById(R.id.ptz_settings_edit_ctrl1);
        edit2 = findViewById(R.id.ptz_settings_edit_ctrl2);

        mPTZLab1 = findViewById(R.id.ptz_settings_edit_lab1);
        mPTZLab2 = findViewById(R.id.ptz_settings_edit_lab2);

        String settings[];
        ArrayAdapter<String> arrayAdapter;
        switch (index)
        {
            case 1:
                title.setText("工作模式");

                container1.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);

                lab.setText("工作模式");
                settings = new String[Constants.workmodetype.length];
                for(int i=0;i<Constants.workmodetype.length;i++)
                {
                    settings[i] = Constants.workmodetype[i][0];
                }
                arrayAdapter = setSpinneradpater(spinner,settings);
                break;
            case 2:
                title.setText("脉冲模式");
                container1.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                lab.setText("脉冲模式");
                settings = new String[Constants.plusemode.length];
                for(int i=0;i<Constants.plusemode.length;i++)
                {
                    settings[i] = Constants.plusemode[i][0];
                }
                arrayAdapter = setSpinneradpater(spinner,settings);
                break;
            case 3:
                title.setText("传感模式");
                container1.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                lab.setText("传感模式");
                settings = new String[Constants.plusedevice.length];
                for(int i=0;i<Constants.plusedevice.length;i++)
                {
                    settings[i] = Constants.plusedevice[i][0];
                }
                arrayAdapter = setSpinneradpater(spinner,settings);
                break;
            case 4:
                title.setText("数据脉冲");
                container1.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                lab.setText("数据脉冲");
                settings = new String[Constants.plusedata.length];
                for(int i=0;i<Constants.plusedata.length;i++)
                {
                    settings[i] = Constants.plusedata[i][0];
                }
                arrayAdapter = setSpinneradpater(spinner,settings);
                break;
            case 5:
                title.setText("压感模式");
//                container1.setVisibility(View.VISIBLE);
//                container2.setVisibility(View.VISIBLE);
                lab.setText("压感模式");

                String setstr1;
                String setstr2;
                String curb;

                setstr1 = intent.getStringExtra("set2");
                setstr2 = intent.getStringExtra("set3");
                curb = intent.getStringExtra("curb");
                edit1.setText(setstr1);
                edit2.setText(setstr2);

                index = intent.getIntExtra("index",-1);
                set1 = intent.getIntExtra("set1",-1);


                int intcurb = 0;
                for(String temp[]:Constants.workmodetype)
                {
                    if(temp[0].equals(curb))
                    {
                        intcurb = Integer.parseInt(temp[1]);
                        break;
                    }
                }
                if(intcurb == 1)
                {
                    settings = new String[1];
                    settings[0] = Constants.pressmode[1][0];
                }
                else
                {
                    settings = new String[2];
                    for(int i=2;i<Constants.pressmode.length;i++)
                    {
                        settings[i-2] = Constants.pressmode[i][0];
                    }
                }
                if(intcurb == 1)
                {
                    mPTZLab2.setVisibility(View.INVISIBLE);
                    edit2.setVisibility(View.INVISIBLE);
                    mPTZLab1.setText("常量");
                }
                else if(intcurb == 2){
                    mPTZLab2.setVisibility(View.VISIBLE);
                    edit2.setVisibility(View.VISIBLE);

                    mPTZLab1.setText("上限(Kp)");
                    mPTZLab2.setText("下限(Kp)");
                }

                arrayAdapter = setSpinneradpater(spinner,settings);
                break;
            case 6:
                title.setText("温度模式");
                lab.setText("温度模式");

                setstr1 = intent.getStringExtra("set2");
                setstr2 = intent.getStringExtra("set3");
                curb = intent.getStringExtra("curb");
                edit1.setText(setstr1);
                edit2.setText(setstr2);

                index = intent.getIntExtra("index",-1);
                set1 = intent.getIntExtra("set1",-1);

                settings =new String[3];
                for(int i=1;i<Constants.temperaturemode.length;i++)
                {
                    settings[i-1] = Constants.temperaturemode[i][0];
                }
                arrayAdapter = setSpinneradpater(spinner,settings);
                mPTZLab1.setText("上限(℃)");
                mPTZLab2.setText("下限(℃)");
                break;
            case 7:
                title.setText("压缩因子");
                lab.setText("模式");
                container2.setVisibility(View.GONE);

                setstr1 = intent.getStringExtra("set2");
                setstr2 = intent.getStringExtra("set3");
                curb = intent.getStringExtra("curb");
                edit1.setText(setstr1);
                edit2.setText(setstr2);

                index = intent.getIntExtra("index",-1);
                set1 = intent.getIntExtra("set1",-1);

                settings =new String[1];
                intcurb = 0;
                for(String temp[]:Constants.workmodetype)
                {
                    if(temp[0].equals(curb))
                    {
                        intcurb = Integer.parseInt(temp[1]);
                        break;
                    }
                }
                if(intcurb ==3)
                {
                    settings[0] = Constants.compressibilityfactormode[1][0];
                }
                else
                {
                    settings[0] = Constants.compressibilityfactormode[0][0];
                }
                arrayAdapter = setSpinneradpater(spinner,settings);
                mPTZLab1.setText("参数");

                break;
            case 8:
                title.setText("扫描时间");
                SeletctContainer.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                mPTZLab1.setText("扫描时间");
                break;
            case 9:
                curb = intent.getStringExtra("curb");
                title.setText("数据脉冲:"+curb);
                SeletctContainer.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                mPTZLab1.setText("初始读数");
                break;
        }
    }

    private ArrayAdapter<String> setSpinneradpater(Spinner spinner, String items[] )
    {
        //适配器
        ArrayAdapter<String> arr_adapter;
        String list[]=items;

        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        return  arr_adapter;
    }
}
