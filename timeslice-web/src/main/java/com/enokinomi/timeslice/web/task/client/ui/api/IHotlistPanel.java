package com.enokinomi.timeslice.web.task.client.ui.api;

import com.enokinomi.timeslice.web.core.client.ui.IIsWidget;


public interface IHotlistPanel extends IIsWidget
{
    void addHotlistPanelListener(IHotlistPanelListener listener);
    void removeHotlistPanelListener(IHotlistPanelListener listener);
    void repopulate();
    int getHotlistItemCount();
    void addAsHotlistItem(String name, String description);
}
