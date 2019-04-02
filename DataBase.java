package com.example.tuugu.positioningapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 ** Created by Tuguldur Batjargal on 01.04.2017.
 **/

public class DataBase extends SQLiteOpenHelper {
    // Defining database table content
    // Four columns in the table: BSSID, signal stength level, latitude, and longitude
    public static final String DATABASE_NAME = "WifiData.db";
    public static final String TABLE_NAME = "wifitable";
    public static final String COL_1 = "BSSID";
    public static final String COL_2 = "signalLvl";
    public static final String COL_3 = "lat";
    public static final String COL_4 = "lng";
    // Create database if it did not exist
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (BSSID TEXT PRIMARY KEY, signalLvl TEXT, lat REAL, lng REAL)");
    }
    // Upgrade the database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    // Insert data into the database
    public boolean insertData(String ID, String signal, double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, ID);
        contentValues.put(COL_2, signal);
        contentValues.put(COL_3, lat);
        contentValues.put(COL_4, lng);
        Log.e("query", lat + " " + lng);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else
            return true;
    }
//Get all data from the database
    public Cursor getAllData() {
        Log.i("TAG", "getalldata1");
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("TAG", "getalldata2");
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        Log.i("TAG", "getalldata3");
        return res;
    }

//remove all data from the database
    public void removeAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
//Remove one data from the database
    public void removeOneData(LatLng latLng) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_3 + "= " + latLng.latitude + " AND " + COL_4 + "= " + latLng.longitude;
        db.execSQL(query);
        Log.e("query", query);

    }
}
