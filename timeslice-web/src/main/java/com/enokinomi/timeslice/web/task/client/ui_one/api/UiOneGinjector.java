package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.task.client.ui_one.api.TopLevel.ActivityMapRegistration;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.UiOneClientModule;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;

@GinModules({UiOneClientModule.class})
public interface UiOneGinjector extends Ginjector
{
    ActivityManager getActivityManager();
    EventBus getEventBus();
    PlaceController getPlaceController();
    PlaceHistoryHandler getPlaceHistoryHandler();
    PlaceHistoryMapper getPlaceHistoryMapper();

    ActivityMapRegistration getRegistration();

}
