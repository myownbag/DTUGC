package gc.dtu.weeg.dtugc.fregment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.CodeFormat;

public class instrumentWorkModeSetFragment extends instrumentbaseFragment {
    View mView;
    Spinner mdevicestatus;
    Spinner mdevicetype;
    EditText maddrET;
    EditText mPowsuptime;
    EditText mElsteraddr;
    ArrayList<Map<String,String>> mdevicestatuslist;
    ArrayList<Map<String,String>> mdevicetypelist;

    String[] mSettings;

    public int[] mCurrentposition={0,0};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrument_workmode_setting_layout,null,false);
        initview();
        return  mView;
    }

    private void initview() {
        initdata();
        mdevicestatus=mView.findViewById(R.id.instrument_device_status_value);
        mdevicetype=mView.findViewById(R.id.instrument_device_type_value);

        maddrET=mView.findViewById(R.id.instrument_device_addr_value);
        mPowsuptime=mView.findViewById(R.id.instrument_device_pow_suply_value);
        mElsteraddr=mView.findViewById(R.id.instrument_device_elster_value);

        setSpinneradpater(mdevicestatus,mdevicestatuslist);
        setSpinneradpater(mdevicetype,mdevicetypelist);

        mdevicestatus.setOnItemSelectedListener(new onSpinnerSelectimp());
        mdevicetype.setOnItemSelectedListener(new onSpinnerSelectimp());

        initsettiing();


    }

    private void initsettiing() {
        for(int i=0;i<mdevicestatuslist.size();i++)
        {
            if(mSettings[0].equals(mdevicestatuslist.get(i).get("items")))
            {
                mdevicestatus.setSelection(i,true);
                mCurrentposition[0]=i;
                break;
            }

        }
        for(int i=0;i<mdevicetypelist.size();i++)
        {
            if(mSettings[1].equals(mdevicetypelist.get(i).get("items")))
            {
                mdevicetype.setSelection(i,true);
                mCurrentposition[1]=i;
                break;
            }

        }
        maddrET.setText(mSettings[2]);
        mPowsuptime.setText(mSettings[3]);
        mElsteraddr.setText(mSettings[4]);
    }

    private void initdata() {
        Map<String,String> tmap=new HashMap<String,String>();
        tmap.put("items","请选择");
        tmap.put("value","请选择");
        mdevicestatuslist=new ArrayList<>();
        mdevicetypelist=new ArrayList<>();

        mdevicestatuslist.add(tmap);
        mdevicetypelist .add(tmap);

        for(int i=0;i<mActivity.baseinfo.length;i++)
        {
            if(mActivity.baseinfo[i][0].equals("2000")==false)
            {
                continue;
            }
            else
            {
                if(mActivity.baseinfo[i][1].equals("1"))
                {
                    Map<String,String> temp=new HashMap<>();
                    temp.put("items",mActivity.baseinfo[i][2]);
                    temp.put("value",mActivity.baseinfo[i][3]);
                    mdevicestatuslist.add(temp);
                }
                else if(mActivity.baseinfo[i][1].equals("2"))
                {
                    Map<String,String> temp=new HashMap<>();
                    temp.put("items",mActivity.baseinfo[i][2]);
                    temp.put("value",mActivity.baseinfo[i][3]);
                    mdevicetypelist.add(temp);
                }
            }

        }
    }
    private ArrayAdapter<String> setSpinneradpater(Spinner spinner,ArrayList<Map<String,String>> arrayList )
    {
        //适配器
        ArrayAdapter<String> arr_adapter;
        String list[]=new String[arrayList.size()];
        for(int i=0;i<arrayList.size();i++)
        {
            list[i]=arrayList.get(i).get("items");
        }
        arr_adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        return  arr_adapter;
    }
    @Override
   public ArrayList<Map<String, String>> OnbutOKPress( byte[] sendbuf) {
        ArrayList<Map<String,String>> list = new ArrayList<>();
//        mdevicestatuslist;
//        mdevicetypelist;
        for(int i=0;i<2;i++)
        {
            if(mCurrentposition[i]==0)
            {
                return  null;
            }
        }
        if(maddrET.getText().length()==0||mPowsuptime.getText().length()==0)
        {
            return null;
        }
        //仪表状态
        list.add(mdevicestatuslist.get(mCurrentposition[0]));
        int temp= Integer.valueOf(mdevicestatuslist.get(mCurrentposition[0]).get("value"));
        ByteBuffer buf;
        buf=ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(temp);
        buf.rewind();
        buf.get(sendbuf,16,1);

        //仪表类型
        list.add(mdevicetypelist.get(mCurrentposition[1]));
        temp= Integer.valueOf(mdevicetypelist.get(mCurrentposition[1]).get("value"));
        buf=ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(temp);
        buf.rewind();
        buf.get(sendbuf,17,2);

        //仪表地址
        String addr=maddrET.getText().toString();
        temp= Integer.valueOf(addr);
        Map<String,String> map=new HashMap<>();
        map.put("items",addr);
        map.put("value",addr);
        list.add(map);
        if(temp>256)
        {
            Toast.makeText(mActivity,"仪表地址不能大于256",Toast.LENGTH_SHORT).show();
            return null;
        }
        buf=ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(temp);
        buf.rewind();
        buf.get(sendbuf,19,1);
        //供电时长
        String powtime=mPowsuptime.getText().toString();
        temp= Integer.valueOf(powtime);
        map=new HashMap<>();
        map.put("items",powtime);
        map.put("value",powtime);
        list.add(map);
        if(temp>5000)
        {
            Toast.makeText(mActivity,"供电时长不能超过 5000",Toast.LENGTH_SHORT).show();
            return null;
        }
        buf=ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(temp);
        buf.rewind();
        buf.get(sendbuf,24,4);
        //Elster Press 地址
        String elster=mElsteraddr.getText().toString();
        if(elster.length()>16)
        {
            Toast.makeText(mActivity,"Elster Press 地址错误",Toast.LENGTH_SHORT).show();
            return null;
        }
        else
        {
            int len=elster.length();
            for(int i=len;i<16;i++)
            {
                elster="0"+elster;
            }
        }
        byte [] elsterbyte=elster.getBytes();
        byte[] hexbyte = CodeFormat.ASCII_To_BCD(elsterbyte,elsterbyte.length);
        map=new HashMap<>();
        map.put("items",elster);
        map.put("value",elster);
        list.add(map);

        buf=ByteBuffer.allocateDirect(8);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(hexbyte);
        buf.rewind();
        buf.get(sendbuf,28,8);

        return list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle temp=getArguments();
        mSettings=temp.getStringArray("listdata");
    }
    private class onSpinnerSelectimp implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int idhere=parent.getId();
            switch(idhere)
            {
                case R.id.instrument_device_status_value:
                    mCurrentposition[0]=position;
                    break;
                case R.id.instrument_device_type_value:
                    mCurrentposition[1]=position;
                    break;

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
