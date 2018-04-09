package gc.dtu.weeg.dtugc.fregment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.MainActivity;
import gc.dtu.weeg.dtugc.R;

/**
 * Created by Administrator on 2018-03-22.
 */

public class InstrumentInputFregment extends BaseFragment {
    View mView;
    MainActivity mainActivity=MainActivity.getInstance();
    ListView mReg2000;
    listadpater list2000adpater;
    String[] listitem={"仪表状态:","仪表类型:","仪表地址:","供电时长(步长:10ms):","Elster press 地址:"};
    ArrayList<Map<String,String>> reg2000list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrumentfraglayout,null,false);

        mReg2000=mView.findViewById(R.id.tv_ins_2000_list);

        initdata();
        return mView;
    }

    private void initdata() {
        reg2000list=new ArrayList<>();

        for(int i=0;i<listitem.length;i++)
        {
            Map<String,String> tmp=new HashMap<>();
            tmp.put("lable",listitem[i]);
            tmp.put("value","");
            reg2000list.add(tmp);
        }
        list2000adpater = new listadpater();
        mReg2000.setAdapter(list2000adpater);
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
}
