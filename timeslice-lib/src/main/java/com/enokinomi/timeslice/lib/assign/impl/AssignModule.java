package com.enokinomi.timeslice.lib.assign.impl;

import com.enokinomi.timeslice.lib.assign.api.INowProvider;
import com.enokinomi.timeslice.lib.assign.api.ITagStore;
import com.enokinomi.timeslice.lib.assign.api.ITagWorks;
import com.google.inject.AbstractModule;

public class AssignModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(ITagStore.class).to(HsqldbTagStore.class);
        bind(ITagWorks.class).to(HsqldbTagWorks.class);

        // this may move down, so more modules can use it.
        bind(INowProvider.class).to(RealtimeNowProvider.class);
    }

}
