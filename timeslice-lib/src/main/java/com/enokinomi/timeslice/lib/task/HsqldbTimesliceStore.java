package com.enokinomi.timeslice.lib.task;

import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.google.inject.Inject;


public class HsqldbTimesliceStore implements ITimesliceStore
{
    private final IConnectionContext connContext;
    private final ITimesliceWorks timesliceWorks;

    @Inject
    public HsqldbTimesliceStore(IConnectionContext connContext, ITimesliceWorks timesliceWorks)
    {
        this.connContext = connContext;
        this.timesliceWorks = timesliceWorks;
    }

    @Override
    public synchronized void add(final StartTag tag)
    {
        connContext.doWorkWithinContext(timesliceWorks.workAdd(tag));
    }

    @Override
    public synchronized void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        throw new RuntimeException("TODO: need to implement addAll"); // TODO: addAll()
    }

    @Override
    public synchronized List<StartTag> query(final String owner, final Instant starting, final Instant ending, final int pageSize, final int pageIndex)
    {
        return connContext.doWorkWithinContext(timesliceWorks.workQuery(owner, starting, ending, pageSize, pageIndex));
    }

    @Override
    public synchronized void remove(final StartTag tag)
    {
        connContext.doWorkWithinContext(timesliceWorks.workRemove(tag));
    }

    @Override
    public synchronized void updateText(final StartTag tag)
    {
        connContext.doWorkWithinContext(timesliceWorks.workUpdateText(tag));
    }

}
