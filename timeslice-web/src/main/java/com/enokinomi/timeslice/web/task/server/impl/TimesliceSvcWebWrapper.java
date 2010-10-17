package com.enokinomi.timeslice.web.task.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.task.TimesliceSvc;
import com.enokinomi.timeslice.lib.util.Transforms;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.SortDir;
import com.google.inject.Inject;

/**
 * Translates between server- and client- types.
 *
 * @author dbacon
 *
 */
public class TimesliceSvcWebWrapper
{
    private final TimesliceSvc timesliceSvc;

    @Inject
    TimesliceSvcWebWrapper(TimesliceSvc timesliceSvc)
    {
        this.timesliceSvc = timesliceSvc;
    }

    public List<StartTag> refreshItems(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, int tzOffset)
    {
        return Transforms.tr(timesliceSvc.refreshItems(
                    user,
                    maxSize,
                    com.enokinomi.timeslice.lib.task.SortDir.valueOf(sortDir.name()),
                    startingInstant,
                    endingInstant),
                new ArrayList<StartTag>(),
                ServerToClient.createStartTagTx(tzOffset));
    }

    public List<TaskTotal> refreshTotals(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, int tzOffset)
    {
        return Transforms.tr(
                timesliceSvc.refreshTotals(
                        user,
                        maxSize,
                        com.enokinomi.timeslice.lib.task.SortDir.valueOf(sortDir.name()),
                        startingInstant,
                        endingInstant,
                        allowWords,
                        ignoreWords),
                new ArrayList<TaskTotal>(),
                ServerToClient.createTaskTotal(tzOffset));
    }

    public String persistTotals(String persistAsName, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, int tzOffset, String user)
    {
        return timesliceSvc.persistTotals(
                persistAsName,
                maxSize,
                com.enokinomi.timeslice.lib.task.SortDir.valueOf(sortDir.name()),
                startingInstant,
                endingInstant,
                allowWords,
                ignoreWords,
                tzOffset,
                user);
    }

    public void addItem(String instantString, String taskDescription, String user)
    {
        timesliceSvc.addItem(instantString, taskDescription, user);
    }

    public void addItems(final String user, List<StartTag> items)
    {
        timesliceSvc.addItems(user, Transforms.tr(items, new ArrayList<com.enokinomi.timeslice.lib.task.StartTag>(), new ClientToServer(user)));
    }

    public void update(String user, StartTag editedStartTag)
    {
        timesliceSvc.update(user, new ClientToServer(user).apply(editedStartTag));
    }
}
