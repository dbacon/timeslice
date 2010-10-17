package com.enokinomi.timeslice.web.session.server.impl;

import com.enokinomi.timeslice.lib.userinfo.UserInfoModule;
import com.enokinomi.timeslice.web.session.client.core.ISessionSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class SessionModule extends AbstractModule
{
    private final String aclFilename;

    public SessionModule(String aclFilename)
    {
        this.aclFilename = aclFilename;
    }

    @Override
    protected void configure()
    {
        install(new UserInfoModule());

        bind(ISessionTracker.class).to(SessionTracker.class).asEagerSingleton();
        bind(SessionDataProvider.class);

        bind(String.class).annotatedWith(Names.named("acl")).toInstance(aclFilename);

        bind(ISessionSvc.class).to(SessionSvc.class);
    }

}
