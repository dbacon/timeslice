package com.enokinomi.timeslice.appjob.stockjobs;

import com.enokinomi.timeslice.web.gwt.server.appjob.AppJob;

public class TestJob1 implements AppJob
{
    private final String jobId;

    TestJob1(String jobId)
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
        return "[doing " + jobId + " ... worked]";
    }

}
