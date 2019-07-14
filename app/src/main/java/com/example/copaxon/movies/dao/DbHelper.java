package com.example.copaxon.movies.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.copaxon.movies.Utils.MovieDbConstants;

/**
 * Created by CoPaXoN on 29/07/2018.
 */

public class DbHelper extends SQLiteOpenHelper{

    private static String LOG_TAG = DbHelper.class.getName();

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "Creating all the tables");
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + MovieDbConstants.TABLE_NAME +
                "(" + MovieDbConstants.COL_ID + " INTEGER PRIMARY KEY,"
                + MovieDbConstants.COL_TITLE + " TEXT,"
                + MovieDbConstants.COL_BODY + " TEXT,"
                + MovieDbConstants.COL_URL + " TEXT,"
                + MovieDbConstants.COL_RATING + " REAL,"
                + MovieDbConstants.COL_ISWATCHED + " INTEGER)";
        try {
            db.execSQL(CREATE_BOOKS_TABLE);
        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, "Create table exception: " +
                    ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int
            newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL("DROP TABLE IF EXISTS " + MovieDbConstants.TABLE_NAME);
        onCreate(db);
    }
}
