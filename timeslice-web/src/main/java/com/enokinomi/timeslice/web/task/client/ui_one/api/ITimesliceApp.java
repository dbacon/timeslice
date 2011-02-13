package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.google.gwt.user.client.ui.IsWidget;

public interface ITimesliceApp extends IsWidget
{
    void bind(IController presenter, IProrataManagerPresenter prorataPresenter, ISettingsPresenter settingsPresenter);

    void startup();
}
