package com.enokinomi.timeslice.web.ordering.client.core;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IOrderingSvc extends RemoteService
{
    List<String> requestOrdering(String authToken, String setName, List<String> items);
    void setOrdering(String authToken, String setName, List<String> items);
}
