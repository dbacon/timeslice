package com.enokinomi.timeslice.web.task.server.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.web.branding.api.IBranding;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.enokinomi.timeslice.web.task.client.core.ITimesliceSvc;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.google.inject.Inject;

public class TimesliceSvcSession implements ITimesliceSvc
{
    private final ISessionTracker sessionTracker;
    private final TimesliceSvcWebWrapper timesliceSvc;
    private final IBranding branding;

    @Inject
    TimesliceSvcSession(TimesliceSvcWebWrapper timesliceSvc, ISessionTracker sessionTracker, IBranding branding)
    {
        this.timesliceSvc = timesliceSvc;
        this.sessionTracker = sessionTracker;
        this.branding = branding;
    }

    @Override
    public String serverInfo()
    {
        String version = "version-unknown";

        InputStream versionIs = ClassLoader.getSystemResourceAsStream("timeslice-version");

        if (null != versionIs)
        {
            try
            {
                version = IOUtils.toString(versionIs, "UTF-8");
            }
            catch (Exception e)
            {
            }
        }

        return version;
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
    public List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        TsSettings settings = sd.getSettings();
        return timesliceSvc.refreshItems(sd.getUser(), maxSize, sortDir, startingInstant, endingInstant, settings.getTzOffsetMinutes());
    }

    @Override
    public List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        TsSettings settings = sd.getSettings();
        return timesliceSvc.refreshTotals(sd.getUser(), maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, settings.getTzOffset());
    }

    @Override
    public String persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        TsSettings settings = sd.getSettings();
        return timesliceSvc.persistTotals(persistAsName, maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, settings.getTzOffset(), sd.getUser());
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

    @Override
    public BrandInfo getBrandInfo()
    {
        return new BrandInfo(branding.projectHref(), branding.issueHref());
    }
}
