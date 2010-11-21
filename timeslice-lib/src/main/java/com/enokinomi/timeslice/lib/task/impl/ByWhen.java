package com.enokinomi.timeslice.lib.task.impl;

import java.util.Comparator;

import com.enokinomi.timeslice.lib.task.api.StartTag;

public class ByWhen implements Comparator<StartTag>
{
    ByWhen() { }

    @Override
    public int compare(StartTag o1, StartTag o2)
    {
        return o1.getWhen().compareTo(o2.getWhen());
    }
}
