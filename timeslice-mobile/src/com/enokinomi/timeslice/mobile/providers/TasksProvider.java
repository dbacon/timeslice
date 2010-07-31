package com.enokinomi.timeslice.mobile.providers;

import java.util.HashMap;

import com.enokinomi.timeslice.mobile.providers.TimesliceContract.Tasks;
import com.enokinomi.timeslice.mobile.providers.TimesliceContract.Tasks.StatusValue;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TasksProvider extends ContentProvider
{
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String DB_NAME = "timeslice-mobile-1.db";
    private static final int DB_VERSION = 1;
    private static final String TB_TASK = "tasks";

    private static final String LOG_TAG_TIMESLICE = "timeslice-db";

    private static class DbHelper extends SQLiteOpenHelper
    {
        public DbHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("create table " + TB_TASK + "(" +
                        Tasks._ID + " integer primary key," +
                        Tasks.WHO + " text," +
                        Tasks.WHEN + " text," +
                        Tasks.WHAT + " text," +
                        Tasks.STATUS + " text" +
                        ");");
            Log.i(LOG_TAG_TIMESLICE, "Created table.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(LOG_TAG_TIMESLICE, String.format("Upgrading %d -> %d", oldVersion, newVersion));
            db.execSQL("drop table if exists " + TB_TASK);
            onCreate(db);
        }
    }

    private DbHelper dbh;

    private static final int URI_MATCH_TASKS = 1000;
    private static final int URI_MATCH_TASKS_ID = 1001;
    private static final int URI_MATCH_TASKS_FILTER = 1002;

    static
    {
        uriMatcher.addURI(TimesliceContract.AUTHORITY, "tasks", URI_MATCH_TASKS);
        uriMatcher.addURI(TimesliceContract.AUTHORITY, "tasks/#", URI_MATCH_TASKS_ID);
        uriMatcher.addURI(TimesliceContract.AUTHORITY, "tasks/filter/*", URI_MATCH_TASKS_FILTER);
    }


    @Override
    public boolean onCreate()
    {
        dbh = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = uriMatcher.match(uri);

        switch(match)
        {
            case URI_MATCH_TASKS:
                return Tasks.CONTENT_TYPE;
            case URI_MATCH_TASKS_ID:
                return Tasks.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues)
    {
        if (URI_MATCH_TASKS != uriMatcher.match(uri))
        {
            throw new IllegalArgumentException("Unsupported URI for insert: " + uri);
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        ContentValues values;
        if (null != initialValues) values = new ContentValues(initialValues);
        else values = new ContentValues();

        if (!values.containsKey(Tasks.STATUS)) values.put(Tasks.STATUS, StatusValue.ST_UNSENT);
        if (!values.containsKey(Tasks.WHEN)) values.put(Tasks.WHEN, now.toString());
        if (!values.containsKey(Tasks.WHO)) values.put(Tasks.WHO, "todo-enter-from-prefs");
        if (!values.containsKey(Tasks.WHAT)) throw new IllegalArgumentException("Missing description.");

        long rowId = dbh.getWritableDatabase().insert(TB_TASK, Tasks.WHAT, values);

        if (rowId > 0)
        {
            Uri resultUri = ContentUris.withAppendedId(Tasks.CONTENT_URI, rowId);
            Log.i("", "Inserted: " + resultUri);
            notifyChange();
            return resultUri;
        }
        else
        {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if (URI_MATCH_TASKS_ID != uriMatcher.match(uri)) throw new UnsupportedOperationException("Only update of single item is supported: " + uri);

        String id = uri.getPathSegments().get(1);

        int rows = dbh.getWritableDatabase().update(TB_TASK, values, Tasks._ID + "=?", new String[] { id });

        notifyChange();
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        throw new UnsupportedOperationException("Delete");
    }

    private HashMap<String, String> taskProjectionMap = buildColumnMap();

    private static HashMap<String, String> buildColumnMap()
    {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put(Tasks._ID, Tasks._ID);
        result.put(Tasks.WHO, Tasks.WHO);
        result.put(Tasks.WHAT, Tasks.WHAT);
        result.put(Tasks.WHEN, Tasks.WHEN);
        result.put(Tasks.STATUS, Tasks.STATUS);
        return result;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TB_TASK);

        switch (uriMatcher.match(uri))
        {
            case URI_MATCH_TASKS:
                qb.setProjectionMap(taskProjectionMap);
                break;

            case URI_MATCH_TASKS_ID:
                qb.setProjectionMap(taskProjectionMap);
                selection = Tasks._ID + " = ?";
                selectionArgs = new String[] { uri.getPathSegments().get(1) };
                break;

            case URI_MATCH_TASKS_FILTER:
                qb.setProjectionMap(taskProjectionMap);
                selection = Tasks.WHAT + " like ?";
                selectionArgs = new String[] { uri.getPathSegments().get(1) };
                break;
        }

        String groupBy = null;
        String having = null;
        String limit = "100";
        Log.i(LOG_TAG_TIMESLICE, "Queried.");
        return qb.query(dbh.getReadableDatabase(), projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
    }

    private void notifyChange()
    {
        // TODO: can we pass on who caused the update ?
        // TODO: can we use the sync adapters ?
        getContext().getContentResolver().notifyChange(TimesliceContract.Tasks.CONTENT_URI, null, false);
    }

}
