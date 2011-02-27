package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel.IAppJobPanelListener;
import com.enokinomi.timeslice.web.appjob.client.ui.impl.IAppJobPresenter.IAppJobPresenterListener;
import com.enokinomi.timeslice.web.core.client.util.RegistrationManager;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class AppJobActivity extends AbstractActivity
{
    private final Provider<IAppJobPanel> widgetProvider;

    @SuppressWarnings("unused")
    private final AppJobPlace place;

//    private final ILoginSupport loginSupport;

    private final RegistrationManager registrations = new RegistrationManager();

    private final IAppJobPresenter presenter;

    @Inject
    AppJobActivity(Provider<IAppJobPanel> widgetProvider, AppJobPlace place, IAppJobPresenter presenter/*, ILoginSupport loginSupport*/)
    {
        this.widgetProvider = widgetProvider;
        this.place = place;
        this.presenter = presenter;
//        this.loginSupport = loginSupport;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus)
    {
        final IAppJobPanel widget = widgetProvider.get();

//        registrations.add(widget.getNavPanel().addListener(new FooterListenerImplementation(controller, loginSupport)));
//        registrations.add(controller.addControllerListener("appjob-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
//        registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));

        registrations.add(widget.addListener(new IAppJobPanelListener()
        {
            @Override
            public void appJobRequested(String jobId)
            {
                presenter.startPerformJob(jobId);
            }

            @Override
            public void appJobListRefreshRequested()
            {
                presenter.startListAvailableJobs();
            }
        }));

        registrations.add(presenter.addListener(new IAppJobPresenterListener()
        {

            @Override
            public void onListAvailableJobsDone(List<String> result)
            {
                widget.redisplayJobIds(result);
            }

            @Override
            public void onPerformJobDone(AppJobCompletion result)
            {
                AppJobCompletion completion = result;
                widget.addResult(completion.getJobId(), completion.getStatus(), completion.getDescription());
            }
        }));

        widget.initialize("app-job.activity-starting");

        panel.setWidget(widget.asWidget());
    }

    @Override
    public void onStop()
    {
        registrations.terminateAll();
    }
}
