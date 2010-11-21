package com.enokinomi.timeslice.web.assign.server.impl;

import com.enokinomi.timeslice.lib.assign.impl.AssignModule;
import com.enokinomi.timeslice.web.assign.client.core.IAssignmentSvc;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class AssignServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new AssignModule());

        bind(IAssignmentSvc.class).to(AssignmentSvcSession.class);
        bind(String.class).annotatedWith(Names.named("assignDefault")).toInstance("");
    }
}
