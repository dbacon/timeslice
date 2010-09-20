package com.enokinomi.timeslice.lib.userinfo;


public class UserInfoDao implements IUserInfoDao
{
    @Override
    public TsSettings loadUserSettings(String username)
    {
        TsSettings settings = new TsSettings(9);
        return settings;
    }
}
