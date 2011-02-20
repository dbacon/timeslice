package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.Date;


public interface IParamChangedListener
{
    void paramChanged(IParamPanel source);
    void historyRequested(Date date);
    void allowWordsChanged(String allowWords);
    void ignoreWordsChanged(String ignoreWords);
}
