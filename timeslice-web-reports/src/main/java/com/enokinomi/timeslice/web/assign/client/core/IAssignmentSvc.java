package com.enokinomi.timeslice.web.assign.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IAssignmentSvc extends RemoteService
{
    void assign(String authToken, String description, String billTo) throws ServiceException;
    String lookup(String authToken, String description, String valueWhenAssignmentNotFound) throws ServiceException;
//    List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws ServiceException;
    List<AssignedTaskTotal> refreshAssignedTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws ServiceException;
    List<String> getAllBillees(String authToken) throws ServiceException;
}
