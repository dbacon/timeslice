package com.enokinomi.timeslice.lib.ordering.impl;

import java.sql.Connection;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.ordering.api.IOrderingWorks;
import com.google.inject.Inject;

public class OrderingWorks implements IOrderingWorks
{
    private final BaseLowLevelOrderingWorks basicWorks;

    @Inject
    OrderingWorks(BaseLowLevelOrderingWorks basicWorks)
    {
        this.basicWorks = basicWorks;
    }

    @Override
    public IConnectionWork<List<String>> workRequestOrdering(final String setName)
    {
        return new IConnectionWork<List<String>>()
        {
            @Override
            public List<String> performWithConnection(Connection conn)
            {
                List<String> orderSet = basicWorks.workGetSet(setName).performWithConnection(conn);
                return orderSet;
            }
        };
    }

    @Override
    public IConnectionWork<Void> workSetOrdering(final String setName, final List<String> orderedSetMembers)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                basicWorks.workDeleteSetByName(setName).performWithConnection(conn);
                basicWorks.workInsertSet(setName, orderedSetMembers).performWithConnection(conn);
                return null;
            }
        };
    }

    @Override
    public IConnectionWork<Void> workAddPartialOrdering(final String setName, final String smaller, final List<String> larger)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                List<String> order = basicWorks.workGetSet(setName).performWithConnection(conn);

                order.removeAll(larger);

                int indexOfAnchor = -1;

                if (null != smaller)
                {
                    if (larger.contains(smaller)) throw new IllegalArgumentException("larger set cannot contain smaller element");

                    if (!order.contains(smaller)) order.add(smaller);

                    indexOfAnchor = order.indexOf(smaller);
                }

                order.addAll(indexOfAnchor + 1, larger);

                basicWorks.workDeleteSetByName(setName).performWithConnection(conn);
                basicWorks.workInsertSet(setName, order).performWithConnection(conn);

                return null; // Void
            }
        };
    }
}
