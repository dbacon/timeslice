package com.enokinomi.timeslice.web.branding.server.impl;

import com.enokinomi.timeslice.web.branding.client.core.IBrandingSvc;
import com.google.inject.AbstractModule;

public class BrandingServerModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(IBrandingSvc.class).to(BrandingSvc.class);
    }

}
