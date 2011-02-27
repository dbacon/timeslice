package com.enokinomi.timeslice.web.settings.client.core;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ISettingsSvc extends RemoteService
{
    Map<String, List<String>> getSettings(String authToken, String prefix) throws ServiceException;
    void addSetting(String authToken, String name, String value) throws ServiceException;
    void editSetting(String authToken, String name, String oldValue, String newValue) throws ServiceException;
    void addOrEditSetting(String authToken, String name, String value);
    void deleteSetting(String authToken, String name, String value) throws ServiceException;
}
