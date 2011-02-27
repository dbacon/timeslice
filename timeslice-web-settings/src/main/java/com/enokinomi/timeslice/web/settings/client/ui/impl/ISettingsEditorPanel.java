package com.enokinomi.timeslice.web.settings.client.ui.impl;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

public interface ISettingsEditorPanel extends IsWidget
{
    static interface Listener
    {
        void onRefreshButtonClicked();
        void onItemDeleted(String name, String value);
        void onItemEdited(String name, String oldValue, String newValue);
        void onItemAdded(String name, String value);
    }

    void addListener(Listener listener);

    void setSettings(Map<String, List<String>> settings);

    void clear();
}
