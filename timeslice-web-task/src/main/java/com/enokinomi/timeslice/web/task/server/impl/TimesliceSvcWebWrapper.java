package com.enokinomi.timeslice.web.task.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.util.Transforms;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.google.inject.Inject;

/**
 * Translates between server- and client- types.
 *
 */
class TimesliceSvcWebWrapper
{
    private final ITimesliceSvc timesliceSvc;

    @Inject
    TimesliceSvcWebWrapper(ITimesliceSvc timesliceSvc)
    {
        this.timesliceSvc = timesliceSvc;
    }

    List<StartTag> refreshItems(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, int tzOffsetMinutes)
    {
        return Transforms.tr(timesliceSvc.refreshItems(
                    user,
                    maxSize,
                    com.enokinomi.timeslice.lib.task.api.SortDir.valueOf(sortDir.name()),
                    startingInstant,
                    endingInstant),
                new ArrayList<StartTag>(),
                ServerToClient.createStartTagTx(tzOffsetMinutes));
    }

    void addItem(String instantString, String taskDescription, String user)
    {
        timesliceSvc.addItem(instantString, taskDescription, user);
    }

    void addItems(final String user, List<StartTag> items)
    {
        timesliceSvc.addItems(user, Transforms.tr(items, new ArrayList<com.enokinomi.timeslice.lib.task.api.StartTag>(), new ClientToServer(user)));
    }

    void update(String user, StartTag editedStartTag)
    {
        timesliceSvc.update(user, new ClientToServer(user).apply(editedStartTag));
    }
}
