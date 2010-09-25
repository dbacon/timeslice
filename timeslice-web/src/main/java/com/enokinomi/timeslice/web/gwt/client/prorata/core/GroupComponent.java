package com.enokinomi.timeslice.web.gwt.client.prorata.core;

public class GroupComponent
{
    private final String name;
    private final String weight;

    public GroupComponent(String name, String weight)
    {
        this.name = name;
        this.weight = weight;
    }

    public String getName()
    {
        return name;
    }

    public String getWeight()
    {
        return weight;
    }

}
