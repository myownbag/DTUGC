package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import gc.dtu.weeg.dtugc.R;


public class SensoritemsettingActivity extends Activity {
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensoritemsettinglayout);
        intent=getIntent();
        initview();
    }

    private void initview() {
//        serverIntent.putExtra("name","第二路压力");
//        serverIntent.putExtra("position",2);
//        serverIntent.putExtra("item1",mpressmode2.getText().toString());
//        serverIntent.putExtra("item2",mPress2H.getText().toString());
//        serverIntent.putExtra("item3",mPress2L.getText().toString());
            int position=intent.getIntExtra("position",-1);
    }
}
