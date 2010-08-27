package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.UUID;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.timeslice.IUserInfoDao;
import com.enokinomi.timeslice.timeslice.TsSettings;

public class SessionDataProvider
{
    private final IUserInfoDao userInfoDao;

    public SessionDataProvider(IUserInfoDao userInfoDao)
    {
        this.userInfoDao = userInfoDao;
    }

    public SessionData createSessionForAuthenticatedUser(String username)
    {
        DateTime loggedInAt = new DateTime();
        DateTime expiresAt = loggedInAt.plusDays(1);
        String uuid = UUID.randomUUID().toString();

        TsSettings settings = userInfoDao.loadUserSettings(username);

        return new SessionData(username, settings, loggedInAt, expiresAt, uuid);
    }
}
