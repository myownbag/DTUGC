package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import gc.dtu.weeg.dtugc.R;

public class NbServiceAddrInputActivity extends Activity {
    Button but;
    EditText edtext;
    String requestpage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nb_service_addr_input_layout);
        but=findViewById(R.id.butnbaddrcommite);
        edtext=findViewById(R.id.edtextnbaddripput);

        Intent intent = getIntent();
        requestpage = intent.getStringExtra("requestpage");

        if (requestpage == null) {
            requestpage = "";
        } else {
//            Log.d("zl","NbServiceAddrInputActivity:"+requestpage);
        }
        String addrurl;
        SharedPreferences sp=null;
        sp=getSharedPreferences("User", Context.MODE_PRIVATE);
        if(requestpage.equals("ICCARD"))
        {
            addrurl=sp.getString(Constants.ICCARD_SERVICE_KEY,"");
        }
        else
        {
            addrurl=sp.getString(Constants.NB_SERVICE_KEY,"");
        }

        Log.d("zl","onActivityResult: "+addrurl);
        edtext.setText(addrurl);


        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp;
                sp=NbServiceAddrInputActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                if(requestpage.equals("ICCARD"))
                {
                    edit.putString(Constants.ICCARD_SERVICE_KEY,edtext.getText().toString());
                }
                else
                {
                    edit.putString(Constants.NB_SERVICE_KEY,edtext.getText().toString());
                }

                //edit.putInt("info",1);
                edit.commit();
//                Log.d("zl",sp.getString(Constants.NB_SERVICE_KEY,"未获取"));
                NbServiceAddrInputActivity.this.finish();
            }
        });
    }
}
