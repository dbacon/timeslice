package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.UiOneClientModule;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({UiOneClientModule.class})
public interface UiOneGinjector extends Ginjector
{
    ITimesliceApp getTimesliceApp();

    IProrataManagerPresenter getProrataPresenter();
    IController getController();
    ISettingsPresenter getSettingsPresenter();
}
