package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.google.gwt.user.client.ui.IsWidget;

public interface IHistoryPanel extends IsWidget
{
    void setSuggestWords(List<String> words);
    void removeHistoryPanelListener(IHistoryPanelListener listener);
    void addHistoryPanelListener(IHistoryPanelListener listener);
    void clear(boolean shouldUpdate);
    void addItems(List<StartTag> items);
}
