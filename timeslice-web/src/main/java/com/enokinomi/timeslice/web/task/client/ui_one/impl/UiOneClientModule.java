package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobClientModule;
import com.enokinomi.timeslice.web.task.client.ui.impl.TaskClientModule;
import com.enokinomi.timeslice.web.task.client.ui_one.api.IImportBulkItemsDialog;
import com.enokinomi.timeslice.web.task.client.ui_one.api.ITimesliceApp;
import com.google.gwt.inject.client.AbstractGinModule;

public class UiOneClientModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        install(new AppJobClientModule());
        install(new TaskClientModule());

        bind(IImportBulkItemsDialog.class).to(ImportBulkItemsDialog.class);
        bind(ITimesliceApp.class).to(TimesliceApp.class);
    }
}
