package com.enokinomi.timeslice.lib.prorata.impl;

import java.math.BigDecimal;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.prorata.api.GroupComponent;
import com.enokinomi.timeslice.lib.prorata.api.IProrataStore;
import com.enokinomi.timeslice.lib.prorata.api.IProrataWorks;
import com.enokinomi.timeslice.lib.util.Pair;
import com.google.inject.Inject;

public class HsqldbStore implements IProrataStore
{
    private final IConnectionContext connContext;
    private final IProrataWorks proRataWorks;

    @Inject
    public HsqldbStore(IConnectionContext connContext, IProrataWorks proRataWorks)
    {
        this.connContext = connContext;
        this.proRataWorks = proRataWorks;
    }

    @Override
    public void removeComponent(final String groupName, final String componentName)
    {
        connContext.doWorkWithinWritableContext(proRataWorks.workRemoveComponent(groupName, componentName));
    }

    @Override
    public void addComponent(final String groupName, final String componentName, final BigDecimal weight)
    {
        connContext.doWorkWithinWritableContext(proRataWorks.workAddComponent(groupName, componentName, weight));
    }

    @Override
    public List<String> listGroupNames()
    {
        return connContext.doWorkWithinWritableContext(proRataWorks.workListGroupNames());
    }

    @Override
    public List<GroupComponent> dereferenceGroup(final String groupName)
    {
        return connContext.doWorkWithinWritableContext(proRataWorks.workDereferenceGroup(groupName));
    }

    @Override
    public List<Pair<String, List<GroupComponent>>> listAllGroupsInfo()
    {
        return connContext.doWorkWithinWritableContext(proRataWorks.workListAllGroupsInfo());
    }

}
