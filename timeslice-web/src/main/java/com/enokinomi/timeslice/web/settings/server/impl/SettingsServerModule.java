package com.enokinomi.timeslice.web.settings.server.impl;

import com.enokinomi.timeslice.web.settings.client.core.ISettingsSvc;
import com.google.inject.AbstractModule;

public class SettingsServerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(ISettingsSvc.class).to(SettingsSvc.class);
    }
}
