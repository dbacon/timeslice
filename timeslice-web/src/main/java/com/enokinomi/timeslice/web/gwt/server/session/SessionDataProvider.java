package com.enokinomi.timeslice.web.gwt.server.session;

import java.util.UUID;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.userinfo.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.TsSettings;
import com.google.inject.Inject;

public class SessionDataProvider
{
    private final IUserInfoDao userInfoDao;

    @Inject
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
