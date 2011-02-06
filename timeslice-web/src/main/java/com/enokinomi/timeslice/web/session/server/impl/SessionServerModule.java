package com.enokinomi.timeslice.web.session.server.impl;

import com.enokinomi.timeslice.web.session.client.core.ISessionSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class SessionServerModule extends AbstractModule
{
    private final String aclFilename;

    public SessionServerModule(String aclFilename)
    {
        this.aclFilename = aclFilename;
    }

    @Override
    protected void configure()
    {
        bind(ISessionTracker.class).to(SessionTracker.class).asEagerSingleton();
        bind(SessionDataProvider.class);

        bind(String.class).annotatedWith(Names.named("acl")).toInstance(aclFilename);

        bind(ISessionSvc.class).to(SessionSvc.class);
    }

}
