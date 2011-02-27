package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.enokinomi.timeslice.web.core.client.util.IActivityFactory;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class AppJobActivityFactory implements IActivityFactory
{
    private final Provider<IAppJobPanel> widgetProvider;
//    private final ILoginSupport loginSupport;
    private final IAppJobPresenter presenter;

    @Inject
    public AppJobActivityFactory(Provider<IAppJobPanel> widgetProvider, IAppJobPresenter presenter/*, ILoginSupport loginSupport*/)
    {
        this.widgetProvider = widgetProvider;
        this.presenter = presenter;
//        this.loginSupport = loginSupport;
    }

    public AppJobActivity get(Place place)
    {
        AppJobPlace specificPlace = (AppJobPlace) place;

        return new AppJobActivity(widgetProvider, specificPlace, presenter/*, loginSupport*/);
    }
}
