package timeslice.mobile.providers;

import timeslice.mobile.providers.TimesliceContract.Tasks;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class TasksProvider extends ContentProvider
{
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    private static final int TASKS = 1000;
    private static final int TASKS_ID = 1001;
    private static final int TASKS_FILTER = 1002;

    static
    {
        uriMatcher.addURI(TimesliceContract.AUTHORITY, "tasks", TASKS);
        uriMatcher.addURI(TimesliceContract.AUTHORITY, "tasks/#", TASKS_ID);
        uriMatcher.addURI(TimesliceContract.AUTHORITY, "tasks/filter/*", TASKS_FILTER);
    }


    @Override
    public boolean onCreate()
    {
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = uriMatcher.match(uri);

        switch(match)
        {
            case TASKS:
                return Tasks.CONTENT_TYPE;
            case TASKS_ID:
                return Tasks.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        notifyChange();
        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        notifyChange();
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        notifyChange();
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        // only implement a specific projection, w/ hard-coded results!
        MatrixCursor cursor = new MatrixCursor(projection);
        cursor.addRow(new Object[] { "hello", "1.2" });
        return cursor;
    }

    private void notifyChange()
    {
        // TODO: can we pass on who caused the update ?
        // TODO: can we use the sync adapters ?
        getContext().getContentResolver().notifyChange(TimesliceContract.Tasks.CONTENT_URI, null, false);
    }

}
