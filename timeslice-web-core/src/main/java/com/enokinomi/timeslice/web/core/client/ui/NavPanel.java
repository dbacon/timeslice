package com.enokinomi.timeslice.web.core.client.ui;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NavPanel extends Composite implements Initializable
{
    private static NavPanelUiBinder uiBinder = GWT.create(NavPanelUiBinder.class);
    interface NavPanelUiBinder extends UiBinder<Widget, NavPanel> { }

    @UiField protected CellPanel panelLeft;
    @UiField protected CellPanel panelRight;

    @UiField protected Anchor logoutAnchor;
    @UiField protected Anchor supportAnchor;
    @UiField protected Label serverInfoLabel;

    private final ListenerManager<Listener> listenerMgr = new ListenerManager<Listener>();
    public Registration addListener(Listener listener) { return listenerMgr.addListener(listener); }
    protected void fireLinkClicked(Place place) { for (Listener l: listenerMgr.getListeners()) l.navigateLinkClicked(place); }
    protected void fireLogoutClicked() { for (Listener l: listenerMgr.getListeners()) l.logoutRequested(); }
    protected void fireServerInfoClicked() { for (Listener l: listenerMgr.getListeners()) l.serverInfoRequested(); }
    protected void fireSupportClicked() { for (Listener l: listenerMgr.getListeners()) l.supportLinkRequested(); }

    @UiHandler("logoutAnchor")
    protected void logoutClicked(ClickEvent e)
    {
        fireLogoutClicked();
    }
    @UiHandler("supportAnchor")
    protected void supportClicked(ClickEvent e)
    {
        fireSupportClicked();
    }

    @UiHandler("serverInfoLabel")
    protected void serverInfoClicked(ClickEvent e)
    {
        fireServerInfoClicked();
    }





    public NavPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));

        logoutAnchor.setHref("#");
        supportAnchor.setHref("#");
    }

    public static interface Listener
    {
        void logoutRequested();
        void serverInfoRequested();
        void supportLinkRequested();

        void navigateLinkClicked(Place place);
    }


    @Override
    public void initialize(String callerPurpose)
    {
        fireServerInfoClicked();
    }

    public void setServerInfo(String info)
    {
        serverInfoLabel.setText(info);
    }

    public void populateLeft(List<Place> places)
    {
        populateOne(panelLeft, places);
    }

    public void populateRight(List<Place> places)
    {
        populateOne(panelRight, places);
    }

    private void populateOne(CellPanel panel, List<Place> places)
    {
        panel.clear();
        panel.add(serverInfoLabel);

        for (final Place place: places)
        {
            Anchor anchor = new Anchor(place.toString());
            anchor.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    fireLinkClicked(place);
                }
            });
            panel.add(anchor);
        }

        panel.add(supportAnchor);
        panel.add(logoutAnchor);
    }

}
