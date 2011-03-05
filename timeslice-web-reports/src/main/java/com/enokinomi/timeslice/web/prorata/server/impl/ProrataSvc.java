package com.enokinomi.timeslice.web.prorata.server.impl;

import static com.enokinomi.timeslice.lib.util.Transforms.tr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.enokinomi.timeslice.lib.prorata.api.IProrataStore;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.core.server.util.Catcher;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;
import com.enokinomi.timeslice.web.prorata.client.core.IProrataSvc;
import com.enokinomi.timeslice.web.session.server.api.ISessionTracker;
import com.google.inject.Inject;

class ProrataSvc implements IProrataSvc
{
    private final class ToComponent implements ITransform<com.enokinomi.timeslice.lib.prorata.api.GroupComponent, GroupComponent>
    {
        @Override
        public GroupComponent apply(com.enokinomi.timeslice.lib.prorata.api.GroupComponent r)
        {
            return new GroupComponent(r.getGroupName(), r.getName(), r.getWeight().doubleValue());
        }
    }

    private final ISessionTracker sessionTracker;
    private final IProrataStore store;

    @Inject
    ProrataSvc(ISessionTracker sessionTracker, IProrataStore store)
    {
        this.sessionTracker = sessionTracker;
        this.store = store;
    }

    @Override
    public void addGroupComponent(String authToken, final String groupName, final String componentName, final Double weight)
    {
        sessionTracker.checkToken(authToken);

        new Catcher().catchAndWrap("service-call add-group-component", new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                store.addComponent(groupName, componentName, new BigDecimal(weight));
                return null; // Void
            }
        });
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
        List<com.enokinomi.timeslice.lib.prorata.api.GroupComponent> groupComponents = store.dereferenceGroup(groupName);
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
        for (com.enokinomi.timeslice.lib.util.Pair<String, List<com.enokinomi.timeslice.lib.prorata.api.GroupComponent>> group: store.listAllGroupsInfo())
        {
            result.add(new Group(group.first, tr(group.second, new ArrayList<GroupComponent>(group.second.size()), tx)));
        }

        return result;
    }
}
