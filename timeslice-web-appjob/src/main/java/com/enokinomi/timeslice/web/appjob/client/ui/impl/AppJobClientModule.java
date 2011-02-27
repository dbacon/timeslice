package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class AppJobClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(IAppJobPanel.class).to(AppJobPanel.class);

        bind(IAppJobPresenter.class).to(AppJobPresenter.class).in(Singleton.class);
    }
}
