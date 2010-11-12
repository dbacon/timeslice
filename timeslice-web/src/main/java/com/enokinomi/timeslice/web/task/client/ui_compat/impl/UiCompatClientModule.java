package com.enokinomi.timeslice.web.task.client.ui_compat.impl;

import com.enokinomi.timeslice.web.task.client.ui_compat.api.ITs107Reader;
import com.google.gwt.inject.client.AbstractGinModule;

public class UiCompatClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(ITs107Reader.class).to(Ts107Reader.class);
    }

}
