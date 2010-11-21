package com.enokinomi.timeslice.web.task.server.impl;

import java.io.File;

import com.enokinomi.timeslice.lib.task.impl.TaskModule;
import com.enokinomi.timeslice.web.task.client.core.ITimesliceSvc;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class TaskServerModule extends AbstractModule
{
    private final String safeDir;

    public TaskServerModule(String safeDir)
    {
        this.safeDir = safeDir;
    }

    @Override
    protected void configure()
    {
        install(new TaskModule());

        bind(ITimesliceSvc.class).to(TimesliceSvcSession.class);

        bind(File.class).annotatedWith(Names.named("safeDir")).toInstance(new File(safeDir));
    }
}
