package com.enokinomi.timeslice.lib.appjob.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.appjob.api.AppJobCompletion;
import com.enokinomi.timeslice.lib.appjob.api.IAppJobProcessor;
import com.google.inject.Inject;

public class AppJobProcessor implements IAppJobProcessor
{
    private final Set<AppJob> appJobs;

    private final Map<String, AppJob> jobsById = new LinkedHashMap<String, AppJob>();

    @Inject
    AppJobProcessor(Set<AppJob> appJobs)
    {
        this.appJobs = appJobs;

        for (AppJob job: appJobs)
        {
            jobsById.put(job.getJobId(), job);
        }
    }

    @Override
    public List<String> getAvailableJobIds()
    {
        List<String> ids = new ArrayList<String>(appJobs.size());
        for (AppJob job: appJobs)
        {
            ids.add(job.getJobId());
        }
        return ids;
    }

    @Override
    public AppJobCompletion performJob(String jobId)
    {
        AppJob requestedJob = jobsById.get(jobId);

        if (null != requestedJob)
        {
            try
            {
                return new AppJobCompletion(jobId, "ok", requestedJob.perform());
            }
            catch (Exception e)
            {
                return new AppJobCompletion(jobId, "failed", e.getMessage());
            }
        }
        else
        {
            return new AppJobCompletion("-", "not run", "job not found");
        }
    }

}
