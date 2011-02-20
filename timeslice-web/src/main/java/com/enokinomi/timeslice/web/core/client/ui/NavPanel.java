package com.enokinomi.timeslice.web.core.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NavPanel extends Composite
{
    private static NavPanelUiBinder uiBinder = GWT.create(NavPanelUiBinder.class);
    interface NavPanelUiBinder extends UiBinder<Widget, NavPanel> { }

    @UiField protected CellPanel panelLeft;
    @UiField protected CellPanel panelRight;

    public NavPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public static interface Listener
    {
        void navigateLinkClicked(Place place);
    }

    private List<Listener> listeners = new ArrayList<NavPanel.Listener>();

    public void addListener(Listener listener)
    {
        if (listener != null) listeners.add(listener);
    }

    protected void fireLinkClicked(Place place)
    {
        for (Listener l: listeners) l.navigateLinkClicked(place);
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
    }

}
