package com.enokinomi.timeslice.web.settings.client.core;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISettingsSvcAsync
{
    void getSettings(String authToken, String prefix, AsyncCallback<Map<String, List<String>>> callback);
    void addSetting(String authToken, String name, String value, AsyncCallback<Void> callback);
    void editSetting(String authToken, String name, String oldValue, String newValue, AsyncCallback<Void> callback);
    void deleteSetting(String authToken, String name, String value, AsyncCallback<Void> callback);
}
