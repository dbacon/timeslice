package com.enokinomi.timeslice.web.report.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.TaskTotal;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IReportsSvcAsync
{

    void refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<List<TaskTotal>> callback);

}
