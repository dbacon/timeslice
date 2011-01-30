package com.enokinomi.timeslice.web.task.server.impl;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

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
    public List<StartTag> refreshItems(final String authToken, final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant)
    {
        return new Catcher().catchAndWrap("Refreshing items", new Callable<List<StartTag>>()
        {
            @Override
            public List<StartTag> call()
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                TsSettings settings = sd.getSettings();
                return timesliceSvc.refreshItems(sd.getUser(), maxSize, sortDir, startingInstant, endingInstant, settings.getTzOffsetMinutes());
            }
        });
    }

    @Override
    public List<TaskTotal> refreshTotals(final String authToken, final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        return new Catcher().catchAndWrap("Refreshing totals", new Callable<List<TaskTotal>>()
        {
            @Override
            public List<TaskTotal> call()
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                TsSettings settings = sd.getSettings();
                return timesliceSvc.refreshTotals(sd.getUser(), maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, settings.getTzOffset());
            }
        });
    }

    @Override
    public String persistTotals(final String authToken, final String persistAsName, final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        return new Catcher().catchAndWrap("Persisting totals", new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                TsSettings settings = sd.getSettings();
                return timesliceSvc.persistTotals(persistAsName, maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, settings.getTzOffset(), sd.getUser());
            }
        });
    }

    @Override
    public void addItem(final String authToken, final String instantString, final String taskDescription)
    {
        new Catcher().catchAndWrap("Adding item", new Callable<Void>()
        {
            @Override
            public Void call()
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                timesliceSvc.addItem(instantString, taskDescription, sd.getUser());
                return null; // Void
            }
        });
    }

    @Override
    public void addItems(final String authToken, final List<StartTag> items)
    {
        new Catcher().catchAndWrap("Adding items", new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                timesliceSvc.addItems(sd.getUser(), items);
                return null; // Void
            }
        });
    }

    @Override
    public void update(final String authToken, final StartTag editedStartTag)
    {
        new Catcher().catchAndWrap("Updating", new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                timesliceSvc.update(sd.getUser(), editedStartTag);
                return null; // Void
            }
        });
    }

    @Override
    public BrandInfo getBrandInfo()
    {
        return new BrandInfo(branding.projectHref(), branding.issueHref());
    }
}
