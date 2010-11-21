package com.enokinomi.timeslice.lib.userinfo;

public class ConfEntry
{
    private final String name;
    private final String type;
    private final String value;

    ConfEntry(String name, String type, String value)
    {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }
}
