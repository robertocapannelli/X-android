package com.walkap.x_android;

/**
 * Created by Morcrat on 13/06/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class dbHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "personalSchedule";
    // Contacts table name
    private static final String TABLE_SCHEDULER = "schedule";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CLASSROOM = "classroom";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MATTER = "matter";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SCHEDULER + "("
        + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_CLASSROOM + " TEXT,"
        + KEY_HOUR + " TEXT, "+ KEY_MATTER + " TEXT " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULER);

        // Creating tables again
        onCreate(db);
    }

    // Adding hours
    public void addhours(dbElement dbElement) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CLASSROOM, dbElement.getClassRoom());
        values.put(KEY_HOUR, dbElement.getHour());
        values.put(KEY_MATTER, dbElement.getMatter());

        // Inserting Row
        db.insert(TABLE_SCHEDULER, null, values);
        db.close(); // Closing database connection

    }

    // Getting one hour
    public dbElement getHour(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCHEDULER, new String[] { KEY_ID,
                        KEY_CLASSROOM, KEY_HOUR, KEY_MATTER }, KEY_ID + "=?",
        new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        dbElement contact = new dbElement(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        // return hour
        return contact;
    }

    // Getting All scheduler
    public List<dbElement> getAllShops() {
        List<dbElement> schedulerList = new ArrayList<dbElement>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dbElement dbElement = new dbElement();
                dbElement.setId(Integer.parseInt(cursor.getString(0)));
                dbElement.setClassRoom(cursor.getString(1));
                dbElement.setHour(cursor.getString(2));
                dbElement.setMatter(cursor.getString(2));

                // Adding contact to list
                schedulerList.add(dbElement);
            } while (cursor.moveToNext());
        }
        // return contact list
        return schedulerList;
    }

    // Getting scheduler Count
    public int getSchedulerCount() {
        String countQuery = "SELECT * FROM " + TABLE_SCHEDULER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating a scheduler
    public int updateScheduler(dbElement dbElement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CLASSROOM, dbElement.getClassRoom());
        values.put(KEY_HOUR, dbElement.getHour());
        values.put(KEY_MATTER, dbElement.getMatter());

        // updating row
        return db.update(TABLE_SCHEDULER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(dbElement.getId())});
    }

    // Deleting a scheduler
    public void deleteScheduler(dbElement dbElement) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULER, KEY_ID + " = ?",
                new String[] { String.valueOf(dbElement.getId()) });
        db.close();
    }
}
