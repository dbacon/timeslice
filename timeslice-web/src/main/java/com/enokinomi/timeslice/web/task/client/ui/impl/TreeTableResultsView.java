package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.ui_tree.ItemsToTree;
import com.enokinomi.timeslice.web.task.client.ui_tree.Mutable;
import com.enokinomi.timeslice.web.task.client.ui_tree.NodeTraverser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TreeTableResultsView extends ResizeComposite
{
    private final TreeTableResultsViewConstants constants = GWT.create(TreeTableResultsViewConstants.class);

    private FlexTable resultsTable = new FlexTable();
    private final TaskTotalIntegrator integrator;

    TreeTableResultsView(TaskTotalIntegrator integrator)
    {
        this.integrator = integrator;
        initWidget(new ScrollPanel(resultsTable));
    }

    public Integer postInc(Mutable<Integer> value)
    {
        Integer v = value.getValue();
        value.setValue(value.getValue() + 1);
        return v;
    }

    public Integer preInc(Mutable<Integer> value)
    {
        value.setValue(value.getValue() + 1);
        return value.getValue();
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
        final Mutable<Integer> row = new Mutable<Integer>(0);

        int col = 0;
        resultsTable.setWidget(row.getValue(), col++, new HTML("<b><u>" + constants.who() + "</u></b>", false));
        resultsTable.setWidget(row.getValue(), col++, new HTML("<b><u>" + constants.totalHours() + "</u></b>", false));
        resultsTable.setWidget(row.getValue(), col++, new HTML("<b><u>" + constants.totalPercent() + "</u></b>", false));
        resultsTable.setWidget(row.getValue(), col++, new HTML("<b><u>" + constants.hours() + "</u></b>", false));
        resultsTable.setWidget(row.getValue(), col++, new HTML("<b><u>" + constants.percent() + "</u></b>", false));
        resultsTable.getColumnFormatter().addStyleName(col, "resultTree-What");
        resultsTable.setWidget(row.getValue(), col++, new HTML("<b><u>" + constants.what() + "</u></b>", false));

        row.setValue(row.getValue() + 1);

        new NodeTraverser<String, TaskTotal, TaskTotal>()
        {
            @Override
            protected void visit(List<String> path, TaskTotal dataRow, TaskTotal aggregate)
            {
                int col = 0;
                String who = "";
                String hours = "";
                String totalhours = NumberFormat.getDecimalFormat().format(aggregate.getHours()); // some bug if no datastore.
                String totalpercent = NumberFormat.getPercentFormat().format(aggregate.getPercentage());
                String percent = "";
//                String what = new PathRenderer<String>("/").apply(path);
                StringBuilder p = new StringBuilder();
                for (int i = 0; i < path.size(); ++i) p.append("  ");

                String what = "/";

                if (path.size() > 0) what = path.get(path.size() - 1);

                resultsTable.getRowFormatter().addStyleName(row.getValue(), "resultTree-Depth" + path.size());

                if (null != dataRow)
                {
                    resultsTable.getRowFormatter().addStyleName(row.getValue(), "resultTree-NonEmpty");

                    who = dataRow.getWho();
                    hours = NumberFormat.getDecimalFormat().format(dataRow.getHours());
                    percent = NumberFormat.getPercentFormat().format(dataRow.getPercentage());
//                    what = dataRow.getWhat();
                }
                else
                {
                    resultsTable.getRowFormatter().addStyleName(row.getValue(), "resultTree-Empty");
                }

                HorizontalPanel hb = new HorizontalPanel();
                for (int i = 0; i < path.size(); ++i)
                {
                    Label w = new Label("");
                    w.setWidth("1em");
                    hb.add(w);
                }
                hb.add(new Label(what));

                // TODO: use messages for consistent numeric formatting

                resultsTable.getCellFormatter().setAlignment(row.getValue(), col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
                resultsTable.setText(row.getValue(), col++, who);

                resultsTable.getCellFormatter().setAlignment(row.getValue(), col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
                resultsTable.setText(row.getValue(), col++, totalhours);

                resultsTable.getCellFormatter().setAlignment(row.getValue(), col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
                resultsTable.setText(row.getValue(), col++, totalpercent);

                resultsTable.getCellFormatter().setAlignment(row.getValue(), col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
                resultsTable.setText(row.getValue(), col++, hours);

                resultsTable.getCellFormatter().setAlignment(row.getValue(), col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
                resultsTable.setText(row.getValue(), col++, percent);

                resultsTable.setWidget(row.getValue(), col++, hb);

                row.setValue(row.getValue() + 1);
            }
        }.visit(ItemsToTree.create(integrator).rowsToTree(report, new TaskTotal()));
    }
}
