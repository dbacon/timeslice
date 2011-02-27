package com.enokinomi.timeslice.web.task.client.presenter;

import com.enokinomi.timeslice.web.core.client.util.IActivityFactory;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.ui.InputPanel;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class InputActivityFactory implements IActivityFactory
{
    private final Provider<InputPanel> widgetProvider;
    private final PlaceController placeController;
    private final ISettingsPresenter settingsPresenter;
    private final ITaskPresenter presenter;

    @Inject
    InputActivityFactory(Provider<InputPanel> widgetProvider, PlaceController placeController, ITaskPresenter presenter, ISettingsPresenter settingsPresenter)
    {
        this.widgetProvider = widgetProvider;
        this.placeController = placeController;
        this.presenter = presenter;
        this.settingsPresenter = settingsPresenter;
    }

    public InputActivity get(Place place)
    {
        InputPlace inputPlace = (InputPlace) place;

        return new InputActivity("factory:from-place(" + inputPlace.getCreator() + inputPlace.isCurrent() + "," + inputPlace.getWhen() + ")",  widgetProvider, inputPlace, placeController, presenter, settingsPresenter);
    }
}
