package com.enokinomi.timeslice.web.launcher;

import com.google.inject.AbstractModule;

public class TsWebLaunchModule extends AbstractModule
{
    TsWebLaunchModule()
    {
    }

    @Override
    protected void configure()
    {
        bind(TsHost.class);
    }
}
