package com.enokinomi.timeslice.web.core.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

public class NavPanelListenerImplementation implements NavPanel.Listener
{
//    private final ILoginSupport loginSupport;
//    private final IBrandingPresenter brandPresenter;
    private final PlaceController placeController;

//    @Inject
    public NavPanelListenerImplementation(/*IBrandingPresenter brandPresenter, ILoginSupport loginSupport,  */PlaceController placeController)
    {
//        this.loginSupport = loginSupport;
//        this.brandPresenter = brandPresenter;
        this.placeController = placeController;
    }

    @Override
    public void logoutRequested()
    {
//        loginSupport.logout();
    }

    @Override
    public void serverInfoRequested()
    {
//        brandPresenter.serverInfo();
    }

    @Override
    public void supportLinkRequested()
    {
        // TODO: do something?
        GWT.log("support-link clicked");
    }

    @Override
    public void navigateLinkClicked(Place place)
    {
        placeController.goTo(place);
    }
}
