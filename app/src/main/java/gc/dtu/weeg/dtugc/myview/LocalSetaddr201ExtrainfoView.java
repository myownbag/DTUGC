package gc.dtu.weeg.dtugc.myview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import gc.dtu.weeg.dtugc.R;

public class LocalSetaddr201ExtrainfoView extends LinearLayout {
    String m198Modul="";
    Context mParent=null;
    SettingInterface settingInterface=null;
    private  View view201show;

    EditText EditviewApn;
    EditText EditviewUsers;
    EditText EditviewPSWD;

    String mAPN;
    String mUSERS;
    String mPWSD;

    public LocalSetaddr201ExtrainfoView(Context context,String addr198Modul) {
        super(context);
        mParent = context;
        m198Modul = addr198Modul;



        view201show = View.inflate(mParent, R.layout.localsetting_addr201_layout,null);
        mAPN="";
        mUSERS="";
        mPWSD="";
        EditviewApn=view201show.findViewById(R.id.addr202_APN);
        EditviewUsers=view201show.findViewById(R.id.addr202_USERS);
        EditviewPSWD =view201show.findViewById(R.id.addr202_PASSWORDS);

        EditviewApn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // Log.d("zl",charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d("zl",editable.toString());
                LocalSetaddr201ExtrainfoView.this.mAPN= editable.toString();
                setContent();
            }
        });

        EditviewUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LocalSetaddr201ExtrainfoView.this.mUSERS= editable.toString();
                setContent();
            }
        });

        EditviewPSWD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LocalSetaddr201ExtrainfoView.this.mPWSD= editable.toString();
                setContent();
            }
        });

        addView(view201show);
        LinearLayout lineatView;
        if(m198Modul.equals("BC95")||m198Modul.equals("M72"))
        {

            lineatView = view201show.findViewById(R.id.addr201_USER_Container);
            lineatView.setVisibility(View.GONE);

            lineatView = view201show.findViewById(R.id.addr201_PSWD_Container);
            lineatView.setVisibility(View.GONE);
        }
        else if(m198Modul.equals("MC323"))
        {
            lineatView = view201show.findViewById(R.id.addr201_APN_Container);
            lineatView.setVisibility(View.GONE);
        }
        else if(m198Modul.equals("EC20"))
        {

        }
        else
        {
            view201show.setVisibility(View.GONE);
        }
    }
    public void setContent()
    {
        if(settingInterface==null)
        {
            return;
        }
        switch (m198Modul) {
            case "BC95":
            case "M72":
                settingInterface.OncurSetting(mAPN);
                break;
            case "MC323":
                settingInterface.OncurSetting(mUSERS + "," + mPWSD);
                break;
            case "EC20":
                settingInterface.OncurSetting(mAPN + "," + mUSERS + "," + mPWSD);
                break;
        }
    }

    public interface SettingInterface
    {
         void OncurSetting(String set);
    }

    public void setOncursettingChanged(SettingInterface si)
    {
        settingInterface=si;
    }

}
