package com.enokinomi.timeslice.launcher;

import com.enokinomi.timeslice.web.gwt.server.appjob.AppJob;

public class TestJob1 implements AppJob
{
    private final String jobId;

    public TestJob1(String jobId)
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
