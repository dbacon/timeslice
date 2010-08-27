package com.enokinomi.timeslice.timeslice;

import com.enokinomi.timeslice.timeslice.IUserInfoDao;

public class UserInfoDao implements IUserInfoDao
{
    @Override
    public TsSettings loadUserSettings(String username)
    {
        TsSettings settings = new TsSettings(9);
        return settings;
    }
}
