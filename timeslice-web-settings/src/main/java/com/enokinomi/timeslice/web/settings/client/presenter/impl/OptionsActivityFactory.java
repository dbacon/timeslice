package com.enokinomi.timeslice.web.settings.client.presenter.impl;

import com.enokinomi.timeslice.web.core.client.util.IActivityFactory;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.settings.client.ui.api.IOptionsPanel;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class OptionsActivityFactory implements IActivityFactory
{
    private final Provider<IOptionsPanel> widgetProvider;
//    private final ILoginSupport loginSupport;
    private final ISettingsPresenter settingsPresenter;

    @Inject
    public OptionsActivityFactory(Provider<IOptionsPanel> widgetProvider, /*ILoginSupport loginSupport, */ISettingsPresenter settingsPresenter)
    {
        this.widgetProvider = widgetProvider;
//        this.loginSupport = loginSupport;
        this.settingsPresenter = settingsPresenter;
    }

    public OptionsActivity get(Place place)
    {
        OptionsPlace specificPlace = (OptionsPlace) place;

        return new OptionsActivity(widgetProvider, specificPlace, /*loginSupport, */settingsPresenter);
    }
}
