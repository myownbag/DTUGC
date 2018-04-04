package gc.dtu.weeg.dtugc.fregment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.ItemSetingActivity;
import gc.dtu.weeg.dtugc.utils.SensoritemsettingActivity;

/**
 * Created by Administrator on 2018-03-22.
 */

public class SensorInputFregment extends Fragment {
    View mView;
    LinearLayout mlayoutpress1;
    LinearLayout mlayoutpress2;
    LinearLayout mlayouttemperature;
    LinearLayout mlayouttime;
    TextView mpressmode1;
    TextView mPress1H;
    TextView mPress1L;
    TextView mpressmode2;
    TextView mPress2H;
    TextView mPress2L;
    TextView mtempmode;
    TextView mtempIn1;
    TextView mtempIn2;
    TextView mtimemode;
    TextView mtime1;
    TextView mtime2;
    ArrayList<Map<String,String>> mdataitem;
    private SharedPreferences sp ;

    //基础数据
    public  String sensorinfo[][]=
            {
                    {"1","无","65534"},
                    {"1","I2C","65533"},
                    {"1","RS485","65532"},
                    {"1","转换模块","65531"},
                    {"1","模拟量量程","65535"},

                    {"2","无","0"},
                    {"2","PT100","1"},
            };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.sensorinputsettingslayout,null);

        mlayoutpress1=mView.findViewById(R.id.sensor_press1set);
        mlayoutpress2=mView.findViewById(R.id.sensor_press2set);
        mlayouttemperature=mView.findViewById(R.id.sensor_temperatureset);
        mlayouttime=mView.findViewById(R.id.sensor_timeset);

        mpressmode1=mView.findViewById(R.id.tv_sensor_type1);
        mpressmode2=mView.findViewById(R.id.tv_sensor_type2);
        mtempmode=mView.findViewById(R.id.tv_sensor_type3);
        mtimemode=mView.findViewById(R.id.tv_sensor_type4);

        mPress1H=mView.findViewById(R.id.tv_sensor_pressvalue1h);
        mPress1L=mView.findViewById(R.id.tv_sensor_pressvalue1l);
        mPress2H=mView.findViewById(R.id.tv_sensor_pressvalue2h);
        mPress2L=mView.findViewById(R.id.tv_sensor_pressvalue2l);
        mtempIn1=mView.findViewById(R.id.tv_sensor_temperatureh);
        mtempIn2=mView.findViewById(R.id.tv_sensor_temperaturel);
        mtime1=mView.findViewById(R.id.tv_sensor_time1);
        mtime2=mView.findViewById(R.id.tv_sensor_time2);
        initview();

        return mView;
    }

    private void initview() {
        mlayoutpress1.setOnClickListener(new OnclicklistenerImp());
        mlayoutpress2.setOnClickListener(new OnclicklistenerImp());
        mlayouttemperature.setOnClickListener(new OnclicklistenerImp());
        mlayouttime.setOnClickListener(new OnclicklistenerImp());
        MainActivity.getInstance().SetonPageSelectedinviewpager(new Oncurrentpageselect());

    }
    private class OnclicklistenerImp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Intent serverIntent = new Intent(MainActivity.getInstance(), SensoritemsettingActivity.class);
           int vid= v.getId();
           switch (vid)
           {
               case R.id.sensor_press1set:
                   serverIntent.putExtra("name","第一路压力");
                   serverIntent.putExtra("position",1);
                   serverIntent.putExtra("item1",mpressmode1.getText().toString());
                   serverIntent.putExtra("item2",mPress1H.getText().toString());
                   serverIntent.putExtra("item3",mPress1L.getText().toString());
//                   Log.d("zl","R.id.sensor_press1set:");
                   break;
               case R.id.sensor_press2set:
                   serverIntent.putExtra("name","第二路压力");
                   serverIntent.putExtra("position",2);
                   serverIntent.putExtra("item1",mpressmode2.getText().toString());
                   serverIntent.putExtra("item2",mPress2H.getText().toString());
                   serverIntent.putExtra("item3",mPress2L.getText().toString());
//                   Log.d("zl","R.id.sensor_press2set:");
                   break;
               case R.id.sensor_temperatureset:
                   serverIntent.putExtra("name","温度");
                   serverIntent.putExtra("position",3);
                   serverIntent.putExtra("item1",mtempmode.getText().toString());
                   serverIntent.putExtra("item2",mtempIn1.getText().toString());
                   serverIntent.putExtra("item3",mtempIn2.getText().toString());
//                   Log.d("zl","R.id.sensor_temperatureset:");
                   break;
               case R.id.sensor_timeset:
                   serverIntent.putExtra("name","时间");
                   serverIntent.putExtra("position",4);
                   serverIntent.putExtra("item1","");
                   serverIntent.putExtra("item2",mtime1.getText().toString());
                   serverIntent.putExtra("item3",mtime2.getText().toString());
//                   Log.d("zl","R.id.sensor_timeset:");
                   break;
                   default:
                       break;
           }
            startActivityForResult(serverIntent, Constants.SensorlsetingFlag);
        }
    }
    private  class Oncurrentpageselect implements MainActivity.OnPageSelectedinviewpager
    {

        @Override
        public void currentviewpager(int position) {
            if(position==4)
            {
                sp = MainActivity.getInstance().getSharedPreferences("User", Context.MODE_PRIVATE);
                int inftshow=sp.getInt("info",-1);
                if(inftshow!=1)
                {
                    Dialog dialog = new AlertDialog.Builder(MainActivity.getInstance()) // 实例化对象
                            .setIcon(R.drawable.i_ve_got_it) 						// 设置显示图片
                            .setTitle("操作提示") 							// 设置显示标题
                            .setMessage("单击条目可以进行设置") 				// 设置显示内容
                            .setPositiveButton("确定", 						// 增加一个确定按钮
                                    new DialogInterface.OnClickListener() {	// 设置操作监听
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) { 			// 单击事件
                                            SharedPreferences.Editor edit = sp.edit();
                                            edit.putInt("info",1);
                                            edit.commit();
                                        }
                                    }).create(); 							// 创建Dialog
                    dialog.show();
                }
//                 Toast.makeText(MainActivity.getInstance(),"单击各个条目进行设置",Toast.LENGTH_SHORT).show();

            }
        }
    }
    public void updateallsettingitems(ArrayList<Map<String,String>> arrayList)
    {
            this.mdataitem=arrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)
        {

        }
    }
}
