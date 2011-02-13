package com.enokinomi.timeslice.web.task.client.ui.impl;

import com.enokinomi.timeslice.web.task.client.controller.impl.ControllerClientModule;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.ISettingsPresenter;
import com.google.gwt.inject.client.AbstractGinModule;

public class TaskClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        install(new ControllerClientModule());

        bind(IOptionsPanel.class).to(OptionsPanel.class);
        bind(IHistoryPanel.class).to(HistoryPanel.class);
        bind(IReportPanel.class).to(ReportPanel.class);
        bind(IParamPanel.class).to(ParamPanel.class);
        bind(IHotlistPanel.class).to(HotlistPanel.class);
        bind(ISettingsEditorPanel.class).to(SettingsEditorPanel.class);
        bind(ISettingsPresenter.class).to(SettingsPresenter.class);
    }
}
