package gc.dtu.weeg.dtugc.fregment;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
    abstract public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1);
    public void Oncurrentpageselect(int index)
    {

    }

}
