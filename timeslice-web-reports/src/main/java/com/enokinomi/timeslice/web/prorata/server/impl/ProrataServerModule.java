package com.enokinomi.timeslice.web.prorata.server.impl;

import com.enokinomi.timeslice.lib.prorata.impl.ProrataModule;
import com.enokinomi.timeslice.web.prorata.client.core.IProrataSvc;
import com.google.inject.AbstractModule;

public class ProrataServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new ProrataModule());

        bind(IProrataSvc.class).to(ProrataSvc.class);
    }
}
