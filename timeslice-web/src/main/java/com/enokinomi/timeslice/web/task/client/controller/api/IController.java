package com.enokinomi.timeslice.web.task.client.controller.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.Registration;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.task.client.core.StartTag;


public interface IController
{

    Registration addControllerListener(String ownerPurpose, IControllerListener listener);
    void removeControllerListener(String listenerId);

    void serverInfo();
    void startGetBranding();
    void startEditDescription(StartTag editedStartTag);
    void startAddItem(String instantString, String taskDescription);
    void startAddItems(List<StartTag> items);
    void startRefreshItems(int maxSize, String startingInstant, String endingInstant);
    void startRefreshTotals(int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startRefreshTotalsAssigned(int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startAssignBillee(String description, String newBillee);
    void startGetAllBillees();

    void startListAvailableJobs();
    void startPerformJob(String jobId);

}
