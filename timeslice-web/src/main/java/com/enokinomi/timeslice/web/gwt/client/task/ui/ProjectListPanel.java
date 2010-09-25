package com.enokinomi.timeslice.web.gwt.client.task.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvc;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvcAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

// TODO: this widget should probably come from assigned.ui package
public class ProjectListPanel extends Composite
{
    private final FlexTable table = new FlexTable();

    private final IProRataSvcAsync prorataSvc = GWT.create(IProRataSvc.class);

    private IAuthTokenHolder tokenHolder;

    public ProjectListPanel()
    {
        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.add(table);
        initWidget(dp);

        clearAndInstallHeaders();
    }

    private void clearAndInstallHeaders()
    {
        table.removeAllRows();
        table.setWidget(0, 0, new Label("Project"));
    }

    public void setAuthTokenHolder(IAuthTokenHolder tokenHolder)
    {
        this.tokenHolder = tokenHolder;
    }

    private class Row
    {
        public final String project;
        public final Double total;

        public Row(String project, Double total)
        {
            this.project = project;
            this.total = total;
        }
    }

    public void update(List<AssignedTaskTotal> items)
    {
        clearAndInstallHeaders();

        String authToken = (null == tokenHolder) ? null : tokenHolder.getAuthToken();

        if (null == authToken)
        {
            GWT.log("No authorization token available - can't update.");
            return;
        }

        Map<String, Row> rowMap = new LinkedHashMap<String, Row>();
        for (final AssignedTaskTotal item: items)
        {
            String project = item.getBilledTo();
            Row row = rowMap.get(project);
            if (null == row)
            {
                row = new Row(project, 0.);
                rowMap.put(project, row);
            }

            rowMap.put(row.project, new Row(row.project, row.total + item.getHours()));
        }

        int row = 1;
        for (Entry<String, Row> entry: rowMap.entrySet())
        {
            Row rowItem = entry.getValue();
            int column = 0;

            table.setWidget(row, column++, new Label(rowItem.project));
            table.setWidget(row, column++, new Label(rowItem.total.toString()));

            ++row;
        }
        // kick off the group expansion for any meta-items.
        for (final String uniqueProject: rowMap.keySet())
        {
            prorataSvc.expandGroup(authToken, uniqueProject, new AsyncCallback<List<GroupComponent>>()
            {
                @Override
                public void onSuccess(List<GroupComponent> result)
                {
                    doGroupExpanded(uniqueProject, result);
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    GWT.log("group-expansion failed: " + caught.getMessage(), caught);
                }
            });

        }
    }

    protected void doGroupExpanded(String project, List<GroupComponent> result)
    {
        GWT.log("Expanded group - '" + project + "' -> " + result.toString());
    }
}
