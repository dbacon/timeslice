package com.enokinomi.timeslice.web.assign.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.task.client.core_todo_move_out.SortDir;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAssignmentSvcAsync
{
    void assign(String authToken, String description, String billTo, AsyncCallback<Void> callback);
    void lookup(String authToken, String description, String valueWhenAssignmentNotFound, AsyncCallback<String> callback);
    void refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<List<AssignedTaskTotal>> callback);
    void getAllBillees(String authToken, AsyncCallback<List<String>> callback);
}
