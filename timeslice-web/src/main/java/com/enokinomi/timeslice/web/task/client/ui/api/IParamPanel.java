package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.ui.Registration;
import com.google.gwt.user.client.ui.IsWidget;

public interface IParamPanel extends IsWidget
{
    Registration addParamChangedListener(IParamChangedListener listener);
    void removeParamChangedListener(IParamChangedListener listener);

    String getStartingTimeRendered();
    String getEndingTimeRendered();
    String getFullDaySelected();

    String getIgnoreWords();
    void setIgnoreWords(String ignoreWords, boolean fireEvents);

    String getAllowWords();
    void setAllowWords(String allowWords, boolean fireEvents);

    void restoreFromSettings(Map<String, List<String>> result);
    void setFullDaySelected(Date when, boolean fireEvents);

//    void bind(ISettingsPresenter settingsPresenter);

}
