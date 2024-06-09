package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2;

    // SQL для создания таблицы задач
    private static final String SQL_CREATE_TASKS =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.TaskEntry.COLUMN_NAME_TITLE + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_NAME_DATE + " TEXT)";

    // SQL для создания таблицы подзадач
    private static final String SQL_CREATE_SUBTASKS =
            "CREATE TABLE " + SubTaskContract.SubTaskEntry.TABLE_NAME + " (" +
                    SubTaskContract.SubTaskEntry._ID + " INTEGER PRIMARY KEY," +
                    SubTaskContract.SubTaskEntry.COLUMN_NAME_TITLE + " TEXT," +
                    SubTaskContract.SubTaskEntry.COLUMN_NAME_TASK_ID + " INTEGER," +
                    SubTaskContract.SubTaskEntry.COLUMN_NAME_COMPLETED + " INTEGER)";

    // SQL для удаления таблицы задач
    private static final String SQL_DELETE_TASKS =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    // SQL для удаления таблицы подзадач
    private static final String SQL_DELETE_SUBTASKS =
            "DROP TABLE IF EXISTS " + SubTaskContract.SubTaskEntry.TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASKS);
        db.execSQL(SQL_CREATE_SUBTASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + SubTaskContract.SubTaskEntry.TABLE_NAME + " ADD COLUMN " + SubTaskContract.SubTaskEntry.COLUMN_NAME_TASK_ID + " INTEGER");
            db.execSQL("ALTER TABLE " + SubTaskContract.SubTaskEntry.TABLE_NAME + " ADD COLUMN " + SubTaskContract.SubTaskEntry.COLUMN_NAME_TITLE + " TEXT");
        } else {
            db.execSQL(SQL_DELETE_TASKS);
            db.execSQL(SQL_DELETE_SUBTASKS);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}








