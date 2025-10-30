package com.example.climate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "weather";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_TEMP = "temperature";
    private static final String COLUMN_HUMIDITY = "humidity";
    private static final String COLUMN_DESC = "description";
    private static final String COLUMN_TIMEZONE = "timezone";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY + " TEXT, " +
                COLUMN_TEMP + " REAL, " +
                COLUMN_HUMIDITY + " INTEGER, " +
                COLUMN_DESC + " TEXT, " +
                COLUMN_TIMEZONE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertWeather(WeatherModel weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, weather.getCity());
        values.put(COLUMN_TEMP, weather.getTemperature());
        values.put(COLUMN_HUMIDITY, weather.getHumidity());
        values.put(COLUMN_DESC, weather.getDescription());
        values.put(COLUMN_TIMEZONE, weather.getTimezone());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<WeatherModel> getAllWeather() {
        List<WeatherModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                WeatherModel weather = new WeatherModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getLong(5)
                );
                list.add(weather);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteCity(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_CITY + " = ?", new String[]{cityName});
        db.close();
        return rowsDeleted > 0;
    }
}