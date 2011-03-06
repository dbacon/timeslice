package com.enokinomi.timeslice.web.settings.client.ui.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.settings.client.ui.api.IOptionsPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OptionsPanel extends Composite implements IOptionsPanel
{
    public static final class UiOptionKey
    {
        public static final String MaxSeconds = "ui.options.maxseconds";
        public static final String MaxSize = "ui.options.maxsize";
        public static final String ControlSpaceSendsEnabled = "ui.options.controlspacesends.enabled";
        public static final String TaskInTitleBarTemplate = "ui.options.taskintitlebar.template";
        public static final String TaskInTitleBarEnabled = "ui.options.taskintitlebar.enabled";
    }

    private static OptionsPanelUiBinder uiBinder = GWT.create(OptionsPanelUiBinder.class);
    interface OptionsPanelUiBinder extends UiBinder<Widget, OptionsPanel> { }

    private static final class Defaults
    {
        public static final int MaxResults = 10;
        public static final long MaxSeconds = 60 * 60 * 24;
    }

    private static final String DefaultTitlebarTemplate = "[TS] " + CURRENTTASK;

    @UiField(provided=true) protected NavPanel navPanel;
    @UiField protected ISettingsEditorPanel settingsEditor;
    @UiField protected FlexTable sessionDataTable;
    @UiField protected TextBox titleBarTemplate;
    @UiField protected CheckBox controlSpaceSends;
    @UiField protected CheckBox currentTaskInTitlebar;
    @UiField protected TextBox maxSize;
    @UiField protected TextBox maxSeconds;


    @Override
    public NavPanel getNavPanel()
    {
        return navPanel;
    };

//    public static void bind(final IOptionsPanel ui, final ISettingsPresenter presenter)
//    {
//        presenter.addListener(new ISettingsPresenter.Listener()
//        {
//            @Override
//            public void settingsChanged()
//            {
//                ui.handleSettingsChanged();
//            }
//
//            @Override
//            public void userSettingsDone(Map<String, List<String>> result)
//            {
//                ui.handleUserSettingDone(result);
//            }
//
//            @Override
//            public void userSessionDataDone(Map<String, String> result)
//            {
//                ui.handleUserSessionDataDone(result);
//            }
//        });
//
//        presenter.addLoginListener(new LoginListener()
//        {
//            @Override
//            public void sessionEnded(boolean retry)
//            {
//                ui.handleSessionEnded();
//            }
//
//            @Override
//            public void newSessionStarted()
//            {
//                ui.handleSessionStarted();
//            }
//        });
//
//        ui.addListener(new Listener()
//        {
//            @Override
//            public void userSettingAddRequested(String name, String value)
//            {
//                presenter.userSettingAddRequested(name, value);
//            }
//
//            @Override
//            public void userSettingEditRequested(String name, String oldValue, String newValue)
//            {
//                presenter.userSettingEditRequested(name, oldValue, newValue);
//            }
//
//            @Override
//            public void userSettingCreateOrUpdate(String name, String value)
//            {
//                presenter.userSettingCreateOrUpdateRequested(name, value);
//            }
//
//            @Override
//            public void userSettingDeleteRequested(String name, String value)
//            {
//                presenter.userSettingDeleteRequested(name, value);
//            }
//
//            @Override
//            public void refreshRequested()
//            {
//                presenter.refreshRequested();
//            }
//        });
//    }

    private List<Listener> listeners = new ArrayList<Listener>();

    @Override
    public Registration addListener(Listener listener)
    {
        if (listener != null)
        {
            listeners.add(listener);
            return Registration.wrap(listeners, listener);
        }
        return Registration.Null;
    }

    private void copyToForm(Map<String, List<String>> result)
    {
        if (result.containsKey(UiOptionKey.MaxSeconds))
        {
            maxSeconds.setValue(Double.toString(Double.parseDouble(result.get(UiOptionKey.MaxSeconds).get(0)) / 60. / 60.), false);
        }
        if (result.containsKey(UiOptionKey.MaxSize))
        {
            maxSize.setValue(result.get(UiOptionKey.MaxSize).get(0), false);
        }
        if (result.containsKey(UiOptionKey.ControlSpaceSendsEnabled))
        {
            controlSpaceSends.setValue(Boolean.valueOf(result.get(UiOptionKey.ControlSpaceSendsEnabled).get(0)), false);
        }
        if (result.containsKey(UiOptionKey.TaskInTitleBarEnabled))
        {
            currentTaskInTitlebar.setValue(Boolean.valueOf(result.get(UiOptionKey.TaskInTitleBarEnabled).get(0)), false);
        }
        if (result.containsKey(UiOptionKey.TaskInTitleBarTemplate))
        {
            titleBarTemplate.setValue(result.get(UiOptionKey.TaskInTitleBarTemplate).get(0), false);
        }

        consistentize(); // TODO: should publish event to everybody
    }

    @Override
    public void clear()
    {
        sessionDataTable.removeAllRows();
        sessionDataTable.setText(0, 0, "Not logged in.");

        settingsEditor.setSettings(new LinkedHashMap<String, List<String>>());
    }

    protected void onUserSettingsRefreshRequested()
    {
        fireRefreshRequested();
    }

    protected void fireUserSettingDeleteRequested(String name, String value)
    {
        for (Listener listener: listeners) listener.userSettingDeleteRequested(name, value);
    }

    protected void fireUserSettingEditRequested(String name, String oldValue, String newValue)
    {
        for (Listener listener: listeners) listener.userSettingEditRequested(name, oldValue, newValue);
    }

    protected void fireUserSettingEditRequested(String name, String value)
    {
        for (Listener listener: listeners) listener.userSettingAddRequested(name, value);
    }

    protected void fireRefreshRequested()
    {
        for (Listener listener: listeners) listener.refreshRequested();
    }

    protected void fireUserSettingCreateOrUpdate(String name, String value)
    {
        for (Listener listener: listeners) listener.userSettingCreateOrUpdate(name, value);
    }


    protected void onUserSettingDeleteRequested(String name, String value)
    {
        fireUserSettingDeleteRequested(name, value);
    }

    private void onUserSettingEdited(String name, String oldValue, String newValue)
    {
        fireUserSettingEditRequested(name, oldValue, newValue);
    }

    private void onUserSettingAdded(String name, String value)
    {
        fireUserSettingEditRequested(name, value);
    }

    @Inject
    OptionsPanel(@Named("populated") NavPanel navPanel)
    {
        this.navPanel = navPanel;
        initWidget(uiBinder.createAndBindUi(this));

        localWidgetsInit();
    }

    @Override
    public void setSessionData(Map<String, String> sessionSettings)
    {
        sessionDataTable.clear();

        int row = 0;
        for (Entry<String, String> entry: sessionSettings.entrySet())
        {
            sessionDataTable.setText(row, 0, entry.getKey());
            sessionDataTable.setText(row, 1, entry.getValue());

            ++row;
        }
    }

    @Override
    public void setUserSettings(Map<String, List<String>> settings)
    {
        settingsEditor.setSettings(settings);
        copyToForm(settings);
    }

    private void localWidgetsInit()
    {
        // TODO: raise only events that ui signaled.

        currentTaskInTitlebar.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                fireUserSettingCreateOrUpdate(UiOptionKey.TaskInTitleBarEnabled, currentTaskInTitlebar.getValue() ? "true" : "false");
                consistentize();
            }
        });

        titleBarTemplate.addValueChangeHandler(new ValueChangeHandler<String>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<String> event)
            {
                fireUserSettingCreateOrUpdate(UiOptionKey.TaskInTitleBarTemplate, titleBarTemplate.getText());
            }
        });

        controlSpaceSends.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                fireUserSettingCreateOrUpdate(UiOptionKey.ControlSpaceSendsEnabled, controlSpaceSends.getValue() ? "true" : "false");
            }
        });

        maxSize.addValueChangeHandler(new ValueChangeHandler<String>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<String> event)
            {
                fireUserSettingCreateOrUpdate(UiOptionKey.MaxSize, maxSize.getText());
            }
        });

        maxSeconds.addValueChangeHandler(new ValueChangeHandler<String>()
        {

            @Override
            public void onValueChange(ValueChangeEvent<String> event)
            {
                fireUserSettingCreateOrUpdate(UiOptionKey.MaxSeconds, Double.toString(Double.parseDouble(maxSeconds.getText()) * 60. * 60.));
            }
        });

        settingsEditor.addListener(new ISettingsEditorPanel.Listener()
        {
            @Override
            public void onRefreshButtonClicked()
            {
                onUserSettingsRefreshRequested();
            }

            @Override
            public void onItemDeleted(String name, String value)
            {
                onUserSettingDeleteRequested(name, value);
            }

            @Override
            public void onItemEdited(String name, String oldValue, String newValue)
            {
                onUserSettingEdited(name, oldValue, newValue);
            }

            @Override
            public void onItemAdded(String name, String value)
            {
                onUserSettingAdded(name, value);
            }
        });

        initValues();

        consistentize();
    }

    private void consistentize()
    {
        titleBarTemplate.setEnabled(currentTaskInTitlebar.getValue());
    }

    @Override
    public int getMaxSize()
    {
        try
        {
            return Integer.valueOf(maxSize.getText());
        }
        catch (Exception e)
        {
            return Defaults.MaxResults;
        }
    }

    @Override
    public long getMaxSeconds()
    {
        try
        {
            return Math.round(Double.parseDouble(maxSeconds.getText()) * 60 * 60);
        }
        catch (NumberFormatException e)
        {
            return Defaults.MaxSeconds;
        }
    }

    @Override
    public boolean isControlSpaceSends()
    {
        return controlSpaceSends.getValue();
    }

    @Override
    public boolean isCurrentTaskInTitlebar()
    {
        return currentTaskInTitlebar.getValue();
    }

    @Override
    public String getTitleBarTemplate()
    {
        return titleBarTemplate.getText();
    }

    private void initValues()
    {
        if (maxSize.getText().trim().isEmpty())
        {
            maxSize.setText("" + Defaults.MaxResults);
        }

        if (maxSeconds.getText().trim().isEmpty())
        {
            maxSeconds.setText("" + Defaults.MaxSeconds / 60. / 60.);
        }

        if (titleBarTemplate.getText().trim().isEmpty())
        {
            titleBarTemplate.setText(DefaultTitlebarTemplate);
        }
    }

    @Override
    public void update()
    {
        fireRefreshRequested();
    }

    @Override
    public void initialize(String callerPurpose)
    {
        update();
        getNavPanel().initialize(callerPurpose);
    }

}
