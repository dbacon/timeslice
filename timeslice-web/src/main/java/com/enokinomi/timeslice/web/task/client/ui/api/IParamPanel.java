package com.enokinomi.timeslice.web.task.client.ui.api;

import com.enokinomi.timeslice.web.core.client.ui.IIsWidget;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.TextBox;

public interface IParamPanel extends IIsWidget
{
    // 2009-03-21T13:30:42.626 -- assume +9
    public static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+09:00'");

    void addParamChangedListener(IParamChangedListener listener);
    void removeParamChangedListener(IParamChangedListener listener);
    String getStartingTimeRendered();
    String getEndingTimeRendered();
    TextBox getIgnoreWords();
    TextBox getAllowWords();
    String getFullDaySelected();
}
