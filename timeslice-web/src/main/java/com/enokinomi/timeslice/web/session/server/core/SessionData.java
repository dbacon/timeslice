package com.enokinomi.timeslice.web.session.server.core;

import org.joda.time.DateTime;

public class SessionData
{
    private final String user;
    private final DateTime loggedInAt;
    private final DateTime expiresAt;
    private final String uuid;

    public SessionData(String user, DateTime loggedInAt, DateTime expiresAt, String uuid)
    {
        this.user = user;
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
}
