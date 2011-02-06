package com.enokinomi.timeslice.web.settings.server.impl;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.enokinomi.timeslice.web.settings.client.core.ISettingsSvc;
import com.google.inject.Inject;

public class SettingsSvc implements ISettingsSvc
{
    private final ISessionTracker tracker;
    private final IUserInfoDao userInfoDao;

    @Inject
    SettingsSvc(ISessionTracker tracker, IUserInfoDao userInfoDao)
    {
        this.tracker = tracker;
        this.userInfoDao = userInfoDao;
    }

    @Override
    public void addSetting(String authToken, String name, String value)
    {
        SessionData sd = tracker.checkToken(authToken);
        userInfoDao.addSetting(sd.getUser(), name, value);
    }

    @Override
    public void editSetting(String authToken, String name, String oldValue, String newValue)
    {
        SessionData sd = tracker.checkToken(authToken);
        userInfoDao.editSetting(sd.getUser(), name, oldValue, newValue);
    }

    @Override
    public void deleteSetting(String authToken, String name, String value)
    {
        SessionData sd = tracker.checkToken(authToken);
        userInfoDao.deleteSetting(sd.getUser(), name, value);
    }

    @Override
    public Map<String, List<String>> getSettings(String authToken, String prefix)
    {
        SessionData sd = tracker.checkToken(authToken);
        return userInfoDao.loadUserSettings(sd.getUser(), prefix).getMap();
    }
}
