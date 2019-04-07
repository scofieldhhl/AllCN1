package com.db.cls;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.datas.ProgremPlayerInfo;

public class ProgremPlayerDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "ALLCN_DB.db";
    private final String TALBE_NAME = "PROGREM_PLAYER";
    private final String KEY_NAME = "name";
    private final String KEY_URL = "url";
    private final String KEY_POSITION = "position";

    private final static int VERSION = 1;
    private Context mContext;

    public ProgremPlayerDB( Context context) {
        this(context, DB_NAME);
    }
    public ProgremPlayerDB( Context context, String name) {
        this(context, name, VERSION);
    }
    public ProgremPlayerDB( Context context, String name, int version) {
        this(context, name, null, version);
    }
    public ProgremPlayerDB( Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TALBE_NAME+" ("+KEY_URL+" text PRIMARY KEY , "+KEY_NAME+" text not null, "+KEY_POSITION+"integer);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void instert(ProgremPlayerInfo playerInfo){

        SQLiteDatabase writableDatabase = new ProgremPlayerDB(mContext).getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(KEY_NAME, playerInfo.getColName());
        value.put(KEY_URL, playerInfo.getUrl());
        value.put(KEY_POSITION, playerInfo.getPosition());
        long insert = writableDatabase.insert(TALBE_NAME, null, value);
        writableDatabase.close();
    }

    public long getPosition(ProgremPlayerInfo playerInfo){
        SQLiteDatabase readableDatabase = new ProgremPlayerDB(mContext).getReadableDatabase();
        try {
            Cursor query = readableDatabase.query(TALBE_NAME,
                    null,
                    null,//KEY_NAME + "=" + playerInfo.getColName() + " AND " + KEY_URL + "=\"" + playerInfo.getUrl()+"\"",
                    null,
                    null,
                    null,
                    null);

            long position = -1;
            boolean b = query.moveToNext();
            while (b) {
                String url = query.getString(query.getColumnIndex(KEY_URL));
                if (url.equals(playerInfo.getUrl()))
                    position = query.getLong(query.getColumnIndex(KEY_POSITION));
            }
            readableDatabase.close();
            query.close();
            return position;
        }catch (Exception e){
            return -1;
        }
    }
}
