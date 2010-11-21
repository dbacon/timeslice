package com.enokinomi.timeslice.lib.prorata.impl;

import java.math.BigDecimal;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.prorata.api.GroupComponent;
import com.enokinomi.timeslice.lib.prorata.api.IProRataStore;
import com.enokinomi.timeslice.lib.prorata.api.IProRataWorks;
import com.enokinomi.timeslice.lib.util.Pair;
import com.google.inject.Inject;

public class HsqldbStore implements IProRataStore
{
    private final IConnectionContext connContext;
    private final IProRataWorks proRataWorks;

    @Inject
    public HsqldbStore(IConnectionContext connContext, IProRataWorks proRataWorks)
    {
        this.connContext = connContext;
        this.proRataWorks = proRataWorks;
    }

    @Override
    public void removeComponent(final String groupName, final String componentName)
    {
        connContext.doWorkWithinContext(proRataWorks.workRemoveComponent(groupName, componentName));
    }

    @Override
    public void addComponent(final String groupName, final String componentName, final BigDecimal weight)
    {
        connContext.doWorkWithinContext(proRataWorks.workAddComponent(groupName, componentName, weight));
    }

    @Override
    public List<String> listGroupNames()
    {
        return connContext.doWorkWithinContext(proRataWorks.workListGroupNames());
    }

    @Override
    public List<GroupComponent> dereferenceGroup(final String groupName)
    {
        return connContext.doWorkWithinContext(proRataWorks.workDereferenceGroup(groupName));
    }

    @Override
    public List<Pair<String, List<GroupComponent>>> listAllGroupsInfo()
    {
        return connContext.doWorkWithinContext(proRataWorks.workListAllGroupsInfo());
    }

}
