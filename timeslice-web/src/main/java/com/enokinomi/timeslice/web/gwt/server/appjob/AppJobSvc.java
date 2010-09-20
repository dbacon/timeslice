package com.enokinomi.timeslice.web.gwt.server.appjob;

import java.util.List;

import com.enokinomi.timeslice.lib.appjob.AppJobProcessor;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.gwt.client.appjob.core.AppJobCompletion;
import com.enokinomi.timeslice.web.gwt.client.appjob.core.IAppJobSvc;
import com.enokinomi.timeslice.web.gwt.client.core.NotAuthenticException;
import com.google.inject.Inject;

public class AppJobSvc implements IAppJobSvc
{
    private final AppJobProcessor appJobSvc;

    @Inject
    public AppJobSvc(AppJobProcessor appJobSvc)
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
        return new ITransform<com.enokinomi.timeslice.lib.appjob.AppJobCompletion, AppJobCompletion>()
        {
            @Override
            public AppJobCompletion apply(com.enokinomi.timeslice.lib.appjob.AppJobCompletion r)
            {
                return new AppJobCompletion(r.getJobId(), r.getStatus(), r.getDescription());
            }
        }.apply(appJobSvc.performJob(jobId));
    }

}
