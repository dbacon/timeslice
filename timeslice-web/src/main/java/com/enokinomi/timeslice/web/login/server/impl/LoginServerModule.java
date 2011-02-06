package com.enokinomi.timeslice.web.login.server.impl;

import com.enokinomi.timeslice.web.login.client.core.ILoginSvc;
import com.google.inject.AbstractModule;

public class LoginServerModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(ILoginSvc.class).to(LoginSvc.class);
    }

}
