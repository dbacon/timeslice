package com.enokinomi.timeslice.web.gwt.client.assigned.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.controller.EditableLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;

public class TabularResultsAssignedView extends ResizeComposite
{
    private static final String ColumnLabel_Billee = "Billee";
    private static final String ColumnLabel_Code = "Code";
    private static final String ColumnLabel_What = "What";
    private static final String ColumnLabel_Percent = "%";
    private static final String ColumnLabel_Hours = "Hours";
    private static final String ColumnLabel_Who = "Who";

    private static final String Label_Updating = "Updating...";

    private FlexTable resultsTable = new FlexTable();
    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

    public TabularResultsAssignedView()
    {
        initWidget(new ScrollPanel(resultsTable));
    }

    public static interface Listener
    {
        void billeeUpdate(String description, String newBillee);
    }

    private final ArrayList<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.remove(listener);
        }
    }

    protected void fireBilleeUpdate(String description, String newBillee)
    {
        for (Listener listener: listeners)
        {
            try
            {
                listener.billeeUpdate(description, newBillee);
            }
            catch (Exception e)
            {
                GWT.log("Updating listener failed: " + e.getMessage());
            }
        }
    }

    public void setResults(List<AssignedTaskTotal> report)
    {
        Collections.sort(report, Collections.reverseOrder(new Comparator<AssignedTaskTotal>()
                {
                    public int compare(AssignedTaskTotal o1, AssignedTaskTotal o2)
                    {
                        return o1.getHours().compareTo(o2.getHours());
                    }
                }));

        resultsTable.removeAllRows();
        resultsTable.setCellSpacing(5);
        int row = 0;

        // TODO: move column header style to CSS
        int col = 0;
        resultsTable.setWidget(row, col++, new HTML("<b><u>" + ColumnLabel_Who + "</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>" + ColumnLabel_Hours + "</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>" + ColumnLabel_Percent + "</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>" + ColumnLabel_What + "</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>" + ColumnLabel_Code + "</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>" + ColumnLabel_Billee + "</u></b>", false));

        ++row;


        for (final AssignedTaskTotal reportRow: report)
        {
            col = 0;

            resultsTable.setText(row, col++, reportRow.getWho());
            resultsTable.setText(row, col++, NumberFormat.getDecimalFormat().format(reportRow.getHours()));
            resultsTable.setText(row, col++, NumberFormat.getPercentFormat().format(reportRow.getPercentage()));
            resultsTable.setText(row, col++, reportRow.getWhat());
            resultsTable.setText(row, col++, "" + reportRow.getWhat().hashCode());

            // TODO: set style for edited editable-label

            final SuggestBox suggestBox = new SuggestBox(oracle);
            final EditableLabel label = new EditableLabel(suggestBox, reportRow.getBilledTo());
            label.addListener(new EditableLabel.Listener()
            {
                @Override
                public void editCanceled()
                {
                }

                @Override
                public void editBegun()
                {
                }

                @Override
                public void editAccepted(String oldValue, String newValue)
                {
                    if (!oldValue.equals(newValue))
                    {
                        label.getLabel().setText(Label_Updating);
                        sendBilleeUpdate(reportRow, newValue);
                    }
                }
            });

            label.setWidth("20em"); // TODO: assigned label width to CSS ?
            resultsTable.setWidget(row, col++, label);

            // TODO: provide way to escape, reverting the text to its original.
            // TODO: provide way to focus 1st unassigned text box
            // TODO: put editable box in same cell as label, only showing 1-at-a-time when clicked/entered.
            // TODO: add style for unassigned items.

            row++;
        }
    }

    private void sendBilleeUpdate(AssignedTaskTotal reportRow, String newValue)
    {
        onBilleeUpdate(reportRow.getWhat(), newValue);
    }

    protected void onBilleeUpdate(String description, String newBillee)
    {
        fireBilleeUpdate(description, newBillee);
    }

    public void setBillees(List<String> billees)
    {
        oracle.clear();
        oracle.addAll(billees);
    }
}
