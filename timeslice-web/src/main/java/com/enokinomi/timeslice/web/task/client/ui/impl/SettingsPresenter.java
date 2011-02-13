package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.session.client.core.ISessionSvcAsync;
import com.enokinomi.timeslice.web.settings.client.core.ISettingsSvcAsync;
import com.enokinomi.timeslice.web.task.client.ui.api.ISettingsPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class SettingsPresenter implements ISettingsPresenter
{
    private final ISettingsSvcAsync settingsSvc;
    private final ISessionSvcAsync sessionSvc;
    private final ILoginSupport loginSupport;

    @Inject
    SettingsPresenter(final ILoginSupport loginSupport, final ISettingsSvcAsync settingsSvc, final ISessionSvcAsync sessionSvc)
    {
        this.loginSupport = loginSupport;
        this.settingsSvc = settingsSvc;
        this.sessionSvc = sessionSvc;
    }

    private List<ISettingsPresenter.Listener> listeners = new ArrayList<ISettingsPresenter.Listener>();

    @Override
    public void addLoginListener(ILoginSupport.LoginListener listener)
    {
        loginSupport.addLoginListener(listener);
    }

    @Override
    public void addListener(ISettingsPresenter.Listener l)
    {
        if (l != null) listeners.add(l);
    }

    private void fireUserSettingsChanged()
    {
        for (ISettingsPresenter.Listener l: listeners) l.settingsChanged();
    }

    private void fireUserSettingsDone(Map<String, List<String>> result)
    {
        for (ISettingsPresenter.Listener l: listeners) l.userSettingsDone(result);
    }

    private void fireUserSessionDataDone(Map<String, String> result)
    {
        for (ISettingsPresenter.Listener l: listeners) l.userSessionDataDone(result);
    }

    @Override
    public void userSettingDeleteRequested(String name, String value)
    {
        settingsSvc.deleteSetting(loginSupport.getAuthToken(), name, value, new AsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void result)
                    {
                        fireUserSettingsChanged();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Deleting item failed: " + caught.getMessage());
                    }
                });
    }

    @Override
    public void userSettingEditRequested(String name, String oldValue, String newValue)
    {
        settingsSvc.editSetting(loginSupport.getAuthToken(), name, oldValue, newValue, new AsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void result)
                    {
                        fireUserSettingsChanged();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Editing item failed: " + caught.getMessage());
                    }
                });
    }

    @Override
    public void userSettingAddRequested(String name, String value)
    {
        settingsSvc.addSetting(loginSupport.getAuthToken(), name, value, new AsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void result)
                    {
                        fireUserSettingsChanged();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Adding item failed: " + caught.getMessage());
                    }
                });
    }

    private void startRefreshUserSettings()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                settingsSvc.getSettings(
                        loginSupport.getAuthToken(),
                        "",
                        loginSupport.withRetry(this, new AsyncCallback<Map<String,List<String>>>()
                        {
                            @Override
                            public void onSuccess(Map<String, List<String>> result)
                            {
                                fireUserSettingsDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Getting session settings failed: " + caught.getMessage(), caught);
                            }
                        }));
            }
        }.runAsync();
    }

    private void startRefreshSessionData()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                sessionSvc.getSessionData(loginSupport.getAuthToken(),
                        loginSupport.withRetry(this, new AsyncCallback<Map<String,String>>()
                        {
                            @Override
                            public void onSuccess(Map<String, String> result)
                            {
                                fireUserSessionDataDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: log error
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void refreshRequested()
    {
        startRefreshUserSettings();
        startRefreshSessionData();
    }
}
