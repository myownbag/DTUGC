package gc.dtu.weeg.dtugc.sqltools;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import gc.dtu.weeg.dtugc.utils.Constants;

public class MytabOperate {
    			// 表名称
    private SQLiteDatabase db = null ;								// SQLiteDatabase
    public MytabOperate(SQLiteDatabase db) {						// 构造方法
        this.db = db ;
    }
    public void insert1(String mac, String temperature,String press1,String press2,String timeinfo) {
        String sql = "INSERT INTO " + Constants.TABLENAME1 + " ("
                +Constants.COLUMN_MAC+","
                +Constants.COLUMN_TEM+","
                +Constants.COLUMN_PRESS1+","
                +Constants.COLUMN_PRESS2+","
                +Constants.COLUMN_DATE
                +") VALUES ('"
                + mac + "','"
                + temperature + "','"
                + press1 + "','"
                + press2 + "','"
                + timeinfo
                + "')";
        // SQL语句
       // Log.d("zl","SQL:"+sql);
        this.db.execSQL(sql);										// 执行SQL语句
        this.db.close() ;											// 关闭数据库操作
    }

    public void insert2(String mac, String ins1,String ins2,String timeinfo) {
        String sql = "INSERT INTO " + Constants.TABLENAME2 + " ("
                +Constants.COLUMN_MAC+","
                +Constants.COLUMN_INS1+","
                +Constants.COLUMN_INS2+","
                +Constants.COLUMN_DATE
                +") VALUES ('"
                + mac + "','"
                + ins1 + "','"
                + ins2 + "','"
                + timeinfo
                + "')"; 					// SQL语句


        this.db.execSQL(sql);										// 执行SQL语句
        this.db.close() ;												// 关闭数据库操作
    }

    public void insert3(String mac, String Androidid,String userid,String userpassword, int updateflag,String gateresult,String date) {
        String sql = "INSERT INTO " + Constants.TABLENAME3 + " ("
                +"mac"+","
                +"androidid"+","
                +"userid"+","
                +"userpassword"+","
                +"updateflag"+","
                +"gateresult"+","
                +"date"
                +") VALUES ('"
                + mac + "','"
                + Androidid + "','"
                + userid + "','"
                + userpassword+"','"
                + updateflag+"','"
                + gateresult+"','"
                + date
                + "')";// SQL语句

    Log.d("zl","MytabOperate out sql:"+sql);
        this.db.execSQL(sql);										// 执行SQL语句
        this.db.close() ;												// 关闭数据库操作
    }
    public void update1(int id, String mac, String temperature,String press1,String press2,String timeinfo) {
        String sql = "UPDATE " + Constants.TABLENAME1 + " SET "
                +Constants.COLUMN_MAC+"='" + mac+"',"
                +Constants.COLUMN_TEM+"='" + temperature+"',"
                +Constants.COLUMN_PRESS1+"='" + press1+"',"
                +Constants.COLUMN_PRESS2+"='" + press2+"',"
                +Constants.COLUMN_DATE+"='" + timeinfo+"'"
                + " WHERE id=" + id; 	// SQL语句
        this.db.execSQL(sql); 										// 执行SQL语句
        this.db.close() ;											// 关闭数据库操作
    }
    public void update2(int id, String mac, String ins1,String ins2,String timeinfo) {
        String sql = "UPDATE " + Constants.TABLENAME2 + " SET "
                +Constants.COLUMN_MAC+"='" + mac+"',"
                +Constants.COLUMN_INS1+"='" + ins1+"',"
                +Constants.COLUMN_INS2+"='" + ins2+"',"
                +Constants.COLUMN_DATE+"='" + timeinfo+"'"
                + " WHERE id=" + id; 	// SQL语句
        this.db.execSQL(sql); 										// 执行SQL语句
        this.db.close() ;											// 关闭数据库操作
    }

    public void update3(int ids,int updateflag) {
        String sql = "UPDATE " + Constants.TABLENAME3 + " SET "
                +"updateflag"+"=" + updateflag
                + " WHERE id=" + ids; 	// SQL语句
//        Log.d("zl",sql);
        this.db.execSQL(sql); 										// 执行SQL语句
        this.db.close() ;											// 关闭数据库操作
    }
    public void delete1(int id) {
        String sql = "DELETE FROM " + Constants.TABLENAME1 + " WHERE id=" + id;// SQL语句
        this.db.execSQL(sql) ;										// 执行SQL语句
        this.db.close() ;											// 关闭数据库操作
    }
    public void delete2(int id) {
        String sql = "DELETE FROM " + Constants.TABLENAME2 + " WHERE id=" + id;// SQL语句
        this.db.execSQL(sql) ;										// 执行SQL语句
        this.db.close();											// 关闭数据库操作
    }
}
