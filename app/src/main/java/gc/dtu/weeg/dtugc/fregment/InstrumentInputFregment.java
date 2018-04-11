package gc.dtu.weeg.dtugc.fregment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.utils.Constants;
import gc.dtu.weeg.dtugc.utils.InstrumemtItemseetingActivity;

/**
 * Created by Administrator on 2018-03-22.
 */

public class InstrumentInputFregment extends BaseFragment {
    View mView;
    public MainActivity mainActivity=MainActivity.getInstance();
    private ListView mReg2000;
    private listadpater list2000adpater;
    private RelativeLayout mReg1998clickrecv;
    private RelativeLayout mReg1999clickrecv;
    private RelativeLayout mReg2000clickrecv;
    private TextView mBuardTx;
    private TextView mParityTx;
    private TextView mDataTx;
    private TextView mStopTx;
    private TextView mRecodeTmTx;
    private Intent intent;


    String[] listitem={"仪表状态:","仪表类型:","仪表地址:","供电时长(步长:10ms):","Elster press 地址:"};
    ArrayList<Map<String,String>> reg2000list;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrumentfraglayout,null,false);
        mReg2000=mView.findViewById(R.id.tv_ins_2000_list);
        mReg1998clickrecv=mView.findViewById(R.id.but_layout_1998);
        mReg1999clickrecv=mView.findViewById(R.id.but_layout_1999);
        mReg2000clickrecv=mView.findViewById(R.id.but_layout_2000);
        mBuardTx=mView.findViewById(R.id.tv_ins_baud_value);
        mParityTx=mView.findViewById(R.id.tv_ins_paritybit_value);
        mDataTx=mView.findViewById(R.id.tv_ins_stopbit_value);
        mStopTx=mView.findViewById(R.id.tv_ins_stopbit_value);
        mRecodeTmTx=mView.findViewById(R.id.tv_ins_recodegap_value);

        initview();
        initdata();
        return mView;
    }

    private void initview() {
        mReg1998clickrecv.setOnClickListener(new OnMyclicklisternerImp());
        mReg1999clickrecv.setOnClickListener(new OnMyclicklisternerImp());
        mReg2000clickrecv.setOnClickListener(new OnMyclicklisternerImp());
    }

    private void initdata() {
        reg2000list=new ArrayList<>();

        for (String aListitem : listitem) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("lable", aListitem);
            tmp.put("value", "");
            reg2000list.add(tmp);
        }
        list2000adpater = new listadpater();
        mReg2000.setAdapter(list2000adpater);
        mReg2000.setOnItemClickListener(new OnmyOnItemClickListenerlistenerImp());
    }
    private void putdata2000(Intent srcint)
    {
        String [] listdata=null;
        if(reg2000list!=null)
        {
            listdata=new String[reg2000list.size()];
            for(int i=0;i<reg2000list.size();i++)
            {
                listdata[i]=reg2000list.get(i).get("value");
            }
        }
        srcint.putExtra("listdata",listdata);
        srcint.putExtra("regaddr",2000);
    }
    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {

    }
    private class listadpater extends BaseAdapter
    {
        @Override
        public int getCount() {
            return reg2000list.size();
        }

        @Override
        public Object getItem(int position) {
            return reg2000list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView=View.inflate(mainActivity,R.layout.ins2000itemlayout,null);
            }
            Log.d("zl",""+convertView);
            TextView viewlable=convertView.findViewById(R.id.ins_item_lable);
            TextView viewvlaue=convertView.findViewById(R.id.ins_item_value);
            viewlable.setText(reg2000list.get(position).get("lable"));
            viewvlaue.setText(reg2000list.get(position).get("value"));
            return convertView;
        }
    }
    public class OnMyclicklisternerImp implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            intent=new Intent(mainActivity, InstrumemtItemseetingActivity.class);
            int id=v.getId();
            switch (id)
            {
                case R.id.but_layout_1998:
                    intent.putExtra("title","Reg 1998");
                    intent.putExtra("buad",mBuardTx.getText().toString());
                    intent.putExtra("parity",mParityTx.getText().toString());
                    intent.putExtra("databit",mDataTx.getText().toString());
                    intent.putExtra("stopbit",mStopTx.getText().toString());
                    intent.putExtra("regaddr",1998);
                    break;
                case R.id.but_layout_1999:
                    intent.putExtra("title","Reg 1999");
                    intent.putExtra("recordgap",mRecodeTmTx.getText().toString());
                    intent.putExtra("regaddr",1999);
                    break;
                case R.id.but_layout_2000:
                    intent.putExtra("title","Reg 2000");
                    putdata2000(intent);
                    break;
                    default:
                        break;
            }
            startActivityForResult(intent, Constants.InstrumemtsetingFlag);
        }
    }
    public class OnmyOnItemClickListenerlistenerImp implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent=new Intent(mainActivity, InstrumemtItemseetingActivity.class);
            intent.putExtra("title","Reg 2000");
            putdata2000(intent);
            startActivityForResult(intent, Constants.InstrumemtsetingFlag);
        }
    }
}
