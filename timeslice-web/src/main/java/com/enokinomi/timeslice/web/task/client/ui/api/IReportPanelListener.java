package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.Date;
import java.util.List;

public interface IReportPanelListener
{
    void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords);
    void billeeUpdateRequested(String description, String newBillee);
    void itemHistoryRequested(Date when);
}
