package com.enokinomi.timeslice.web.settings.client.presenter.impl;

import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class SettingsClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(ISettingsPresenter.class).to(SettingsPresenter.class).in(Singleton.class);
    }

}
