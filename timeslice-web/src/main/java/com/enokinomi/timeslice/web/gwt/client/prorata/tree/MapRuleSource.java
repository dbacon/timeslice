package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import java.util.LinkedHashMap;
import java.util.Map;

import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;

public class MapRuleSource implements IRuleSource
{
    private final Map<String, GroupComponent[]> rules = new LinkedHashMap<String, GroupComponent[]>();

    @Override
    public GroupComponent[] expand(String name)
    {
        return rules.get(name);
    }

    public MapRuleSource add(String name, GroupComponent[] groupComponent)
    {
        rules.put(name, groupComponent);
        return this;
    }
}
