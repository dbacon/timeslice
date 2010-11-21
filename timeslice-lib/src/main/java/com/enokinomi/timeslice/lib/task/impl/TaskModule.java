package com.enokinomi.timeslice.lib.task.impl;

import com.enokinomi.timeslice.lib.task.api.ISafeDirProvider;
import com.enokinomi.timeslice.lib.task.api.ITimesliceStore;
import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.task.api.ITimesliceWorks;
import com.google.inject.AbstractModule;

public class TaskModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(ITimesliceSvc.class).to(TimesliceSvc.class);
        bind(ITimesliceStore.class).to(HsqldbTimesliceStore.class);
        bind(ITimesliceWorks.class).to(TimesliceWorks.class);
        bind(ISafeDirProvider.class).to(SafeDirProvider.class);
    }

}
