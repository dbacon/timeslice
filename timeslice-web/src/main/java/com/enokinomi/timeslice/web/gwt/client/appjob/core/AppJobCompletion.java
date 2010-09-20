package com.enokinomi.timeslice.web.gwt.client.appjob.core;

import java.io.Serializable;

public class AppJobCompletion implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String jobId;
    private String status;
    private String description;

    public AppJobCompletion()
    {
    }

    public AppJobCompletion(String jobId, String status, String description)
    {
        this.jobId = jobId;
        this.status = status;
        this.description = description;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}
