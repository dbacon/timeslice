package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;


public interface IOptionsPanel extends IsWidget
{
    public static final String CURRENTTASK = "@current.task@";

    public static interface Listener
    {
        void userSettingAddRequested(String name, String value);
        void userSettingEditRequested(String name, String oldValue, String newValue);
        void userSettingCreateOrUpdate(String name, String value);
        void userSettingDeleteRequested(String name, String value);
        void refreshRequested();
    }


    int getMaxSize();

    long getMaxSeconds();

    boolean isControlSpaceSends();

    boolean isCurrentTaskInTitlebar();

    String getTitleBarTemplate();

    void setUserSettings(Map<String, List<String>> settings);
    void setSessionData(Map<String, String> sessionSettings);

    void handleUserSettingDone(Map<String, List<String>> result);
    void handleSettingsChanged();
    void handleUserSessionDataDone(Map<String, String> result);
    void handleSessionEnded();
    void handleSessionStarted();

    void addListener(Listener listener);

}
