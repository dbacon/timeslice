package com.enokinomi.timeslice.core;

import com.enokinomi.timeslice.web.gwt.server.session.SessionDataProvider;
import com.enokinomi.timeslice.web.gwt.server.session.SessionTracker;
import com.google.inject.AbstractModule;

public class SessionModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(SessionTracker.class).asEagerSingleton();
        bind(SessionDataProvider.class);
    }

}
