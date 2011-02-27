package com.enokinomi.timeslice.web.login.client.ui.impl;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class LoginClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(ILoginSupport.class).to(LoginSupport.class);
        bind(LoginSupport.class).in(Singleton.class);
    }

}
