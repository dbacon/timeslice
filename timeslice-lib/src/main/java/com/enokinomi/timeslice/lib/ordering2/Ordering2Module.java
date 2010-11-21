package com.enokinomi.timeslice.lib.ordering2;

import com.google.inject.AbstractModule;

public class Ordering2Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IOrderingStore.class).to(OrderingStore.class);
        bind(IOrderingWorks.class).to(OrderingWorks.class);
    }
}
