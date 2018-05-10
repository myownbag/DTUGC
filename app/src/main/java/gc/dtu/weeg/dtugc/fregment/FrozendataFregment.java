package gc.dtu.weeg.dtugc.fregment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;

/**
 * Created by Administrator on 2018-03-22.
 */

public class FrozendataFregment extends BaseFragment implements View.OnClickListener {
    View mView;
    public Button mBut;
    public Spinner mSpiner;
    String [] mylist={"1","2","3","4","5","全部"};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        mView = inflater.inflate(R.layout.freeze_data_layout, container, false);
        initView();
        return  mView;

    }

    private void initView() {
        mBut=mView.findViewById(R.id.freeze_but);
        mSpiner=mView.findViewById(R.id.freeze_selc);
        setSpinneradpater(mSpiner,mylist);
    }

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {

    }

    private void setSpinneradpater(Spinner spinner, String[] list )
    {
        int i=0;
        ArrayList<String> arrayList;
        arrayList=new ArrayList<>();
        for(i=0;i<list.length;i++)
        {
            arrayList.add(list[i]);
        }
        //适配器
        ArrayAdapter<String> arr_adapter;
        Activity activity=getActivity();
        if(activity!=null)
        {
            arr_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(arr_adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if(mSpiner.getSelectedItemPosition()==(mylist.length-1))
        {
//            AlertDialog dialog = builder.setTitle(R.string.please_choose)
//                    .setSingleChoiceItems(itemStrs, chooseItemIndex, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //...
//                                }
//                            }
//                    )
//                    .show();
        }

    }
}
