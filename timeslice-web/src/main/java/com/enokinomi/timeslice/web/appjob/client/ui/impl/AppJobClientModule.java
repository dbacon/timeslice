package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.google.gwt.inject.client.AbstractGinModule;

public class AppJobClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(IAppJobPanel.class).to(AppJobPanel.class);
    }
}
