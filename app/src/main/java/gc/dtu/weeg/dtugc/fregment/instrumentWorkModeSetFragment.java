package gc.dtu.weeg.dtugc.fregment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;

public class instrumentWorkModeSetFragment extends instrumentbaseFragment {
    View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrument_workmode_setting_layout,null,false);
        return  mView;
    }


    @Override
   public ArrayList<Map<String, String>> OnbutOKPress( byte[] sendbuf) {
        return null;
    }
}
