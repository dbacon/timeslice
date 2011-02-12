package com.enokinomi.timeslice.web.prorata.client.ui.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectReportPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ProjectReportPanel extends Composite implements IProjectReportPanel
{
    private static ProjectReportPanelUiBinder uiBinder = GWT.create(ProjectReportPanelUiBinder.class);
    interface ProjectReportPanelUiBinder extends UiBinder<Widget, ProjectReportPanel> { }

    private final ProjectReportPanelConstants constants = GWT.create(ProjectReportPanelConstants.class);
    private final ProjectReportPanelMessages messages = GWT.create(ProjectReportPanelMessages.class);

    @UiField protected FlexTable projectTable;

    @UiField protected TextBox scaleToTextBox;
    @UiField protected CheckBox scaleCheckBox;

    public static interface Listener
    {
        void assignPartialOrderingRequested(Map<String, Double> projectMap, int i, int j); // -> sendReorder
    }

    private final List<Listener> listeners = new ArrayList<ProjectReportPanel.Listener>();

    @Override
    public void addListener(Listener listener)
    {
        if (listener != null)
        {
            listeners.add(listener);
        }
    }

    protected void fireAssignPartialOrderingRequested(Map<String, Double> projectMap, int i, int j)
    {
        for (Listener listener: listeners)
        {
            listener.assignPartialOrderingRequested(projectMap, i, j);
        }
    }

    @Inject
    ProjectReportPanel()
    {
        Widget widget = uiBinder.createAndBindUi(this);

        scaleCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                scaleToTextBox.setEnabled(scaleCheckBox.getValue());

                drawLastProjects();
            }
        });

        scaleToTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event)
            {
                drawLastProjects();
            }
        });

        scaleToTextBox.setEnabled(scaleCheckBox.getValue());

        initWidget(widget);
    }

    private void clearAndInstallHeaders_projectTable()
    {
        projectTable.removeAllRows();
        int col = 0;

        projectTable.getRowFormatter().setStylePrimaryName(0, "tsTableHeader");

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.moveDown()));
        ++col;

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.moveUp()));
        ++col;

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.project()));
        ++col;

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.total()));
        ++col;

        if (scaleCheckBox.getValue())
        {
            projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.setWidget(0, col, new HTML(constants.scaled()));
        }

        ++col;
    }

    private double lastTotal;
    private Map<String, Double> lastProjectMap = new LinkedHashMap<String, Double>();

    @Override
    public void setProjects(double total, Map<String, Double> projectMap)
    {
        lastTotal = total;
        lastProjectMap.clear();
        lastProjectMap.putAll(projectMap);

        drawLastProjects();
    }

    private void drawLastProjects()
    {
        drawProjects(lastTotal, lastProjectMap);
    }

    private void drawProjects(final double total, final Map<String, Double> projectMap)
    {
        clearAndInstallHeaders_projectTable();

        int rowi = 1;
        Set<Entry<String, Double>> leafTotals = projectMap.entrySet();
        int rowmax = leafTotals.size();
        Double totalTotal = 0.;
        Double scaledTotal = 0.;
        for (Entry<String, Double> p: leafTotals)
        {
            int coli = 0;

            projectTable.getRowFormatter().addStyleName(rowi, (rowi % 2) == 0 ? "evenRow" : "oddRow");

            if (rowi < rowmax)
            {
                Anchor moveDownLink = new Anchor("v");
                final int fRowi = rowi;
                moveDownLink.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent event)
                    {
                        fireAssignPartialOrderingRequested(projectMap, fRowi - 1, 1);
                    }
                });
                projectTable.setWidget(rowi, coli, moveDownLink);
            }
            ++coli;

            if (1 < rowi)
            {
                Anchor moveUpLink = new Anchor("^");
                final int fRowi = rowi;
                moveUpLink.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent event)
                    {
                        fireAssignPartialOrderingRequested(projectMap, fRowi - 1, -1);
                    }
                });
                projectTable.setWidget(rowi, coli, moveUpLink);
            }
            ++coli;


            projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.setWidget(rowi, coli++, new Label(p.getKey()));

            totalTotal += p.getValue();
            projectTable.setWidget(rowi, coli, new Label(messages.direct(p.getValue())));
            projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.getCellFormatter().removeStyleName(rowi, coli, "totalsRow");
            ++coli;

            if (scaleCheckBox.getValue())
            {
                Double target = Double.valueOf(scaleToTextBox.getText());
                double scaled = p.getValue() / total * target;
                scaledTotal += scaled;
                projectTable.setWidget(rowi, coli, new Label(messages.grandTotalScaled(scaled)));
                projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
                projectTable.getCellFormatter().removeStyleName(rowi, coli, "totalsRow");
                coli++;
            }

            ++rowi;
        }

        projectTable.setWidget(rowi, 3, new Label(messages.direct(totalTotal)));
        projectTable.getCellFormatter().setAlignment(rowi, 3, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.getCellFormatter().addStyleName(rowi, 3, "totalsRow");

        if (scaleCheckBox.getValue())
        {
            projectTable.setWidget(rowi, 4, new Label(messages.grandTotalScaled(scaledTotal)));
            projectTable.getCellFormatter().addStyleName(rowi, 4, "totalsRow");
            projectTable.getCellFormatter().setAlignment(rowi, 4, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        }

    }

}
