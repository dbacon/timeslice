package com.enokinomi.timeslice.lib.ordering2;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

public class Ordering2Module extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(new TypeLiteral<IOrderingStore>() {})
            .to(MemoryOrderingStore.class)
            .in(Singleton.class);
    }
}
