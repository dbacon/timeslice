package com.enokinomi.timeslice.web.report.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.TaskTotal;
import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IReportsSvc extends RemoteService
{
    List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws ServiceException;
}
