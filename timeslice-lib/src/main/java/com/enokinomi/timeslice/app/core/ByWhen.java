package com.enokinomi.timeslice.app.core;

import java.util.Comparator;

public class ByWhen implements Comparator<StartTag>
{
    @Override
    public int compare(StartTag o1, StartTag o2)
    {
        return o1.getWhen().compareTo(o2.getWhen());
    }
}
