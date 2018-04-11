package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;

public class instrumenttimegapFragment extends instrumentbaseFragment {
    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrumenttimegap,null,false);

        initview();
        initdata();
        return mView;
    }

    private void initview() {
    }
    private void initdata() {
    }
    @Override
   public ArrayList<Map<String, String>> OnbutOKPress( byte[] sendbuf) {
        return null;
    }
}
