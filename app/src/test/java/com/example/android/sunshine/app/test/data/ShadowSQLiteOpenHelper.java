package com.example.android.sunshine.app.test.data;

/**
 * Created by Sheraz on 6/11/2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * Shadow for {@code SQLiteOpenHelper}.  Provides basic support for retrieving
 * databases and partially implements the subclass contract.  (Currently,
 * support for {@code #onUpgrade} is missing).
 */
@Implements(SQLiteOpenHelper.class)
public class ShadowSQLiteOpenHelper {
    @RealObject
    private SQLiteOpenHelper realHelper;
    private static SQLiteDatabase database;

    private static Context previousContext;
    private String name;

    public void __constructor__(Context context, String name, CursorFactory factory, int version) {
        this.name = name;
        if (previousContext == null) {
            previousContext = context;
        } else {
            if(previousContext == context) {
                return;
            } else {
                previousContext = context;
            }
        }
        if (database != null) {
            database.close();
        }
        database = null;
    }

    @Implementation
    public synchronized void close() {
        if(previousContext != null)
            return;
        if (database != null) {
            database.close();
        }
        database = null;
    }

    @Implementation
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (database == null) {
//            database = SQLiteDatabase.openDatabase("path", null, 0);
            database = SQLiteDatabase.create(null);
            realHelper.onCreate(database);
        }

        realHelper.onOpen(database);
        return database;
    }

    @Implementation
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (database == null) {
//            database = SQLiteDatabase.openDatabase("path", null, 0);
            database = SQLiteDatabase.create(null);
            realHelper.onCreate(database);
        }

        realHelper.onOpen(database);
        return database;
    }

    @Implementation
    public String getDatabaseName() {
        return name;
    }
}
