package com.enokinomi.timeslice.app.todo;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.generic.IHasWhen;




public class TodoItem implements IHasWhen
{
    private final Instant when;
    private final String description;

    public TodoItem(Instant when, String description)
    {
        this.when = when;
        this.description = description;
    }

    public Instant getWhen()
    {
        return when;
    }

    public String getDescription()
    {
        return description;
    }
}
