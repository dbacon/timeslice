package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.ui.api.ITabularResultsAssignedView;
import com.enokinomi.timeslice.web.assign.client.ui.api.ITabularResultsAssignedViewListener;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectListPanel;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamChangedListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanelListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ReportPanel extends ResizeComposite implements IReportPanel
{
    private static ReportPanelUiBinder uiBinder = GWT.create(ReportPanelUiBinder.class);
    interface ReportPanelUiBinder extends UiBinder<Widget, ReportPanel> { }

    @UiField protected TreeTableResultsView resultsTreeView;
    @UiField protected ITabularResultsAssignedView resultsAssignedView;
    @UiField protected IProjectListPanel projectListPanel;
    @UiField protected IParamPanel params;
    @UiField protected Button refreshButton;

    private ArrayList<IReportPanelListener> listeners = new ArrayList<IReportPanelListener>();

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

    protected void fireBilleeUpdateRequested(String description, String newBillee)
    {
        for (IReportPanelListener listener: listeners)
        {
            listener.billeeUpdateRequested(description, newBillee);
        }
    }

    @Override
    public void bindProrataBits(IProrataManagerPresenter prorataPresenter, ISettingsPresenter settingsPresenter)
    {
        projectListPanel.bind(prorataPresenter, settingsPresenter);
    }

    @Override
    public void bind(ISettingsPresenter settingsPresenter)
    {
        params.bind(params, settingsPresenter); // will be static/moved
    }

    @UiHandler("refreshButton")
    protected void refreshClicked(ClickEvent e)
    {
        fireRefreshRequested(
                params.getStartingTimeRendered(),
                params.getEndingTimeRendered(),
                Arrays.asList(params.getAllowWords().split(",")),
                Arrays.asList(params.getIgnoreWords().split(",")));
    }

    @Inject
    ReportPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));

        params.addParamChangedListener(new IParamChangedListener()
        {
            public void paramChanged(IParamPanel source)
            {
                fireRefreshRequested(
                    params.getStartingTimeRendered(),
                    params.getEndingTimeRendered(),
                    Arrays.asList(params.getAllowWords().split(",")),
                    Arrays.asList(params.getIgnoreWords().split(",")));
                //reselectData();
            }
        });

        refreshButton.setAccessKey('t');

        resultsAssignedView.addListener(new ITabularResultsAssignedViewListener()
        {
            @Override
            public void billeeUpdate(String description, String newBillee)
            {
                fireBilleeUpdateRequested(description, newBillee);
            }
        });

        clear();
    }

    @Override
    public void clear()
    {
        resultsTreeView.clear();
        resultsAssignedView.clear();
        projectListPanel.clear();
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
