package com.enokinomi.timeslice.web.task.client.controller.api;

import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.SortDir;


public interface IController
{

    void addControllerListener(IControllerListener listener);
    void removeControllerListener(IControllerListener listener);

    void logout();

    void serverInfo();
    void startGetBranding();
    void startEditDescription(StartTag editedStartTag);
    void startAddItem(String instantString, String taskDescription);
    void startAddItems(List<StartTag> items);
    void startRefreshItems(int maxSize, String startingInstant, String endingInstant);
    void startRefreshTotals(int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startRefreshTotalsAssigned(int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startPersistTotals(String persistAsName, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startAssignBillee(String description, String newBillee);
    void startGetAllBillees();

    void startListAvailableJobs();
    void startPerformJob(String jobId);

}
