package com.enokinomi.timeslice.web.gwt.server.task;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.timeslice.TsSettings;

public class SessionData
{
    private final String user;
    private final DateTime loggedInAt;
    private final DateTime expiresAt;
    private final String uuid;
    private final TsSettings settings;

    public SessionData(String user, TsSettings settings, DateTime loggedInAt, DateTime expiresAt, String uuid)
    {
        this.user = user;
        this.settings = settings;
        this.loggedInAt = loggedInAt;
        this.expiresAt = expiresAt;
        this.uuid = uuid;
    }

    public String getUser()
    {
        return user;
    }

    public DateTime getLoggedInAt()
    {
        return loggedInAt;
    }

    public DateTime getExpiresAt()
    {
        return expiresAt;
    }

    public String getUuid()
    {
        return uuid;
    }

    public TsSettings getSettings()
    {
        return settings;
    }
}
