package com.enokinomi.timeslice.web.task.client.ui.api;

import com.enokinomi.timeslice.web.task.client.core.StartTag;

public interface IHistoryPanelListener
{
    void interestingThing(String p);
    void fireEdited(StartTag startTag);
    void fireTimeEdited(StartTag startTag);
    void hotlisted(String name, String description);
    void editModeEntered();
    void editModeLeft();
}
