package com.enokinomi.timeslice.web.branding.server.impl;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.enokinomi.timeslice.branding.api.IBranding;
import com.enokinomi.timeslice.web.branding.client.core.BrandInfo;
import com.enokinomi.timeslice.web.branding.client.core.IBrandingSvc;
import com.google.inject.Inject;

public class BrandingSvc implements IBrandingSvc
{
    private final IBranding branding;

    @Inject
    public BrandingSvc(IBranding branding)
    {
        this.branding = branding;
    }

    @Override
    public String serverInfo()
    {
        String version = "version-unknown";

        InputStream versionIs = ClassLoader.getSystemResourceAsStream("timeslice-version");

        if (null != versionIs)
        {
            try
            {
                version = IOUtils.toString(versionIs, "UTF-8");
            }
            catch (Exception e)
            {
            }
        }

        return version;
    }

    @Override
    public BrandInfo getBrandInfo()
    {
        return new BrandInfo(branding.projectHref(), branding.issueHref());
    }
}
