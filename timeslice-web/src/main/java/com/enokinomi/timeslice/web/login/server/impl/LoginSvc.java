package com.enokinomi.timeslice.web.login.server.impl;

import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.login.client.core.ILoginSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.google.inject.Inject;

class LoginSvc implements ILoginSvc
{
    private final ISessionTracker sessionTracker;

    @Inject
    LoginSvc(ISessionTracker sessionTracker)
    {
        this.sessionTracker = sessionTracker;
    }

    @Override
    public void logout(String authToken) throws NotAuthenticException
    {
        sessionTracker.logout(authToken);
    }

    @Override
    public String authenticate(String username, String password)
    {
        return sessionTracker.authenticate(username, password);
    }

}
