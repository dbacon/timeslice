package com.enokinomi.timeslice.web.task.client.ui_one;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobClientModule;
import com.enokinomi.timeslice.web.task.client.ui.TaskClientModule;
import com.google.gwt.inject.client.AbstractGinModule;

public class UiOneClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        install(new AppJobClientModule());
        install(new TaskClientModule());

        bind(TimesliceApp.class);
    }
}
