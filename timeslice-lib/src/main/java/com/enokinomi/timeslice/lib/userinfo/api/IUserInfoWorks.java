package com.enokinomi.timeslice.lib.userinfo.api;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;

public interface IUserInfoWorks
{

    IConnectionWork<TsSettings> workLoadUserSettings(String username, String prefix);
    IConnectionWork<Void> workSaveUserSettings(String username, TsSettings settings);

    IConnectionWork<Void> addSetting(String user, String name, String value);
    IConnectionWork<Void> editSetting(String user, String name, String oldValue, String newValue);
    IConnectionWork<Void> deleteSetting(String user, String name, String value);
    IConnectionWork<Void> deleteSetting(String username, String name);

}
