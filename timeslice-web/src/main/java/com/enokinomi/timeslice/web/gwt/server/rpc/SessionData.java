package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.UUID;

import org.joda.time.DateTime;

public class SessionData
{
    final String user;
    final DateTime loggedInAt;
    final DateTime expiresAt;
    final String uuid;

    public SessionData(String user)
    {
        this.user = user;
        this.uuid = UUID.randomUUID().toString();

        loggedInAt = new DateTime();
        expiresAt = loggedInAt.plusHours(12);
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
