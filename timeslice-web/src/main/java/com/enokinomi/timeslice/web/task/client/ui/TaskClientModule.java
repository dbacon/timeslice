package com.enokinomi.timeslice.web.task.client.ui;

import com.enokinomi.timeslice.web.task.client.controller.GwtRpcController;
import com.enokinomi.timeslice.web.task.client.controller.IAuthTokenHolder;
import com.enokinomi.timeslice.web.task.client.controller.IController;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class TaskClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(OptionsPanel.class);

        bind(IController.class).to(GwtRpcController.class);
        bind(IAuthTokenHolder.class).to(GwtRpcController.class);

        bind(GwtRpcController.class).in(Singleton.class);
    }
}
