package com.enokinomi.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.beans.AssignedTaskTotal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class TabularResultsAssignedView extends ResizeComposite
{
    private FlexTable resultsTable = new FlexTable();

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

        int col = 0;
        resultsTable.setWidget(row, col++, new HTML("<b><u>Who</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>Hours</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>%</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>What</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>Code</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>Billee</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>New billee</u></b>", false));
        resultsTable.setWidget(row, col++, new HTML("<b><u>U</u></b>", false));

        ++row;


        for (final AssignedTaskTotal reportRow: report)
        {
            col = 0;

            resultsTable.setText(row, col++, reportRow.getWho());
            resultsTable.setText(row, col++, NumberFormat.getDecimalFormat().format(reportRow.getHours()));
            resultsTable.setText(row, col++, NumberFormat.getPercentFormat().format(reportRow.getPercentage()));
            resultsTable.setText(row, col++, reportRow.getWhat());
            resultsTable.setText(row, col++, "" + reportRow.getWhat().hashCode());
            resultsTable.setText(row, col++, reportRow.getBilledTo());

            final TextBox textBox = new TextBox();
            textBox.setText(reportRow.getBilledTo());
            resultsTable.setWidget(row, col++, textBox);

            Anchor updateLink = new Anchor("[u]");
            updateLink.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    sendBilleeUpdate(reportRow, textBox);
                }
            });

            resultsTable.setWidget(row, col++, updateLink);

            row++;
        }
    }

    protected void sendBilleeUpdate(AssignedTaskTotal reportRow, TextBox textBox)
    {
        String description = reportRow.getWhat();
        String newBillee = textBox.getText();

        fireBilleeUpdate(description, newBillee);
    }
}
