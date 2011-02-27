package com.enokinomi.timeslice.web.prorata.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IProrataSvc extends RemoteService
{
    void addGroupComponent(String authToken, String groupName, String componentName, Double weight) throws ServiceException;
    void removeGroupComponent(String authToken, String groupName, String componentName) throws ServiceException;
    List<GroupComponent> expandGroup(String authToken, String groupName) throws ServiceException;
    List<String> listGroups(String authToken) throws ServiceException;
    List<Group> listAllGroupInfo(String authToken) throws ServiceException;
}
