package com.enokinomi.timeslice.web.appjob.client.core;

import com.enokinomi.timeslice.web.appjob.client.ui.AppJobPanel;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({ AppJobClientModule.class })
public interface AppJobGinjector extends Ginjector
{
    AppJobPanel getAppJobPanel();
}
