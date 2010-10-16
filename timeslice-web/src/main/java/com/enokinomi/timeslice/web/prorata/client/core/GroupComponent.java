package com.enokinomi.timeslice.web.prorata.client.core;

import java.io.Serializable;

public class GroupComponent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String groupName;
    private String name;
    private Double weight;

    public GroupComponent()
    {
        this(null, null, null);
    }

    public GroupComponent(String groupName, String name, Double weight)
    {
        this.groupName = groupName;
        this.name = name;
        this.weight = weight;
    }

    public String getName()
    {
        return name;
    }

    public Double getWeight()
    {
        return weight;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setWeight(Double weight)
    {
        this.weight = weight;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

}
