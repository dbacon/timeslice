package timeslice.mobile;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class TimesliceWidget extends AppWidgetProvider
{
    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        QueryHelper queryHelper = new QueryHelper(context);
        int unsent = queryHelper.queryUnsentCount();

        for (int i = 0; i < appWidgetIds.length; ++i)
        {
            int wid = appWidgetIds[i];

            RemoteViews rviews = new RemoteViews(context.getPackageName(), R.layout.hsw);
            rviews.setTextViewText(R.id.addbutton, "" + unsent + " unsent");

            Intent intent = new Intent(context, TaskListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rviews.setOnClickPendingIntent(R.id.addbutton, pendingIntent);

            appWidgetManager.updateAppWidget(wid, rviews);
        }
    }
}
