package gc.dtu.weeg.dtugc.myview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import gc.dtu.weeg.dtugc.MainActivity;

public class MyDlg extends Dialog {
    MainActivity mainActivity;
    public MyDlg(@NonNull Context context) {
       // mainActivity= (MainActivity) context;
        super(context);
    }
}
