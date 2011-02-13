package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;


public interface IOptionsPanel extends IsWidget
{
    public static final String CURRENTTASK = "@current.task@";

    int getMaxSize();

    long getMaxSeconds();

    boolean isControlSpaceSends();

    boolean isCurrentTaskInTitlebar();

    String getTitleBarTemplate();

    void setUserSettings(Map<String, List<String>> settings);
    void setSessionData(Map<String, String> sessionSettings);

    void bind(ISettingsPresenter settingsPresenter);

}
