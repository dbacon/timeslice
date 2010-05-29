package timeslice.mobile;

import timeslice.mobile.providers.TimesliceContract;
import timeslice.mobile.providers.TimesliceContract.Tasks;
import timeslice.mobile.providers.TimesliceContract.Tasks.StatusValue;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class TaskListActivity extends Activity
{
    private static final String LOG_TAG_TIMESLICE = "timeslice";

    private static final int MENU_ADD = 1;
    private static final int MENU_SEND = 2;
    private static final int MENU_PREF = 3;
//    private static final int MENU_SETUP = 4;

    private Cursor cursor;
    private TextView tv;
    private ListView list;

    private void note(String msg, boolean toast)
    {
        Log.i(LOG_TAG_TIMESLICE, msg);
        if (toast) Toast.makeText(TaskListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private String createDescription()
    {
        String descr = tv.getText().toString().trim();
        if (descr.length() == 0) descr = "new item";
        // todo: provide a better clue here when there's no description?
        return descr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view1);

        tv = (TextView) findViewById(R.id.desc);
        tv.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    createEntry(createDescription());
                    return true;
                }

                return false;
            }

        });

        cursor = managedQuery(
                TimesliceContract.Tasks.CONTENT_URI,
                new String[] { Tasks._ID, Tasks.STATUS, Tasks.WHAT, Tasks.WHO },
                null, null, null);

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_row,
                cursor,
                new String[] { Tasks.STATUS, Tasks.WHAT },
                new int[] { R.id.status, R.id.description });

        list = (ListView) findViewById(R.id.tasklist);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                note("click item: position=" + position + " id=" + id, false);
                toggleStatus(id);
            }
        });

        list.setOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                note("long-click item: position=" + position + " id=" + id, true);
                return true;
            }

        });

        Button b = (Button) findViewById(R.id.b1);

        b.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createEntry(createDescription());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_SEND, Menu.NONE, "Send").setIcon(android.R.drawable.ic_menu_send);
        menu.add(Menu.NONE, MENU_ADD, Menu.NONE, "Add").setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, MENU_PREF, Menu.NONE, "Preferences").setIcon(android.R.drawable.ic_menu_preferences);
//        menu.add(Menu.NONE, MENU_SETUP, Menu.NONE, "Setup").setIcon(android.R.drawable.ic_menu_set_as);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_ADD:
                note("menu: Add", true);
                return true;

            case MENU_SEND:
                note("menu: Send", true);
                return true;

            case MENU_PREF:
                note("menu: Preferences", true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createEntry(String description)
    {
        ContentValues values = new ContentValues();
        values.put(Tasks.WHAT, description);
        getContentResolver().insert(Tasks.CONTENT_URI, values);

        note("Item entered.", true);
        tv.setText("");
        cursor.requery();
    }

    private void toggleStatus(long id)
    {
        Cursor target = getContentResolver().query(
                Uri.withAppendedPath(Tasks.CONTENT_URI, Long.toString(id)),
                new String[] { Tasks._ID, Tasks.STATUS },
                null, null, null);

        if (!target.moveToFirst())
        {
            note("Could not toggle: did not find task with id " + id, true);
            return;
        }

        String currentStatus = target.getString(cursor.getColumnIndex(Tasks.STATUS));
        String nextStatus = StatusValue.ST_UNSENT;

        if (StatusValue.ST_UNSENT.equals(currentStatus)) nextStatus = StatusValue.ST_SENT;

        ContentValues values = new ContentValues();
        values.put(Tasks.STATUS, nextStatus);
        getContentResolver().update(Uri.withAppendedPath(Tasks.CONTENT_URI, Long.toString(id)), values, null, null);

        note("Toggled id=" + id + " status: " + currentStatus + "->" + nextStatus, false);
        cursor.requery();
    }
}
