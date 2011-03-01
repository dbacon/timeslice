package com.enokinomi.timeslice.web.report.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.task.api.ITimesliceSvc;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.lib.util.Transforms;
import com.enokinomi.timeslice.web.assign.client.core.TaskTotal;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.report.client.core.IReportsSvc;
import com.enokinomi.timeslice.web.session.server.api.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.api.SessionData;
import com.google.inject.Inject;

public class ReportsSvcSession implements IReportsSvc
{
    private final ISessionTracker sessionTracker;
    private final ITimesliceSvc svc;
    private final IUserInfoDao userInfoDao;

    @Inject
    public ReportsSvcSession(ISessionTracker sessionTracker, ITimesliceSvc svc, IUserInfoDao userInfoDao)
    {
        this.sessionTracker = sessionTracker;
        this.svc = svc;
        this.userInfoDao = userInfoDao;
    }

    public List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        SessionData sd = sessionTracker.checkToken(authToken);
        TsSettings settings = userInfoDao.loadUserSettings(sd.getUser(), "");
        return Transforms.tr(
                svc.refreshTotals(
                        sd.getUser(),
                        maxSize,
                        com.enokinomi.timeslice.lib.task.api.SortDir.valueOf(sortDir.name()),
                        startingInstant,
                        endingInstant,
                        allowWords,
                        ignoreWords),
                        new ArrayList<TaskTotal>(),
                        ServerToClient.createTaskTotal(settings.getTzOffsetMinutes()));
    }

}
