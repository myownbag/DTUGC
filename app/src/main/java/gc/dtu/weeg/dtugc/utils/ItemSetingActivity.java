package gc.dtu.weeg.dtugc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;

/**
 * Created by Administrator on 2018-03-27.
 */

public class ItemSetingActivity extends Activity {
    EditText currentshow;
    KeyListener keyListener;
    Button   mybutton;
    Intent intent;
    TextView mtextaddr;
    TextView mtextaddrname;
    Spinner spinner;
    MainActivity mainActivity;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    boolean isSpinnerFirst = true ;
    RelativeLayout spinerconter;
    int mposition=0;
    int spinerposition=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curentsettinglayout);
//
//        serverIntent.putExtra("addrs",registername);
//        serverIntent.putExtra("name",registersetting);
//        serverIntent.putExtra("settings",registerconnet);
        String temp;
        currentshow=findViewById(R.id.currentset_item_addrsettings);
        keyListener=currentshow.getKeyListener();
        mybutton =findViewById(R.id.tv_itemsettings_btn_write);
        mtextaddr=findViewById(R.id.currentset_item_addr);
        mtextaddrname=findViewById(R.id.currentset_item_addrname);
        mybutton.setOnClickListener(new buttonclickimp());
        intent=getIntent();
        temp=intent.getStringExtra("addrs");
        mtextaddr.setText(temp);
        temp=intent.getStringExtra("name");
        mtextaddrname.setText(temp);
        temp=intent.getStringExtra("settings");
        if(temp!=null)
        {
            currentshow.setText(temp);
        }
        spinner=findViewById(R.id.currentset_item_addrspiner);
        spinerconter=findViewById(R.id.selectitemspiner);
        mainActivity=MainActivity.getInstance();

        initview();
    }


    private void initview() {
        int i=0;
        int j=0;
        String temp=mtextaddr.getText().toString();
        for( i=0;i<mainActivity.fregment4.baseinfo.length;i++)
        {
            if(temp.equals(mainActivity.fregment4.baseinfo[i][0]))
            {
                mposition=i;
                if("L".equals(mainActivity.fregment4.baseinfo[i][3]))
                {
                    spinerconter.setVisibility(View.VISIBLE);
                    currentshow.setFocusable(false);
                    currentshow.setFocusableInTouchMode(false);
                    data_list=new ArrayList<String>();
                    for(j=0;j<mainActivity.fregment4.registerinfosel.length;j++)
                    {
                        if(temp.equals(mainActivity.fregment4.registerinfosel[j][0]))
                        {
                            data_list.add(mainActivity.fregment4.registerinfosel[j][1]);
                        }
                    }
                }
                else if("T".equals(mainActivity.fregment4.baseinfo[i][3]))
                {
                    spinerconter.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(this,"未知类型",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        if(spinerconter.getVisibility()==View.VISIBLE)
        {
            //适配器
            arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(arr_adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isSpinnerFirst) {
                        //第一次初始化spinner时，不显示默认被选择的第一项即可
                        view.setVisibility(View.INVISIBLE) ;
                        isSpinnerFirst = false ;
                    }
                    else
                    {
                        currentshow.setText(data_list.get(position));
                        spinerposition=position;
//                        intent.putExtra("name",data_list.get(position));
//                        intent.putExtra("addrs",mposition);
//                        ItemSetingActivity.this.setResult(position,intent);
                    }
                   // Log.d("zl","position:"+position+"  "+"id:"+id);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
//        currentshow.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                return false;
//            }
//        });
 //       currentshow.addTextChangedListener(new MyEditTextChangeListener());
    }

    private class buttonclickimp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
                        String temp=currentshow.getText().toString();
                        Log.d("zl","temp:"+temp+" "+"position:"+mposition);
                        intent.putExtra("name",temp);
                        intent.putExtra("addrs",mposition);
                        ItemSetingActivity.this.setResult(spinerposition,intent);
        }
    }
    private class MyEditTextChangeListener implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d("zl","before:"+currentshow.getText());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("zl","after:"+currentshow.getText());
        }
    }
}
