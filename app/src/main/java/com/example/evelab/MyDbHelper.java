package com.example.evelab;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "StudentResult";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MARK = "mark";


    public MyDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" +
                        COLUMN_ID + " integer primary key, " +
                        COLUMN_NAME + " text, " +
                        COLUMN_MARK + " text" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public long create(String name, String mark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,name);
        contentValues.put(COLUMN_MARK, mark);
        long id = db.insert(TABLE_NAME, null, contentValues);
        return id;
    }
    @SuppressLint("Range")
    public ArrayList<String> readAll() {
        ArrayList<String> all = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String mark = cursor.getString(cursor.getColumnIndex(COLUMN_MARK));
            cursor.moveToNext();
            all.add("Name: " + name + ", Mark: " + mark);
        }
        return all;
    }
    public void update(String name, String new_mark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MARK, new_mark);
        db.update(TABLE_NAME, contentValues, "name = ? ", new String[]{name});
    }
    public void delete(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "name = ? ", new String[]{name});
    }

}
