package com.enokinomi.timeslice.web.ordering.server.impl;

import java.util.List;

import com.enokinomi.timeslice.lib.ordering.IOrderingStore;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.google.inject.Inject;

public class OrderingSvc implements IOrderingSvc
{
    private final ISessionTracker sessionTracker;
    private final IOrderingStore<String> store;

    @Inject
    OrderingSvc(ISessionTracker sessionTracker, IOrderingStore<String> store)
    {
        this.sessionTracker = sessionTracker;
        this.store = store;
    }

    @Override
    public List<String> requestOrdering(String authToken, String setName, List<String> items)
    {
        sessionTracker.checkToken(authToken);
        return store.requestOrdering(setName, items);
    }

    @Override
    public void setOrdering(String authToken, String setName, List<String> items)
    {
        sessionTracker.checkToken(authToken);
        store.setOrdering(setName, items);
    }
}
