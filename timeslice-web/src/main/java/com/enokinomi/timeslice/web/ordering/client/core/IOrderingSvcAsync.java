package com.enokinomi.timeslice.web.ordering.client.core;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IOrderingSvcAsync
{
    void setPartialOrdering(String authToken, String setName, String smaller, List<String> larger, AsyncCallback<Void> callback);
    void requestOrdering(String authToken, String setName, AsyncCallback<List<String>> callback);
}
