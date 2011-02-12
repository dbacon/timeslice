package com.enokinomi.timeslice.web.ordering.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IOrderingSvc extends RemoteService
{
    void setPartialOrdering(String authToken, String setName, String smaller, List<String> larger) throws ServiceException;
    List<String> requestOrdering(String authToken, String setName) throws ServiceException;
}
