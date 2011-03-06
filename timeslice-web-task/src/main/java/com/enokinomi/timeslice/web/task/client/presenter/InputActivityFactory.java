package com.enokinomi.timeslice.web.task.client.presenter;

import com.enokinomi.timeslice.web.core.client.util.IActivityFactory;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.ui.InputPanel;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class InputActivityFactory implements IActivityFactory
{
    private final Provider<InputPanel> widgetProvider;
    private final ISettingsPresenter settingsPresenter;
    private final ITaskPresenter presenter;

    @Inject
    InputActivityFactory(Provider<InputPanel> widgetProvider, ITaskPresenter presenter, ISettingsPresenter settingsPresenter)
    {
        this.widgetProvider = widgetProvider;
        this.presenter = presenter;
        this.settingsPresenter = settingsPresenter;
    }

    public InputActivity get(Place place)
    {
        InputPlace inputPlace = (InputPlace) place;

        return new InputActivity(widgetProvider, inputPlace, presenter, settingsPresenter);
    }
}
