package com.enokinomi.timeslice.lib.task;

import com.google.inject.AbstractModule;

public class TaskModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(ITimesliceStore.class).to(HsqldbTimesliceStore.class).asEagerSingleton();
        bind(ISafeDirProvider.class).to(SafeDirProvider.class);
    }

}
