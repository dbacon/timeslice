package com.enokinomi.timeslice.app.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enokinomi.timeslice.app.core.StartTag;
import com.enokinomi.timeslice.app.core.TaskTotal;
import com.enokinomi.timeslice.lib.util.Transforms;


public class Aggregate
{
    public Map<String, List<StartTag>> aggregate(List<StartTag> items)
    {
        return Bucket.create(Transforms.member(StartTag.class, String.class, "what")).bucket(items).getBuckets();
    }

    public Map<String, TaskTotal> sumThem(Sum summer, Map<String, List<StartTag>> buckets)
    {
        Map<String, TaskTotal> result = new LinkedHashMap<String, TaskTotal>();

        for (Entry<String, List<StartTag>> entry: buckets.entrySet())
        {
            result.put(entry.getKey(), summer.sum(entry.getValue()));
        }

        return result;
    }
}
