package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import java.util.Arrays;

import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobClientModule;
import com.enokinomi.timeslice.web.assign.client.ui.impl.AssignClientModule;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.login.client.ui.impl.LoginClientModule;
import com.enokinomi.timeslice.web.prorata.client.presenter.impl.ProrataPresenterClientModule;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProrataClientModule;
import com.enokinomi.timeslice.web.settings.client.presenter.impl.SettingsClientModule;
import com.enokinomi.timeslice.web.task.client.ui.impl.TaskClientModule;
import com.enokinomi.timeslice.web.task.client.ui_compat.impl.UiCompatClientModule;
import com.enokinomi.timeslice.web.task.client.ui_one.api.GenericActivityMapper;
import com.enokinomi.timeslice.web.task.client.ui_one.api.TopLevel.AppJobPlace;
import com.enokinomi.timeslice.web.task.client.ui_one.api.TopLevel.InputPlace;
import com.enokinomi.timeslice.web.task.client.ui_one.api.TopLevel.OptionsPlace;
import com.enokinomi.timeslice.web.task.client.ui_one.api.TopLevel.ReportPlace;
import com.enokinomi.timeslice.web.task.client.ui_one.api.TopLevel.TimeslicePlaceHistoryMapper;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class UiOneClientModule extends AbstractGinModule
{
    @Provides @Singleton PlaceController createPlaceController(EventBus eventBus)
    {
        GWT.log("created place-controller");
        return new PlaceController(eventBus);
    }

    @Provides @Singleton EventBus createEventBus()
    {
        GWT.log("created event-bus");
        return new SimpleEventBus();
    }

    @Provides @Singleton ActivityManager createActivityManager(GenericActivityMapper activityMapper, EventBus eventBus)
    {
        GWT.log("created activity-manager");
        return new ActivityManager(activityMapper, eventBus);
    }

    @Provides @Singleton PlaceHistoryHandler createPlaceHistoryHandler(TimeslicePlaceHistoryMapper historyMapper)
    {
        GWT.log("created place-history-handler");
        return new PlaceHistoryHandler(historyMapper);
    }

    @Provides @Named("populated") NavPanel createNavPanel(NavPanel navPanel, final PlaceController placeController)
    {
        navPanel.populateLeft(Arrays.asList(
                new InputPlace("nav-panel", true, null),
                new ReportPlace(null, null)
                ));
        navPanel.populateRight(Arrays.asList(
                new OptionsPlace(),
                new AppJobPlace()
                ));

        navPanel.addListener(new NavPanel.Listener()
        {
            @Override
            public void navigateLinkClicked(Place place)
            {
                placeController.goTo(place);
            }
        });

        return navPanel;
    }


    @Override
    protected void configure()
    {
        install(new LoginClientModule());
        install(new AssignClientModule());
        install(new AppJobClientModule());
        install(new SettingsClientModule());
        install(new ProrataPresenterClientModule());
        install(new ProrataClientModule());
        install(new TaskClientModule());
        install(new UiCompatClientModule());

        bind(GenericActivityMapper.class).in(Singleton.class);
        bind(TimeslicePlaceHistoryMapper.class).in(Singleton.class);
        bind(PlaceHistoryMapper.class).to(TimeslicePlaceHistoryMapper.class);
    }
}
