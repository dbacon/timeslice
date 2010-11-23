package com.enokinomi.timeslice.web.assign.server.impl;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.IAssignmentSvc;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;

public class AssignmentSvcSession implements IAssignmentSvc
{
    public final ISessionTracker sessionTracker;
    private final AssignmentSvc assignmentSvc;

    @Inject
    AssignmentSvcSession(ISessionTracker sessionTracker, AssignmentSvc assignmentSvc)
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
    public List<AssignedTaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sessionData = sessionTracker.checkToken(authToken);
        return assignmentSvc.refreshTotals(sessionData.getUser(), maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords);
    }

    @Override
    public List<String> getAllBillees(String authToken)
    {
        sessionTracker.checkToken(authToken);
        return assignmentSvc.getAllBillees();
    }
}
