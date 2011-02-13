package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class TopLevel implements EntryPoint
{
    private final UiOneGinjector injector = GWT.create(UiOneGinjector.class);

    @Override
    public void onModuleLoad()
    {
        ITimesliceApp timesliceApp = injector.getTimesliceApp();
        IController controller = injector.getController();
        IProrataManagerPresenter prorataPresenter = injector.getProrataPresenter();
        ISettingsPresenter settingsPresenter = injector.getSettingsPresenter();

        timesliceApp.bind(controller, prorataPresenter, settingsPresenter);

        RootLayoutPanel.get().add(timesliceApp.asWidget());

        timesliceApp.startup();
    }
}
