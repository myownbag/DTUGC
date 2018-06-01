package gc.dtu.weeg.dtugc.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.fregment.FrozendataFregment;
import gc.dtu.weeg.dtugc.sqltools.FreezedataSqlHelper;
import gc.dtu.weeg.dtugc.sqltools.MytabCursor;
import gc.dtu.weeg.dtugc.sqltools.MytabOperate;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import static lecho.lib.hellocharts.gesture.ZoomType.HORIZONTAL;

public class FreezeDataDrawChartActivit extends AppCompatActivity {
    public LineChartView mLinercharview;
    public android.support.v7.app.ActionBar actionBar;

    public LineChartData data;
    List<PointValue> values;
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private int numberOfLines = 1;
    private int maxNumberOfLines = 1;
    private int numberOfPoints = 0;
    public int mMaxValue=0;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;


    public FreezedataSqlHelper helper = null ;		 //mysqlhelper				// 数据库操作
    private MytabOperate mtab = null ;
    public ArrayList<Map<String,String>> all;

    Intent intent;
    public String mstraddr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeze_data_draw_layout);
        mLinercharview=findViewById(R.id.freese_data_draw_chat);
        mLinercharview.setOnValueTouchListener(new ValueTouchListener());
        actionBar= getSupportActionBar();
        actionBar.setTitle("压力1");

        mLinercharview.setViewportCalculationEnabled(false);
//        mLinercharview.setZoomType(ZoomType.HORIZONTAL);
//        Log.d("zl","getMaxZoom"+mLinercharview.getMaxZoom());
//        mLinercharview.getMaxZoom();
        mLinercharview.setMaxZoom(100);
        intent=getIntent();
        mstraddr= intent.getStringExtra(Constants.DEVICEID);
        //Log.d("zl","FreezeDataDrawChartActivit onCreate: "+mstraddr);
        getdata(mstraddr);

        generateData(all,Constants.DEVICECHART1);
        resetViewport();
    }

    private void generateData(ArrayList<Map<String, String>> alldata,String key) {
        int len=alldata.size();
        String temp;
        values = new ArrayList<>();
        mAxisXValues.clear();
        List<Line> lines = new ArrayList<>();
        int index=0;
        for(int i=0;i<len;i++)
        {
            temp=alldata.get(i).get(key);
            if (!temp.equals(Constants.SENSOR_DISCONNECT) && !temp.equals(Constants.SENSOR_ERROR)) {

                float tempfloat= Float.valueOf(temp);
                if(tempfloat>mMaxValue)
                {
                    mMaxValue=(int) tempfloat;
                }
                values.add(new PointValue(index,tempfloat));
                mAxisXValues.add(new AxisValue(index).setLabel(alldata.get(i).get("time")));
                index++;
            }
        }
        if(values.size()==0)
        {
            Toast.makeText(this,"没有有效数据",Toast.LENGTH_SHORT).show();
        }
        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);

        //line.setMaxLabelChars



//        line.setHasGradientToTransparent(hasGradientToTransparent);
//        if (pointsHaveDifferentColor){
//            line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//        }
        lines.add(line);
        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("采样时间");
                axisY.setName("压力值");
//                getAxisXLables();
                axisX.setValues(mAxisXValues);
               // axisX.setHasTiltedLabels(true);  斜体
                axisX.setMaxLabelChars(5); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
                axisX.setHasLines(true); //x 轴分割线
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mLinercharview.setLineChartData(data);
        numberOfPoints=values.size();
    }



    private void getdata(String deviceaddr) {
        helper = new FreezedataSqlHelper(this, Constants.TABLENAME1
                ,null,1);  //this.helper = new MyDatabaseHelper(this) ;
        MytabCursor cur = new MytabCursor(	// 实例化查询
                // 取得SQLiteDatabase对象
                helper.getReadableDatabase()) ;
         all  =      cur.find1(deviceaddr,
                "ASC"
                ,-1,480);
        if(all==null)
        {
            Log.d("zl","all=null");
            return;
        }
        int count=all.size();
//        int i;
//        for(i=0;i<count;i++)
//        {
//            Log.d("zl",""+i+":"
//                    +all.get(i).get("mac")+"  "
//                    +all.get(i).get("temp")+"  "
//                    +all.get(i).get("press1")+"  "
//                    +all.get(i).get("press2")+"  "
//                    +all.get(i).get("time")+"\r\n"
//            );
//        }

    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mLinercharview.getMaximumViewport());

        int grade=0;
        int temp;
        int temp1=1;
        boolean flag=true;
        if(mMaxValue<0)
        {
            temp=0-mMaxValue;
        }
        else
        {
            temp=mMaxValue;
        }
        while (flag)
        {
            if(temp>=10)
            {
                temp/=10;
                grade++;
            }
            else
            {
                flag=false;
            }
        }
        if(mMaxValue<0)
        {
            temp=0-temp;
        }
        while (grade>0)
        {
            temp*=10;
            temp1*=10;
            grade--;
        }
        v.bottom = 0;
        v.top = temp+temp1;
        v.left = 0;
        v.right =  numberOfPoints;  //numberOfPoints;
        mLinercharview.setMaximumViewport(v);
        v.bottom = 0;
        v.top = temp+temp1;
        v.left = 0;
        v.right = 5;
        mLinercharview.setCurrentViewport(v);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id)
        {
            case R.id.munu_press1:
                actionBar.setTitle("压力1");
                generateData(all,Constants.DEVICECHART1);
                resetViewport();
//                prepareDataAnimation();
//                mLinercharview.startDataAnimation();
                break;
            case R.id.munu_press2:
                actionBar.setTitle("压力2");
                generateData(all,Constants.DEVICECHART2);
                resetViewport();
//                prepareDataAnimation();
//                mLinercharview.startDataAnimation();
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareDataAnimation() {
        for (Line line : data.getLines()) {
            for (PointValue value : line.getValues()) {
                // Here I modify target only for Y values but it is OK to modify X targets as well.
                value.setTarget(value.getX(), (float) Math.random() * 100);
            }
        }
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener{

        @Override
        public void onValueSelected(int i, int i1, PointValue pointValue) {

                Log.d("zl","i="+i+"  "+"i1="+i1);
               String temp=new String(mAxisXValues.get(i1).getLabelAsChars());
                Toast.makeText(FreezeDataDrawChartActivit.this
                        ,"时间："+temp+"  "+"压力值:"+pointValue.getY(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
