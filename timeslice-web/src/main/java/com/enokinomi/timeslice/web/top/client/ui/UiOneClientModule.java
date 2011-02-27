package com.enokinomi.timeslice.web.top.client.ui;

import java.util.Arrays;

import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobClientModule;
import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobPlace;
import com.enokinomi.timeslice.web.branding.client.presenter.BrandingClientModule;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.login.client.ui.impl.LoginClientModule;
import com.enokinomi.timeslice.web.report.client.presenter.ReportClientModule;
import com.enokinomi.timeslice.web.report.client.presenter.ReportPlace;
import com.enokinomi.timeslice.web.settings.client.presenter.impl.OptionsPlace;
import com.enokinomi.timeslice.web.settings.client.presenter.impl.SettingsClientModule;
import com.enokinomi.timeslice.web.task.client.presenter.InputPlace;
import com.enokinomi.timeslice.web.task.client.presenter.TaskClientModule;
import com.enokinomi.timeslice.web.top.client.ui.TopLevel.TimeslicePlaceHistoryMapper;
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

            @Override
            public void logoutRequested()
            {
            }

            @Override
            public void serverInfoRequested()
            {
            }

            @Override
            public void supportLinkRequested()
            {
            }
        });

        return navPanel;
    }


    @Override
    protected void configure()
    {

        install(new BrandingClientModule());
        install(new LoginClientModule());

        install(new TaskClientModule());
        install(new ReportClientModule());
        install(new SettingsClientModule());
        install(new AppJobClientModule());

        bind(GenericActivityMapper.class).in(Singleton.class);
        bind(TimeslicePlaceHistoryMapper.class).in(Singleton.class);
        bind(PlaceHistoryMapper.class).to(TimeslicePlaceHistoryMapper.class);
    }
}
