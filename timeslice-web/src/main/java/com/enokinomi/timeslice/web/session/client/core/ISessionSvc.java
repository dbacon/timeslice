package com.enokinomi.timeslice.web.session.client.core;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ISessionSvc extends RemoteService
{
    Map<String, List<String>> getSettings(String authToken);
}
