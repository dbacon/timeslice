package com.enokinomi.timeslice.lib.task;

import com.google.inject.AbstractModule;

public class TaskModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(ITimesliceStore.class).to(HsqldbTimesliceStore.class);
        bind(ITimesliceWorks.class).to(TimesliceWorks.class);
        bind(ISafeDirProvider.class).to(SafeDirProvider.class);
    }

}
