package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;

public class GroupComponent
{
    private final String groupName;
    private final String name;
    private final BigDecimal weight;

    public GroupComponent(String groupName, String name, BigDecimal weight)
    {
        this.groupName = groupName;
        this.name = name;
        this.weight = weight;
    }

    public String getName()
    {
        return name;
    }

    public BigDecimal getWeight()
    {
        return weight;
    }

    public String getGroupName()
    {
        return groupName;
    }
}
