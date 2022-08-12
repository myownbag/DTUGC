package gc.dtu.weeg.dtugc.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import gc.dtu.weeg.dtugc.R;

public class DeviceSelectedDlg extends Dialog {

    Context m_Activity;
    Button but;
    RadioGroup radioGroup;
    OnSelectedOKEvent onSelectedOKEvent;
    int mIO;

    public DeviceSelectedDlg(@NonNull Context context) {
        super(context);
        m_Activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_file_select);
        but=findViewById(R.id.devices_selected_ok);
        radioGroup=findViewById(R.id.devices_selected_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.devices_dtugc)
                {
                    mIO=0;
                }
                else if(checkedId==R.id.devices_msugc)
                {
                    mIO=1;
                }
                else if(checkedId == R.id.devices_sgugc)
                {
                    mIO=2;
                }
            }
        });
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedOKEvent!=null)
                {
                    onSelectedOKEvent.OnDevicesSelected(mIO);
                }
                DeviceSelectedDlg.this.dismiss();
            }
        });
        setTitle("请选择");
//        Log.d("zl","MyDlg onCreate");
    }

    public interface  OnSelectedOKEvent {
        void OnDevicesSelected(int devicesID);
    }
    public void SetOnDevicesSelectedListerner(OnSelectedOKEvent  li){
        onSelectedOKEvent = li;
    }


}
