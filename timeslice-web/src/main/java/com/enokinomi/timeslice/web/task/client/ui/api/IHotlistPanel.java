package com.enokinomi.timeslice.web.task.client.ui.api;


public interface IHotlistPanel extends IIsWidget
{
    void addHotlistPanelListener(IHotlistPanelListener listener);
    void removeHotlistPanelListener(IHotlistPanelListener listener);
    void repopulate();
    int getHotlistItemCount();
    void addAsHotlistItem(String name, String description);
}
