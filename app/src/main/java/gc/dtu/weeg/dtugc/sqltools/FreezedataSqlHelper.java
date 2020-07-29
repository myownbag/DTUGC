package gc.dtu.weeg.dtugc.sqltools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gc.dtu.weeg.dtugc.utils.Constants;

public class FreezedataSqlHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "freezedata.db" ;		// 数据库名称
    private static final int DATABASEVERSION = 1 ;				// 数据库版本


    public FreezedataSqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Constants.TABLENAME1 + " (" +
                "id			INTEGER 		PRIMARY KEY ," +
                "mac		VARCHAR(50)		NOT NULL ," +
                "temperature		VARCHAR(50)		NOT NULL ," +
                "press1		VARCHAR(50)		NOT NULL ," +
                "press2		VARCHAR(50)		NOT NULL ," +
                "date	TEXT			NOT NULL)";				// SQL语句
        db.execSQL(sql) ;

         sql = "CREATE TABLE " + Constants.TABLENAME2 + " (" +
                "id			INTEGER 		PRIMARY KEY ," +
                "mac		VARCHAR(50)		NOT NULL ," +
                "instrument1		VARCHAR(50)		NOT NULL ," +
                "instrument2		VARCHAR(50)		NOT NULL ," +
                "date	TEXT			NOT NULL)";				// SQL语句
        db.execSQL(sql) ;	// 执行SQL语句

        sql = "CREATE TABLE " + Constants.TABLENAME3 + " (" +
                "id			INTEGER 		PRIMARY KEY ," +
                "mac		VARCHAR(50)		NOT NULL ," +
                "androidid		VARCHAR(50)		NOT NULL ," +
                "userid		VARCHAR(50)		NOT NULL ," +
                "userpassword		VARCHAR(50)		NOT NULL ," +
                "updateflag		INTEGER		NOT NULL ," +
                "gateresult		VARCHAR(50)		NOT NULL ," +
                "date	TEXT			NOT NULL)";				// SQL语句
        db.execSQL(sql) ;	// 执行SQL语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + Constants.TABLENAME1 ;		// SQL语句
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + Constants.TABLENAME2 ;		// SQL语句
        db.execSQL(sql);// 执行SQL语句
        this.onCreate(db);
    }
}
