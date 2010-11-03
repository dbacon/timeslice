package com.enokinomi.timeslice.web.ordering.server.impl;

import java.util.List;

import com.enokinomi.timeslice.lib.ordering2.IOrderingStore;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvc2;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.google.inject.Inject;

public class OrderingSvc2 implements IOrderingSvc2
{
    private final ISessionTracker sessionTracker;
    private final IOrderingStore orderingStore;

    @Inject
    OrderingSvc2(ISessionTracker sessionTracker, IOrderingStore orderingStore2)
    {
        this.sessionTracker = sessionTracker;
        this.orderingStore = orderingStore2;
    }

    @Override
    public List<String> requestOrdering(String authToken, String setName, List<String> items)
    {
        sessionTracker.checkToken(authToken);
        return orderingStore.requestOrdering(setName, items);
    }

    @Override
    public void setPartialOrdering(String authToken, String setName, String smaller, List<String> larger)
    {
        sessionTracker.checkToken(authToken);
        orderingStore.addPartialOrdering(setName, smaller, larger);
    }
}
