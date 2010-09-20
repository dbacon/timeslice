package com.enokinomi.timeslice.web.gwt.client.assigned.core;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.core.ProcType;
import com.enokinomi.timeslice.web.gwt.client.core.SortDir;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAssignmentSvcAsync
{
    void assign(String authToken, String description, String billTo, AsyncCallback<Void> callback);
    void lookup(String authToken, String description, String valueWhenAssignmentNotFound, AsyncCallback<String> callback);
    void refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<List<AssignedTaskTotal>> callback);
}
