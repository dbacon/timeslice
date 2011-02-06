package com.enokinomi.timeslice.web.task.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ITimesliceSvc extends RemoteService
{
    BrandInfo getBrandInfo();

    // TODO: move these into a separate service, since others now also use it.
    String serverInfo();

    List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant) throws ServiceException;
    List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws ServiceException;
    void addItem(String authToken, String instantString, String taskDescription) throws ServiceException;
    void addItems(String authToken, List<StartTag> items) throws ServiceException;
    void update(String authToken, StartTag editedStartTag) throws ServiceException;

}
