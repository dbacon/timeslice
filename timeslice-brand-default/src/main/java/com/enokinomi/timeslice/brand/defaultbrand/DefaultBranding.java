package com.enokinomi.timeslice.brand.defaultbrand;

import com.enokinomi.timeslice.launcher.IBranding;


public class DefaultBranding implements IBranding
{
    DefaultBranding()
    {
    }

    @Override
    public String issueHref()
    {
        return "http://code.google.com/p/timeslice/issues/list";
    }

    @Override
    public String projectHref()
    {
        return "http://code.google.com/p/timeslice/";
    }

}
