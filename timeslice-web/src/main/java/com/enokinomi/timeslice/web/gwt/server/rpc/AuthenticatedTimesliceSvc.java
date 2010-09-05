package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.List;

import com.enokinomi.timeslice.timeslice.TsSettings;
import com.enokinomi.timeslice.web.gwt.client.beans.NotAuthenticException;
import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.server.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;

public class AuthenticatedTimesliceSvc implements ITimesliceSvc
{
    private final SessionTracker sessionTracker;
    private final TimesliceSvc timesliceSvc;

    public AuthenticatedTimesliceSvc(TimesliceSvc timesliceSvc, SessionTracker sessionTracker)
    {
        this.timesliceSvc = timesliceSvc;
        this.sessionTracker = sessionTracker;
    }

    @Override
    public String serverInfo()
    {
        return "1.0.10-beta-20100905-2-SNAPSHOT"; // TODO: get from resources/build
    }

    @Override
    public String authenticate(String username, String password)
    {
        return sessionTracker.authenticate(username, password);
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
        TsSettings settings = sd.getSettings();
        return timesliceSvc.refreshItems(sd.getUser(), maxSize, sortDir, procType, startingInstant, endingInstant, settings.getTzOffset());
    }

    @Override
    public List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        return timesliceSvc.refreshTotals(sd.getUser(), maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
    }

    @Override
    public String persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        TsSettings settings = sd.getSettings();
        return timesliceSvc.persistTotals(persistAsName, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords, settings.getTzOffset(), sd.getUser());
    }

    @Override
    public void addItem(String authToken, String instantString, String taskDescription)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        timesliceSvc.addItem(instantString, taskDescription, sd.getUser());
    }

    @Override
    public void addItems(String authToken, List<StartTag> items)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        timesliceSvc.addItems(sd.getUser(), items);
    }

    @Override
    public void update(String authToken, StartTag editedStartTag)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        timesliceSvc.update(sd.getUser(), editedStartTag);
    }
}
