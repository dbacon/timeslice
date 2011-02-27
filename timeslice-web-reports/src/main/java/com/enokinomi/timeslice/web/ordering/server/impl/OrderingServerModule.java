package com.enokinomi.timeslice.web.ordering.server.impl;

import com.enokinomi.timeslice.lib.ordering.impl.OrderingModule;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvc;
import com.google.inject.AbstractModule;

public class OrderingServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new OrderingModule());

        bind(IOrderingSvc.class).to(OrderingSvc.class);
    }
}
