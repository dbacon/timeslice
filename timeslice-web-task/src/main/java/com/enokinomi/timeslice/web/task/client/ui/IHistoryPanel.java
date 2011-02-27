package com.enokinomi.timeslice.web.task.client.ui;

import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.google.gwt.user.client.ui.IsWidget;

public interface IHistoryPanel extends IsWidget
{
    public interface Listener
    {
        void interestingThing(String p);
        void fireEdited(StartTag startTag);
        void fireTimeEdited(StartTag startTag);
        void hotlisted(String name, String description);
        void editModeEntered();
        void editModeLeft();
    }

    void setSuggestWords(List<String> words);
    void removeHistoryPanelListener(Listener listener);
    void addListener(Listener listener);
    void clear(boolean shouldUpdate);
    void addItems(List<StartTag> items);
}
