package com.enokinomi.timeslice.web.report.client.presenter;

import com.enokinomi.timeslice.web.assign.client.ui.impl.AssignClientModule;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProrataClientModule;
import com.enokinomi.timeslice.web.report.client.ui.IParamPanel;
import com.enokinomi.timeslice.web.report.client.ui.IReportPanel;
import com.enokinomi.timeslice.web.report.client.ui.ParamPanel;
import com.enokinomi.timeslice.web.report.client.ui.ReportPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class ReportClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        install(new AssignClientModule());
        install(new ProrataClientModule());

        bind(IReportPanel.class).to(ReportPanel.class).in(Singleton.class);
        bind(IParamPanel.class).to(ParamPanel.class).in(Singleton.class);

        bind(IReportPresenter.class).to(ReportPresenter.class).in(Singleton.class);
    }

}
