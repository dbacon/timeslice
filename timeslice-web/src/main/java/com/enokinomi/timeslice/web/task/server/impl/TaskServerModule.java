package com.enokinomi.timeslice.web.task.server.impl;

import com.enokinomi.timeslice.lib.task.impl.TaskModule;
import com.enokinomi.timeslice.web.task.client.core.ITimesliceSvc;
import com.google.inject.AbstractModule;

public class TaskServerModule extends AbstractModule
{
    public TaskServerModule()
    {
    }

    @Override
    protected void configure()
    {
        install(new TaskModule());

        bind(ITimesliceSvc.class).to(TimesliceSvcSession.class);
    }
}
