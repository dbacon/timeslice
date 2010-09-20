package com.enokinomi.timeslice.web.gwt.server.assigned;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.assigned.core.IAssignmentSvc;
import com.enokinomi.timeslice.web.gwt.client.core.ProcType;
import com.enokinomi.timeslice.web.gwt.client.core.SortDir;
import com.enokinomi.timeslice.web.gwt.server.task.SessionData;
import com.enokinomi.timeslice.web.gwt.server.task.SessionTracker;
import com.google.inject.Inject;

public class AssignmentSvcSession implements IAssignmentSvc
{
    public final SessionTracker sessionTracker;
    private final AssignmentSvc assignmentSvc;

    @Inject
    public AssignmentSvcSession(SessionTracker sessionTracker, AssignmentSvc assignmentSvc)
    {
        this.sessionTracker = sessionTracker;
        this.assignmentSvc = assignmentSvc;
    }

    @Override
    public void assign(String authToken, String description, String billTo)
    {
        sessionTracker.checkToken(authToken);
        assignmentSvc.assign(description, billTo);
    }

    @Override
    public String lookup(String authToken, String description, String valueWhenAssignmentNotFound)
    {
        sessionTracker.checkToken(authToken);
        return assignmentSvc.lookup(description, valueWhenAssignmentNotFound);
    }

    @Override
    public List<AssignedTaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sessionData = sessionTracker.checkToken(authToken);
        return assignmentSvc.refreshTotals(sessionData.getUser(), maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
    }
}
