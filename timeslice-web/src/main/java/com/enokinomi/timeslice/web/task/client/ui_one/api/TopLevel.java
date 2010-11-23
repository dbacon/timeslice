package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class TopLevel implements EntryPoint
{
    private final UiOneGinjector injector = GWT.create(UiOneGinjector.class);

    @Override
    public void onModuleLoad()
    {
        RootLayoutPanel.get().add(injector.getTimesliceApp().asWidget());
    }
}
