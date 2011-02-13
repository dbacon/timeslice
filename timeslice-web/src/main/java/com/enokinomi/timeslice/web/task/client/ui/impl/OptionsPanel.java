package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.ISettingsPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class OptionsPanel extends Composite implements IOptionsPanel
{
    private static OptionsPanelUiBinder uiBinder = GWT.create(OptionsPanelUiBinder.class);
    interface OptionsPanelUiBinder extends UiBinder<Widget, OptionsPanel> { }

    private static final class Defaults
    {
        public static final int MaxResults = 10;
        public static final long MaxSeconds = 60 * 60 * 24;
    }

    private static final String DefaultTitlebarTemplate = "[TS] " + CURRENTTASK;

    @UiField protected ISettingsEditorPanel settingsEditor;
    @UiField protected FlexTable sessionDataTable;
    @UiField protected TextBox titleBarTemplate;
    @UiField protected CheckBox controlSpaceSends;
    @UiField protected CheckBox currentTaskInTitlebar;
    @UiField protected TextBox maxSize;
    @UiField protected TextBox maxSeconds;


    @Override
    public void bind(final ISettingsPresenter presenter)
    {
        presenter.addListener(new ISettingsPresenter.Listener()
        {
            @Override
            public void settingsChanged()
            {
                handleSettingsChanged();
            }

            @Override
            public void userSettingsDone(Map<String, List<String>> result)
            {
                handleUserSettingDone(result);
            }

            @Override
            public void userSessionDataDone(Map<String, String> result)
            {
                handleUserSessionDataDone(result);
            }
        });

        presenter.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
                handleSessionEnded();
            }

            @Override
            public void newSessionStarted()
            {
                handleSessionStarted();
            }
        });

        addListener(new Listener()
        {
            @Override
            public void userSettingAddRequested(String name, String value)
            {
                presenter.userSettingAddRequested(name, value);
            }

            @Override
            public void userSettingEditRequested(String name, String oldValue, String newValue)
            {
                presenter.userSettingEditRequested(name, oldValue, newValue);
            }

            @Override
            public void userSettingDeleteRequested(String name, String value)
            {
                presenter.userSettingDeleteRequested(name, value);
            }

            @Override
            public void refreshRequested()
            {
                presenter.refreshRequested();
            }
        });
    }

    public static interface Listener
    {
        void userSettingAddRequested(String name, String value);
        void userSettingEditRequested(String name, String oldValue, String newValue);
        void userSettingDeleteRequested(String name, String value);
        void refreshRequested();
    }

    private List<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener)
    {
        if (listener != null) listeners.add(listener);
    }


    private void handleSettingsChanged()
    {
        fireRefreshRequested();
    }

    private void handleUserSessionDataDone(Map<String, String> result)
    {
        setSessionData(result);
    }

    protected void handleUserSettingDone(Map<String, List<String>> result)
    {
        setUserSettings(result);
    }

    protected void handleSessionStarted()
    {
        fireRefreshRequested();
    }

    protected void handleSessionEnded()
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
    OptionsPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));

        localWidgetsInit();

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                fireRefreshRequested();
            }
        });
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
    }

    private void localWidgetsInit()
    {
        // TODO: raise only events that ui signaled.

//        maxSize.addChangeHandler(CommonChangeFireChanged);
//        maxSeconds.addChangeHandler(CommonChangeFireChanged);
//        controlSpaceSends.addClickHandler(CommonClickFireChanged);
//        titleBarTemplate.addChangeHandler(CommonChangeFireChanged);
//        currentTaskInTitlebar.addClickHandler(CommonClickFireChanged);

        currentTaskInTitlebar.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                consistentize();
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

}
