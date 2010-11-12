package com.enokinomi.timeslice.web.assign.client.ui.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.ui.api.ITabularResultsAssignedView;
import com.enokinomi.timeslice.web.core.client.ui.EditableLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TabularResultsAssignedView extends ResizeComposite implements ITabularResultsAssignedView
{
    private final TabularResultsAssignedViewConstants constants;

    private FlexTable resultsTable = new FlexTable();
    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

    @Inject
    TabularResultsAssignedView(TabularResultsAssignedViewConstants constants)
    {
        this.constants = constants;

        resultsTable.setStylePrimaryName("tsMathTable");

        initWidget(new ScrollPanel(resultsTable));
    }

    public static interface Listener
    {
        void billeeUpdate(String description, String newBillee);
    }

    private final ArrayList<Listener> listeners = new ArrayList<Listener>();

    @Override
    public void addListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.remove(listener);
        }
    }

    @Override
    public Widget asWidget()
    {
        return this;
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

    @Override
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

        resultsTable.getRowFormatter().setStylePrimaryName(row, "tsTableHeader");

        int col = 0;

        resultsTable.setWidget(row, col++, new HTML(constants.who(), false));
        resultsTable.setWidget(row, col++, new HTML(constants.hours(), false));
        resultsTable.setWidget(row, col++, new HTML(constants.percent(), false));
        resultsTable.setWidget(row, col++, new HTML(constants.what(), false));
        resultsTable.setWidget(row, col++, new HTML(constants.billee(), false));

        ++row;


        Double totalHours = 0.;
        Double totalPercentage = 0.;

        for (final AssignedTaskTotal reportRow: report)
        {
            resultsTable.getRowFormatter().addStyleName(row, (row % 2 == 0) ? "evenRow" : "oddRow");

            col = 0;

            resultsTable.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            resultsTable.setText(row, col++, reportRow.getWho());

            resultsTable.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            resultsTable.setText(row, col++, NumberFormat.getDecimalFormat().format(reportRow.getHours()));
            totalHours += reportRow.getHours();

            resultsTable.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            resultsTable.setText(row, col++, NumberFormat.getPercentFormat().format(reportRow.getPercentage()));
            totalPercentage += reportRow.getPercentage();

            resultsTable.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            resultsTable.setText(row, col++, reportRow.getWhat());

            // TODO: set style for edited editable-label

            final SuggestBox suggestBox = new SuggestBox(oracle);
            final EditableLabel label = new EditableLabel(suggestBox, reportRow.getBilledTo());
            label.setLabelStylePrimaryName("ts-project");
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
                        label.getLabel().setText(constants.updating());
                        sendBilleeUpdate(reportRow, newValue);
                    }
                }
            });

            label.setWidth("20em"); // TODO: assigned label width to CSS ?
            resultsTable.setWidget(row, col++, label);

            // TODO: provide way to focus 1st unassigned text box

            row++;
        }

        // Totals row

        resultsTable.getCellFormatter().setAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        resultsTable.getCellFormatter().addStyleName(row, 1, "totalsRow");
        resultsTable.setText(row, 1, NumberFormat.getDecimalFormat().format(totalHours));

        resultsTable.getCellFormatter().setAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        resultsTable.getCellFormatter().addStyleName(row, 2, "totalsRow");
        resultsTable.setText(row, 2, NumberFormat.getPercentFormat().format(totalPercentage));

    }

    private void sendBilleeUpdate(AssignedTaskTotal reportRow, String newValue)
    {
        onBilleeUpdate(reportRow.getWhat(), newValue);
    }

    protected void onBilleeUpdate(String description, String newBillee)
    {
        fireBilleeUpdate(description, newBillee);
    }

    @Override
    public void setBillees(List<String> billees)
    {
        oracle.clear();
        oracle.addAll(billees);
    }
}