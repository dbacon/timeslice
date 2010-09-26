package com.enokinomi.timeslice.web.gwt.client.prorata.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.controller.IAuthTokenHolder;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvc;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvcAsync;
import com.enokinomi.timeslice.web.gwt.client.prorata.ui.ProRataManagerPanel.Listener;
import com.enokinomi.timeslice.web.gwt.client.ui.PrefHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProjectListPanel extends Composite
{
    private final ProjectListPanelConstants constants = GWT.create(ProjectListPanelConstants.class);
    private final ProjectListPanelMessages messages = GWT.create(ProjectListPanelMessages.class);

    public static class PrefKeys
    {
        public static final String Scale = "timeslice.project.scale";
        public static final String ScaleTo = "timeslice.project.scaleto";
    }

    private final FlexTable table = new FlexTable();

    private final CheckBox scaleCheckBox = new CheckBox(constants.scaleTotals());
    private final TextBox scaleToTextBox = new TextBox();
    private final ProRataManagerPanel proRataManagePanel = new ProRataManagerPanel();

    private final IProRataSvcAsync prorataSvc = GWT.create(IProRataSvc.class);

    private IAuthTokenHolder tokenHolder;

    private Map<String, Row> rowMap = new LinkedHashMap<String, Row>();

    private List<AssignedTaskTotal> itemsCache = new ArrayList<AssignedTaskTotal>();

    public ProjectListPanel()
    {
        table.setStylePrimaryName("tsMathTable");


        scaleCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                scaleToTextBox.setEnabled(scaleCheckBox.getValue());
                scaleToTextBox.setVisible(scaleCheckBox.getValue());

                writePrefs();
            }
        });

        scaleToTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event)
            {
                writePrefs();
            }
        });

        scaleToTextBox.setWidth("3em");

        readPrefs();

        scaleToTextBox.setEnabled(scaleCheckBox.getValue());
        scaleToTextBox.setVisible(scaleCheckBox.getValue());

        HorizontalPanel hp1 = new HorizontalPanel();
        hp1.add(scaleCheckBox);
        hp1.add(scaleToTextBox);

        proRataManagePanel.addListener(new Listener()
        {
            @Override
            public void groupsChanged()
            {
                update(null);
            }
        });

        VerticalPanel vp = new VerticalPanel();
        vp.add(table);

        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.addNorth(hp1, 4);
        dp.add(vp);

        TabLayoutPanel tabs = new TabLayoutPanel(2, Unit.EM);
        tabs.add(dp, constants.report());
        tabs.add(proRataManagePanel, constants.proRataMaintenance());

        initWidget(tabs);

        clearAndInstallHeaders();
    }

    private void clearAndInstallHeaders()
    {
        table.removeAllRows();
        int col = 0;

        table.getRowFormatter().setStylePrimaryName(0, "tsTableHeader");

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.project()));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.direct()));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.inherited()));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.grandTotal()));
        ++col;

        if (scaleCheckBox.getValue())
        {
            table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            table.setWidget(0, col, new HTML(constants.grandTotalScaled()));
        }

        ++col;
    }

    public void setAuthTokenHolder(IAuthTokenHolder tokenHolder)
    {
        this.tokenHolder = tokenHolder;

        proRataManagePanel.setAuthTokenHolder(tokenHolder);
    }

    private class Row
    {
        public final String project;
        public final Double total;
        public final Double inherited;

        public Row(String project, Double total, Double inherited)
        {
            this.project = project;
            this.total = total;
            this.inherited = inherited;
        }

        public Double calcGrandTotal()
        {
            return total + inherited;
        }

        public Double calcGrandTotalScaled(Double one, Double total)
        {
            return calcGrandTotal() / total * one;
        }
    }

    public void update(List<AssignedTaskTotal> items)
    {
        proRataManagePanel.refresh();

        rowMap.clear();

        if (items != null)
        {
            itemsCache = items;
        }

        String authToken = (null == tokenHolder) ? null : tokenHolder.getAuthToken();

        if (null == authToken)
        {
            GWT.log("No authorization token available - can't update.");
            return;
        }

        for (final AssignedTaskTotal item: itemsCache)
        {
            String project = item.getBilledTo();
            Row row = rowMap.get(project);
            if (null == row)
            {
                row = new Row(project, 0., 0.);
                rowMap.put(project, row);
            }

            rowMap.put(row.project, new Row(row.project, row.total + item.getHours(), 0.));
        }

        redraw();

        // kick off the group expansion for any meta-items.
        // TODO: take care of case where expanded group targets are themselves expandable groups..

        for (final Row row: rowMap.values())
        {
            prorataSvc.expandGroup(authToken, row.project, new AsyncCallback<List<GroupComponent>>()
            {
                @Override
                public void onSuccess(List<GroupComponent> result)
                {
                    doGroupExpanded(row.project, result);
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    GWT.log("group-expansion failed: " + caught.getMessage(), caught);
                }
            });

        }
    }


    private void redraw()
    {
        clearAndInstallHeaders();

        Double total = 0.;
        for (Row row: rowMap.values())
        {
            total += row.calcGrandTotal();
        }

        int rowIndex = 1;
        for (Row row: rowMap.values())
        {
            int col = 0;

            table.setWidget(rowIndex, col, new Label(row.project));
            table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            col++;

            table.setWidget(rowIndex, col, new Label(messages.direct(row.total)));
            table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            col++;

            table.setWidget(rowIndex, col, new Label(messages.inherit(row.inherited)));
            table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            col++;

            table.setWidget(rowIndex, col, new Label(messages.grandTotal(row.calcGrandTotal())));
            table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            col++;

            if (scaleCheckBox.getValue())
            {
                table.setWidget(rowIndex, col, new Label(messages.grandTotalScaled(row.calcGrandTotalScaled((double) Double.valueOf(scaleToTextBox.getText()), total))));
                table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            }

            col++;

            ++rowIndex;
        }
    }

    protected void doGroupExpanded(String project, List<GroupComponent> result)
    {
        if (result.isEmpty())
        {
            return;
        }

        Row metaRow = rowMap.get(project);

        if (null == metaRow)
        {
            GWT.log("Could not find expected row after expansion: '" + project + "'");
            return;
        }

        // find the total of weightings so we can normalize
        Double weightTotal = 0.;
        for (GroupComponent comp: result)
        {
            weightTotal += Double.valueOf(comp.getWeight());
        }

        Double debit = 0.;

        for (GroupComponent comp: result)
        {
            Double componentWeight = Double.valueOf(comp.getWeight()) / weightTotal;
            Double inherited = metaRow.total * componentWeight;
            debit -= inherited;

            Row compRow = rowMap.get(comp.getName());

            Row newComponentRow;
            if (null == compRow)
            {
                newComponentRow = new Row(comp.getName(), 0., inherited);
            }
            else
            {
                newComponentRow = new Row(compRow.project, compRow.total, compRow.inherited + inherited);
            }

            rowMap.put(newComponentRow.project, newComponentRow);
        }

        Row newMetaRow = new Row(metaRow.project, metaRow.total, debit);
        rowMap.put(newMetaRow.project, newMetaRow);

        GWT.log("Expanded group - '" + metaRow.project + "' -> " + result.size());

        redraw();
    }

    private void readPrefs()
    {
        scaleCheckBox.setValue("true".equals(Cookies.getCookie(PrefKeys.Scale)));
        scaleToTextBox.setText(Cookies.getCookie(PrefKeys.ScaleTo));
    }

    private void writePrefs()
    {
        Cookies.setCookie(PrefKeys.Scale, scaleCheckBox.getValue() ? "true" : "false", PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.ScaleTo, scaleToTextBox.getValue(), PrefHelper.createDateSufficientlyInTheFuture());
    }
}
