package com.enokinomi.timeslice.web.login.client.core;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ILoginSvc extends RemoteService
{
    void logout(String authToken);
    String authenticate(String user, String password);
}
