package gc.dtu.weeg.dtugc.fregment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.InstrumemtItemseetingActivity;


public class instrumentComSetFragment extends instrumentbaseFragment {
    View mView;
    Spinner mBuad;
    Spinner mParity;
    Spinner mDatabit;
    Spinner mStopbit;

    ArrayList<String> mBuadlist;
    ArrayList<String> mParitylist;
    ArrayList<String> mDatabitlist;
    ArrayList<String> mStopbitlist;
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

    private void initview() {
        mBuadlist=new ArrayList<>();
        for(int i=0;i<mActivity.baseinfo.length;i++)
        {
            if(mActivity.baseinfo[i][0].equals("1998")==false)
            {
                continue;
            }
            else if(mActivity.baseinfo[i][1].equals("1")==false)
            {
                continue;
            }
            else
            {
                mBuadlist.add(mActivity.baseinfo[i][3]);
            }
        }
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
