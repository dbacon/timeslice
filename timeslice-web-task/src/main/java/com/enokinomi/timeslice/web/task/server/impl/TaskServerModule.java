package com.enokinomi.timeslice.web.task.server.impl;

import com.enokinomi.timeslice.lib.task.impl.TaskModule;
import com.enokinomi.timeslice.web.task.client.core.ITaskSvc;
import com.google.inject.AbstractModule;

public class TaskServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new TaskModule());

        bind(ITaskSvc.class).to(TaskSvc.class);
    }
}
