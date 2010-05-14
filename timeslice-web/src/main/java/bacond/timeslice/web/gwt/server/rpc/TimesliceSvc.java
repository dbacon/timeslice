package bacond.timeslice.web.gwt.server.rpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import bacond.timeslice.web.gwt.client.beans.NotAuthenticException;
import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.beans.TaskTotal;
import bacond.timeslice.web.gwt.client.server.ITimesliceSvc;
import bacond.timeslice.web.gwt.client.server.ProcType;
import bacond.timeslice.web.gwt.client.server.SortDir;
import bacond.timeslice.web.gwt.server.beantx.ServerToClient;
import bacond.timeslicer.app.core.AclFile;
import bacond.timeslicer.app.core.Aggregate;
import bacond.timeslicer.timeslice.TimesliceApp;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TimesliceSvc extends RemoteServiceServlet implements ITimesliceSvc
{
	private static final long serialVersionUID = 1L;

    private Map<String, SessionData> validSessions = new LinkedHashMap<String, SessionData>();

	protected TimesliceApp getTimesliceApp()
	{
		return TimesliceStartupServletContextListener.getTimesliceApp(getServletContext());
	}

	@Override
    public void logout(String authenticationToken)
    {
	    validSessions.remove(authenticationToken);
    }

    @Override
    public String serverInfo()
    {
        return "1.0.8-snapshot"; // TODO: get from resources/build
    }

    @Override
    public String authenticate(String username, String password)
    {
        System.out.println("authenticate(" + username + ").");

	    // TODO: implement hashing the pw, lookup in db, applying authorization.
	    // for now, just make sure it matches what's in their acl.
        String aclFileName = getTimesliceApp().getAclFileName();
        if (null == aclFileName) return null;

        String realPw = new AclFile(aclFileName).lookupPassword(username);
        if (null == realPw) return null;

        if (!realPw.equals(password)) return null;

        SessionData sd = new SessionData(username);
        validSessions.put(sd.getUuid(), sd);

        return sd.getUuid();
    }

	protected SessionData checkToken(String authenticationToken)
	{
	    if (!validSessions.containsKey(authenticationToken))
	    {
	        throw new NotAuthenticException("Invalid token.");
	    }

        SessionData sessionData = validSessions.get(authenticationToken);
        if (sessionData.getExpiresAt().isBeforeNow())
        {
            SessionData expiredSession = validSessions.remove(authenticationToken);
            throw new NotAuthenticException("Expired token: " + expiredSession.expiresAt.toString());
        }

        // great, have a nice time.
        return sessionData;
	}

    @Override
	public List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant)
	{
        SessionData sd = checkToken(authToken);

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

	@Override
	public List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant)
	{
	    SessionData sd = checkToken(authToken);

		List<bacond.timeslicer.app.core.StartTag> tags = getTimesliceApp().queryForTags(
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

		return new ArrayList<TaskTotal>(Transform.tr(new Aggregate().sumThem(new Aggregate().aggregate(tags)).values(), ServerToClient.TaskTotal));
	}

	@Override
	public String persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant)
	{
        SessionData sd = checkToken(authToken);

        List<bacond.timeslicer.app.core.StartTag> tags = getTimesliceApp().queryForTags(
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

        Collection<bacond.timeslicer.app.core.TaskTotal> values = new Aggregate().sumThem(new Aggregate().aggregate(tags)).values();
        ArrayList<String> lines = new ArrayList<String>();
        for (bacond.timeslicer.app.core.TaskTotal total: values)
        {
            lines.add(String.format("%s,%s,%s", total.getWho(), total.getWhat(), total.getMillis()));
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
	    SessionData sd = checkToken(authToken);
	    throwIfNoAvailableStore();

	    getTimesliceApp().getFrontStore().add(new bacond.timeslicer.app.core.StartTag(sd.getUser(), instantString, taskDescription, null));
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
	    SessionData sd = checkToken(authToken);
	    throwIfNoAvailableStore();

	    bacond.timeslicer.app.core.StartTag edited = new bacond.timeslicer.app.core.StartTag(sd.getUser(), editedStartTag.getInstantString(), editedStartTag.getDescription(), null);

	    getTimesliceApp().getFrontStore().updateText(edited);
	}
}
