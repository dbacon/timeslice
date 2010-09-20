package com.enokinomi.timeslice.branding;

public class DefaultBranding implements IBranding
{
    @Override
    public String projectHref()
    {
        return "#";
    }

    @Override
    public String issueHref()
    {
        return "#";
    }
}
