package com.enokinomi.timeslice.lib.util;

import java.util.Comparator;

public class CompareByTime<T extends IHasWhen> implements Comparator<T>
{
    @Override
    public int compare(T o1, T o2)
    {
        return o1.getWhen().compareTo(o2.getWhen());
    }
}
