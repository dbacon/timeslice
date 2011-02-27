package com.enokinomi.timeslice.web.login.client.ui.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.ErrorBox;
import com.enokinomi.timeslice.web.core.client.util.NeedsSetupException;
import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.core.client.util.PrefHelper;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.login.client.core.ILoginSvcAsync;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.impl.YesNoDialog.YesNoListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class LoginSupport implements ILoginSupport
{
    private final LoginConstants constants;
    private final ILoginSvcAsync svc;
    private String authToken = Cookies.getCookie("timeslice.authtoken");
    private LoginDialog loginDialog = null;

    @Inject
    LoginSupport(LoginConstants constants, ILoginSvcAsync svc)
    {
        this.constants = constants;
        this.svc = svc;
    }

    private final List<LoginListener> listeners = new ArrayList<LoginSupport.LoginListener>();

    @Override
    public Registration addLoginListener(LoginListener listener)
    {
        if (listener != null)
        {
            listeners.add(listener);
            return Registration.wrap(listeners, listener);
        }
        return Registration.Null;
    }

    protected void fireNewSessionStarted()
    {
        for (LoginListener listener: listeners)
        {
            listener.newSessionStarted();
        }
    }

    protected void fireSessionEnded(boolean retry)
    {
        for (LoginListener listener: listeners)
        {
            listener.sessionEnded(retry);
        }
    }

    public <R1> RetryingAsyncCallback<R1> withRetry(LoginSupport.IOnAuthenticated retryAction, AsyncCallback<R1> wrapped)
    {
        return new RetryingAsyncCallback1<R1>(retryAction, wrapped);
    }

    class RetryingAsyncCallback1<R> implements RetryingAsyncCallback<R>
    {
        private final AsyncCallback<R> wrapped;
        private final LoginSupport.IOnAuthenticated retryAction;

        public RetryingAsyncCallback1(LoginSupport.IOnAuthenticated retryAction, AsyncCallback<R> wrapped)
        {
            this.retryAction = retryAction;
            this.wrapped = wrapped;
        }

        @Override
        public void onSuccess(R result)
        {
            wrapped.onSuccess(result);
        }

        @Override
        public void onFailure(Throwable caught)
        {
            if (caught instanceof NotAuthenticException)
            {
                authenticate(retryAction);
            }
            else
            {
                wrapped.onFailure(caught);
            }
        }
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void logout()
    {
        svc.logout(authToken, new AsyncCallback<Void>()
        {
            @Override
            public void onSuccess(Void result)
            {
                GWT.log("forgetting auth token");
                authToken = null;
                fireSessionEnded(false);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("logging out failed ??");
                // eh ? leave it I guess.
            }
        });
    }

    void authenticate(LoginSupport.IOnAuthenticated retryAction)
    {
        authenticate(constants.pleaseLogin(), null, retryAction);
    }

    void authenticate(String subtext, LoginSupport.IOnAuthenticated retryAction)
    {
        authenticate(constants.pleaseLogin(), subtext, retryAction);
    }

    void authenticate(String title, String subText, final LoginSupport.IOnAuthenticated action)
    {
        if (null == loginDialog)
        {
            loginDialog = new LoginDialog(title, subText, new LoginDialog.IListener()
            {
                @Override
                public void submitted(String user, String password)
                {
                    requestAuthentication(user, password, action);
                    loginDialog = null;
                }

                @Override
                public void canceled()
                {
                    // nothing. let them have at it.
                    loginDialog = null;
                }
            });

            loginDialog.center();
        }
        else
        {
        }
    }

    private <R> void requestAuthentication(final String user, final String password, final LoginSupport.IOnAuthenticated action)
    {
        GWT.log("Requesting authentication token for '" + user + "'.");
        svc.authenticate(user, password, new AsyncCallback<String>()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        authToken = result;
                        Cookies.setCookie("timeslice.authtoken", result, PrefHelper.createDateSufficientlyInTheFuture());
                        fireNewSessionStarted();
                        if (null != action) action.runAsync();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        if (caught instanceof NeedsSetupException)
                        {
                            NeedsSetupException nse = (NeedsSetupException) caught;

                            new YesNoDialog(
                                    "Setup required - " + nse.getMessage(),
                                    "Need to create this as the 1st account, fine?",
                                    new YesNoListener()
                                    {
                                        @Override
                                        public void yes()
                                        {
                                            svc.createUserAccount(null, user, password,
                                                    new AsyncCallback<Void>()
                                                    {
                                                        @Override
                                                        public void onSuccess(Void result)
                                                        {
                                                            requestAuthentication(user, password, action);
                                                        }

                                                        @Override
                                                        public void onFailure(Throwable caught)
                                                        {
                                                            new ErrorBox("Creating account failed", caught.getMessage());
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void no()
                                        {
                                            authToken = null;
                                            fireSessionEnded(false);
                                        }
                                    }).center();

                        }
                        else
                        {
                            StringBuilder sb = new StringBuilder();
                            for (StackTraceElement f: caught.getStackTrace())
                            {
                                sb.append(f.toString()).append("\n");
                            }
                            GWT.log(sb.toString());

                            new ErrorBox("authentication", caught.getMessage()).show();

                            authToken = null;
                            fireSessionEnded(false);
                        }
                    }
                });
    }
}
