package com.example.cyk.cloudweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    public static SQLiteDatabase database;
    /*初始化数据库信息*/
    public static void initDB(Context context){
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }
    /*查找数据量当中城市列表*/
    public static List<String>queryALLCityName(){
        Cursor cursor = database.query("info",null,null,null,null,null,null);
        List<String>cityList = new ArrayList<>();
        while (cursor.moveToNext()){
            String city = cursor.getString(cursor.getColumnIndex("city"));
            cityList.add(city);
        }
        cursor.close();
        return cityList;
    }
    /*根据城市名称，替换信息内容*/
    public static int updateInfoByCity(String city, String content){
        ContentValues values = new ContentValues();
        values.put("content",content);
        return database.update("info",values,"city=?",new String[]{city});
    }
    /*新增一条城市记录*/
    public static long addCityInfo(String city, String content){
        ContentValues values = new ContentValues();
        values.put("city",city);
        values.put("content",content);
        return database.insert("info",null,values);
    }
    /*根据城市名查询数据库中的内容*/
    public static String queryInfoByCity(String city){
        Cursor cursor = database.query("info",null,"city=?",new String[]{city},null,null,null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("content"));
            cursor.close();
            return content;
        }
        return null;
    }
    /*存储城市天气信息要求最多存储5个城市的信息，一旦超过5个城市就不能存储，获取目前已经存储的城市数量*/
    public static int getCityCount(){
        Cursor cursor = database.query("info",null,null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    /*查询数据库中的全部信息*/
    public static List<DatabaseBean>queryALLInfo(){
        Cursor cursor = database.query("info",null,null,null,null,null,null);
        List<DatabaseBean>list = new ArrayList<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            DatabaseBean databaseBean = new DatabaseBean(id, city, content);
            list.add(databaseBean);
        }
        cursor.close();
        return list;
    }

    /*根据城市名称，删除这个城市在数据库中的数据*/
    public static int deleteInfoByCity(String city){
        return database.delete("info","city=?",new String[]{city});
    }

    /*删除表中所有数据信息*/
    public static void deleteALLInfo(){
        String sql = "delete from info";
        database.execSQL(sql);
    }

}
