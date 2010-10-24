package com.enokinomi.timeslice.web.task.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.ui.TabularResultsAssignedView;
import com.enokinomi.timeslice.web.assign.client.ui.TabularResultsAssignedView.Listener;
import com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.ui.ParamPanel.IParamChangedListener;
import com.enokinomi.timeslice.web.task.client.ui_one.PrefHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

public class ReportPanel extends ResizeComposite
{
    private final ReportPanelConstants constants = GWT.create(ReportPanelConstants.class);

    private final ParamPanel params = new ParamPanel();
    private final Button refreshButton = new Button(constants.refresh());
    private final Button persistButton = new Button(constants.persist());
    private final TextBox persistAsName = new TextBox();
    private final Label persisted = new Label();
    private final TabularResultsAssignedView resultsAssignedView = new TabularResultsAssignedView();
    private final TaskTotalIntegrator integrator = new TaskTotalIntegrator("/");
    private final TreeTableResultsView resultsTreeView = new TreeTableResultsView(integrator);
    private final ProjectListPanel projectListPanel;

    private static class PrefKey
    {
        public static final String IgnoreStrings = "timeslice.report.ignorestrings";
        public static final String AllowStrings = "timeslice.report.allowstrings";
    }

    public static interface IReportPanelListener
    {
        void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords);
        void persistRequested(String persistAsName, String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords);
        void billeeUpdateRequested(String description, String newBillee);
    }

    private ArrayList<IReportPanelListener> listeners = new ArrayList<IReportPanelListener>();

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

    private void readPrefs()
    {
        params.getIgnoreWords().setText(Cookies.getCookie(PrefKey.IgnoreStrings));
        params.getAllowWords().setText(Cookies.getCookie(PrefKey.AllowStrings));

        params.fireParamChanged();
    }

    private void writePrefs()
    {
        Cookies.setCookie(PrefKey.IgnoreStrings, params.getIgnoreWords().getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKey.AllowStrings, params.getAllowWords().getText(), PrefHelper.createDateSufficientlyInTheFuture());
    }

    @Inject
    public ReportPanel(ProjectListPanel projectListPanel)
    {
        this.projectListPanel = projectListPanel;

        params.addParamChangedListener(new IParamChangedListener()
        {
            public void paramChanged(ParamPanel source)
            {
                writePrefs();
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
        vp.add(params);
        vp.add(buttonPanel);

        resultsAssignedView.addListener(new Listener()
        {
            @Override
            public void billeeUpdate(String description, String newBillee)
            {
                fireBilleeUpdateRequested(description, newBillee);
            }
        });

        TabLayoutPanel resultsTabs = new TabLayoutPanel(2, Unit.EM);
        resultsTabs.add(resultsTreeView, constants.totaling());
        resultsTabs.add(resultsAssignedView, constants.assigned());
        resultsTabs.add(projectListPanel, constants.projectList());

        SplitLayoutPanel dp = new SplitLayoutPanel();
        dp.addNorth(vp, 180);
        dp.add(resultsTabs);

        readPrefs();

        initWidget(dp);
    }

    public void setResults(List<TaskTotal> results)
    {
        resultsTreeView.setResults(results);
    }

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

    public void setPersisted(String persistedName)
    {
        persisted.setText(persistedName);
    }

    public ParamPanel getParamsPanel()
    {
        return params;
    }

    public void setBillees(List<String> billees)
    {
        resultsAssignedView.setBillees(billees);
    }
}
