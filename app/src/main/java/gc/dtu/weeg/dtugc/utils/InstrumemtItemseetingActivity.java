package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gc.dtu.weeg.dtugc.R;

public class InstrumemtItemseetingActivity extends Activity {

    private TextView mtltie;
    private ImageView mbutback;

    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instrumemt_itemset_layout);
        mtltie=findViewById(R.id.txt_titles_insitem);
        mbutback=findViewById(R.id.imgBack_insitem);
        initview();
        initdata();
    }

    private void initview() {
        mbutback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstrumemtItemseetingActivity.this.finish();
            }
        });
    }
    private void initdata()
    {
        intent=getIntent();
        String titlehere=intent.getStringExtra("title");
        mtltie.setText(titlehere);
    }
}
