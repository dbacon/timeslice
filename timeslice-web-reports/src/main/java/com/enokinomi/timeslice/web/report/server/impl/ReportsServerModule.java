package com.enokinomi.timeslice.web.report.server.impl;

import com.enokinomi.timeslice.web.report.client.core.IReportsSvc;
import com.google.inject.AbstractModule;

public class ReportsServerModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(IReportsSvc.class).to(ReportsSvcSession.class);
    }

}
