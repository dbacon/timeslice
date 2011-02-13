package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.google.gwt.user.client.ui.IsWidget;

public interface IParamPanel extends IsWidget
{
    void addParamChangedListener(IParamChangedListener listener);
    void removeParamChangedListener(IParamChangedListener listener);

    String getStartingTimeRendered();
    String getEndingTimeRendered();
    String getFullDaySelected();

    String getIgnoreWords();
    void setIgnoreWords(String ignoreWords, boolean fireEvents);

    String getAllowWords();
    void setAllowWords(String allowWords, boolean fireEvents);

    void bind(IParamPanel paramPanel, ISettingsPresenter settingsPresenter); // will be static/moved
    void restoreFromSettings(Map<String, List<String>> result);

}
