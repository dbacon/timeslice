package com.enokinomi.timeslice.web.gwt.client.controller;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.core.ProcType;
import com.enokinomi.timeslice.web.gwt.client.core.SortDir;
import com.enokinomi.timeslice.web.gwt.client.task.core.StartTag;


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
    void startRefreshTotals(int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startRefreshTotalsAssigned(int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startPersistTotals(String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startAssignBillee(String description, String newBillee);

    void startListAvailableJobs();
    void startPerformJob(String jobId);

}
