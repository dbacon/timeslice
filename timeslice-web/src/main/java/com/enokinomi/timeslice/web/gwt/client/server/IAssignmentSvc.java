package com.enokinomi.timeslice.web.gwt.client.server;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.beans.AssignedTaskTotal;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IAssignmentSvc extends RemoteService
{
    void assign(String authToken, String description, String billTo);
    String lookup(String authToken, String description, String valueWhenAssignmentNotFound);
    List<AssignedTaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
}
