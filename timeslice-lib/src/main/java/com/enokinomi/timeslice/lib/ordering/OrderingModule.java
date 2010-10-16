package com.enokinomi.timeslice.lib.ordering;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class OrderingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(new TypeLiteral<IOrderingStore<String>>() {})
            .to(OrderingStore.class);
    }
}
