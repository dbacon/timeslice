package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;

public class GroupComponent
{
    private final String name;
    private final BigDecimal weight;

    public GroupComponent(String name, BigDecimal weight)
    {
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
}
