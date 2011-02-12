package com.enokinomi.timeslice.web.prorata.client.core;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IProrataSvcAsync
{
    void addGroupComponent(String authToken, String groupName, String componentName, Double weight, AsyncCallback<Void> callback);
    void removeGroupComponent(String authToken, String groupName, String componentName, AsyncCallback<Void> callback);
    void expandGroup(String authToken, String groupName, AsyncCallback<List<GroupComponent>> callback);
    void listGroups(String authToken, AsyncCallback<List<String>> callback);
    void listAllGroupInfo(String authToken, AsyncCallback<List<Group>> callback);
}
