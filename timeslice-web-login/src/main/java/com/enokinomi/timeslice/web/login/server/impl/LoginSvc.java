package com.enokinomi.timeslice.web.login.server.impl;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.impl.Sha1V1Scheme;
import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.login.client.core.ILoginSvc;
import com.enokinomi.timeslice.web.session.server.api.ISessionTracker;
import com.google.inject.Inject;

class LoginSvc implements ILoginSvc
{
    private final ISessionTracker sessionTracker;
    private final IUserInfoDao userInfoDao;

    @Inject
    LoginSvc(ISessionTracker sessionTracker, IUserInfoDao userInfoDao)
    {
        this.sessionTracker = sessionTracker;
        this.userInfoDao = userInfoDao;
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

    @Override
    public void createUserAccount(String authToken, String user, String password)
    {
        System.out.println("creating an account");
        if (userInfoDao.userCount() > 0)
        {
            System.out.println(" found some users, not allowing 1st-time setup.");
            sessionTracker.checkToken(authToken);
        }
        else
        {
            System.out.println(" found no users, allowing 1st-time setup");
            // allow it - 1st time setup.
        }

        userInfoDao.createUser(user, password, Sha1V1Scheme.class.getCanonicalName());
    }

}
