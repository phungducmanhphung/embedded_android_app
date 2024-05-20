package com.example.apenadetect.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BreathDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "breath_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "breath_records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BREATH_RATE = "breath_rate";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public BreathDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BREATH_RATE + " DOUBLE, " +
                COLUMN_TIMESTAMP + " LONG)";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void insertBreathData(double breathRate, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BREATH_RATE, breathRate);
        values.put(COLUMN_TIMESTAMP, timestamp);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public void insertBreathData(double breathRate) {
        long currentTime = System.currentTimeMillis() / 1000;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BREATH_RATE, breathRate);
        values.put(COLUMN_TIMESTAMP, currentTime);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public void insertBreathData(Breathing breathing) {
        long currentTime = System.currentTimeMillis() / 1000;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BREATH_RATE, breathing.getBreathRate());
        values.put(COLUMN_TIMESTAMP, breathing.getTimestamp());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public List<Breathing> getBreathDataBetween(long startTime, long endTime) {
        List<Breathing> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = String.format("%s >= ? AND %s <= ?", COLUMN_TIMESTAMP, COLUMN_TIMESTAMP);
        String[] selectionArgs = { String.valueOf(startTime), String.valueOf(endTime) };

        Cursor cursor = db.query(
                TABLE_NAME,
                null,  // Columns to return (null means all columns)
                selection,  // The columns for the WHERE clause
                selectionArgs,  // The values for the WHERE clause
                null,  // Group by
                null,  // Having
                null   // Order by
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") double breathRate = cursor.getDouble(cursor.getColumnIndex(COLUMN_BREATH_RATE));
                @SuppressLint("Range") long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                data.add(
                        Breathing.builder()
                                .id(id)
                                .breathRate(breathRate)
                                .timestamp(timestamp)
                                .build()
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    public List<Breathing> getBreathDataBetween(long startTime, long endTime, int pageNumber, int pageSize) {
        List<Breathing> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        int offset = (pageNumber - 1) * pageSize;

        String selection = String.format("%s >= ? AND %s <= ?", COLUMN_TIMESTAMP, COLUMN_TIMESTAMP);
        String[] selectionArgs = { String.valueOf(startTime), String.valueOf(endTime) };

        String limit = String.valueOf(pageSize); // Giới hạn số lượng bản ghi trên mỗi trang.
        String offsetLimit = String.valueOf(offset) + "," + limit;

        Cursor cursor = db.query(
                TABLE_NAME,
                null,  // Columns to return (null means all columns)
                selection,  // The columns for the WHERE clause
                selectionArgs,  // The values for the WHERE clause
                null,  // Group by
                null,  // Having
                COLUMN_TIMESTAMP + " ASC",
                offsetLimit    // Order by
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") double breathRate = cursor.getDouble(cursor.getColumnIndex(COLUMN_BREATH_RATE));
                @SuppressLint("Range") long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                data.add(
                        Breathing.builder()
                                .id(id)
                                .breathRate(breathRate)
                                .timestamp(timestamp)
                                .build()
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }
    public List<Breathing> getAllData() {
        List<Breathing> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") double breathRate = cursor.getDouble(cursor.getColumnIndex(COLUMN_BREATH_RATE));
                @SuppressLint("Range") long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                data.add(
                        Breathing.builder()
                                .id(id)
                                .breathRate(breathRate)
                                .timestamp(timestamp)
                                .build()
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }
}
