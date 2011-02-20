package com.enokinomi.timeslice.web.login.client.ui.api;

import com.enokinomi.timeslice.web.core.client.ui.Registration;
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

    /** Signifies that Authentication errors will be handled and retried */
    public static interface RetryingAsyncCallback<R> extends AsyncCallback<R>
    {
    }

    <R1> RetryingAsyncCallback<R1> withRetry(IOnAuthenticated retryAction, AsyncCallback<R1> wrapped);

    String getAuthToken();
    void logout();
    Registration addLoginListener(LoginListener listener);
}
