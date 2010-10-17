package com.enokinomi.timeslice.web.session.client.ui;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class SessionSettingsPanel extends Composite
{
    private final FlexTable table = new FlexTable();

    public SessionSettingsPanel()
    {
        initWidget(table);
    }

    public void clear()
    {
        table.removeAllRows();
    }

    public void add(Map<String, List<String>> settings)
    {
        int row = 1;
        for (Entry<String, List<String>> entry: settings.entrySet())
        {
            String key = entry.getKey();

            for (String value: entry.getValue())
            {
                int col = 0;

                table.setText(row, col, key);
                ++col;

                table.setText(row, col, value);
                ++col;

                ++row;
            }
        }
    }
}
