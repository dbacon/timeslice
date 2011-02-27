package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.core.client.util.Registration;

public interface IAppJobPresenter
{
    public interface IAppJobPresenterListener
    {
        void onListAvailableJobsDone(List<String> result);
        void onPerformJobDone(AppJobCompletion result);
    }

    Registration addListener(IAppJobPresenterListener listener);

    void startListAvailableJobs();
    void startPerformJob(String jobId);
}
