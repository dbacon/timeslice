package com.enokinomi.timeslice.web.gwt.client.task.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.task.core.TaskTotal;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TabularResultsView extends ResizeComposite
{
    private FlexTable resultsTable = new FlexTable();

    public TabularResultsView()
    {
        initWidget(new ScrollPanel(resultsTable));
    }

    public void setResults(List<TaskTotal> report)
    {
        Collections.sort(report, Collections.reverseOrder(new Comparator<TaskTotal>()
                {
                    public int compare(TaskTotal o1, TaskTotal o2)
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

        ++row;


        for (TaskTotal reportRow: report)
        {
            col = 0;

            resultsTable.setText(row, col++, reportRow.getWho());
            resultsTable.setText(row, col++, NumberFormat.getDecimalFormat().format(reportRow.getHours()));
            resultsTable.setText(row, col++, NumberFormat.getPercentFormat().format(reportRow.getPercentage()));
            resultsTable.setText(row, col++, reportRow.getWhat());
            resultsTable.setText(row, col++, "" + reportRow.getWhat().hashCode());

            row++;
        }
    }
}
