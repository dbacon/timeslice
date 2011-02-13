package com.enokinomi.timeslice.lib.userinfo.impl;


import java.util.LinkedHashMap;
import java.util.Map;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.userinfo.api.IUserDbWorks;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoWorks;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.lib.userinfo.impl.UserDbWorks.AccountData;
import com.google.inject.Inject;


public class UserInfoDao implements IUserInfoDao
{
    private final IConnectionContext connContext;
    private final IUserInfoWorks userInfoWorks;
    private final IUserDbWorks userDbWorks;

    @Inject
    UserInfoDao(IConnectionContext connContext, IUserInfoWorks userInfoWorks, IUserDbWorks userDbWorks)
    {
        this.connContext = connContext;
        this.userInfoWorks = userInfoWorks;
        this.userDbWorks = userDbWorks;

        Sha1V1Scheme scheme = new Sha1V1Scheme();
        schemes.put(scheme.getClass().getCanonicalName(), scheme);
    }

    @Override
    public TsSettings loadUserSettings(final String username, final String prefix)
    {
        return connContext.doWorkWithinWritableContext(userInfoWorks.workLoadUserSettings(username, prefix));
    }

    @Override
    public void saveUserSettings(final String username, final TsSettings settings)
    {
        connContext.doWorkWithinWritableContext(userInfoWorks.workSaveUserSettings(username, settings));
    }

    @Override
    public void addSetting(String username, String name, String value)
    {
        connContext.doWorkWithinWritableContext(userInfoWorks.addSetting(username, name, value));
    }

    @Override
    public void editSetting(String username, String name, String oldValue, String newValue)
    {
        connContext.doWorkWithinWritableContext(userInfoWorks.editSetting(username, name, oldValue, newValue));
    }

    @Override
    public void deleteSetting(String username, String name, String value)
    {
        connContext.doWorkWithinWritableContext(userInfoWorks.deleteSetting(username, name, value));
    }

    @Override
    public void deleteSetting(String username, String name)
    {
        connContext.doWorkWithinWritableContext(userInfoWorks.deleteSetting(username, name));
    }

    @Override
    public boolean authenticate(String username, String password)
    {
        AccountData accountData = connContext.doWorkWithinWritableContext(userDbWorks.getUserAccountData(username, password));

        if (accountData == null) return false;

        if (!schemes.containsKey(accountData.getHashscheme())) return false;

        IPasswordScheme scheme = schemes.get(accountData.getHashscheme());

        return scheme.encode(accountData.getHashsalt(), password).equals(accountData.getHashvalue());
    }

    private Map<String, IPasswordScheme> schemes = new LinkedHashMap<String, IPasswordScheme>();

    @Override
    public int userCount()
    {
        return connContext.doWorkWithinWritableContext(userDbWorks.userCount());
    }

    @Override
    public void createUser(String username, String password, String schemeName)
    {
        if (!schemes.containsKey(schemeName)) throw new RuntimeException("Unsupported scheme '" + schemeName + "'.  Supported schemes are: " + schemes.keySet().toString());

        IPasswordScheme scheme = schemes.get(schemeName);

        String hashsalt = scheme.newSalt();
        String hashvalue = scheme.encode(hashsalt, password);

        connContext.doWorkWithinWritableContext(userDbWorks.createUser(
                username,
                scheme.getClass().getCanonicalName(),
                hashsalt,
                hashvalue));
    }

}
