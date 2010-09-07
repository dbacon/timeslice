package com.enokinomi.timeslice.app.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

public class MemoryTimesliceStore implements ITimesliceStore
{
    private final Map<Instant, StartTag> store = new LinkedHashMap<Instant, StartTag>();
    private Comparator<StartTag> sorter;

    public MemoryTimesliceStore(Instant starting, Instant ending, String firstTagText, Comparator<StartTag> sorter)
    {
        this.sorter = sorter;
    }

    @Override
    public void add(StartTag tag)
    {
        store.put(tag.getWhen(), tag);
    }

    @Override
    public void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        Map<Instant, StartTag> p = new LinkedHashMap<Instant, StartTag>();
        for(StartTag tag: tags)
        {
            p.put(tag.getWhen(), tag);
        }

        store.putAll(p);
    }

    @Override
    public List<StartTag> query(String who, Instant starting, Instant ending, int pageSize, int pageIndex)
    {
        if (pageSize < 0) throw new RuntimeException("Page-size must not be negative.");
        if (pageIndex < 0) throw new RuntimeException("Page-index must not be negative.");

        ArrayList<StartTag> result = new ArrayList<StartTag>();

        for(StartTag tag: store.values())
        {
            if (tag.getWhen().isAfter(starting) && tag.getWhen().isBefore(ending))
            {
                result.add(tag);
            }
        }

        Collections.sort(result, sorter);
        Collections.reverse(result);

        int si = pageIndex*pageSize;
        int ei = (pageIndex+1)*pageSize;

        si = Math.max(0, si);
        si = Math.min(result.size(), si);
        ei = Math.max(0, si);
        ei = Math.max(result.size(), ei);

        return result.subList(si, ei);
    }

    @Override
    public void remove(StartTag tag)
    {
        store.remove(tag.getWhen());
    }

    @Override
    public void updateText(StartTag tag)
    {
        if (!store.containsKey(tag.getWhen())) throw new RuntimeException("Specified tag not found.");

        store.put(tag.getWhen(), tag);
    }
}
