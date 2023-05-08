package gc.dtu.weeg.dtugc.utils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class Constants {
    //快捷键：大小写转换 Ctrl+Shift+U
    //超时秒数默认60秒
    public static final int  TimeOutSecond = 18000000;

    public static final int  LocalsetingFlag = 800;
    public static final int  SensorlsetingFlag = 801;
    public static final int  InstrumemtsetingFlag = 802;
    public static final int  NBINPUTSETTINGFLAG = 803;
    public static final int  EXTERNEDALARMINPUTSETTINGFLAG = 805;

    public static final int PTZSETTINGFLAG1 = 801;
    public static final int PTZSETTINGFLAG2 = 802;
    public static final int PTZSETTINGFLAG3 = 803;
    public static final int PTZSETTINGFLAG4 = 804;
    public static final int PTZSETTINGFLAG5 = 805;
    public static final int PTZSETTINGFLAG6 = 806;
    public static final int PTZSETTINGFLAG7 = 807;
    public static final int PTZSETTINGFLAG8 = 808;
    public static final int PTZSETTINGFLAG9 = 809;
    public static final int PTZSETTINGFLAG10 = 810;

    public static final int  GCOPENTIME = 8000;
    public static final int  GCCLOSETIME = 6000;
    public static final int  G6OPENTIME = 15000;
    public static final int  G6CLOSETIME = 9000;


    public static final int  MYDIF_MAXOPEN = 18000;
    public static final int  MYDJF_MAXCLOSE = 18000;
    public static final int  MYDJF_OPENDELY = 2000;
    public static final int  MYDJF_CLOSEDELY = 1000;
    //SQL

    public static final String TABLENAME1 = "freezedatasensor" ;			// 数据表名称
    public static final String TABLENAME2 = "freezedatainstrument" ;
    public static final String TABLENAME3 = "GateRecorders" ;

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

    //IC CARD  service
    public static final String ICCARD_SERVICE_KEY="iccardservice";
    //IC EXTERNED ALARM service
    public static final String EXALARM_SERVICE_KEY="exalarmservice";
    //历史数据业务标记
    public static final int NB_FRESONDATA_KEY_BLOCK_FINISHED=1;
    public static final int NB_FRESONDATA_KEY_TASKFINISHED_FINISHED=2;

    //固件升级业务
    public static final int FIRMWARE_CONVERT_SUCCESS=1;
    public static final int FIRMWARE_CONVERT_FAIL=2;
    public static final int FIRMWARE_CONVERT_BUSING=3;
    //"http://58.216.223.222:8000/download/DTUGC2018/"; "http://192.168.1.71:8000/download/DTUGC2018/"
    public static final String FIRM_BASEUPDATESERVICER_P = "http://58.216.223.222:8000/download/DTUGC2018P/";
    public static final String FIRM_BASEUPDATESERVICER = "http://58.216.223.222:8000/download/DTUGC2018/";
    public static final String FIRM_UPDATESERVER_INFO = "DTUGC2018.txt";

    public static final String FIRM_BASEUPDATESERVICER_MSU = "http://58.216.223.222:8000/download/MSUGC2018/";
    public static final String FIRM_UPDATESERVER_INFO_MSU = "MSUGC.txt";

    public static final String FIRM_BASEUPDATESERVICER_SGU = "http://58.216.223.222:8000/download/SGU/";
    public static final String FIRM_UPDATESERVER_INFO_SGU = "SGU.txt";

    public static final String FIRM_BASEUPDATESERVICER_FMMCU_DTUGC = "http://58.216.223.222:8000/download/FMMCU/DTUGC/";
    public static final String FIRM_UPDATESERVER_FMMCU_DTUGC = "DTUGC.txt";

    public static final String FIRM_BASEUPDATESERVICER_FMMCU_MSUGC = "http://58.216.223.222:8000/download/FMMCU/MSUGC/";
    public static final String FIRM_UPDATESERVER_FMMCU_MSUGC = "MSUGC.txt";

    public static final String FIRM_BASEUPDATESERVICER_FMMCU_SGULCA = "http://58.216.223.222:8000/download/FMMCU/SGULCA/";
    public static final String FIRM_UPDATESERVER_FMMCU_SGULCA = "SGULCA.txt";

    public static final int FIRMWARE_DATAWRITE_TIMEOUT=6;
    public static final int FIRMWARE_DATAERROR_TIMEOUT=7;
    public static final int FIRMWARE_DATAFINISH_TIMEOUT=8;

    public static final int FIRM_WRITE_FRAMELEN=512;


    public static final int NUMOFGAGE=20;

    //图表常量关键字
    public static final String DEVICEID = "deviceid";
    public static final String DEVICECHART1 = "press1";
    public static final String DEVICECHART2 = "press2";

    //数据解析类型
    public static final int PARSE_FLOAT1=0;
    public static final int PARSE_FLOAT2=1;
    public static final int PARSE_INT=2;

    //PTZ相关设置的常量
    public static String[][] workmodetype ={
            {"脉冲计数器","0"},
            {"T标况修正仪","1"},
            {"PT标况修正仪","2"},
            {"PTZ标况修正仪","3"},
    };

    public static String[][] plusemode ={
            {"双脉冲","1"},
            {"单脉冲","2"},
            {"单脉冲+对比脉冲","3"}
    };



    public static String[][] plusedevice ={
            {"干簧管","1"},
            {"磁阻开关","2"}
    };

    public static String[][] plusedata ={
            {"0.001","1"},
            {"0.01","2"},
            {"0.1","3"},
            {"1","4"},
            {"10","5"}
    };
    public static String[][] pressmode =  //压力传感器
            {
                    {"无","0"},
                    {"常量","1"},
                    {"I2C","2"},
                    {"I2C-206C","3"}
            };
    public static String[][] temperaturemode ={
            {"无","0"},
            {"脉冲","1"},
            {"I2C","2"},
            {"I2C-206C","3"}
    };

    public static String[][] compressibilityfactormode ={
            {"常量","0"},
            {"计算","2"}
    };

    public static String[][] Scantimemode ={
            {"10","10"},
            {"15","15"},
            {"20","20"},
            {"30","30"}
    };



}
