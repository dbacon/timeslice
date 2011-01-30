package com.enokinomi.timeslice.lib.task.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.lib.task.api.ITimesliceStore;
import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.task.api.SortDir;
import com.enokinomi.timeslice.lib.task.api.StartTag;
import com.enokinomi.timeslice.lib.task.api.TaskTotalMember;
import com.google.inject.Inject;

public class TimesliceSvc implements ITimesliceSvc
{
    private final ITimesliceStore store;
    private final Sum summer;
    private final Aggregate aggregator;
    private final Split splitter;

    @Inject
    TimesliceSvc(ITimesliceStore store, Sum summer, Aggregate aggregate, Split splitter)
    {
        this.store = store;
        this.summer = summer;
        this.aggregator = aggregate;
        this.splitter = splitter;
    }

    @Override
    public List<com.enokinomi.timeslice.lib.task.api.StartTag> queryForTags(String who, Boolean sortReverse, Instant minDate, Instant maxDate, int pageSize, int pageIndex)
    {
        return splitter.split(
                store.query(
                        who,
                        minDate,
                        maxDate,
                        pageSize,
                        pageIndex),
                new Instant()); // TODO: change to now-provider
    }

    @Override
    public List<StartTag> refreshItems(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant)
    {
        return queryForTags(
                user,
                sortDir == SortDir.desc, // check this
                startingInstant == null
                    ? new Instant(0)
                    : ISODateTimeFormat.dateTime().parseDateTime(startingInstant).toInstant(),
                endingInstant == null
                    ? new Instant(Long.MAX_VALUE)
                    : ISODateTimeFormat.dateTime().parseDateTime(endingInstant).toInstant(),
                maxSize,
                0);
    }

    private Collection<com.enokinomi.timeslice.lib.task.api.TaskTotal> filterItems(Collection<com.enokinomi.timeslice.lib.task.api.TaskTotal> items, List<String> allowWords, List<String> ignoreWords)
    {
        ArrayList<com.enokinomi.timeslice.lib.task.api.TaskTotal> result = new ArrayList<com.enokinomi.timeslice.lib.task.api.TaskTotal>();

        boolean allowfilter = !allowWords.isEmpty();
        for (com.enokinomi.timeslice.lib.task.api.TaskTotal item: items)
        {
            if (allowfilter)
            {
                boolean foundatleastone = false;
                for (String allowWord: allowWords)
                {
                    if (item.getWhat().contains(allowWord))
                    {
                        foundatleastone = true;
                        break;
                    }
                }
                if (!foundatleastone) continue;
            }

            boolean shouldIgnore = false;
            for (String ignoreWord: ignoreWords)
            {
                if (!ignoreWord.isEmpty() && item.getWhat().contains(ignoreWord))
                {
                    shouldIgnore = true;
                    break;
                }
            }
            if (shouldIgnore) continue;

            result.add(item);
        }

        return result;
    }

    private Double calcTotal(List<com.enokinomi.timeslice.lib.task.api.TaskTotal> items)
    {
        Double total = 0.;
        for (com.enokinomi.timeslice.lib.task.api.TaskTotal item: items)
        {
            total += item.getMillis();
        }
        return total;
    }

    @Override
    public List<TaskTotalMember> createReport(List<com.enokinomi.timeslice.lib.task.api.TaskTotal> items)
    {
        ArrayList<TaskTotalMember> result = new ArrayList<TaskTotalMember>();

        Double total = calcTotal(items);

        for(com.enokinomi.timeslice.lib.task.api.TaskTotal item: items)
        {
            result.add(new TaskTotalMember(
                    item.getWho(),
                    item.getMillis(),
                    item.getWhat(),
                    item.getMillis() / total
                    ));
        }

        return result;
    }

    @Override
    public List<TaskTotalMember> refreshTotals(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        List<com.enokinomi.timeslice.lib.task.api.StartTag> tags = queryForTags(
                user,
                sortDir == SortDir.asc,
                startingInstant == null
                    ? new Instant(0)
                    : ISODateTimeFormat.dateTime().parseDateTime(startingInstant).toInstant(),
                endingInstant == null
                    ? new Instant(Integer.MAX_VALUE)
                    : ISODateTimeFormat.dateTime().parseDateTime(endingInstant).toInstant(),
                maxSize,
                0);

        return createReport(new ArrayList<com.enokinomi.timeslice.lib.task.api.TaskTotal>(
               filterItems(
                       aggregator.sumThem(summer, aggregator.aggregate(tags)).values(),
                       allowWords,
                       ignoreWords)));
    }

    @Override
    public void addItem(String instantString, String taskDescription, String user)
    {
        store.add(new StartTag(user, instantString, taskDescription, null));
    }

    @Override
    public void addItems(String user, List<StartTag> items)
    {
        for (StartTag item: items)
        {
            store.add(item);
        }
    }

    @Override
    public void update(String user, StartTag editedStartTag)
    {
        store.updateText(editedStartTag);
    }
}
