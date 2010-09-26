package com.enokinomi.timeslice.web.gwt.client.prorata.core;

import java.io.Serializable;

public class GroupComponent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String groupName;
    private String name;
    private String weight;

    public GroupComponent()
    {
        this(null, null, null);
    }

    public GroupComponent(String groupName, String name, String weight)
    {
        this.groupName = groupName;
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

    public void setName(String name)
    {
        this.name = name;
    }

    public void setWeight(String weight)
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
