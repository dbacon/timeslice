package com.enokinomi.timeslice.web.task.client.ui_one;

import com.enokinomi.timeslice.web.appjob.client.ui.AppJobPanel;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({UiOneClientModule.class})
public interface UiOneGinjector extends Ginjector
{
    AppJobPanel getAppJobPanel();
}
