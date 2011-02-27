package com.enokinomi.timeslice.web.login.client.ui.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class YesNoDialog extends DialogBox
{
    private final YesNoListener listener;

    public static interface YesNoListener
    {
        void yes();
        void no();
    }

    public YesNoDialog(String title, String message, YesNoListener listener)
    {
        super(false, true);

        this.listener = listener;

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new Button("Yes", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireYes();
            }
        }));
        hp.add(new Button("No", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireNo();
            }
        }));

        VerticalPanel vp = new VerticalPanel();
        vp.add(new Label(message));
        vp.add(hp);

        setText(title);
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setWidget(vp);
    }

    private void fireYes()
    {
        hide();
        if (listener != null) listener.yes();
    }

    private void fireNo()
    {
        hide();
        if (listener != null) listener.no();
    }

}
