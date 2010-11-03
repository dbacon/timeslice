package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import com.enokinomi.timeslice.web.task.client.ui_one.api.ITimesliceApp;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({UiOneClientModule.class})
public interface UiOneGinjector extends Ginjector
{
    ITimesliceApp getTimesliceApp();
}
