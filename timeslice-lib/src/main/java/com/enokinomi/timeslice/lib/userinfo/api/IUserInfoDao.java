package com.enokinomi.timeslice.lib.userinfo.api;



public interface IUserInfoDao
{
    TsSettings loadUserSettings(String username, String prefix);
    void saveUserSettings(String username, TsSettings settings);

    void addSetting(String username, String name, String value);
    void editSetting(String username, String name, String oldValue, String newValue);
    void deleteSetting(String username, String name, String value);
    void deleteSetting(String user, String name);
}
