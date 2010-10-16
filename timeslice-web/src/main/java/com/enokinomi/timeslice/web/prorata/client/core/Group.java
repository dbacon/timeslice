package com.enokinomi.timeslice.web.prorata.client.core;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;
    private List<GroupComponent> components;

    public Group()
    {
    }

    public Group(String name, List<GroupComponent> components)
    {
        this.name = name;
        this.components = components;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<GroupComponent> getComponents()
    {
        return components;
    }

    public void setComponents(List<GroupComponent> components)
    {
        this.components = components;
    }

}
