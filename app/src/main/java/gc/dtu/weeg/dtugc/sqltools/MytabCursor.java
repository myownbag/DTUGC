package gc.dtu.weeg.dtugc.sqltools;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.utils.Constants;

public class MytabCursor {

    private SQLiteDatabase db = null ;							// SQLiteDatabase
    public MytabCursor(SQLiteDatabase db) { 					// 构造方法
        this.db = db ;											// 接收SQLiteDatabase
    }

    public ArrayList<Map<String,String>> find1(String idinfo) {								// 查询数据表
        ArrayList<Map<String,String>> all = new ArrayList<>() ;			// 定义List集合
        String columns[] = new String[] {"id",Constants.COLUMN_MAC,Constants.COLUMN_PRESS1
                ,Constants.COLUMN_PRESS2,Constants.COLUMN_PRESS2} ;	// 查询列
        String select=Constants.COLUMN_MAC+"=?";
        String[] selectionArgs = new  String[]{ idinfo };
        Cursor result = this.db.query(Constants.TABLENAME1, columns, select, selectionArgs, null,
                null, Constants.ORDER_BY);									// 查询数据表
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
//            all.add("【" + result.getInt(0) + "】" + " " + result.getString(1)
//                    + "，" + result.getString(2));				// 设置集合数据
            Map<String,String> map=new HashMap<>();
            map.put("temp",result.getString(1));
            map.put("press1",result.getString(2));
            map.put("press2",result.getString(3));
            map.put("time",result.getString(4));
        }
        this.db.close() ;										// 关闭数据库连接
        return all ;
    }

//    public List<String> find2() {								// 查询数据表
//        List<String> all = new ArrayList<String>() ;			// 定义List集合
//        String columns[] = new String[] {"id","name","birthday"} ;	// 查询列
//        Cursor result = this.db.query(TABLENAME, columns, null, null, null,
//                null, null);									// 查询数据表
//        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
//            all.add("【" + result.getInt(0) + "】" + " " + result.getString(1)
//                    + "，" + result.getString(2));				// 设置集合数据
//        }
//        this.db.close() ;										// 关闭数据库连接
//        return all ;
//    }
}
