package gc.dtu.weeg.dtugc.fregment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;

public class instrumentWorkModeSetFragment extends instrumentbaseFragment {
    View mView;
    Spinner mdevicestatus;
    Spinner mdevicetype;
    ArrayList<Map<String,String>> mdevicestatuslist;
    ArrayList<Map<String,String>> mdevicetypelist;

    ArrayAdapter<String> mdevicestatusad;
    ArrayAdapter<String> mdevicetypead;

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
        setSpinneradpater(mdevicestatus,mdevicestatuslist);
        setSpinneradpater(mdevicetype,mdevicetypelist);
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
        return null;
    }
}
