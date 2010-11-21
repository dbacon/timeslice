package com.enokinomi.timeslice.web.appjob.server.impl;

import com.enokinomi.timeslice.lib.appjob.impl.AppJobModule;
import com.enokinomi.timeslice.lib.appjobs.stock.StockJobsModule;
import com.enokinomi.timeslice.web.appjob.client.core.IAppJobSvc;
import com.google.inject.AbstractModule;

public class AppJobServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new AppJobModule());
        install(new StockJobsModule());
        bind(IAppJobSvc.class).to(AppJobSvc.class);
    }
}
