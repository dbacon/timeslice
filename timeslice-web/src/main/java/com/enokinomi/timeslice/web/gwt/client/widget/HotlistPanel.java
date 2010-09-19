package com.enokinomi.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class HotlistPanel extends Composite
{
    public static final class Mode
    {
        public static final String EditSingle = "Hotlist [delete-single]";
        public static final String EditMulti = "Hotlist [delete-multi]";
        public static final String Hotlist = "Hotlist";
    }

    private static final String CookieNamePrefix = "timeslice.hotlist.";

    private final FlowPanel vp = new FlowPanel();
    private final Anchor mode = new Anchor(Mode.Hotlist, true);

    public static interface IHotlistPanelListener
    {
        void hotlistItemClicked(String description);
        void hotlistChanged();
    }

    private final List<IHotlistPanelListener> listeners = new ArrayList<IHotlistPanelListener>();

    public void addHotlistPanelListener(IHotlistPanelListener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

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

    public HotlistPanel()
    {
        mode.setTitle("Hotlist panel - click to switch between live and delete mode.");
        mode.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                if (Mode.Hotlist.equals(mode.getText()) && 1 < vp.getWidgetCount())
                {
                    mode.setText(Mode.EditSingle);
                }
                else if (Mode.EditSingle.equals(mode.getText()))
                {
                    mode.setText(Mode.EditMulti);
                }
                else if (Mode.EditMulti.equals(mode.getText()))
                {
                    mode.setText(Mode.Hotlist);
                }
                else
                {
                    mode.setText(Mode.Hotlist);
                }
            }
        });

        repopulate();

        initWidget(vp);
    }

    public void repopulate()
    {
        vp.clear();

        vp.add(mode);

        int added = 0;

        for (final String name: Cookies.getCookieNames())
        {
            if (name.startsWith(CookieNamePrefix))
            {
                final String description = Cookies.getCookie(name);

                Button button = new Button(description, new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent event)
                    {
                        if (Mode.Hotlist.equals(mode.getText()))
                        {
                            fireHotlistItemClicked(description);
                        }
                        else
                        {
                            Cookies.removeCookie(name);

                            if (Mode.EditSingle.equals(mode.getText()))
                            {
                                mode.setText(Mode.Hotlist);
                            }

                            repopulate();

                            fireHotlistChanged();
                        }
                    }
                });

                if (added < 10)
                {
                    char digitChar = Character.forDigit((1 + added) % 10, 10);
                    button.setAccessKey(digitChar);
                    button.setHTML("<u>" + digitChar + "</u> " + button.getText());
                }

                vp.add(button);

                ++added;
            }
        }

        if (added <= 0)
        {
            mode.setText(Mode.Hotlist);
        }
    }

    public int getHotlistItemCount()
    {
        return vp.getWidgetCount() - 1;
    }

    public void addAsHotlistItem(String name, String description)
    {
        Cookies.setCookie(CookieNamePrefix  + name.hashCode(), description, createDateSufficientlyInTheFuture());
        repopulate();
        fireHotlistChanged();
    }

    @SuppressWarnings("deprecation")
    public static Date createDateSufficientlyInTheFuture()
    {
        return new Date(2099, 0, 1);
    }
}
