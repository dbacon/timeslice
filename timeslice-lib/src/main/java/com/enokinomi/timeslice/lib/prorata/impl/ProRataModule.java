package com.enokinomi.timeslice.lib.prorata.impl;

import com.enokinomi.timeslice.lib.prorata.api.IProRataStore;
import com.enokinomi.timeslice.lib.prorata.api.IProRataWorks;
import com.google.inject.AbstractModule;

public class ProRataModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IProRataStore.class).to(HsqldbStore.class);
        bind(IProRataWorks.class).to(ProRataWorks.class);
    }

}
