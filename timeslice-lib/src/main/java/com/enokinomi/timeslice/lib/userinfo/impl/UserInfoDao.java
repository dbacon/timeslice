package com.enokinomi.timeslice.lib.userinfo.impl;


import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoWorks;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.google.inject.Inject;


public class UserInfoDao implements IUserInfoDao
{
    private final IConnectionContext connContext;
    private final IUserInfoWorks userInfoWorks;

    @Inject
    UserInfoDao(IConnectionContext connContext, IUserInfoWorks userInfoWorks)
    {
        this.connContext = connContext;
        this.userInfoWorks = userInfoWorks;
    }

    @Override
    public TsSettings loadUserSettings(final String username, final String prefix)
    {
        return connContext.doWorkWithinContext(userInfoWorks.workLoadUserSettings(username, prefix));
    }

    @Override
    public void saveUserSettings(final String username, final TsSettings settings)
    {
        connContext.doWorkWithinContext(userInfoWorks.workSaveUserSettings(username, settings));
    }

    @Override
    public void addSetting(String username, String name, String value)
    {
        connContext.doWorkWithinContext(userInfoWorks.addSetting(username, name, value));
    }

    @Override
    public void editSetting(String username, String name, String oldValue, String newValue)
    {
        connContext.doWorkWithinContext(userInfoWorks.editSetting(username, name, oldValue, newValue));
    }

    @Override
    public void deleteSetting(String username, String name, String value)
    {
        connContext.doWorkWithinContext(userInfoWorks.deleteSetting(username, name, value));
    }

    @Override
    public void deleteSetting(String username, String name)
    {
        connContext.doWorkWithinContext(userInfoWorks.deleteSetting(username, name));
    }

}
