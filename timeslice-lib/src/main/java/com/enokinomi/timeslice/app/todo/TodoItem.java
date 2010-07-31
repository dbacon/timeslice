package com.enokinomi.timeslice.app.todo;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.app.generic.IHasWhen;
import com.enokinomi.timeslice.app.generic.IListable;




public class TodoItem implements IListable, IHasWhen
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

    @Override
    public String getListableHref()
    {
        return ISODateTimeFormat.dateTime().print(getWhen());
    }

    @Override
    public String getListableName()
    {
        return getDescription();
    }
}
