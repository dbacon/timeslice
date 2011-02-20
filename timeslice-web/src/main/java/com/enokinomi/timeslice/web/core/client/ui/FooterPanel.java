package com.enokinomi.timeslice.web.core.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FooterPanel extends Composite implements Initializable
{
    private static FooterPanelUiBinder uiBinder = GWT.create(FooterPanelUiBinder.class);
    interface FooterPanelUiBinder extends UiBinder<Widget, FooterPanel> { }

    public static interface FooterListener
    {
        void logoutRequested();
        void serverInfoRequested();
        void supportLinkRequested();
    }

    @UiField protected Anchor logoutAnchor;
    @UiField protected Anchor supportAnchor;
    @UiField protected Label serverInfoLabel;

    private List<FooterListener> listeners = new ArrayList<FooterPanel.FooterListener>();


    public Registration addFooterListener(FooterListener l)
    {
        if (listeners != null)
        {
            listeners.add(l);
            return GenericRegistration.wrap(listeners, l);
        }
        return NullRegistration.Instance;
    }

    protected void fireLogoutClicked()
    {
        for (FooterListener l: listeners) l.logoutRequested();
    }

    protected void fireServerInfoClicked()
    {
        for (FooterListener l: listeners) l.serverInfoRequested();
    }

    protected void fireSupportClicked()
    {
        for (FooterListener l: listeners) l.supportLinkRequested();
    }

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

    public FooterPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));

        logoutAnchor.setHref("#");
        supportAnchor.setHref("#");
    }

    public void setServerInfo(String info)
    {
        serverInfoLabel.setText(info);
    }

    @Override
    public void initialize(String callerPurpose)
    {
        fireServerInfoClicked();
    }

}
