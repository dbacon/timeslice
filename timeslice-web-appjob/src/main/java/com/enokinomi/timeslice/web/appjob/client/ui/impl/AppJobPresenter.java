package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.core.IAppJobSvcAsync;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class AppJobPresenter implements IAppJobPresenter
{
    private final IAppJobSvcAsync jobSvc;
    private final ILoginSupport loginSupport;

    private final ListenerManager<IAppJobPresenterListener> listenerMgr = new ListenerManager<IAppJobPresenterListener>();

    @Override public Registration addListener(IAppJobPresenterListener listener) { return listenerMgr.addListener(listener); }
    protected void fireListAvailableJobsDone(List<String> result) { for (IAppJobPresenterListener l: listenerMgr.getListeners()) l.onListAvailableJobsDone(result); }
    protected void firePerformJobDone(AppJobCompletion result) { for (IAppJobPresenterListener l: listenerMgr.getListeners()) l.onPerformJobDone(result); }

    @Inject
    public AppJobPresenter(IAppJobSvcAsync jobSvc, ILoginSupport loginSupport)
    {
        this.jobSvc = jobSvc;
        this.loginSupport = loginSupport;
    }

    @Override
    public void startListAvailableJobs()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                jobSvc.getAvailableJobIds(loginSupport.getAuthToken(),
                        loginSupport.withRetry(this, new AsyncCallback<List<String>>()
                        {
                            @Override
                            public void onSuccess(List<String> result)
                            {
                                fireListAvailableJobsDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startPerformJob(final String jobId)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                jobSvc.performJob(loginSupport.getAuthToken(),
                        jobId,
                        loginSupport.withRetry(this, new AsyncCallback<AppJobCompletion>()
                        {
                            @Override
                            public void onSuccess(AppJobCompletion result)
                            {
                                firePerformJobDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                            }
                        }));
            }
        }.runAsync();
    }

}
