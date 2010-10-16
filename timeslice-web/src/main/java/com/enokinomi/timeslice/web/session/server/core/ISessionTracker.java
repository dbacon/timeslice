package com.enokinomi.timeslice.web.session.server.core;

import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;

public interface ISessionTracker
{
    void logout(String authenticationToken);
    String authenticate(String username, String password);
    SessionData checkToken(String authenticationToken) throws NotAuthenticException;
}
