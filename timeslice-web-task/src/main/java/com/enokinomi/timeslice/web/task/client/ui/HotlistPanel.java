package com.enokinomi.timeslice.web.task.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.PrefHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class HotlistPanel extends Composite implements IHotlistPanel
{
    private static HotlistPanelUiBinder uiBinder = GWT.create(HotlistPanelUiBinder.class);
    interface HotlistPanelUiBinder extends UiBinder<Widget, HotlistPanel> { }

    private static final String CookieNamePrefix = "timeslice.hotlist.";

    private final HotlistPanelConstants constants = GWT.create(HotlistPanelConstants.class);

    @UiField protected FlowPanel vp;
    @UiField protected Anchor mode;

    private final List<IHotlistPanelListener> listeners = new ArrayList<IHotlistPanelListener>();

    @Override
    public void addHotlistPanelListener(IHotlistPanelListener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    @Override
    public void removeHotlistPanelListener(IHotlistPanelListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireHotlistItemClicked(String description)
    {
        for (IHotlistPanelListener listener: listeners)
        {
            listener.hotlistItemClicked(description);
        }
    }

    protected void fireHotlistChanged()
    {
        for (IHotlistPanelListener listener: listeners)
        {
            listener.hotlistChanged();
        }
    }

    @UiHandler("mode")
    protected void modeClicked(ClickEvent e)
    {
        if (constants.hotlist().equals(mode.getText()) && 1 < vp.getWidgetCount())
        {
            mode.setText(constants.editDeleteSingle());
        }
        else if (constants.editDeleteSingle().equals(mode.getText()))
        {
            mode.setText(constants.editDeleteMulti());
        }
        else if (constants.editDeleteMulti().equals(mode.getText()))
        {
            mode.setText(constants.hotlist());
        }
        else
        {
            mode.setText(constants.hotlist());
        }
    }


    @Inject
    HotlistPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));

        mode.setTitle("Hotlist panel - click to switch between live and delete mode.");

        repopulate();
    }

    @Override
    public void repopulate()
    {
        vp.clear();

        vp.add(mode);

        int added = 0;

        for (final String name: Cookies.getCookieNames())
        {
            if (name.startsWith(CookieNamePrefix))
            {
                vp.add(createHotlistButton(added, name, Cookies.getCookie(name)));

                ++added;
            }
        }

        if (added <= 0)
        {
            mode.setText(constants.hotlist());
        }
    }

    private Button createHotlistButton(int index, final String name, final String description)
    {
        Button button = new Button(description, new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                handleHotlistButtonClicked(name, description);
            }
        });

        if (index < 10)
        {
            char digitChar = Character.forDigit((1 + index) % 10, 10);
            button.setAccessKey(digitChar);
            button.setHTML("<u>" + digitChar + "</u> " + button.getText());
        }

        return button;
    }

    private void handleHotlistButtonClicked(final String name, final String description)
    {
        if (constants.hotlist().equals(mode.getText()))
        {
            fireHotlistItemClicked(description);
        }
        else
        {
            Cookies.removeCookie(name);

            if (constants.editDeleteSingle().equals(mode.getText()))
            {
                mode.setText(constants.hotlist());
            }

            repopulate();

            fireHotlistChanged();
        }
    }

    @Override
    public int getHotlistItemCount()
    {
        return vp.getWidgetCount() - 1;
    }

    @Override
    public void addAsHotlistItem(String name, String description)
    {
        Cookies.setCookie(CookieNamePrefix  + name.hashCode(), description, PrefHelper.createDateSufficientlyInTheFuture());
        repopulate();
        fireHotlistChanged();
    }
}
