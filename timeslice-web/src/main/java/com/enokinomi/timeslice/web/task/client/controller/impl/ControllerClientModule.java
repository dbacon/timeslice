package com.enokinomi.timeslice.web.task.client.controller.impl;

import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class ControllerClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(IController.class).to(GwtRpcController.class);
        bind(GwtRpcController.class).in(Singleton.class);
    }
}
