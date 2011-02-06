package com.enokinomi.timeslice.web.login.client.core;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ILoginSvcAsync
{
    void logout(String authToken, AsyncCallback<Void> callback);
    void authenticate(String user, String password, AsyncCallback<String> callback);
}
