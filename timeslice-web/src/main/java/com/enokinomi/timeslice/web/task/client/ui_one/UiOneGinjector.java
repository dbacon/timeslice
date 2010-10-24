package com.enokinomi.timeslice.web.task.client.ui_one;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({UiOneClientModule.class})
public interface UiOneGinjector extends Ginjector
{
    TimesliceApp getTimesliceApp();
}
