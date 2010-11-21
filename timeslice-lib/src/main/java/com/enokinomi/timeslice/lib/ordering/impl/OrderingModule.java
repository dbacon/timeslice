package com.enokinomi.timeslice.lib.ordering.impl;

import com.enokinomi.timeslice.lib.ordering.api.IOrderingStore;
import com.enokinomi.timeslice.lib.ordering.api.IOrderingWorks;
import com.google.inject.AbstractModule;

public class OrderingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IOrderingStore.class).to(OrderingStore.class);
        bind(IOrderingWorks.class).to(OrderingWorks.class);
    }
}
