package com.enokinomi.timeslice.web.session.server.core;

import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;

public interface ISessionTracker
{
    void logout(String authenticationToken);
    String authenticate(String username, String password);
    /** throws an exception with a specific reason if the token is not valid, otherwise always returns a valid, non-null {@link SessionData} */
    SessionData checkToken(String authenticationToken) throws NotAuthenticException;
}
