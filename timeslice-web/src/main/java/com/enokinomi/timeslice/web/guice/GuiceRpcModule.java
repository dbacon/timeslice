package com.enokinomi.timeslice.web.guice;

import java.util.Arrays;

import com.google.inject.servlet.ServletModule;

public final class GuiceRpcModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        // TODO: these don't all belong here - move to respective modules

        for (String moduleName: Arrays.asList(
                "timeslice.App",
                "com.enokinomi.timeslice.web.assign.Assign",
                "com.enokinomi.timeslice.web.appjob.AppJob",
                "com.enokinomi.timeslice.web.task.Task",
                "com.enokinomi.timeslice.web.ordering.Ordering",
                "com.enokinomi.timeslice.web.prorata.ProRata",
                "com.enokinomi.timeslice.web.settings.Settings",
                "com.enokinomi.timeslice.web.core.Core"
                ))
        {
            serve("/" + moduleName + "/gwtrpc").with(GuiceRpcService.class);
        }
    }
}
