package com.enokinomi.timeslice.web.session.client.core;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISessionSvcAsync
{
    void getSessionData(String authToken, AsyncCallback<Map<String, String>> callback);
}
