package gc.dtu.weeg.dtugc.fregment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.InstrumemtItemseetingActivity;


public class instrumentComSetFragment extends instrumentbaseFragment {
    View mView;
    Spinner mBuad;
    Spinner mParity;
    Spinner mDatabit;
    Spinner mStopbit;


    ArrayAdapter<String> mBuadadpater;
    ArrayAdapter<String> mParityadpater;
    ArrayAdapter<String> mDatabitadpater;
    ArrayAdapter<String> mStopbitadpater;

    ArrayList<Map<String,String>> mBuadlist;
    ArrayList<Map<String,String>> mParitylist;
    ArrayList<Map<String,String>> mDatabitlist;
    ArrayList<Map<String,String>> mStopbitlist;
    String[] mSettings;
    @Override
   public ArrayList<Map<String, String>> OnbutOKPress(byte[] sendbuf) {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrument_com_setting_fragment,null,false); //instrument_workmode_setting_layout

        mBuad=mView.findViewById(R.id.ins_fragment_buad_select);
        mParity=mView.findViewById(R.id.ins_fragment_parity_select);
        mDatabit=mView.findViewById(R.id.ins_fragment_databit_select);
        mStopbit=mView.findViewById(R.id.ins_fragment_stopbit_select);
        initview();
        return mView;
    }

    private void initdata() {
       Map<String,String> tmap=new HashMap<String,String>();
        tmap.put("items","请选择");
        tmap.put("value","请选择");
        mBuadlist=new ArrayList<>();
        mParitylist=new ArrayList<>();
        mDatabitlist=new ArrayList<>();
        mStopbitlist=new ArrayList<>();
        mBuadlist.add(tmap);
        mParitylist .add(tmap);
        mDatabitlist.add(tmap);
        mStopbitlist.add(tmap);
        for(int i=0;i<mActivity.baseinfo.length;i++)
        {
            if(mActivity.baseinfo[i][0].equals("1998")==false)
            {
                continue;
            }
            else
            {
                if(mActivity.baseinfo[i][1].equals("1"))
                {
                    Map<String,String> temp=new HashMap<>();
                    temp.put("items",mActivity.baseinfo[i][3]);
                    temp.put("value",mActivity.baseinfo[i][2]);
                    mBuadlist.add(temp);
                }
                else if(mActivity.baseinfo[i][1].equals("2"))
                {
                    Map<String,String> temp=new HashMap<>();
                    temp.put("items",mActivity.baseinfo[i][3]);
                    temp.put("value",mActivity.baseinfo[i][2]);
                    mParitylist.add(temp);
                }
                else if(mActivity.baseinfo[i][1].equals("3"))
                {
                    Map<String,String> temp=new HashMap<>();
                    temp.put("items",mActivity.baseinfo[i][3]);
                    temp.put("value",mActivity.baseinfo[i][2]);
                    mDatabitlist.add(temp);
                }
                else if(mActivity.baseinfo[i][1].equals("4"))
                {
                    Map<String,String> temp=new HashMap<>();
                    temp.put("items",mActivity.baseinfo[i][3]);
                    temp.put("value",mActivity.baseinfo[i][2]);
                    mStopbitlist.add(temp);
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
    private void initview() {
        initdata();
        mBuadadpater=setSpinneradpater(mBuad,mBuadlist);
        mParityadpater=setSpinneradpater(mParity,mParitylist);
        mDatabitadpater=setSpinneradpater(mDatabit,mDatabitlist);
        mStopbitadpater=setSpinneradpater(mStopbit,mStopbitlist);
        for(int i=0;i<mBuadlist.size();i++)
        {
            if(mSettings[0].equals(mBuadlist.get(i).get("items")))
            {
                mBuad.setSelection(i,true);
                break;
            }

        }
        for(int i=0;i<mParitylist.size();i++)
        {
            if(mSettings[1].equals(mParitylist.get(i).get("items")))
            {
                mParity.setSelection(i,true);
                break;
            }

        }
        for(int i=0;i<mDatabitlist.size();i++)
        {
            if(mSettings[2].equals(mDatabitlist.get(i).get("items")))
            {
                mDatabit.setSelection(i,true);
                break;
            }

        }
        for(int i=0;i<mStopbitlist.size();i++)
        {
            if(mSettings[3].equals(mStopbitlist.get(i).get("items")))
            {
                mStopbit.setSelection(i,true);
                break;
            }
        }

        mBuad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(mBuadlist.get(0).get("items").equals("请选择"))
//                {
//                    mBuadlist.remove(0);
//                    mBuadadpater=setSpinneradpater(mBuad,mBuadlist);
//                    mBuadadpater.notifyDataSetChanged();
//                    mBuad.setSelection(position-1,true);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle temp=getArguments();
        if(temp!=null)
        {
            mSettings=temp.getStringArray("settings");
        }
        else
        {
            mSettings=new String[4];
            for(int i=0;i<4;i++)
            {
                mSettings[i]="";
            }
        }
    }
}
