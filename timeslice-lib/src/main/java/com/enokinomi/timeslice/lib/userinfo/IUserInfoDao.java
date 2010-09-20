package com.enokinomi.timeslice.lib.userinfo;


public interface IUserInfoDao
{
    TsSettings loadUserSettings(String username);
}
