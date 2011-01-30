package com.enokinomi.timeslice.lib.appjob.impl;

import com.enokinomi.timeslice.lib.appjob.api.IAppJobProcessor;
import com.google.inject.AbstractModule;

public class AppJobModule extends AbstractModule
{
    public AppJobModule()
    {
    }

    @Override
    protected void configure()
    {
        bind(IAppJobProcessor.class).to(AppJobProcessor.class);
    }

}
