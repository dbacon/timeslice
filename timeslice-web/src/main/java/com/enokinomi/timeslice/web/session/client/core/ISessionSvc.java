package com.enokinomi.timeslice.web.session.client.core;

import java.util.Map;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ISessionSvc extends RemoteService
{
    Map<String, String> getSessionData(String authToken) throws ServiceException;
}
