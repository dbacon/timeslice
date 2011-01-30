package com.enokinomi.timeslice.web.session.server.impl;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.lib.util.Check;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;

public class SessionDataProvider
{
    private static Logger log = Logger.getLogger(SessionDataProvider.class);

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

        // try/catch so database errors don't impact session creation
        TsSettings settings = null;

        try
        {
            settings = userInfoDao.loadUserSettings(username, "usersession.");
        }
        catch (Exception e)
        {
            // save message and attach to notifications sent to user
            // for now, make sure it's in the server log
            log.warn("Loading user-settings died (using blank settings instead): " + e.getMessage());
        }

        settings = Check.mapNullTo(settings, new TsSettings());

        return new SessionData(username, settings, loggedInAt, expiresAt, uuid);
    }
}
