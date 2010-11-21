package com.enokinomi.timeslice.lib.task.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.lib.task.api.ISafeDirProvider;
import com.enokinomi.timeslice.lib.task.api.ITimesliceStore;
import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.task.api.SortDir;
import com.enokinomi.timeslice.lib.task.api.StartTag;
import com.enokinomi.timeslice.lib.task.api.TaskTotalMember;
import com.google.inject.Inject;

public class TimesliceSvc implements ITimesliceSvc
{
    private static final Logger log = Logger.getLogger(TimesliceSvc.class);

    private String reportPrefix = "";
    private final ITimesliceStore store;
    private final Sum summer;
    private final Aggregate aggregator;
    private final Split splitter;
    private final ISafeDirProvider safeDirProvider;

    @Inject
    TimesliceSvc(ITimesliceStore store, Sum summer, Aggregate aggregate, Split splitter, ISafeDirProvider safeDirProvider)
    {
        this.store = store;
        this.summer = summer;
        this.aggregator = aggregate;
        this.splitter = splitter;
        this.safeDirProvider = safeDirProvider;
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
    public String persistTotals(String persistAsName, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, int tzOffset, String user)
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

        List<TaskTotalMember> rows = createReport(new ArrayList<com.enokinomi.timeslice.lib.task.api.TaskTotal>(
                    filterItems(
                        aggregator.sumThem(summer, aggregator.aggregate(tags)).values(),
                        allowWords,
                        ignoreWords)));

        ArrayList<String> lines = new ArrayList<String>();
        for (TaskTotalMember row: rows)
        {
            lines.add(String.format("%s,%s,%.6f", row.getWho(), row.getWhat(), row.getMillis() / 1000. / 60. / 60.));
        }

        String filename = reportPrefix;
        if (null != persistAsName && !persistAsName.isEmpty()) filename = filename + persistAsName;
        String ts = new Instant().toDateTime(DateTimeZone.forOffsetHours(tzOffset)).toString();
        filename = filename + "." + ts + ".ts-snapshot.csv";

        filename = filename.replaceAll(":", "-"); // armor the filename for filesystems which don't support ':'
        File report = new File(safeDirProvider.getSafeDir(), filename);
        try
        {
            FileUtils.writeLines(report, lines);
            if (log.isInfoEnabled()) log.info("Wrote report to '" + report.getAbsolutePath() + "'.");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not persist report: " + e.getMessage());
        }

        // TODO, write to a file in an accessible location, build & return a link.
        // TODO, for security, link should point to a servlet, which checks authentication, like this one does.
        return filename;
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
