package com.example.myapplication;

import android.provider.BaseColumns;

public final class SubTaskContract {
    private SubTaskContract() {}

    public static class SubTaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "subtasks";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_TASK_ID = "taskId";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}




