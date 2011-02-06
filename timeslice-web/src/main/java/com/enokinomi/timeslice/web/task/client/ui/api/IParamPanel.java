package com.enokinomi.timeslice.web.task.client.ui.api;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;

public interface IParamPanel extends IsWidget
{
    void addParamChangedListener(IParamChangedListener listener);
    void removeParamChangedListener(IParamChangedListener listener);
    String getStartingTimeRendered();
    String getEndingTimeRendered();
    TextBox getIgnoreWords();
    TextBox getAllowWords();
    String getFullDaySelected();
}
