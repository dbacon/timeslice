package com.enokinomi.timeslice.web.core.client.ui;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NotificationPanel extends Composite
{
    private static NotificationPanelUiBinder uiBinder = GWT.create(NotificationPanelUiBinder.class);
    interface NotificationPanelUiBinder extends UiBinder<Widget, NotificationPanel> { }

    interface Style extends CssResource
    {
        String messageListPanel();
        String message();
        String info();
        String warning();
    }

    @UiField protected Style style;
    @UiField protected CellPanel messagesBox;
    @UiField protected Button infoMsgAckButton;
    @UiField protected CellPanel errorBox;

    public NotificationPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));
        setStylePrimaryName("ts-notificationPanel");

        cssMap.put(NotificationType.info, style.info());
        cssMap.put(NotificationType.warning, style.warning());
    }

    public enum NotificationType
    {
        info,
        warning,
    }

    private final LinkedHashMap<NotificationType, String> cssMap = new LinkedHashMap<NotificationPanel.NotificationType, String>();

    public void addInfoMsg(NotificationType type, String msg)
    {
        Label message = new Label(msg);
        message.getElement().addClassName(style.message());
        message.getElement().addClassName(cssMap.get(type));

        messagesBox.add(message);
        setStyleDependentName("nonEmpty", messagesBox.getWidgetCount() > 0);
    }

    @UiHandler("infoMsgAckButton")
    protected void ackClicked(ClickEvent e)
    {
        messagesBox.clear();
        setStyleDependentName("nonEmpty", messagesBox.getWidgetCount() > 0);
    }

}
