package com.enokinomi.timeslice.web.task.client.ui.impl;

import com.enokinomi.timeslice.web.task.client.controller.impl.ControllerClientModule;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui_one.api.IImportBulkItemsDialog;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.ImportBulkItemsDialog;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.InputPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class TaskClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        install(new ControllerClientModule());

        bind(IImportBulkItemsDialog.class).to(ImportBulkItemsDialog.class).in(Singleton.class);
        bind(InputPanel.class).in(Singleton.class);
        bind(IHistoryPanel.class).to(HistoryPanel.class).in(Singleton.class);
        bind(IHotlistPanel.class).to(HotlistPanel.class).in(Singleton.class);

        bind(IReportPanel.class).to(ReportPanel.class).in(Singleton.class);
        bind(IParamPanel.class).to(ParamPanel.class).in(Singleton.class);

        bind(IOptionsPanel.class).to(OptionsPanel.class).in(Singleton.class);
        bind(ISettingsEditorPanel.class).to(SettingsEditorPanel.class).in(Singleton.class);
    }
}
