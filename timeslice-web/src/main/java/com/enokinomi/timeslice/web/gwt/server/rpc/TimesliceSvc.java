package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.app.core.Aggregate;
import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.Split;
import com.enokinomi.timeslice.app.core.Sum;
import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;
import com.enokinomi.timeslice.web.gwt.server.beantx.ServerToClient;
import com.google.inject.Inject;

public class TimesliceSvc
{
    private String reportPrefix = "";
    private String safeDir = ".";
    private final ITimesliceStore store;
    private final Sum summer;
    private final Aggregate aggregator;
    private final Split splitter;

    @Inject
    public TimesliceSvc(ITimesliceStore store, Sum summer, Aggregate aggregate, Split splitter)
    {
        this.store = store;
        this.summer = summer;
        this.aggregator = aggregate;
        this.splitter = splitter;
    }

    public List<com.enokinomi.timeslice.app.core.StartTag> queryForTags(String who, Boolean sortReverse, Instant minDate, Instant maxDate, int pageSize, int pageIndex)
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

    public List<StartTag> refreshItems(String user, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, int tzOffset)
    {
        return new ArrayList<StartTag>(Transform.tr(queryForTags(
                user,
                sortDir == SortDir.desc, // check this
                startingInstant == null
                    ? new Instant(0)
                    : ISODateTimeFormat.dateTime().parseDateTime(startingInstant).toInstant(),
                endingInstant == null
                    ? new Instant(Long.MAX_VALUE)
                    : ISODateTimeFormat.dateTime().parseDateTime(endingInstant).toInstant(),
                maxSize,
                0),
            ServerToClient.createStartTagTx(tzOffset)));
    }

    private Collection<com.enokinomi.timeslice.app.core.TaskTotal> filterItems(Collection<com.enokinomi.timeslice.app.core.TaskTotal> items, List<String> allowWords, List<String> ignoreWords)
    {
        ArrayList<com.enokinomi.timeslice.app.core.TaskTotal> result = new ArrayList<com.enokinomi.timeslice.app.core.TaskTotal>();

        boolean allowfilter = !allowWords.isEmpty();
        for (com.enokinomi.timeslice.app.core.TaskTotal item: items)
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

    private Double calcTotal(List<com.enokinomi.timeslice.app.core.TaskTotal> items)
    {
        Double total = 0.;
        for (com.enokinomi.timeslice.app.core.TaskTotal item: items)
        {
            total += item.getMillis();
        }
        return total;
    }

    public List<TaskTotal> createReport(List<com.enokinomi.timeslice.app.core.TaskTotal> items)
    {
        ArrayList<TaskTotal> result = new ArrayList<TaskTotal>();

        Double total = calcTotal(items);

        for(com.enokinomi.timeslice.app.core.TaskTotal item: items)
        {
            result.add(new TaskTotal(
                    item.getWho(),
                    item.getMillis() / 1000. / 60. / 60.,
                    item.getMillis() / total,
                    item.getWhat()
                    ));
        }

        return result;
    }

    public List<TaskTotal> refreshTotals(String user, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        List<com.enokinomi.timeslice.app.core.StartTag> tags = queryForTags(
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

        return createReport(new ArrayList<com.enokinomi.timeslice.app.core.TaskTotal>(
               filterItems(
                       aggregator.sumThem(summer, aggregator.aggregate(tags)).values(),
                       allowWords,
                       ignoreWords)));
    }

    public String persistTotals(String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, int tzOffset, String user)
    {
        List<com.enokinomi.timeslice.app.core.StartTag> tags = queryForTags(
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

        List<TaskTotal> rows = createReport(new ArrayList<com.enokinomi.timeslice.app.core.TaskTotal>(
                    filterItems(
                        aggregator.sumThem(summer, aggregator.aggregate(tags)).values(),
                        allowWords,
                        ignoreWords)));

        ArrayList<String> lines = new ArrayList<String>();
        for (TaskTotal row: rows)
        {
            lines.add(String.format("%s,%s,%.6f", row.getWho(), row.getWhat(), row.getHours()));
        }

        String filename = reportPrefix;
        if (null != persistAsName && !persistAsName.isEmpty()) filename = filename + persistAsName;
        String ts = new Instant().toDateTime(DateTimeZone.forOffsetHours(tzOffset)).toString();
        filename = filename + "." + ts + ".ts-snapshot.csv";

        File safeDirFile = new File(safeDir);
        filename = filename.replaceAll(":", "-"); // armor the filename for filesystems which don't support ':'
        File report = new File(safeDirFile, filename);
        try
        {
            FileUtils.writeLines(report, lines);
            System.out.println("Wrote report to '" + report.getAbsolutePath() + "'.");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not persist report: " + e.getMessage());
        }

        // TODO, write to a file in an accessible location, build & return a link.
        // TODO, for security, link should point to a servlet, which checks authentication, like this one does.
        return filename;
    }

    public void addItem(String instantString, String taskDescription, String user)
    {
        store.add(new com.enokinomi.timeslice.app.core.StartTag(user, instantString, taskDescription, null));
    }

    public void addItems(String user, List<StartTag> items)
    {
        for (StartTag item: items)
        {
            store.add(new com.enokinomi.timeslice.app.core.StartTag(user, item.getInstantString(), item.getDescription(), null));
        }
    }

    public void update(String user, StartTag editedStartTag)
    {
        com.enokinomi.timeslice.app.core.StartTag edited = new com.enokinomi.timeslice.app.core.StartTag(user, editedStartTag.getInstantString(), editedStartTag.getDescription(), null);

        store.updateText(edited);
    }
}
