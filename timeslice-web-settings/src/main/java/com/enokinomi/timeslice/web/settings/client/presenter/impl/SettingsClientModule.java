package com.enokinomi.timeslice.web.settings.client.presenter.impl;

import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.settings.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.settings.client.ui.impl.ISettingsEditorPanel;
import com.enokinomi.timeslice.web.settings.client.ui.impl.OptionsPanel;
import com.enokinomi.timeslice.web.settings.client.ui.impl.SettingsEditorPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class SettingsClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(IOptionsPanel.class).to(OptionsPanel.class).in(Singleton.class);
        bind(ISettingsEditorPanel.class).to(SettingsEditorPanel.class).in(Singleton.class);

        bind(ISettingsPresenter.class).to(SettingsPresenter.class).in(Singleton.class);
    }

}
