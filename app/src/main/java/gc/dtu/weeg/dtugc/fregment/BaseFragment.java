package gc.dtu.weeg.dtugc.fregment;

import android.support.v4.app.Fragment;

import java.text.ParseException;

public abstract class BaseFragment extends Fragment {
    public Boolean mIsatart=false;
    public boolean m_dlgcancled=false;
    abstract public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) throws ParseException;
    public void Oncurrentpageselect(int index)
    {

    }
    public void Ondlgcancled()
    {
        mIsatart=false;
    }
}
