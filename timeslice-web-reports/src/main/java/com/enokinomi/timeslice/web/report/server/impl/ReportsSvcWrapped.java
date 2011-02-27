package com.enokinomi.timeslice.web.report.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.util.Transforms;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.report.client.core.TaskTotal;
import com.google.inject.Inject;

/**
 * converts between server- and client- types.
 *
 */
public class ReportsSvcWrapped
{
    private final ITimesliceSvc timesliceSvc;

    @Inject
    ReportsSvcWrapped(ITimesliceSvc timesliceSvc)
    {
        this.timesliceSvc = timesliceSvc;
    }

    public List<TaskTotal> refreshTotals(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, int tzOffsetMinutes)
    {
        return Transforms.tr(
                timesliceSvc.refreshTotals(
                        user,
                        maxSize,
                        com.enokinomi.timeslice.lib.task.api.SortDir.valueOf(sortDir.name()),
                        startingInstant,
                        endingInstant,
                        allowWords,
                        ignoreWords),
                        new ArrayList<TaskTotal>(),
                        ServerToClient.createTaskTotal(tzOffsetMinutes));
    }
}
