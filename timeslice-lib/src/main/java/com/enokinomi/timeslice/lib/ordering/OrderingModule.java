package com.enokinomi.timeslice.lib.ordering;

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
