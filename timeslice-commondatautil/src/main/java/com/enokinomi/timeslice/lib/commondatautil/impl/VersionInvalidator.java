package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.util.ArrayList;
import java.util.List;

public class VersionInvalidator
{
    public static interface Invalidator
    {
        void invalidate();
    }

    private List<Invalidator> invalidators = new ArrayList<VersionInvalidator.Invalidator>();

    public void register(Invalidator i)
    {
        if (i != null) invalidators.add(i);
    }

    public void fireInvalidate()
    {
        for (Invalidator i: invalidators) i.invalidate();
    }

}
