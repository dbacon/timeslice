package com.enokinomi.timeslice.web.gwt.server.guice;

import com.google.inject.servlet.ServletModule;

public final class GuiceRpcModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        serve("/timeslice.App/gwtrpc").with(GuiceRpcService.class);
    }
}
