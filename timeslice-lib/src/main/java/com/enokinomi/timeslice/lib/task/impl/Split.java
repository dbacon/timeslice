package com.enokinomi.timeslice.lib.task.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.generic.CompareByTime;
import com.enokinomi.timeslice.lib.task.api.StartTag;


/**
 * Responsible for enriching {@link StartTag}s to contain
 * independent, known-length chunks of time.
 *
 * <p>
 * The purpose of this class is to operate on a list of {@code StartTag}s, where for a given tag T,
 * it retrieves the start-time of the next {@code StartTag} and stores it as the end-time of T,
 * giving T the complete description of it's length of time, independent of any other {@code StartTag}.
 * </p>
 *
 * <p>
 * Initially, {@code StartTag}s are created and managed with only a start-time, and their end-time
 * is implicitly the start-time of the nearest {@code StartTag} forward in time.
 * This is useful during input and manipulation of the data during it's creation.
 * However, once the dataset is finalized, it is difficult to move around, sort, sum, group, &amp; c. if
 * part of a {@code StartTag}s definition is still stored in another {@code StartTag}.
 * </p>
 *
 */
public class Split
{
    Split() { }

    public List<StartTag> split(List<? extends StartTag> tags, Instant endInstantOfLastTasks)
    {
        List<StartTag> localTags = new ArrayList<StartTag>(tags);
        Collections.sort(localTags, new CompareByTime<StartTag>());

        List<StartTag> result = new LinkedList<StartTag>();

        Map<String, StartTag> lastStartTagForUser = new LinkedHashMap<String, StartTag>();

        for (StartTag tag: localTags)
        {
            StartTag lastStartTag = lastStartTagForUser.get(tag.getWho());

            if (null != lastStartTag)
            {
                StartTag enrichedLastStartTag = new StartTag(lastStartTag.getWho(), lastStartTag.getWhen(), lastStartTag.getWhat(), tag.getWhen());

                result.add(enrichedLastStartTag);
            }

            lastStartTagForUser.put(tag.getWho(), tag);
        }

        for (StartTag tag: lastStartTagForUser.values())
        {
            result.add(new StartTag(tag.getWho(), tag.getWhen(), tag.getWhat(), max(tag.getWhen(), endInstantOfLastTasks)));
        }

        // we want to return items in the same order as the input.

        Map<Instant, StartTag> map = new LinkedHashMap<Instant, StartTag>();
        for (StartTag tag: result)
        {
            map.put(tag.getWhen(), tag);
        }

        List<StartTag> orderedResult = new ArrayList<StartTag>(result.size());
        for (StartTag origTag: tags)
        {
            orderedResult.add(map.get(origTag.getWhen()));
        }

        return orderedResult;
    }

    public static Instant max(Instant a, Instant b)
    {
        if (a.isAfter(b))
        {
            return a;
        }
        else
        {
            return b;
        }
    }

}
