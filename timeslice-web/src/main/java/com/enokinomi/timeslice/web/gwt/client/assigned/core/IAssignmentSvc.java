package com.enokinomi.timeslice.web.gwt.client.assigned.core;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.core.NotAuthenticException;
import com.enokinomi.timeslice.web.gwt.client.core.SortDir;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IAssignmentSvc extends RemoteService
{
    void assign(String authToken, String description, String billTo) throws NotAuthenticException;
    String lookup(String authToken, String description, String valueWhenAssignmentNotFound) throws NotAuthenticException;
    List<AssignedTaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws NotAuthenticException;
}
