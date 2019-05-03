package com.drink.drinko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;


public class Database extends SQLiteOpenHelper {
    Context context;

    public Database( Context context) {
        super(context, "favourite", null, 2);
        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table favourite (id integer primary key ,name text," +
                "startTime text,endTime text,sType text ,number text,url text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table favourite");
        onCreate(db);
    }

    public long insert(User user){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",user.getName());
        contentValues.put("id",user.getContact());
        contentValues.put("startTime",user.getStartTime());
        contentValues.put("endTime",user.getEndTime());
        contentValues.put("sType",user.getUserType());
        contentValues.put("number",user.getContact());
        contentValues.put("url",user.getProfile());
        return database.insert("favourite",null,contentValues);
    }
    public ArrayList<User> getUser(){
        ArrayList<User> arrayList=new ArrayList<>();
        SQLiteDatabase database=this.getWritableDatabase();
        Cursor res =database.rawQuery("select * from favourite",null);
        while(res.moveToNext()){
            User user=new User();
            user.setName(res.getString(1));
            user.setStartTime(res.getString(2));
            user.setEndTime(res.getString(3));
            user.setUserType(res.getString(4));
            user.setContact(res.getString(5));
            user.setProfile(res.getString(6));
            user.setLocation("recent connected");
            arrayList.add(user);
        }
        return arrayList;
    }
    public void deleteUser(String number){
        SQLiteDatabase database=this.getWritableDatabase();
        database.delete("favourite","id=?", new String[]{number});
    }
}
