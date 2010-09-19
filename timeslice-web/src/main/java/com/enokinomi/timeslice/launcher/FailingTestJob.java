package com.enokinomi.timeslice.launcher;

import com.enokinomi.timeslice.web.gwt.server.rpc.AppJob;

public class FailingTestJob implements AppJob
{
    private final String jobId;

    public FailingTestJob(String jobId)
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
