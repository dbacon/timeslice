package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.ui.api.ITabularResultsAssignedView;
import com.enokinomi.timeslice.web.assign.client.ui.api.ITabularResultsAssignedViewListener;
import com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamChangedListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanelListener;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ReportPanel extends ResizeComposite implements IReportPanel
{
    private final IParamPanel params;
    private final Button refreshButton;
    private final Button persistButton;
    private final TextBox persistAsName = new TextBox();
    private final Label persisted = new Label();
    private final ITabularResultsAssignedView resultsAssignedView;
    private final TaskTotalIntegrator integrator = new TaskTotalIntegrator("/");
    private final TreeTableResultsView resultsTreeView = new TreeTableResultsView(integrator);
    private final ProjectListPanel projectListPanel;

    private ArrayList<IReportPanelListener> listeners = new ArrayList<IReportPanelListener>();

    public Widget asWidget() { return this; };

    @Override
    public void addReportPanelListener(IReportPanelListener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    protected void fireRefreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
    {
        for (IReportPanelListener listener: listeners)
        {
            listener.refreshRequested(startingTimeText, endingTimeText, allowWords, ignoreWords);
        }
    }

    protected void firePersistRequested(String persistAsName, String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
    {
        for (IReportPanelListener listener: listeners)
        {
            listener.persistRequested(persistAsName, startingTimeText, endingTimeText, allowWords, ignoreWords);
        }
    }

    protected void fireBilleeUpdateRequested(String description, String newBillee)
    {
        for (IReportPanelListener listener: listeners)
        {
            listener.billeeUpdateRequested(description, newBillee);
        }
    }

    @Inject
    ReportPanel(ReportPanelConstants constants, ProjectListPanel projectListPanel, IParamPanel paramPanel, ITabularResultsAssignedView resultsAssignedView)
    {
        this.projectListPanel = projectListPanel;
        this.params = paramPanel;
        this.resultsAssignedView = resultsAssignedView;

        refreshButton = new Button(constants.refresh());
        persistButton = new Button(constants.persist());

        params.addParamChangedListener(new IParamChangedListener()
        {
            public void paramChanged(IParamPanel source)
            {
                fireRefreshRequested(
                    params.getStartingTimeRendered(),
                    params.getEndingTimeRendered(),
                    Arrays.asList(params.getAllowWords().getText().split(",")),
                    Arrays.asList(params.getIgnoreWords().getText().split(",")));
                //reselectData();
            }
        });

        refreshButton.setAccessKey('t');
        refreshButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireRefreshRequested(
                        params.getStartingTimeRendered(),
                        params.getEndingTimeRendered(),
                        Arrays.asList(params.getAllowWords().getText().split(",")),
                        Arrays.asList(params.getIgnoreWords().getText().split(",")));
            }
        });

        persistButton.setAccessKey('p');
        persistButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                firePersistRequested(
                        renderPersistName(),
                        params.getStartingTimeRendered(),
                        params.getEndingTimeRendered(),
                        Arrays.asList(params.getAllowWords().getText().split(",")),
                        Arrays.asList(params.getIgnoreWords().getText().split(",")));
            }
        });

        persistAsName.setText("full-day-%D");
        persistAsName.setTitle(constants.persistedNameSubstitutionHint());
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        buttonPanel.add(refreshButton);
        buttonPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        buttonPanel.add(persistAsName);
        buttonPanel.add(persistButton);
        buttonPanel.add(persisted);

        VerticalPanel vp = new VerticalPanel();
        vp.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        vp.add(params.asWidget());
        vp.add(buttonPanel);

        resultsAssignedView.addListener(new ITabularResultsAssignedViewListener()
        {
            @Override
            public void billeeUpdate(String description, String newBillee)
            {
                fireBilleeUpdateRequested(description, newBillee);
            }
        });

        TabLayoutPanel resultsTabs = new TabLayoutPanel(2, Unit.EM);
        resultsTabs.add(resultsTreeView, constants.totaling());
        resultsTabs.add(resultsAssignedView.asWidget(), constants.assigned());
        resultsTabs.add(projectListPanel, constants.projectList());

        SplitLayoutPanel dp = new SplitLayoutPanel();
        dp.addNorth(vp, 180);
        dp.add(resultsTabs);

        initWidget(dp);
    }

    @Override
    public void setResults(List<TaskTotal> results)
    {
        resultsTreeView.setResults(results);
    }

    @Override
    public void setResultsAssigned(List<AssignedTaskTotal> report)
    {
        resultsAssignedView.setResults(report);
        projectListPanel.update(report);
    }

    protected String renderPersistName()
    {
        return persistAsName.getText()
            .replaceAll("%D", params.getFullDaySelected())
            .replaceAll("%S", params.getStartingTimeRendered())
            .replaceAll("%E", params.getEndingTimeRendered());
    }

    @Override
    public void setPersisted(String persistedName)
    {
        persisted.setText(persistedName);
    }

    @Override
    public IParamPanel getParamsPanel()
    {
        return params;
    }

    @Override
    public void setBillees(List<String> billees)
    {
        resultsAssignedView.setBillees(billees);
    }
}
