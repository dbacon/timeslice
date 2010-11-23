package com.enokinomi.timeslice.web.assign.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.assign.api.INowProvider;
import com.enokinomi.timeslice.lib.assign.api.ITagStore;
import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.task.api.TaskTotalMember;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * TODO: split up between app stuff and translation to web-types stuff.
 *
 */
public class AssignmentSvc
{
    private final ITimesliceSvc timesliceSvc;
    private final String valueIfNotAssigned;
    private final ITagStore tagStore;
    private final INowProvider nowProvider;

    @Inject
    AssignmentSvc(ITagStore tagStore, INowProvider nowProvider, ITimesliceSvc timesliceSvc, @Named("assignDefault") String valueIfNotAssigned)
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

    public List<AssignedTaskTotal> refreshTotals(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        return resolveBillees(timesliceSvc.refreshTotals(user, maxSize, com.enokinomi.timeslice.lib.task.api.SortDir.valueOf(sortDir.name()), startingInstant, endingInstant, allowWords, ignoreWords));
    }

    private List<AssignedTaskTotal> resolveBillees(List<TaskTotalMember> taskTotals)
    {
        ArrayList<AssignedTaskTotal> results = new ArrayList<AssignedTaskTotal>();

        for (TaskTotalMember taskTotal: taskTotals)
        {
            String billedTo = tagStore.lookupBillee(taskTotal.getWhat(), nowProvider.getNow(), valueIfNotAssigned);
            results.add(
                    new AssignedTaskTotal(
                            taskTotal.getWho(),
                            taskTotal.getMillis() / 1000. / 60. / 60.,
                            taskTotal.getPercentage(),
                            taskTotal.getWhat(),
                            billedTo));
        }

        return results;
    }

    public List<String> getAllBillees()
    {
        return tagStore.getAllBillees();
    }
}
