package com.enokinomi.timeslice.web.assign.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAssignmentSvcAsync
{
    void assign(String authToken, String description, String billTo, AsyncCallback<Void> callback);
    void lookup(String authToken, String description, String valueWhenAssignmentNotFound, AsyncCallback<String> callback);
//    List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws ServiceException;
    void refreshAssignedTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<List<AssignedTaskTotal>> callback);
    void getAllBillees(String authToken, AsyncCallback<List<String>> callback);
}
