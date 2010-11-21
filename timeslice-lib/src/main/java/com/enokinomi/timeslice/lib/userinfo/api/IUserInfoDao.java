package com.enokinomi.timeslice.lib.userinfo.api;



public interface IUserInfoDao
{
    TsSettings loadUserSettings(String username, String prefix);
    void saveUserSettings(String username, TsSettings settings);
}
