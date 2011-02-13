package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import java.util.ArrayList;
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
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanelListener;
import com.enokinomi.timeslice.web.task.client.ui.impl.OptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui_one.api.ITimesliceApp;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TimesliceApp extends ResizeComposite implements ITimesliceApp
{
    private static TimesliceAppUiBinder uiBinder = GWT.create(TimesliceAppUiBinder.class);
    interface TimesliceAppUiBinder extends UiBinder<Widget, TimesliceApp> { }

    @UiField protected HTML issuesLink;
    @UiField protected Label serverInfoLabel;
    @UiField protected Anchor logoutAnchor;
    @UiField protected Anchor inputLink;
    @UiField protected Anchor reportLink;
    @UiField protected Anchor optionsLink;
    @UiField protected Anchor jobsLink;

    @UiField protected InputPanel inputPanel;
    @UiField protected IReportPanel reportPanel;
    @UiField protected IOptionsPanel optionsPanel;
    @UiField protected IAppJobPanel appJobPanel;

    private final ILoginSupport loginSupport; // LEFTOFFHERE - move login + tzsupport into controller, unify controllers (?)
    private final TzSupport tzSupport;

    private static class Options
    {
        public boolean isCurrentTaskInTitlebar() { return false; }
        public String getTitleBarTemplate() { return "[TS] " + IOptionsPanel.CURRENTTASK; }
    }

    private Options options = new Options();

    private String originalWindowTitle;
    private static final StartTag UnknownTag = new StartTag(null, null, null, "-unknown-", false);

    @Inject
    TimesliceApp(ILoginSupport loginSupport, TzSupport tzSupport)
    {
        this.loginSupport = loginSupport;
        this.tzSupport = tzSupport;

        initWidget(uiBinder.createAndBindUi(this));

        initContents();
    }

    private void initContents()
    {
        originalWindowTitle = Window.getTitle();

        inputLink.setAccessKey('i');
        reportLink.setAccessKey('r');
        optionsLink.setAccessKey('o');
        jobsLink.setAccessKey('j');

        updateIssuesLink("#");
    }

    @Override
    public void startup()
    {
        fireServerInfoRequested();
        fireBrandingRequested();
        fireBilleesRequested(); // TODO: is this really ours? if so, a bunch more should be too.
        fireRefreshRequested();
    }

    public static interface AppListener
    {
        void refreshRequested();

        void billeesRequested();

        void brandingRequested();

        void serverInfoRequested();

        void refreshTotalsRequested(String startingTimeText,
                String endingTimeText, List<String> allowWords,
                List<String> ignoreWords);
    }

    private List<AppListener> listeners = new ArrayList<TimesliceApp.AppListener>();
    public void addAppListener(AppListener l)
    {
        if (l != null) listeners.add(l);
    }

    protected void fireRefreshRequested()
    {
        for (AppListener l: listeners) l.refreshRequested();
    }

    // TODO: is this really ours?
    protected void fireBilleesRequested()
    {
        for (AppListener l: listeners) l.billeesRequested();
    }

    protected void fireBrandingRequested()
    {
        for (AppListener l: listeners) l.brandingRequested();
    }

    protected void fireServerInfoRequested()
    {
        for (AppListener l: listeners) l.serverInfoRequested();
    }

    @Override
    public void bind(IController presenter, IProrataManagerPresenter prorataPresenter, ISettingsPresenter settingsPresenter)
    {
        linkThis(presenter);

        InputPanel.bind(inputPanel, presenter);

        reportPanel.bindProrataBits(prorataPresenter, settingsPresenter);
        reportPanel.bind(settingsPresenter);
        linkReportPanel(presenter, prorataPresenter);

        OptionsPanel.bind(optionsPanel, settingsPresenter);

        linkAppJobPanel(presenter);
    }

    private void linkThis(final IController controller)
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

        this.addAppListener(new AppListener()
        {
            @Override
            public void serverInfoRequested()
            {
                controller.serverInfo();
            }

            @Override
            public void refreshRequested()
            {
                // TODO: will be able to have our own settings w/out panels
                String starting = InputPanel.MachineFormat.format(new Date(new Date().getTime() - 60*60*24 * 1000));
                String ending = null;
                int maxItems = 40;

                controller.startRefreshItems(maxItems, starting, ending);

//                controller.startRefreshTotals(maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords)
            }

            @Override
            public void brandingRequested()
            {
                controller.startGetBranding();
            }

            @Override
            public void billeesRequested()
            {
                controller.startGetAllBillees();
            }

            @Override
            public void refreshTotalsRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
            {
                // TODO: will be able to have our own settings w/out panels
                int maxItems = 40;

                controller.startRefreshTotals(maxItems, SortDir.desc, startingTimeText, endingTimeText, allowWords, ignoreWords);
                controller.startRefreshTotalsAssigned(maxItems, SortDir.desc, startingTimeText, endingTimeText, allowWords, ignoreWords);
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

    private void linkReportPanel(final IController controller, final IProrataManagerPresenter prorataPresenter)
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
                fireRefreshRequested(); // TODO: could be more specific
//                refreshTotals();
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
                        fireRefreshRequested(); // TODO: could be more specific..
//                        refreshTotals();
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
                fireRefreshTotals(startingTimeText, endingTimeText, allowWords, ignoreWords);
            }

            @Override
            public void billeeUpdateRequested(String description, String newBillee)
            {
                controller.startAssignBillee(description, newBillee);
            }
        });
    }

    protected void fireRefreshTotals(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
    {
        for (AppListener l: listeners) l.refreshTotalsRequested(startingTimeText, endingTimeText, allowWords, ignoreWords);
    }

//    private void refreshTotals()
//    {
//        IParamPanel params = reportPanel.getParamsPanel();
//        refreshTotals(
//                params.getStartingTimeRendered(),
//                params.getEndingTimeRendered(),
//                Arrays.asList(params.getAllowWords().getText().split(",")),
//                Arrays.asList(params.getIgnoreWords().getText().split(",")));
//    }
//
//    private void refreshTotals(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
//    {
//        // TODO: do by raising event.
//        controller.startRefreshTotals(
//                1000,
//                SortDir.desc,
//                startingTimeText,
//                endingTimeText,
//                allowWords,
//                ignoreWords);
//
//        controller.startRefreshTotalsAssigned(
//                1000,
//                SortDir.desc,
//                startingTimeText,
//                endingTimeText,
//                allowWords,
//                ignoreWords);
//    }

    private void linkAppJobPanel(final IController controller)
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
