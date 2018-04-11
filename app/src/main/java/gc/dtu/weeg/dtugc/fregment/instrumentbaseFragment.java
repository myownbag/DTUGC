package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
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
}
