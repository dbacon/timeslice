package com.enokinomi.timeslice.web.gwt.client.prorata.core;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IProRataSvc extends RemoteService
{
    void addGroupComponent(String authToken, String groupName, String componentName, Double weight);
    void removeGroupComponent(String authToken, String groupName, String componentName);
    List<GroupComponent> expandGroup(String authToken, String groupName);
    List<String> listGroups(String authToken);
    List<Group> listAllGroupInfo(String authToken);
}
