package com.enokinomi.timeslice.web.session.server.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTimeZone;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.web.session.client.core.ISessionSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;

public class SessionSvc implements ISessionSvc
{
    private final ISessionTracker tracker;
    private final IUserInfoDao userInfoDao;

    @Inject
    SessionSvc(ISessionTracker tracker, IUserInfoDao userInfoDao)
    {
        this.tracker = tracker;
        this.userInfoDao = userInfoDao;
    }

    @Override
    public Map<String, String> getSessionData(String authToken)
    {
        SessionData sd = tracker.checkToken(authToken);

        DateTimeZone zone = DateTimeZone.forOffsetMillis(userInfoDao.loadUserSettings(sd.getUser(), "").getTzOffsetMinutes()*60*1000);

        String sessionExpiresAt = sd.getExpiresAt().toDateTime(zone).toString();
        String loggedInAt = sd.getLoggedInAt().toDateTime(zone).toString();
        String user = sd.getUser();
        String sessionKey = sd.getUuid();

        Map<String, String> sessionData = new LinkedHashMap<String, String>();
        sessionData.put("sessionExpiresAt", sessionExpiresAt);
        sessionData.put("loggedInAt", loggedInAt);
        sessionData.put("user", user);
        sessionData.put("sessionKey", sessionKey);

        return sessionData;
    }
}
