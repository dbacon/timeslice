package com.enokinomi.timeslice.web.task.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ITaskSvc extends RemoteService
{
    // tasks
    List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant) throws ServiceException;
    void addItem(String authToken, String instantString, String taskDescription) throws ServiceException;
    void addItems(String authToken, List<StartTag> items) throws ServiceException;
    void update(String authToken, StartTag editedStartTag) throws ServiceException;

}
