package com.enokinomi.timeslice.web.task.server.impl;

import java.util.List;
import java.util.concurrent.Callable;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.core.server.util.Catcher;
import com.enokinomi.timeslice.web.session.server.api.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.api.SessionData;
import com.enokinomi.timeslice.web.task.client.core.ITaskSvc;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.google.inject.Inject;

class TaskSvc implements ITaskSvc
{
    private final ISessionTracker sessionTracker;
    private final TimesliceSvcWebWrapper taskSvcWrapped;
    private final IUserInfoDao userInfoDao;

    @Inject
    TaskSvc(TimesliceSvcWebWrapper taskSvcWrapped, ISessionTracker sessionTracker, IUserInfoDao userInfoDao)
    {
        this.taskSvcWrapped = taskSvcWrapped;
        this.sessionTracker = sessionTracker;
        this.userInfoDao = userInfoDao;
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
                TsSettings settings = userInfoDao.loadUserSettings(sd.getUser(), "usersession.tzoffsetmin");
                return taskSvcWrapped.refreshItems(sd.getUser(), maxSize, sortDir, startingInstant, endingInstant, settings.getTzOffsetMinutes());
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
                taskSvcWrapped.addItem(instantString, taskDescription, sd.getUser());
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
                taskSvcWrapped.addItems(sd.getUser(), items);
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
                taskSvcWrapped.update(sd.getUser(), editedStartTag);
                return null; // Void
            }
        });
    }

    @Override
    public void mergeBack(final String authToken, final StartTag startTag, final boolean multi) throws ServiceException
    {
        new Catcher().catchAndWrap("Collapsing task", new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                taskSvcWrapped.mergeBack(sd.getUser(), startTag, multi);
                return null; // Void
            }
        });
    }

    @Override
    public void removeItem(final String authToken, final StartTag startTag) throws ServiceException
    {
        new Catcher().catchAndWrap("remove item", new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                SessionData sd = sessionTracker.checkToken(authToken);
                taskSvcWrapped.removeItem(sd.getUser(), startTag);
                return null; // Void
            }
        });
    }
}
