package com.enokinomi.timeslice.web.session.server.impl;

import java.util.UUID;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;

public class SessionDataProvider
{
    private final IUserInfoDao userInfoDao;

    @Inject
    SessionDataProvider(IUserInfoDao userInfoDao)
    {
        this.userInfoDao = userInfoDao;
    }

    public SessionData createSessionForAuthenticatedUser(String username)
    {
        DateTime loggedInAt = new DateTime();
        DateTime expiresAt = loggedInAt.plusDays(1);
        String uuid = UUID.randomUUID().toString();

        TsSettings settings = userInfoDao.loadUserSettings(username, "usersession.");

        return new SessionData(username, settings, loggedInAt, expiresAt, uuid);
    }
}
