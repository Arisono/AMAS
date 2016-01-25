package com.ASMS.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION =1; //���ݿ�汾��
	public static String dataBaseName="appsms.db";//���ݿ�����
	public String sqlCreateTable;
	public Context context;
	public DBOpenHelper(Context ct){
		super(ct, dataBaseName, null, DATABASE_VERSION);
	}
	
	public DBOpenHelper(Context context,String dataBaseName,String sqlCreateTable
			,String tableName,int dbversion) {
		super(context, dataBaseName, null, dbversion);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreateTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

    public boolean deleteDatabase(Context context) {  
     return context.deleteDatabase(dataBaseName);  
   }  
   
   
   @Override
   public void onOpen(SQLiteDatabase db){
       super.onOpen(db); // ÿ�δ����ݿ�֮�����ȱ�ִ��
   }
}
