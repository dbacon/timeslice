package com.enokinomi.timeslice.web.top.client.ui;

import java.util.Arrays;

import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobClientModule;
import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobPlace;
import com.enokinomi.timeslice.web.branding.client.core.BrandInfo;
import com.enokinomi.timeslice.web.branding.client.presenter.BrandingClientModule;
import com.enokinomi.timeslice.web.branding.client.presenter.IBrandingPresenter;
import com.enokinomi.timeslice.web.branding.client.presenter.IBrandingPresenter.IBrandingPresenterListener;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
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

    @Provides @Named("populated") NavPanel createNavPanel(
            final NavPanel navPanel,
            final PlaceController placeController,
            final ILoginSupport loginSupport,
            final IBrandingPresenter brandingPresenter)
    {
        navPanel.populateLeft(Arrays.asList(
                new InputPlace("nav-panel", true, null),
                new ReportPlace(null, null)
                ));

        navPanel.populateRight(Arrays.asList(
                new OptionsPlace(),
                new AppJobPlace()
                ));

        loginSupport.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
            }

            @Override
            public void newSessionStarted()
            {
            }
        });

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
                loginSupport.logout();
            }

            @Override
            public void serverInfoRequested()
            {
                navPanel.setServerInfo("Requesting server info ...");
                brandingPresenter.serverInfo();
            }

            @Override
            public void supportLinkRequested()
            {
            }
        });

        brandingPresenter.addListener(new IBrandingPresenterListener()
        {
            @Override
            public void serverInfoRecieved(String info)
            {
                navPanel.setServerInfo(info);
            }

            @Override
            public void branded(BrandInfo brandInfo)
            {
                // TODO: hook support link up.
//                navPanel.setSupportLink(brandInfo.getIssueHref());
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
