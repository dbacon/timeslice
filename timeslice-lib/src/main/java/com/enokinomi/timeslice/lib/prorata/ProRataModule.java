package com.enokinomi.timeslice.lib.prorata;

import com.google.inject.AbstractModule;

public class ProRataModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IProRataStore.class).to(HsqldbStore.class);
    }

}
