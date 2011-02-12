package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanelListener;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.core.client.util.Checks;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.controller.api.IControllerListener;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanelListener;
import com.enokinomi.timeslice.web.task.client.ui.impl.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.ui_one.api.ITimesliceApp;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TimesliceApp extends ResizeComposite implements ITimesliceApp
{
    private final TimesliceAppConstants constants;
    private final IController controller;
    private final HTML issuesLink;
    private final Label serverInfoLabel;
    private final Anchor logoutAnchor;
    private final InputPanel inputPanel;
    private final IReportPanel reportPanel;
    private final IOptionsPanel optionsPanel;
    private final IAppJobPanel appJobPanel;
    private final ILoginSupport loginSupport;
    private final TzSupport tzSupport;
    private final IProrataManagerPresenter prorataPresenter;

    private static class Options
    {
        public boolean isCurrentTaskInTitlebar() { return false; }
        public String getTitleBarTemplate() { return "[TS] " + IOptionsPanel.CURRENTTASK; }
    }

    private Options options = new Options();

    private String originalWindowTitle;
    private static final StartTag UnknownTag = new StartTag(null, null, null, "-unknown-", false);

    @Override
    public Widget asWidget()
    {
        return this;
    }

    @Inject
    TimesliceApp(TimesliceAppConstants constants, IController controller, InputPanel inputPanel, IReportPanel reportPanel, IOptionsPanel optionsPanel, IAppJobPanel appJobPanel, ILoginSupport loginSupport, TzSupport tzSupport, IProrataManagerPresenter prorataPresenter, ISettingsPresenter settingsPresenter)
    {
        this.constants = constants;
        this.controller = controller;
        this.inputPanel = inputPanel;
        this.reportPanel = reportPanel;
        this.optionsPanel = optionsPanel;
        this.appJobPanel = appJobPanel;
        this.loginSupport = loginSupport;
        this.tzSupport = tzSupport;
        this.prorataPresenter = prorataPresenter;

        reportPanel.bind(prorataPresenter);
        optionsPanel.bind(settingsPresenter);

        issuesLink = new HTML();
        serverInfoLabel = new Label("[querying]");
        logoutAnchor = new Anchor(constants.logout());

        initWidget(initContents());
    }

    private Widget initContents()
    {
        originalWindowTitle = Window.getTitle();

        final TabLayoutPanel tp = new TabLayoutPanel(2, Unit.EM);
        Anchor inputlink = new Anchor(constants.input(), true);
        inputlink.setAccessKey('i');
        tp.add(inputPanel, inputlink);
        Anchor reportslink = new Anchor(constants.reports(), true);
        reportslink.setAccessKey('r');
        tp.add(reportPanel.asWidget(), reportslink);
        Anchor optionslink = new Anchor(constants.options(), true);
        optionslink.setAccessKey('o');
        tp.add(optionsPanel.asWidget(), optionslink);
        Anchor jobsLink = new Anchor(constants.jobs(), true);
        jobsLink.setAccessKey('j');
        tp.add(appJobPanel.asWidget(), jobsLink);

        tp.selectTab(0);

        logoutAnchor.setText(constants.logout());

        HorizontalPanel buildLabelBox = new HorizontalPanel();
        buildLabelBox.setSpacing(15);
        updateIssuesLink("#");
        buildLabelBox.add(issuesLink);
        buildLabelBox.add(logoutAnchor);
        buildLabelBox.add(serverInfoLabel);

        final DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.EM);
        dockPanel.addSouth(buildLabelBox, 4);
        dockPanel.add(tp);

        linkThis();
        linkInputPanel();
        linkReportPanel();
        linkAppJobPanel();

        controller.serverInfo();
        controller.startGetBranding();

        return dockPanel;
    }

    private static class ControllerListenerAdapter implements IControllerListener
    {

        @Override
        public void serverInfoRecieved(String info)
        {
        }

        @Override
        public void onBranded(AsyncResult<BrandInfo> result)
        {
        }

        @Override
        public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
        {
        }

        @Override
        public void onAddItemDone(AsyncResult<Void> result)
        {
        }

        @Override
        public void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result)
        {
        }

        @Override
        public void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result)
        {
        }

        @Override
        public void onAssignBilleeDone(AsyncResult<Void> result)
        {
        }

        @Override
        public void onAllBilleesDone(AsyncResult<List<String>> asyncResult)
        {
        }

        @Override
        public void onListAvailableJobsDone(AsyncResult<List<String>> result)
        {
        }

        @Override
        public void onPerformJobDone(AsyncResult<AppJobCompletion> asyncResult)
        {
        }

    }

    private void linkInputPanel()
    {
        loginSupport.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
                inputPanel.clear();
            }

            @Override
            public void newSessionStarted()
            {
                inputPanel.refresh();
            }
        });

        controller.addControllerListener(new ControllerListenerAdapter()
        {
            public void onAddItemDone(AsyncResult<Void> result)
            {
                if (!result.isError())
                {
                    //messagePanel.add(new AcknowledgableMessagePanel("Item added."));
                    inputPanel.itemAdded();
                }
                else
                {
                    showError(result);
                }
            }

            public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
            {
                if (!result.isError())
                {
                    inputPanel.itemsRefreshed(result.getReturned());
                }
                else
                {
                    showError(result);
                }
            }
        });
    }

    private void linkThis()
    {
        logoutAnchor.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                loginSupport.logout();
            }
        });

        serverInfoLabel.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                serverInfoLabel.setText("[querying...]");
                controller.serverInfo();
            }
        });

        controller.addControllerListener(new ControllerListenerAdapter()
            {
                @Override
                public void serverInfoRecieved(String info)
                {
                    serverInfoLabel.setText(info);
                }

                @Override
                public void onBranded(AsyncResult<BrandInfo> result)
                {
                    if (!result.isError())
                    {
                        handleBrandInfo(result.getReturned());
                    }
                    else
                    {
                        GWT.log("Leaving unbranded.");
                    }
                }

                @Override
                public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
                {
                    Window.setTitle(
                            (!result.isError() && options.isCurrentTaskInTitlebar())
                            ? renderTitlebar(Checks.mapNullTo(findCurrentStartTag(result.getReturned()), UnknownTag).getDescription())
                                    : originalWindowTitle);
                }
            });
    }

    private void linkReportPanel()
    {
        loginSupport.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
                reportPanel.clear();
            }

            @Override
            public void newSessionStarted()
            {
                refreshTotals();
            }
        });

        controller.addControllerListener(new ControllerListenerAdapter()
            {
                @Override
                public void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result)
                {
                    if (result.isError())
                    {
                        showError(result);
                    }
                    else
                    {
                        reportPanel.setResults(result.getReturned());
                    }
                }

                @Override
                public void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result)
                {
                    if (result.isError())
                    {
                        showError(result);
                    }
                    else
                    {
                        List<AssignedTaskTotal> report = result.getReturned();

                        reportPanel.setResultsAssigned(report);
                        // TODO: continue factoring up, out of ui.
                        prorataPresenter.setStuff(report);
                    }
                }

                @Override
                public void onAssignBilleeDone(AsyncResult<Void> result)
                {
                    if (result.isError())
                    {
                        showError(result);
                    }
                    else
                    {
                        refreshTotals();
                    }
                }

                @Override
                public void onAllBilleesDone(AsyncResult<List<String>> asyncResult)
                {
                    if (asyncResult.isError())
                    {
                        showError(asyncResult);
                    }
                    else
                    {
                        reportPanel.setBillees(asyncResult.getReturned());
                    }
                }

            });

        reportPanel.addReportPanelListener(new IReportPanelListener()
        {
            @Override
            public void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
            {
                refreshTotals(startingTimeText, endingTimeText, allowWords, ignoreWords);
            }

            @Override
            public void billeeUpdateRequested(String description, String newBillee)
            {
                controller.startAssignBillee(description, newBillee);
            }
        });

        controller.startGetAllBillees();
    }

    private void refreshTotals()
    {
        IParamPanel params = reportPanel.getParamsPanel();
        refreshTotals(
                params.getStartingTimeRendered(),
                params.getEndingTimeRendered(),
                Arrays.asList(params.getAllowWords().getText().split(",")),
                Arrays.asList(params.getIgnoreWords().getText().split(",")));
    }

    private void refreshTotals(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
    {
        controller.startRefreshTotals(
                1000,
                SortDir.desc,
                startingTimeText,
                endingTimeText,
                allowWords,
                ignoreWords);

        controller.startRefreshTotalsAssigned(
                1000,
                SortDir.desc,
                startingTimeText,
                endingTimeText,
                allowWords,
                ignoreWords);
    }

    private void linkAppJobPanel()
    {
        controller.addControllerListener(new ControllerListenerAdapter()
            {
                // move to jobs panel.
                @Override
                public void onListAvailableJobsDone(AsyncResult<List<String>> result)
                {
                    if (result.isError())
                    {
                        showError(result);
                        appJobPanel.redisplayJobIds(new ArrayList<String>());
                    }
                    else
                    {
                        appJobPanel.redisplayJobIds(result.getReturned());
                    }
                }

                @Override
                public void onPerformJobDone(AsyncResult<AppJobCompletion> result)
                {
                    if (result.isError())
                    {
                        appJobPanel.addResult("-", "call failed", result.getThrown().getMessage());
                        GWT.log("Server-side job failed: " + result.getThrown().getMessage());
                    }
                    else
                    {
                        AppJobCompletion returned = result.getReturned();
                        appJobPanel.addResult(returned.getJobId(), returned.getStatus(), returned.getDescription());
                    }
                }

            });

        appJobPanel.addListener(new IAppJobPanelListener()
        {
            @Override
            public void appJobRequested(String jobId)
            {
                controller.startPerformJob(jobId);
            }

            @Override
            public void appJobListRefreshRequested()
            {
                controller.startListAvailableJobs();
            }
        });

        controller.startListAvailableJobs();
    }

    private void updateIssuesLink(String issuesHref)
    {
        issuesLink.setHTML("<a href=\"" + issuesHref + "\" target=\"_blank\">Feedback / RFEs / Bugs</a>");
    }

    protected void handleBrandInfo(BrandInfo brandInfo)
    {
        updateIssuesLink(brandInfo.getIssueHref());
    }

    private StartTag findCurrentStartTag(List<StartTag> items)
    {
        // Since we are searching for 'now'
        // among the rendered items in history,
        // we must also use the same TZ which they
        // were rendered in, to make the lexicographical
        // comparison valid.

        String now = tzSupport.renderForClientMachine(new Date());

        for (int i = 0; i < items.size(); ++i)
        {
            if (now.compareTo(items.get(i).getInstantString()) >= 0)
            {
                return items.get(i);
            }
        }

        return null;
    }


    private void showError(AsyncResult<?> result)
    {
        // TODO: why not messagePanel?
        //   messagePanel.add(new AcknowledgableMessagePanel("No refresh happened: " + result.getThrown().getMessage()));

        String tmsg = "(nothing thrown)";

        Throwable t = result.getThrown();
        if (null != t) tmsg = t.getMessage();

        Label label = new Label(tmsg);

        VerticalPanel vp = new VerticalPanel();
        vp.add(label);

        DialogBox msgBox = new DialogBox(true);
        msgBox.setWidget(vp);
        msgBox.show();

        GWT.log("showed message: " + tmsg, null);
    }

    public String renderTitlebar(String currentTaskDescription)
    {
        return options.getTitleBarTemplate().replaceAll(IOptionsPanel.CURRENTTASK, currentTaskDescription);
    }
}
