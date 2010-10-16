package com.enokinomi.timeslice.web.task.client.core_todo_move_out;

import java.io.Serializable;

public class BrandInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String projectHref;
    private String issueHref;

    public BrandInfo()
    {
    }

    public BrandInfo(String projectHref, String issueHref)
    {
        this.projectHref = projectHref;
        this.issueHref = issueHref;
    }

    public String getProjectHref()
    {
        return projectHref;
    }

    public void setProjectHref(String projectHref)
    {
        this.projectHref = projectHref;
    }

    public String getIssueHref()
    {
        return issueHref;
    }

    public void setIssueHref(String issueHref)
    {
        this.issueHref = issueHref;
    }

}
