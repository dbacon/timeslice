package com.enokinomi.timeslice.lib.task.impl;

import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.task.api.ITimesliceStore;
import com.enokinomi.timeslice.lib.task.api.ITimesliceWorks;
import com.enokinomi.timeslice.lib.task.api.StartTag;
import com.google.inject.Inject;


public class HsqldbTimesliceStore implements ITimesliceStore
{
    private final IConnectionContext connContext;
    private final ITimesliceWorks timesliceWorks;

    @Inject
    HsqldbTimesliceStore(IConnectionContext connContext, ITimesliceWorks timesliceWorks)
    {
        this.connContext = connContext;
        this.timesliceWorks = timesliceWorks;
    }

    @Override
    public synchronized void add(final StartTag tag)
    {
        connContext.doWorkWithinWritableContext(timesliceWorks.workAdd(tag));
    }

    @Override
    public synchronized void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        throw new RuntimeException("TODO: need to implement addAll"); // TODO: addAll()
    }

    @Override
    public synchronized List<StartTag> query(final String owner, final Instant starting, final Instant ending, final int pageSize, final int pageIndex)
    {
        return connContext.doWorkWithinWritableContext(timesliceWorks.workQuery(owner, starting, ending, pageSize, pageIndex));
    }

    @Override
    public synchronized void remove(final StartTag tag)
    {
        connContext.doWorkWithinWritableContext(timesliceWorks.workRemove(tag));
    }

    @Override
    public synchronized void updateText(final StartTag tag)
    {
        connContext.doWorkWithinWritableContext(timesliceWorks.workUpdateText(tag));
    }

}
