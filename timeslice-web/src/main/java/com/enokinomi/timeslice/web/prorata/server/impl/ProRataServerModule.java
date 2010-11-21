package com.enokinomi.timeslice.web.prorata.server.impl;

import com.enokinomi.timeslice.lib.prorata.impl.ProRataModule;
import com.enokinomi.timeslice.web.prorata.client.core.IProRataSvc;
import com.google.inject.AbstractModule;

public class ProRataServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new ProRataModule());

        bind(IProRataSvc.class).to(ProRataSvc.class);
    }
}
