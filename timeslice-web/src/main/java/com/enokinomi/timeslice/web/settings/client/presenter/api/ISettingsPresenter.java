package com.enokinomi.timeslice.web.settings.client.presenter.api;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;

public interface ISettingsPresenter
{
    public static interface Listener
    {
        void settingsChanged();
        void userSettingsDone(Map<String, List<String>> result);
        void userSessionDataDone(Map<String, String> result);
    }

    void addListener(Listener l);
    void addLoginListener(LoginListener loginListener);

    void userSettingAddRequested(String name, String value);
    void userSettingEditRequested(String name, String oldValue, String newValue);
    void userSettingCreateOrUpdateRequested(String name, String value);
    void userSettingDeleteRequested(String name, String value);
    void refreshRequested();
}
