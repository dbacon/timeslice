package com.enokinomi.timeslice.web.task.client.presenter;

import com.enokinomi.timeslice.web.task.client.ui.HistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.HotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.IHistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.IHotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.IImportBulkItemsDialog;
import com.enokinomi.timeslice.web.task.client.ui.ImportBulkItemsDialog;
import com.enokinomi.timeslice.web.task.client.ui.InputPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class TaskClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(IImportBulkItemsDialog.class).to(ImportBulkItemsDialog.class).in(Singleton.class);
        bind(InputPanel.class).in(Singleton.class);
        bind(IHistoryPanel.class).to(HistoryPanel.class).in(Singleton.class);
        bind(IHotlistPanel.class).to(HotlistPanel.class).in(Singleton.class);
        bind(ITaskPresenter.class).to(TaskPresenter.class).in(Singleton.class);
    }
}
