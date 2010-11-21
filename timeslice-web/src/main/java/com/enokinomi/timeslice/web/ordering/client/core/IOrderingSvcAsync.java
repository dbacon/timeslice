package com.enokinomi.timeslice.web.ordering.client.core;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IOrderingSvcAsync
{

    void requestOrdering(String authToken, String setName, List<String> items,
            AsyncCallback<List<String>> callback);

    void setPartialOrdering(String authToken, String setName, String smaller,
            List<String> larger, AsyncCallback<Void> callback);

}
