package com.enokinomi.timeslice.lib.prorata.impl;

import com.enokinomi.timeslice.lib.prorata.api.IProrataStore;
import com.enokinomi.timeslice.lib.prorata.api.IProrataWorks;
import com.google.inject.AbstractModule;

public class ProrataModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IProrataStore.class).to(HsqldbStore.class);
        bind(IProrataWorks.class).to(ProrataWorks.class);
    }

}
