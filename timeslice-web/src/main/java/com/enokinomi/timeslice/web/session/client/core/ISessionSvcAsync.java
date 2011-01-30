package com.enokinomi.timeslice.web.session.client.core;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISessionSvcAsync
{
    void getSettings(String authToken, AsyncCallback<Map<String, List<String>>> callback);
}
