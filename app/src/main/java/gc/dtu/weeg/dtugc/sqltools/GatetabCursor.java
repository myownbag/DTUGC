package gc.dtu.weeg.dtugc.sqltools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.dtugc.utils.Constants;

public class GatetabCursor {

    private SQLiteDatabase db = null ;							// SQLiteDatabase
    public GatetabCursor(SQLiteDatabase db) { 					// 构造方法
        this.db = db ;											// 接收SQLiteDatabase
    }

    public ArrayList<Map<String,String>> find1(String idinfo,String orderby,int limte,int OFFSET ) {								// 查询数据表
        String offset=null;
        String select=null;
        String[] selectionArgs =null;
        if(limte>0)
        {
             offset=OFFSET+" , "+limte;
        }
        else
        {
            offset=null;
        }
        ArrayList<Map<String,String>> all = new ArrayList<>() ;			// 定义List集合
        String columns[] = new String[] {"id","mac","androidid"
                ,"userid","userpassword","updateflag","gateresult","date"} ;	// 查询列
        if(idinfo!=null)
        {
//            select=Constants.COLUMN_MAC+"=?";
//            selectionArgs = new  String[]{ idinfo };
            select="updateflag=?";
            selectionArgs = new  String[]{ idinfo };
        }

        Cursor result = this.db.query(Constants.TABLENAME3, columns,
                select, selectionArgs, null,
                null,Constants.COLUMN_DATE+" "+orderby,offset);	 //		DESC/ASC 降序/升序
        // 查询数据表
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
//            all.add("【" + result.getInt(0) + "】" + " " + result.getString(1)
//                    + "，" + result.getString(2));				// 设置集合数据
            Map<String,String> map=new HashMap<>();
            map.put("ids",result.getString(0));
            map.put("mac",result.getString(1));
            map.put("androidid",result.getString(2));
            map.put("userid",result.getString(3));
            map.put("userpassword",result.getString(4));
            map.put("updateflag",result.getString(5));
            map.put("gateresult",result.getString(6));
            map.put("date",result.getString(7));
            all.add(map);
        }
        this.db.close() ;// 关闭数据库连接
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
    public int getcount(String idinfo)
    {
        String columns[] = new String[] {"id",Constants.COLUMN_MAC,Constants.COLUMN_TEM
                ,Constants.COLUMN_PRESS1,Constants.COLUMN_PRESS2,Constants.COLUMN_DATE} ;	// 查询列
        String select=Constants.COLUMN_MAC+"=?";
        String[] selectionArgs = new  String[]{ idinfo };
        Cursor result = this.db.query(Constants.TABLENAME1, columns,
                select, selectionArgs, null,
                null,Constants.COLUMN_DATE+" "+"DESC");
        int count=result.getCount();
        this.db.close() ;// 关闭数据库连接
        return count;

    }
    public int ExSqlCmd(String sqlstring)
    {
        Cursor result = this.db.rawQuery(sqlstring, null); // 执行查询语句
        int count=result.getCount();
        this.db.close();;
        return count;
    }
}
