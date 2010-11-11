package com.enokinomi.timeslice.lib.ordering2;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class Ordering2Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IOrderingStore.class)
            .to(OrderingStore.class)
            .in(Singleton.class);
    }
}
