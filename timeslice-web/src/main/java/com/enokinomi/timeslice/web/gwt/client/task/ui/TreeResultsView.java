/**
 *
 */
package com.enokinomi.timeslice.web.gwt.client.task.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import com.enokinomi.timeslice.web.gwt.client.task.core.TaskTotal;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;

public class TreeResultsView extends ResizeComposite
{
    private Tree tree = new Tree();

    public TreeResultsView()
    {
        initWidget(new ScrollPanel(tree));
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
        tree.removeItems();

//        ArrayList<String> currentPath = new ArrayList<String>();
//
//        resultsTable.removeAllRows();
//        resultsTable.setCellSpacing(5);
//        int row = 0;
//        int col = 0;
//
//        resultsTable.setWidget(row, col++, new HTML("<b><u>Who</u></b>", false));
//        resultsTable.setWidget(row, col++, new HTML("<b><u>Hours</u></b>", false));
//        resultsTable.setWidget(row, col++, new HTML("<b><u>%</u></b>", false));
//        resultsTable.setWidget(row, col++, new HTML("<b><u>What</u></b>", false));
//        resultsTable.setWidget(row, col++, new HTML("<b><u>Code</u></b>", false));
//
//        row++;
//
//        for (TaskTotal reportRow: report)
//        {
//            col = 0;
//
//            resultsTable.setText(row, col++, reportRow.getWho());
//            resultsTable.setText(row, col++, NumberFormat.getDecimalFormat().format(reportRow.getHours()));
//            resultsTable.setText(row, col++, NumberFormat.getPercentFormat().format(reportRow.getPercentage()));
//            resultsTable.setText(row, col++, reportRow.getWhat());
//            resultsTable.setText(row, col++, "" + reportRow.getWhat().hashCode());
//
//            row++;
//        }
    }
}
