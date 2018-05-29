package gc.dtu.weeg.dtugc.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gc.dtu.weeg.dtugc.R;
import gc.dtu.weeg.dtugc.fregment.FrozendataFregment;
import gc.dtu.weeg.dtugc.sqltools.FreezedataSqlHelper;
import gc.dtu.weeg.dtugc.sqltools.MytabCursor;
import gc.dtu.weeg.dtugc.sqltools.MytabOperate;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class FreezeDataDrawChartActivit extends AppCompatActivity {
    public LineChartView mLinercharview;

    public LineChartData data;
    List<PointValue> values;
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private int numberOfLines = 1;
    private int maxNumberOfLines = 1;
    private int numberOfPoints = 0;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeze_data_draw_layout);
        mLinercharview=findViewById(R.id.freese_data_draw_chat);
        getdata("94710004");
        generateData(all);
       // mLinercharview.setViewportCalculationEnabled(false);

      //  resetViewport();
    }

    private void generateData(ArrayList<Map<String, String>> alldata) {
        int len=alldata.size();
        String temp;
        values = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        for(int i=0;i<len;i++)
        {
            temp=alldata.get(i).get("press1");
            if(temp.equals(Constants.SENSOR_DISCONNECT)||temp.equals(Constants.SENSOR_ERROR))
            {
                continue;
            }
            else
            {
                values.add(new PointValue(i,Float.valueOf(temp).floatValue()));
                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[0]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);

//        line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor){
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
                getAxisXLables();
                axisX.setValues(mAxisXValues);
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
                ,-1,3);
        if(all==null)
        {
            Log.d("zl","all=null");
            return;
        }
        int count=all.size();
        int i;
        for(i=0;i<count;i++)
        {
            Log.d("zl",""+i+":"
                    +all.get(i).get("mac")+"  "
                    +all.get(i).get("temp")+"  "
                    +all.get(i).get("press1")+"  "
                    +all.get(i).get("press2")+"  "
                    +all.get(i).get("time")+"\r\n"
            );
        }

    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mLinercharview.getMaximumViewport());
        v.bottom = 0;
        v.top = 300;
        v.left = 0;
        v.right = numberOfPoints - 1;
        mLinercharview.setMaximumViewport(v);
        mLinercharview.setCurrentViewport(v);
    }
    private void getAxisXLables(){
        for (int i = 0; i < values.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(""+i));
        }
    }
}
