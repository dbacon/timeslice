package com.enokinomi.timeslice.web.session.client.ui;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class SessionSettingsPanel extends Composite
{
    private final FlexTable table = new FlexTable();

    SessionSettingsPanel()
    {
        initWidget(table);
    }

    public void clear()
    {
        table.removeAllRows();

        table.getRowFormatter().addStyleName(0, "tsTableHeader");
        table.setText(0, 0, "Name");
        table.setText(0, 1, "Value");
    }

    public void add(Map<String, List<String>> settings)
    {
        int row = 1;
        for (Entry<String, List<String>> entry: settings.entrySet())
        {

            table.getRowFormatter().addStyleName(row, (row % 2 == 0) ? "evenRow" : "oddRow");
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
