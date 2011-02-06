package com.enokinomi.timeslice.web.task.client.ui.api;

import com.google.gwt.user.client.ui.IsWidget;


public interface IHotlistPanel extends IsWidget
{
    void addHotlistPanelListener(IHotlistPanelListener listener);
    void removeHotlistPanelListener(IHotlistPanelListener listener);
    void repopulate();
    int getHotlistItemCount();
    void addAsHotlistItem(String name, String description);
}
