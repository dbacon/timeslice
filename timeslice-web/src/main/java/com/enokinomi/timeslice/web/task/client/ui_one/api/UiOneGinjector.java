package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.task.client.ui_one.impl.UiOneClientModule;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({UiOneClientModule.class})
public interface UiOneGinjector extends Ginjector
{
    ITimesliceApp getTimesliceApp();
}
