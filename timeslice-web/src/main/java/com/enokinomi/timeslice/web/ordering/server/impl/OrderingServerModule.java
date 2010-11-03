package com.enokinomi.timeslice.web.ordering.server.impl;

import com.enokinomi.timeslice.lib.ordering.OrderingModule;
import com.enokinomi.timeslice.lib.ordering2.Ordering2Module;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvc;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvc2;
import com.google.inject.AbstractModule;

public class OrderingServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new OrderingModule());
        install(new Ordering2Module());

        bind(IOrderingSvc.class).to(OrderingSvc.class);
        bind(IOrderingSvc2.class).to(OrderingSvc2.class);
    }
}
