package com.enokinomi.timeslice.web.appjob.server.impl;

import java.util.List;

import com.enokinomi.timeslice.lib.appjob.api.IAppJobProcessor;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.core.IAppJobSvc;
import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.google.inject.Inject;

public class AppJobSvc implements IAppJobSvc
{
    private final IAppJobProcessor appJobSvc;

    @Inject
    AppJobSvc(IAppJobProcessor appJobSvc)
    {
        this.appJobSvc = appJobSvc;
    }

    @Override
    public List<String> getAvailableJobIds(String authToken) throws NotAuthenticException
    {
        return appJobSvc.getAvailableJobIds();
    }

    @Override
    public AppJobCompletion performJob(String authToken, String jobId) throws NotAuthenticException
    {
        return new ITransform<com.enokinomi.timeslice.lib.appjob.api.AppJobCompletion, AppJobCompletion>()
        {
            @Override
            public AppJobCompletion apply(com.enokinomi.timeslice.lib.appjob.api.AppJobCompletion r)
            {
                return new AppJobCompletion(r.getJobId(), r.getStatus(), r.getDescription());
            }
        }.apply(appJobSvc.performJob(jobId));
    }

}
