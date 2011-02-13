package com.enokinomi.timeslice.lib.ordering.impl;

import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.ordering.api.IOrderingStore;
import com.enokinomi.timeslice.lib.ordering.api.IOrderingWorks;
import com.google.inject.Inject;

public class OrderingStore implements IOrderingStore
{
    private final IConnectionContext connContext;
    private final IOrderingWorks orderingWorks;

    @Inject
    public OrderingStore(IConnectionContext connContext, IOrderingWorks orderingWorks)
    {
        this.connContext = connContext;
        this.orderingWorks = orderingWorks;
    }

    @Override
    public List<String> requestOrdering(final String setName)
    {
        return connContext.doWorkWithinWritableContext(orderingWorks.workRequestOrdering(setName));
    }

    public void setOrdering(final String setName, final List<String> orderedSetMembers)
    {
        connContext.doWorkWithinWritableContext(orderingWorks.workSetOrdering(setName, orderedSetMembers));
    }

    @Override
    public void addPartialOrdering(final String setName, final String smaller, final List<String> larger)
    {
        connContext.doWorkWithinWritableContext(orderingWorks.workAddPartialOrdering(setName, smaller, larger));
    }
}
