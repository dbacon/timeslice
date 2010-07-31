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
import com.enokinomi.timeslice.app.core.Sum;
import com.enokinomi.timeslice.timeslice.TimesliceApp;
import com.enokinomi.timeslice.web.gwt.client.beans.NotAuthenticException;
import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.server.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;
import com.enokinomi.timeslice.web.gwt.server.beantx.ServerToClient;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TimesliceSvc extends RemoteServiceServlet implements ITimesliceSvc
{
    private static final long serialVersionUID = 1L;

    private Sum summer = new Sum();
    private Aggregate aggregator = new Aggregate();
    private SessionTracker sessionTracker = new SessionTracker();

    @Override
    public String serverInfo()
    {
        return "1.0.10-SNAPSHOT"; // TODO: get from resources/build
    }

    protected TimesliceApp getTimesliceApp()
    {
        return TimesliceStartupServletContextListener.getTimesliceApp(getServletContext());
    }
    

    @Override
    public String authenticate(String username, String password)
    {
        return sessionTracker.authenticate(getTimesliceApp(), username, password);
    }

    @Override
    public void logout(String authToken) throws NotAuthenticException
    {
        sessionTracker.logout(authToken);
    }

    @Override
    public List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant)
    {
        SessionData sd = sessionTracker.checkToken(authToken);

        return new ArrayList<StartTag>(Transform.tr(getTimesliceApp().queryForTags(
                sd.getUser(),
                sortDir == SortDir.desc, // check this
                startingInstant == null
                    ? new Instant(0)
                    : ISODateTimeFormat.dateTime().parseDateTime(startingInstant).toInstant(),
                endingInstant == null
                    ? new Instant(Long.MAX_VALUE)
                    : ISODateTimeFormat.dateTime().parseDateTime(endingInstant).toInstant(),
                maxSize,
                0),
            ServerToClient.createStartTagTx(getTimesliceApp().getTzOffset())));
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

    @Override
    public List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);

        List<com.enokinomi.timeslice.app.core.StartTag> tags = getTimesliceApp().queryForTags(
                sd.getUser(),
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

    @Override
    public String persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);

        List<com.enokinomi.timeslice.app.core.StartTag> tags = getTimesliceApp().queryForTags(
                sd.getUser(),
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

        String filename = getTimesliceApp().getReportPrefix();
        if (null != persistAsName && !persistAsName.isEmpty()) filename = filename + persistAsName;
        String ts = new Instant().toDateTime(DateTimeZone.forOffsetHours(getTimesliceApp().getTzOffset())).toString();
        filename = filename + "." + ts + ".ts-snapshot.csv";

        File safeDir = new File(getTimesliceApp().getSafeDir());
        filename = filename.replaceAll(":", "-"); // armor the filename for filesystems which don't support ':'
        File report = new File(safeDir, filename);
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

    @Override
    public void addItem(String authToken, String instantString, String taskDescription)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        throwIfNoAvailableStore();

        getTimesliceApp().getFrontStore().add(new com.enokinomi.timeslice.app.core.StartTag(sd.getUser(), instantString, taskDescription, null));
    }

    @Override
    public void addItems(String authToken, List<StartTag> items)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        throwIfNoAvailableStore();

        for (StartTag item: items)
        {
            getTimesliceApp().getFrontStore().add(new com.enokinomi.timeslice.app.core.StartTag(sd.getUser(), item.getInstantString(), item.getDescription(), null));
        }
    }

    private void throwIfNoAvailableStore()
    {
        if (null == getTimesliceApp().getFrontStore())
        {
            throw new RuntimeException("No store.");
        }
    }

    @Override
    public void update(String authToken, StartTag editedStartTag)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        throwIfNoAvailableStore();

        com.enokinomi.timeslice.app.core.StartTag edited = new com.enokinomi.timeslice.app.core.StartTag(sd.getUser(), editedStartTag.getInstantString(), editedStartTag.getDescription(), null);

        getTimesliceApp().getFrontStore().updateText(edited);
    }
}
