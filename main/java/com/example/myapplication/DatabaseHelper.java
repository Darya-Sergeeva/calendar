package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_TASK_DATE = "task_date";
    public static final String COLUMN_IS_DONE = "is_done";

    public static final String TABLE_SUBTASKS = "subtasks";
    public static final String COLUMN_SUBTASK_NAME = "subtask_name";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_SUBTASK_IS_DONE = "is_done";

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENT_DATE = "event_date";
    public static final String COLUMN_EVENT_COLOR = "event_color";
    public static final String COLUMN_EVENT_NAME = "event_name";

    public static final String TABLE_DATE_COLORS = "date_colors";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COLOR = "color";

    private static final String TABLE_CREATE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TASK_NAME + " TEXT, " +
                    COLUMN_TASK_DATE + " TEXT, " +
                    COLUMN_IS_DONE + " INTEGER);";

    private static final String TABLE_CREATE_SUBTASKS =
            "CREATE TABLE " + TABLE_SUBTASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SUBTASK_NAME + " TEXT, " +
                    COLUMN_TASK_ID + " INTEGER, " +
                    COLUMN_SUBTASK_IS_DONE + " INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(" + COLUMN_TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + COLUMN_ID + "));";

    private static final String TABLE_CREATE_EVENTS =
            "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EVENT_DATE + " TEXT, " +
                    COLUMN_EVENT_COLOR + " INTEGER, " +
                    COLUMN_EVENT_NAME + " TEXT);";

    private static final String TABLE_CREATE_DATE_COLORS =
            "CREATE TABLE " + TABLE_DATE_COLORS + " (" +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_COLOR + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_TASKS);
        db.execSQL(TABLE_CREATE_SUBTASKS);
        db.execSQL(TABLE_CREATE_EVENTS);
        db.execSQL(TABLE_CREATE_DATE_COLORS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(TABLE_CREATE_SUBTASKS);
        }
        if (oldVersion < 3) {
            db.execSQL(TABLE_CREATE_EVENTS);
        }
        if (oldVersion < 4) {
            db.execSQL(TABLE_CREATE_DATE_COLORS);
        }
    }

    public boolean isColorUsedForEvent(int color) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_EVENT_COLOR}, COLUMN_EVENT_COLOR + " = ?", new String[]{String.valueOf(color)}, null, null, null);
        boolean colorExists = cursor.getCount() > 0;
        cursor.close();
        return colorExists;
    }

    public boolean deleteEvent(String eventName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENTS, COLUMN_EVENT_NAME + " = ?", new String[]{eventName}) > 0;
    }

    public boolean deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)}) > 0;
    }

    public boolean deleteSubtask(long subtaskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SUBTASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(subtaskId)}) > 0;
    }

    public boolean addDateColor(String date, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_COLOR, color);
        long newRowId = db.insert(TABLE_DATE_COLORS, null, values);
        return newRowId != -1;
    }

    public boolean deleteDateColor(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DATE_COLORS, COLUMN_DATE + " = ?", new String[]{date}) > 0;
    }

    public List<String> getTaskDates() {
        List<String> taskDates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_TASK_DATE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE));
            taskDates.add(date);
        }
        cursor.close();
        return taskDates;
    }

    public List<String> getColorDates() {
        List<String> colorDates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DATE_COLORS, new String[]{COLUMN_DATE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            colorDates.add(date);
        }
        cursor.close();
        return colorDates;
    }
}
