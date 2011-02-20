package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.ui.FooterPanel;
import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.Registration;
import com.google.gwt.user.client.ui.IsWidget;


public interface IOptionsPanel extends IsWidget, IClearable, Initializable
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

    FooterPanel getFooterPanel();

    int getMaxSize();

    long getMaxSeconds();

    boolean isControlSpaceSends();

    boolean isCurrentTaskInTitlebar();

    String getTitleBarTemplate();

    void setUserSettings(Map<String, List<String>> settings);
    void setSessionData(Map<String, String> sessionSettings);

    Registration addListener(Listener listener);

    void update();

}
