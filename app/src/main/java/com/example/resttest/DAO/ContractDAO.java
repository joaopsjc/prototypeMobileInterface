package com.example.resttest.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class ContractDAO {
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + ContractModel.TABLE_NAME + " (" +
                ContractModel._ID + " INTEGER PRIMARY KEY," +
                ContractModel.COLUMN_NAME_TIMESTAMP + " TEXT," +
                ContractModel.COLUMN_NAME_TEMPERATURE + " TEXT," +
                ContractModel.COLUMN_NAME_HUMIDITY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContractModel.TABLE_NAME;

    private ContractDAO(){}

    public static class ContractModel implements BaseColumns {
        public static final String TABLE_NAME = "weatherConditions";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TEMPERATURE = "roomTemperature";
        public static final String COLUMN_NAME_HUMIDITY = "dewPoint";
    }

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
