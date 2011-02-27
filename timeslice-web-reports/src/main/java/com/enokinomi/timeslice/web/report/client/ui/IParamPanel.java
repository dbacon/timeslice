package com.enokinomi.timeslice.web.report.client.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.google.gwt.user.client.ui.IsWidget;

public interface IParamPanel extends IsWidget
{
    public interface IParamChangedListener
    {
        void paramChanged(IParamPanel source);
        void historyRequested(Date date);
        void allowWordsChanged(String allowWords);
        void ignoreWordsChanged(String ignoreWords);
    }

    Registration addParamChangedListener(IParamChangedListener listener);

    String getStartingTimeRendered();
    String getEndingTimeRendered();
    String getFullDaySelected();

    String getIgnoreWords();
    void setIgnoreWords(String ignoreWords, boolean fireEvents);

    String getAllowWords();
    void setAllowWords(String allowWords, boolean fireEvents);

    void restoreFromSettings(Map<String, List<String>> result);
    void setFullDaySelected(Date when, boolean fireEvents);

}
