package com.enokinomi.timeslice.web.assign.server.impl;

import java.util.List;
import java.util.concurrent.Callable;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.IAssignmentSvc;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.core.server.util.Catcher;
import com.enokinomi.timeslice.web.session.server.api.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.api.SessionData;
import com.google.inject.Inject;

class AssignmentSvcSession implements IAssignmentSvc
{
    private final ISessionTracker sessionTracker;
    private final AssignmentSvc assignmentSvc;

    @Inject
    AssignmentSvcSession(ISessionTracker sessionTracker, AssignmentSvc assignmentSvc)
    {
        this.sessionTracker = sessionTracker;
        this.assignmentSvc = assignmentSvc;
    }

    @Override
    public void assign(final String authToken, final String description, final String billTo)
    {
        new Catcher().catchAndWrap("Assigning", new Callable<Void>()
        {
            @Override
            public Void call()
            {
                sessionTracker.checkToken(authToken);
                assignmentSvc.assign(description, billTo);
                return null; // Void
            }
        });
    }

    @Override
    public String lookup(final String authToken, final String description, final String valueWhenAssignmentNotFound)
    {
        return new Catcher().catchAndWrap("Looking up assignment", new Callable<String>()
        {
            @Override
            public String call()
            {
                sessionTracker.checkToken(authToken);
                return assignmentSvc.lookup(description, valueWhenAssignmentNotFound);
            }
        });
    }

    @Override
    public List<AssignedTaskTotal> refreshAssignedTotals(final String authToken, final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        return new Catcher().catchAndWrap("Refreshing totals", new Callable<List<AssignedTaskTotal>>()
        {
            @Override
            public List<AssignedTaskTotal> call()
            {
                SessionData sessionData = sessionTracker.checkToken(authToken);
                return assignmentSvc.refreshAssignedTotals(sessionData.getUser(), maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        });
    }

//    @Override
//    public List<TaskTotal> refreshTotals(final String authToken, final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
//    {
//        return new Catcher().catchAndWrap("Refreshing totals", new Callable<List<TaskTotal>>()
//        {
//            @Override
//            public List<TaskTotal> call()
//            {
//                SessionData sd = sessionTracker.checkToken(authToken);
//                return assignmentSvc.refreshTotals(sd.getUser(), maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords);
//            }
//        });
//    }

    @Override
    public List<String> getAllBillees(final String authToken)
    {
        return new Catcher().catchAndWrap("Getting all billees", new Callable<List<String>>()
        {
            @Override
            public List<String> call()
            {
                sessionTracker.checkToken(authToken);
                return assignmentSvc.getAllBillees();
            }
        });
    }
}
