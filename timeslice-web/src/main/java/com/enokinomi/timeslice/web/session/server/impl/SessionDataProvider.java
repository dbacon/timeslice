package com.enokinomi.timeslice.web.session.server.impl;

import java.util.UUID;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;

public class SessionDataProvider
{
    @Inject
    SessionDataProvider()
    {
    }

    public SessionData createSessionForAuthenticatedUser(String username)
    {
        return new SessionData(
                username,
                new DateTime(),
                new DateTime().plusDays(1),
                UUID.randomUUID().toString());
    }
}
