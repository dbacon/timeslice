package timeslice.mobile;

import timeslice.mobile.providers.TimesliceContract.Tasks;
import timeslice.mobile.providers.TimesliceContract.Tasks.StatusValue;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class QueryHelper
{
    public Context context;

    public QueryHelper(Context context)
    {
        this.context = context;
    }

    public Cursor openQueryUnsentItems()
    {
        return context.getContentResolver().query(Tasks.CONTENT_URI, null, Tasks.STATUS + " = ?", new String[] { StatusValue.ST_UNSENT }, null);
    }

    public int queryUnsentCount()
    {
        Cursor items = openQueryUnsentItems();
        int count = items.getCount();
        items.close();
        return count;
    }

    public String queryItemsStatus(long id)
    {
        String result = null;

        Cursor target = context.getContentResolver().query(
                Uri.withAppendedPath(Tasks.CONTENT_URI, Long.toString(id)),
                new String[] { Tasks._ID, Tasks.STATUS },
                null, null, null);

        if (target.moveToFirst())
        {
            result = target.getString(target.getColumnIndex(Tasks.STATUS));
        }

        target.close();
        return result;
    }

}
