package com.enokinomi.timeslice.app.core;

import org.joda.time.DateTime;

public class MemoryTagStore implements ITagStore
{
    @Override
    public String lookupBillee(String description, DateTime asOf)
    {
        return "";
    }

    @Override
    public void assignBillee(String description, String billee, DateTime asOf)
    {
    }

}
