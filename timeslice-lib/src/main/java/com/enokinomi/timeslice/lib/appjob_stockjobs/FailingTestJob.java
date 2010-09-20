package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.appjob.AppJob;

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
