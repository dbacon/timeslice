package com.enokinomi.timeslice.web.task.client.ui.impl;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.impl.LoginSupport;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.controller.impl.GwtRpcController;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsProvider;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class TaskClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(OptionsPanel.class).in(Singleton.class);

        bind(IController.class).to(GwtRpcController.class);
        bind(GwtRpcController.class).in(Singleton.class);

        bind(IOptionsProvider.class).to(OptionsPanel.class);

        bind(ILoginSupport.class).to(LoginSupport.class);
        bind(LoginSupport.class).in(Singleton.class);

        bind(IHistoryPanel.class).to(HistoryPanel.class);
        bind(IReportPanel.class).to(ReportPanel.class);
        bind(IOptionsPanel.class).to(OptionsPanel.class);
        bind(IParamPanel.class).to(ParamPanel.class);
        bind(IHotlistPanel.class).to(HotlistPanel.class);

        bind(ISettingsEditorPanel.class).to(SettingsEditorPanel.class);
    }
}
