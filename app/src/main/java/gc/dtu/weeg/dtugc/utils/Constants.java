package gc.dtu.weeg.dtugc.utils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class Constants {
    //超时秒数默认60秒
    public static final int  TimeOutSecond = 18000000;

    public static final int  LocalsetingFlag = 800;
    public static final int  SensorlsetingFlag = 801;
    public static final int  InstrumemtsetingFlag = 802;
    public static final int  NBINPUTSETTINGFLAG = 803;

    public static final int  GCOPENTIME = 8000;
    public static final int  GCCLOSETIME = 6000;
    public static final int  G6OPENTIME = 15000;
    public static final int  G6CLOSETIME = 9000;
    //SQL

    public static final String TABLENAME1 = "freezedatasensor" ;			// 数据表名称
    public static final String TABLENAME2 = "freezedatainstrument" ;

    public static final String COLUMN_TEM="temperature";
    public static final String COLUMN_MAC="mac";
    public static final String COLUMN_PRESS1="press1";
    public static final String COLUMN_PRESS2="press2";

    public static final String COLUMN_INS1="instrument1";
    public static final String COLUMN_INS2="instrument2";

    public static final String COLUMN_DATE="date";

    public static final String DATE_FORMAT="yyyyMMdd HHmmss";

    /**
     * 时间字段的降序，采用date函数比较
     */
    public static final String ORDER_BY="date("+COLUMN_DATE+") desc";
    //----------------------------------------------------
    //本地设置详情
    //传感器状态
    public static final String SENSOR_ERROR="传感器故障";
    public static final String SENSOR_DISCONNECT="未连接";

    //NB service
    public static final String NB_SERVICE_KEY="service";
    public static final String NB_Service_END="/WeegNB/registerDevice?verifyCode=%s&serial=%s";
    public static final String NB_Service_END1="/WeegNB/setInfoDevice?deviceId=%s&type=%s&deviceType=DTU&model=WeegDTU";


    //历史数据业务标记
    public static final int NB_FRESONDATA_KEY_BLOCK_FINISHED=1;
    public static final int NB_FRESONDATA_KEY_TASKFINISHED_FINISHED=2;

    public static final int NUMOFGAGE=20;

    //图表常量关键字
    public static final String DEVICEID = "deviceid";
    public static final String DEVICECHART1 = "press1";
    public static final String DEVICECHART2 = "press2";
}
