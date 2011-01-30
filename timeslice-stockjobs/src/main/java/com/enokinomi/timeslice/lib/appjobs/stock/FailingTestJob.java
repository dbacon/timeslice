package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;

public class FailingTestJob implements AppJob
{
    private final String jobId;

    FailingTestJob(String jobId)
    {
        this.jobId = jobId;
    }

    @Override
    public String getJobId()
    {
        return jobId;
    }

    @Override
    public String perform()
    {
        throw new RuntimeException("[test-job '" + jobId + "' fails]");
    }

}
