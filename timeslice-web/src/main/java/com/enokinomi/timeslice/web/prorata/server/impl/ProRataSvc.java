package com.enokinomi.timeslice.web.prorata.server.impl;

import static com.enokinomi.timeslice.web.util.Transform.tr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.prorata.IProRataStore;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;
import com.enokinomi.timeslice.web.prorata.client.core.IProRataSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.google.inject.Inject;

public class ProRataSvc implements IProRataSvc
{
    private final class ToComponent implements ITransform<com.enokinomi.timeslice.lib.prorata.GroupComponent, GroupComponent>
    {
        @Override
        public GroupComponent apply(com.enokinomi.timeslice.lib.prorata.GroupComponent r)
        {
            return new GroupComponent(r.getGroupName(), r.getName(), r.getWeight().doubleValue());
        }
    }

    private final ISessionTracker sessionTracker;
    private final IProRataStore store;

    @Inject
    ProRataSvc(ISessionTracker sessionTracker, IProRataStore store)
    {
        this.sessionTracker = sessionTracker;
        this.store = store;
    }

    @Override
    public void addGroupComponent(String authToken, String groupName, String componentName, Double weight)
    {
        sessionTracker.checkToken(authToken);
        store.addComponent(groupName, componentName, new BigDecimal(weight));
    }

    @Override
    public void removeGroupComponent(String authToken, String groupName, String componentName)
    {
        sessionTracker.checkToken(authToken);
        store.removeComponent(groupName, componentName);
    }

    @Override
    public List<GroupComponent> expandGroup(String authToken, String groupName)
    {
        sessionTracker.checkToken(authToken);
        List<com.enokinomi.timeslice.lib.prorata.GroupComponent> groupComponents = store.dereferenceGroup(groupName);
        return tr(groupComponents, new ArrayList<GroupComponent>(groupComponents.size()), new ToComponent());
    }

    @Override
    public List<String> listGroups(String authToken)
    {
        sessionTracker.checkToken(authToken);
        return store.listGroupNames();
    }

    @Override
    public List<Group> listAllGroupInfo(String authToken)
    {
        sessionTracker.checkToken(authToken);

        List<Group> result = new ArrayList<Group>();
        ToComponent tx = new ToComponent();
        for (com.enokinomi.timeslice.lib.util.Pair<String, List<com.enokinomi.timeslice.lib.prorata.GroupComponent>> group: store.listAllGroupsInfo())
        {
            result.add(new Group(group.first, tr(group.second, new ArrayList<GroupComponent>(group.second.size()), tx)));
        }

        return result;
    }
}
