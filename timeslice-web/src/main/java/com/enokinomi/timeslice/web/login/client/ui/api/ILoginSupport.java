package com.enokinomi.timeslice.web.login.client.ui.api;

import com.enokinomi.timeslice.web.login.client.ui.impl.LoginSupport.NoAuthProblemAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ILoginSupport
{
    public static interface LoginListener
    {

        void newSessionStarted();

        void sessionEnded(boolean retry);

    }

    public static interface IOnAuthenticated
    {
        void runAsync();
    }

    <R1> NoAuthProblemAsyncCallback<R1> withRetry(IOnAuthenticated retryAction, AsyncCallback<R1> wrapped);
    String getAuthToken();
    void logout();
    void addLoginListener(LoginListener listener);
}
