package timeslice.mobile;

import timeslice.mobile.providers.TimesliceContract;
import timeslice.mobile.providers.TimesliceContract.Tasks;
import timeslice.mobile.providers.TimesliceContract.Tasks.StatusValue;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView.OnEditorActionListener;

public class TaskListActivity extends Activity
{
    private static final int REQUEST_CODE_SEND_UNSENT = 42;

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
        tv.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (EditorInfo.IME_NULL == actionId)
                {
                    createEntry(createDescription());
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
                sendUnsent();
                return true;

            case MENU_PREF:
                note("menu: Preferences", true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * If keyAlias is provided, the body will be encrypted with the key for the given alias.
     *
     * @param key
     * @return
     */
    private String createBody(String key)
    {
        String body = createStandardParsableOfUnsentItems();
        if (null != key)
        {
            Object id = getDefaultKey();
            body = encrypt(body, id);
        }

        return body;
    }

    private Object getDefaultKey()
    {
        return null;
    }

    private String encrypt(String outgoingPlain, Object key)
    {
        return outgoingPlain;
    }

    private Cursor openQueryUnsentItems()
    {
        return getContentResolver().query(Tasks.CONTENT_URI, null, Tasks.STATUS + " = ?", new String[] { StatusValue.ST_UNSENT }, null);
    }

    private int queryUnsentCount()
    {
        Cursor items = openQueryUnsentItems();
        int count = items.getCount();
        items.close();
        return count;
    }

    private String createStandardParsableOfUnsentItems()
    {
        String result = "";
        Cursor unsentItems = openQueryUnsentItems();

        if (unsentItems.moveToFirst())
        {
            inProgress = new long[unsentItems.getCount()];
            int idx = 0;
            StringBuilder sb = new StringBuilder();

            do
            {
                inProgress[idx++] = unsentItems.getLong(unsentItems.getColumnIndex(Tasks._ID));

                sb
                    .append('[')
                    .append(unsentItems.getString(unsentItems.getColumnIndex(Tasks.WHO)))
                    .append(',')
                    .append(unsentItems.getString(unsentItems.getColumnIndex(Tasks.WHEN)))
                    .append(',')
                    .append(unsentItems.getString(unsentItems.getColumnIndex(Tasks.WHAT)))
                    .append(']')
                    .append('\n');
            }
            while (unsentItems.moveToNext());

            result = sb.toString();
        }

        unsentItems.close();
        return result;
    }

    private String[] createRecipients()
    {
        return new String[] { "dave@enokinomi.com" };
    }

    private String createSubject()
    {
        return "Timeslice data segment";
    }

    private long[] inProgress = null;


    private void sendUnsent()
    {
        if (queryUnsentCount() > 0)
        {
            String body = createBody(null);
            String[] toAddrs = createRecipients();
            String subject = createSubject();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, toAddrs);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            startActivityForResult(Intent.createChooser(intent, "Send Segment"), REQUEST_CODE_SEND_UNSENT);
        }
        else
        {
            note("Nothing to send.", true);
        }
        // what if nothing to choose, or pressed 'back'?
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_SEND_UNSENT:

                switch (resultCode)
                {
                    // Strange thing, even if the mail
                    // is sent, the result code seems to come
                    // back as canceled, every time.
                    //
                    // So we must treat both cancel and ok
                    // as being "out-the-door" from the perspective
                    // of timeslice:
                    //
                    case RESULT_CANCELED:
                    case RESULT_OK:

                        note("Sent ok.", true);

                        if (null != inProgress)
                        {
                            ContentValues values = new ContentValues();
                            values.put(Tasks.STATUS, StatusValue.ST_SENT);
                            ContentResolver contentResolver = getContentResolver();
                            int updated = 0;

                            for (int i = 0; i < inProgress.length; ++i)
                            {
                                int rows = contentResolver.update(
                                        Uri.withAppendedPath(Tasks.CONTENT_URI, Long.toString(inProgress[i])),
                                        values,
                                        null, null);

                                if (1 != rows)
                                {
                                    Log.w(LOG_TAG_TIMESLICE, "update went haywire: affected " + rows + " rows.");
                                }
                                else
                                {
                                    updated++;
                                }
                            }

                            note("Marked " + updated + " as sent.", false);
                            cursor.requery();
                        }

                        break;
                }

                return;
        }

        Log.w(LOG_TAG_TIMESLICE, "Recieved unknown activity result: requestCode=" + requestCode + " action=" + data.getAction());

        super.onActivityResult(requestCode, resultCode, data);
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

    private String queryCurrentStatus(long id)
    {
        String result = null;

        Cursor target = getContentResolver().query(
                Uri.withAppendedPath(Tasks.CONTENT_URI, Long.toString(id)),
                new String[] { Tasks._ID, Tasks.STATUS },
                null, null, null);

        if (target.moveToFirst())
        {
            result = target.getString(cursor.getColumnIndex(Tasks.STATUS));
        }
        else
        {
            note("queryCurrentStatus(" + id + "): did not find task", true);
        }

        target.close();
        return result;
    }

    private void toggleStatus(long id)
    {
        String currentStatus = queryCurrentStatus(id);

        if (null != currentStatus)
        {
            String nextStatus = StatusValue.ST_UNSENT;

            if (StatusValue.ST_UNSENT.equals(currentStatus)) nextStatus = StatusValue.ST_SENT;

            ContentValues values = new ContentValues();
            values.put(Tasks.STATUS, nextStatus);
            getContentResolver().update(Uri.withAppendedPath(Tasks.CONTENT_URI, Long.toString(id)), values, null, null);

            note("Toggled id=" + id + " status: " + currentStatus + "->" + nextStatus, false);
            cursor.requery();
        }
        else
        {
            note("Toggle failed, no current status for id=" + id, false);
        }
    }
}
