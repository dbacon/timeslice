package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enokinomi.timeslice.web.gwt.client.beans.NotAuthenticException;
import com.enokinomi.timeslice.web.gwt.client.server.IAppJobSvc;
import com.google.inject.Inject;

public class AppJobSvc implements IAppJobSvc
{
    private final Set<AppJob> appJobs;

    private final Map<String, AppJob> jobsById = new LinkedHashMap<String, AppJob>();

    @Inject
    public AppJobSvc(Set<AppJob> appJobs)
    {
        this.appJobs = appJobs;

        for (AppJob job: appJobs)
        {
            jobsById.put(job.getJobId(), job);
        }
    }

    @Override
    public List<String> getAvailableJobIds(String authToken) throws NotAuthenticException
    {
        List<String> ids = new ArrayList<String>(appJobs.size());
        for (AppJob job: appJobs)
        {
            ids.add(job.getJobId());
        }
        return ids;
    }

    @Override
    public String performJob(String jobId) throws NotAuthenticException
    {
        AppJob requestedJob = jobsById.get(jobId);

        if (null != requestedJob)
        {
            try
            {
                return "job returned: " + requestedJob.perform();
            }
            catch (Exception e)
            {
                return "job raised exception: " + e.getMessage();
            }
        }
        else
        {
            return "job not found";
        }
    }

}
