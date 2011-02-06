package com.enokinomi.timeslice.web.branding.impl;

import com.enokinomi.timeslice.branding.api.IBranding;

class DefaultBranding implements IBranding
{
    DefaultBranding() { }

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
