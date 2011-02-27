package com.enokinomi.timeslice.web.task.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface IHotlistPanel extends IsWidget
{
    public interface IHotlistPanelListener
    {
        void hotlistItemClicked(String description);
        void hotlistChanged();
    }

    void addHotlistPanelListener(IHotlistPanelListener listener);
    void removeHotlistPanelListener(IHotlistPanelListener listener);
    void repopulate();
    int getHotlistItemCount();
    void addAsHotlistItem(String name, String description);
}
