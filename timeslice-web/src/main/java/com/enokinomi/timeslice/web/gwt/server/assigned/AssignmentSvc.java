package com.enokinomi.timeslice.web.gwt.server.assigned;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.assign.INowProvider;
import com.enokinomi.timeslice.lib.assign.ITagStore;
import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.core.ProcType;
import com.enokinomi.timeslice.web.gwt.client.core.SortDir;
import com.enokinomi.timeslice.web.gwt.client.task.core.TaskTotal;
import com.enokinomi.timeslice.web.gwt.server.task.TimesliceSvc;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AssignmentSvc
{
    private final TimesliceSvc timesliceSvc;
    private final String valueIfNotAssigned;
    private final ITagStore tagStore;
    private final INowProvider nowProvider;

    @Inject
    public AssignmentSvc(ITagStore tagStore, INowProvider nowProvider, TimesliceSvc timesliceSvc, @Named("assignDefault") String valueIfNotAssigned)
    {
        this.tagStore = tagStore;
        this.nowProvider = nowProvider;
        this.timesliceSvc = timesliceSvc;
        this.valueIfNotAssigned = valueIfNotAssigned;
    }

    public void assign(String description, String billTo)
    {
        tagStore.assignBillee(description, billTo, nowProvider.getNow());
    }

    public String lookup(String description, String valueWhenAssignmentNotFound)
    {
        return tagStore.lookupBillee(description, nowProvider.getNow(), valueWhenAssignmentNotFound);
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
            String billedTo = tagStore.lookupBillee(taskTotal.getWhat(), nowProvider.getNow(), valueIfNotAssigned);
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
