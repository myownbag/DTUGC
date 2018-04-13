package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.utils.InstrumemtItemseetingActivity;

public abstract class instrumentbaseFragment extends Fragment {
    protected InstrumemtItemseetingActivity mActivity;
    abstract public ArrayList<Map<String,String>> OnbutOKPress(byte [] sendbuf);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=(InstrumemtItemseetingActivity) context;
    }
    public Map<String,String> findcursetting(int position,ArrayList<Map<String,String>> list,String keyname,String keyvalue){
        Map<String,String> map=new HashMap<>();
        if(list==null)
        {
            return null;
        }
        for(int i=0;i<list.size();i++)
        {

        }
        return map;
    }
}
