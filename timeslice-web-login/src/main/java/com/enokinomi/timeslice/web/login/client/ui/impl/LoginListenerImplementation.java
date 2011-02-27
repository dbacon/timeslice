package com.enokinomi.timeslice.web.login.client.ui.impl;

import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;

public class LoginListenerImplementation implements LoginListener
{
    private final IClearable clearable;
    private final Initializable initializable;

    public LoginListenerImplementation(IClearable clearable, Initializable initializable)
    {
        this.clearable = clearable;
        this.initializable = initializable;
    }

    @Override
    public void sessionEnded(boolean retry)
    {
        clearable.clear();
    }

    @Override
    public void newSessionStarted()
    {
        initializable.initialize("login-listener.new-session");
    }
}
