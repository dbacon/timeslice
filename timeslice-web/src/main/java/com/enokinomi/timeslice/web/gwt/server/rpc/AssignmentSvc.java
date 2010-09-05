package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.app.assign.IAssignmentDao;
import com.enokinomi.timeslice.web.gwt.client.beans.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;

public class AssignmentSvc
{
    private final IAssignmentDao assignmentDao;
    private final TimesliceSvc timesliceSvc;
    private final String valueIfNotAssigned;

    public AssignmentSvc(IAssignmentDao assignmentDao, TimesliceSvc timesliceSvc, String valueIfNotAssigned)
    {
        this.assignmentDao = assignmentDao;
        this.timesliceSvc = timesliceSvc;
        this.valueIfNotAssigned = valueIfNotAssigned;
    }

    public void assign(String description, String billTo)
    {
        assignmentDao.assign(description, billTo);
    }

    public String lookup(String description, String valueWhenAssignmentNotFound)
    {
        return assignmentDao.getBillee(description, valueWhenAssignmentNotFound);
    }

    public List<AssignedTaskTotal> refreshTotals(String user, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        return resolveBillees(timesliceSvc.refreshTotals(user, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords));
    }

    private List<AssignedTaskTotal> resolveBillees(List<TaskTotal> taskTotals)
    {
        ArrayList<AssignedTaskTotal> results = new ArrayList<AssignedTaskTotal>();

        for (TaskTotal taskTotal: taskTotals)
        {
            String billedTo = assignmentDao.getBillee(taskTotal.getWhat(), valueIfNotAssigned);
            results.add(
                    new AssignedTaskTotal(
                            taskTotal.getWho(),
                            taskTotal.getHours(),
                            taskTotal.getPercentage(),
                            taskTotal.getWhat(),
                            billedTo));
        }

        return results;
    }
}
